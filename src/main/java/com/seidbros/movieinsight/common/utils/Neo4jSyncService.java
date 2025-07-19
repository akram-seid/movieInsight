package com.seidbros.movieinsight.common.utils;

import com.seidbros.movieinsight.mongo.model.Cast;
import com.seidbros.movieinsight.mongo.model.Crew;
import com.seidbros.movieinsight.mongo.model.entity.Movie;
import com.seidbros.movieinsight.mongo.model.entity.Rating;
import com.seidbros.movieinsight.mongo.model.entity.User;
import com.seidbros.movieinsight.mongo.repository.MovieRepository;
import com.seidbros.movieinsight.mongo.repository.RatingRepository;
import com.seidbros.movieinsight.mongo.repository.UserRepository;
import com.seidbros.movieinsight.neo4j.model.*;
import com.seidbros.movieinsight.neo4j.relationship.*;
import com.seidbros.movieinsight.neo4j.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class Neo4jSyncService {

    // Neo4j Repositories
    private final MovieNeoRepository movieNeoRepository;
    private final UserNeoRepository userNeoRepository;
    private final PersonRepository personRepository;


    // MongoDB Repositories
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;

    // Utilities
    private final NeoMapper neoMapper;
    private final MongoConverter mongoConverter;

    private static final int BATCH_SIZE = 1000;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    @Transactional("neo4jTransactionManager")
    public void syncMoviesAndGenres() {
        log.info("Starting movie batch sync from MongoDB to Neo4j.");
        int page = 0;
        List<Movie> movies;
        do {
            Pageable pageable = PageRequest.of(page, BATCH_SIZE);
            try {
                movies = movieRepository.findAll(pageable).getContent();
                if (movies.isEmpty()) break;

                log.info("Processing batch {} with {} movies for initial sync.", page, movies.size());
                for (Movie movie : movies) {
                    processSingleMovie(movie);
                }
                page++;
            } catch (DataAccessException e) {
                log.error("Failed to fetch movies from MongoDB for batch {}. Skipping batch. Error: {}", page, e.getMessage(), e);
                break;
            } catch (Exception e) {
                log.error("An unexpected error occurred during movie batch sync at page {}. Error: {}", page, e.getMessage(), e);
                break;
            }
        } while (!movies.isEmpty());
        log.info("Movie batch sync completed.");
    }

    @Transactional("neo4jTransactionManager")
    public void processSingleMovie(Movie movie) {
        if (movie == null) {
            log.warn("Attempted to process a null movie document. Skipping.");
            return;
        }

        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                Map<String, Person> personMap = new HashMap<>();

                MovieNeo movieNeo = neoMapper.maptoMovie(movie);

                Set<GenreNeo> genreNeos = movie.getGenres().stream()
                        .map(neoMapper::maptoGenre)
                        .collect(Collectors.toSet());
                movieNeo.setGenreNeos(genreNeos);

                movieNeoRepository.save(movieNeo);
                log.debug("Movie '{}' (ID: {}) and its genres saved/updated in Neo4j.", movie.getTitle(), movie.getMovieId());

                if (movie.getCredits() != null) {
                    processActors(movie, movieNeo, personMap);
                    processCrew(movie, movieNeo, personMap);
                }

                if (!personMap.isEmpty()) {
                    personRepository.saveAll(personMap.values());
                    log.debug("Processed {} persons for movie '{}'.", personMap.size(), movie.getTitle());
                }

                return;
            } catch (ServiceUnavailableException | DataAccessException e) {
                retries++;
                log.warn("Transient Neo4j error during movie sync for '{}' (ID: {}). Retrying ({}/{}). Error: {}",
                        movie.getTitle(), movie.getMovieId(), retries, MAX_RETRIES, e.getMessage());
                try {
                    TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS * retries); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Retry delay interrupted for movie sync.", ie);
                    break;
                }
            } catch (Exception e) {
                log.error("Failed to sync movie '{}' (ID: {}) to Neo4j after {} retries. Error: {}",
                        movie.getTitle(), movie.getMovieId(), retries, e.getMessage(), e);
                break;
            }
        }
        if (retries == MAX_RETRIES) {
            log.error("Failed to sync movie '{}' (ID: {}) to Neo4j after {} retries. Giving up on this document.",
                    movie.getTitle(), movie.getMovieId(), MAX_RETRIES);
        }
    }

    private void processActors(Movie movie, MovieNeo movieNeo, Map<String, Person> personMap) {
        if (movie.getCredits().getCast() == null) return;
        for (Cast cast : movie.getCredits().getCast()) {
            if (cast.getCreditId() == null || cast.getCreditId().trim().isEmpty()) {
                log.warn("Skipping cast member with null/empty creditId for movie '{}': {}", movie.getTitle(), cast.getName());
                continue;
            }
            Person person = getOrCreatePerson(cast.getCreditId(), cast.getName(), cast.getGender(), personMap);

            ActedInRelationship relationship = ActedInRelationship.builder()
                    .movieNeo(movieNeo)
                    .character(cast.getCharacter())
                    .order(cast.getOrder())
                    .build();

            if (person.getActedInMovies() == null) {
                person.setActedInMovies(new HashSet<>());
            }
            person.getActedInMovies().add(relationship);
        }
    }

    private void processCrew(Movie movie, MovieNeo movieNeo, Map<String, Person> personMap) {
        if (movie.getCredits().getCrew() == null) return;
        for (Crew crew : movie.getCredits().getCrew()) {
            if (crew.getCreditId() == null || crew.getCreditId().trim().isEmpty()) {
                log.warn("Skipping crew member with null/empty creditId for movie '{}': {}", movie.getTitle(), crew.getName());
                continue;
            }
            Person person = getOrCreatePerson(crew.getCreditId(), crew.getName(), crew.getGender(), personMap);

            WorkedAsRelationship relationship = WorkedAsRelationship.builder()
                    .movieNeo(movieNeo)
                    .department(crew.getDepartment())
                    .job(crew.getJob())
                    .build();

            if (person.getWorkedAsMovies() == null) {
                person.setWorkedAsMovies(new HashSet<>());
            }
            person.getWorkedAsMovies().add(relationship);
        }
    }

    private Person getOrCreatePerson(String creditId, String name, Integer gender, Map<String, Person> personMap) {
        return personMap.computeIfAbsent(creditId, id -> {
            try {
                Optional<Person> existingPerson = personRepository.findByPersonId(id);
                return existingPerson.orElseGet(() -> Person.builder()
                        .personId(id)
                        .name(name)
                        .gender(gender)
                        .actedInMovies(new HashSet<>())
                        .workedAsMovies(new HashSet<>())
                        .build());
            } catch (DataAccessException e) {
                log.error("Failed to find existing person by ID '{}' in Neo4j. Creating new person. Error: {}", id, e.getMessage(), e);
                return Person.builder()
                        .personId(id)
                        .name(name)
                        .gender(gender)
                        .actedInMovies(new HashSet<>())
                        .workedAsMovies(new HashSet<>())
                        .build();
            }
        });
    }

    @Transactional("neo4jTransactionManager")
    public void syncMovie(Document fullDocument) {
        if (fullDocument == null) {
            log.warn("Attempted to sync a null movie change stream document. Skipping.");
            return;
        }
        try {
            Movie movie = mongoConverter.read(Movie.class, fullDocument);
            log.info("REAL-TIME SYNC: Processing movie '{}' (ID: {}) from MongoDB change stream.", movie.getTitle(), movie.getMovieId());
            processSingleMovie(movie);
        } catch (Exception e) {
            log.error("Failed to read movie from change stream document or process it. Document ID: {}. Error: {}",
                    fullDocument.get("_id"), e.getMessage(), e);
        }
    }

    @Transactional("neo4jTransactionManager")
    public void syncUser(Document fullDocument) {
        if (fullDocument == null) {
            log.warn("Attempted to sync a null user change stream document. Skipping.");
            return;
        }
        try {
            User user = mongoConverter.read(User.class, fullDocument);
            if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
                log.warn("Skipping user sync due to null/empty userId: {}", user.getUsername());
                return;
            }
            log.info("REAL-TIME SYNC: Processing user '{}' (ID: {}) from MongoDB change stream.", user.getUsername(), user.getUserId());

            int retries = 0;
            while (retries < MAX_RETRIES) {
                try {
                    UserNeo userNeo = neoMapper.maptoUser(user);
                    userNeoRepository.save(userNeo);
                    log.debug("User '{}' (ID: {}) saved/updated in Neo4j.", user.getUsername(), user.getUserId());
                    return;
                } catch (ServiceUnavailableException | DataAccessException e) {
                    retries++;
                    log.warn("Transient Neo4j error during user sync for '{}' (ID: {}). Retrying ({}/{}). Error: {}",
                            user.getUsername(), user.getUserId(), retries, MAX_RETRIES, e.getMessage());
                    try {
                        TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS * retries);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Retry delay interrupted for user sync.", ie);
                        break;
                    }
                } catch (Exception e) {
                    log.error("Failed to sync user '{}' (ID: {}) to Neo4j after {} retries. Error: {}",
                            user.getUsername(), user.getUserId(), retries, e.getMessage(), e);
                    break;
                }
            }
            if (retries == MAX_RETRIES) {
                log.error("Failed to sync user '{}' (ID: {}) to Neo4j after {} retries. Giving up on this document.",
                        user.getUsername(), user.getUserId(), MAX_RETRIES);
            }
        } catch (Exception e) {
            log.error("Failed to read user from change stream document or process it. Document ID: {}. Error: {}",
                    fullDocument.get("_id"), e.getMessage(), e);
        }
    }

    @Transactional("neo4jTransactionManager")
    public void syncRating(Document fullDocument) {
        if (fullDocument == null) {
            log.warn("Attempted to sync a null rating change stream document. Skipping.");
            return;
        }
        try {
            Rating rating = mongoConverter.read(Rating.class, fullDocument);
            if (rating.getUserId() == null || rating.getUserId().trim().isEmpty() ||
                    rating.getMovieId() == null || rating.getMovieId().trim().isEmpty() ||
                    rating.getRating() == null) {
                log.warn("Skipping rating sync due to missing userId, movieId, or rating value. Document ID: {}", fullDocument.get("_id"));
                return;
            }
            log.info("REAL-TIME SYNC: Processing rating for user '{}' on movie '{}' from MongoDB change stream.", rating.getUserId(), rating.getMovieId());

            int retries = 0;
            while (retries < MAX_RETRIES) {
                try {
                    userNeoRepository.createRatedRelationship(
                            rating.getUserId(),
                            rating.getMovieId(),
                            rating.getRating(),
                            rating.getTimestamp()
                    );
                    log.debug("Rated relationship created/updated for user '{}' on movie '{}'.", rating.getUserId(), rating.getMovieId());
                    return;
                } catch (ServiceUnavailableException | DataAccessException e) {
                    retries++;
                    log.warn("Transient Neo4j error during rating sync for user '{}' on movie '{}'. Retrying ({}/{}). Error: {}",
                            rating.getUserId(), rating.getMovieId(), retries, MAX_RETRIES, e.getMessage());
                    try {
                        TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS * retries);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Retry delay interrupted for rating sync.", ie);
                        break;
                    }
                } catch (Exception e) {
                    log.error("Failed to sync rating for user '{}' on movie '{}' after {} retries. Error: {}",
                            rating.getUserId(), rating.getMovieId(), retries, e.getMessage(), e);
                    break;
                }
            }
            if (retries == MAX_RETRIES) {
                log.error("Failed to sync rating for user '{}' on movie '{}' after {} retries. Giving up on this document.",
                        rating.getUserId(), rating.getMovieId(), MAX_RETRIES);
            }
        } catch (Exception e) {
            log.error("Failed to read rating from change stream document or process it. Document ID: {}. Error: {}",
                    fullDocument.get("_id"), e.getMessage(), e);
        }
    }

    @Transactional("neo4jTransactionManager")
    public void syncUsersAndRatings() {
        log.info("Starting user and ratings batch sync from MongoDB to Neo4j.");

        List<User> unsyncedUsers = userRepository.findUsersBySyncedIs(false);
        if (unsyncedUsers.isEmpty()) {
            log.info("No unsynced users found for batch sync.");
            return;
        }

        for (User user : unsyncedUsers) {
            try {
                UserNeo userNeo = neoMapper.maptoUser(user);

                List<Rating> ratings = ratingRepository.findRatingsByUserId(user.getUserId());
                Set<RatedRelationship> ratedRelationships = new HashSet<>();

                for (Rating rating : ratings) {
                    Optional<MovieNeo> movieNeoOpt = movieNeoRepository.findMovieNeoByMovieId(rating.getMovieId());
                    if (movieNeoOpt.isPresent()) {
                        RatedRelationship ratedRelationship = RatedRelationship.builder()
                                .movieNeo(movieNeoOpt.get())
                                .rating(rating.getRating())
                                .timestamp(rating.getTimestamp())
                                .build();
                        ratedRelationships.add(ratedRelationship);
                    } else {
                        log.warn("Movie with ID '{}' not found in Neo4j for rating by user '{}'. Skipping rating relationship.", rating.getMovieId(), user.getUserId());
                    }
                }

                userNeo.setRatings(ratedRelationships);
                userNeoRepository.save(userNeo);
                user.setSynced(true);
                log.debug("User '{}' (ID: {}) and their ratings synced in batch.", user.getUsername(), user.getUserId());
            } catch (Exception e) {
                log.error("Error syncing user '{}' (ID: {}) and their ratings in batch. Error: {}",
                        user.getUsername(), user.getUserId(), e.getMessage(), e);
            }
        }

        try {
            userRepository.saveAll(unsyncedUsers);
            log.info("Updated 'synced' status for {} users in MongoDB.", unsyncedUsers.size());
        } catch (DataAccessException e) {
            log.error("Failed to update 'synced' status for users in MongoDB. Error: {}", e.getMessage(), e);
        }
        log.info("User and ratings batch sync completed.");
    }

    @Transactional("neo4jTransactionManager")
    public void createCollaborations() {
        log.info("Starting creation of collaboration relationships in Neo4j.");
        try {
            movieNeoRepository.createCollaborationRelationships(5);
            log.info("Collaboration relationships created successfully.");
        } catch (Exception e) {
            log.error("Failed to create collaboration relationships. Error: {}", e.getMessage(), e);
        }
    }

}