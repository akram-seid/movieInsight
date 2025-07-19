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
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    @Transactional("neo4jTransactionManager")
    public void syncMoviesAndGenres() {
        log.info("Starting movie batch sync");
        int page = 0;
        List<Movie> movies;
        do {
            Pageable pageable = PageRequest.of(page, BATCH_SIZE);
            movies = movieRepository.findMoviesBySyncedIs(false,pageable).getContent();
            if (movies.isEmpty()) break;

            log.info("Processing batch {} with {} movies", page, movies.size());
            for (Movie movie : movies) {
                processSingleMovie(movie);
            }
            page++;
        } while (!movies.isEmpty());
        log.info("Movie batch sync completed");
    }

    private void processSingleMovie(Movie movie) {
        Map<String, Person> personMap = new HashMap<>();

        MovieNeo movieNeo = neoMapper.maptoMovie(movie);

        Set<GenreNeo> genreNeos = movie.getGenres().stream()
                .map(neoMapper::maptoGenre)
                .collect(Collectors.toSet());
        movieNeo.setGenreNeos(genreNeos);

        movieNeo = movieNeoRepository.save(movieNeo);

        if (movie.getCredits() != null) {
            processActors(movie, movieNeo, personMap);
            processCrew(movie, movieNeo, personMap);
        }

        if (!personMap.isEmpty()) {
            personRepository.saveAll(personMap.values());
        }
    }

    private void processActors(Movie movie, MovieNeo movieNeo, Map<String, Person> personMap) {
        for (Cast cast : movie.getCredits().getCast()) {
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

        for (Crew crew : movie.getCredits().getCrew()) {
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
            Optional<Person> existingPerson = personRepository.findByPersonId(id);
            return existingPerson.orElseGet(() -> Person.builder()
                    .personId(id)
                    .name(name)
                    .gender(gender)
                    .actedInMovies(new HashSet<>())
                    .workedAsMovies(new HashSet<>())
                    .build());

        });
    }

    @Transactional("neo4jTransactionManager")
    public void syncMovie(Document fullDocument) {
        if (fullDocument == null) return;
        Movie movie = mongoConverter.read(Movie.class, fullDocument);
        log.info("REAL-TIME SYNC: Processing movie '{}' (ID: {})", movie.getTitle(), movie.getMovieId());
        processSingleMovie(movie);
    }


    @Transactional("neo4jTransactionManager")
    public void syncUser(Document fullDocument) {
        if (fullDocument == null) return;
        User user = mongoConverter.read(User.class, fullDocument);
        log.info("REAL-TIME SYNC: Processing user '{}'", user.getUserId());
        UserNeo userNeo = neoMapper.maptoUser(user);
        userNeoRepository.save(userNeo);
    }

    @Transactional("neo4jTransactionManager")
    public void syncRating(Document fullDocument) {
        if (fullDocument == null) return;
        Rating rating = mongoConverter.read(Rating.class, fullDocument);
        log.info("REAL-TIME SYNC: Processing rating for user '{}' on movie '{}'", rating.getUserId(), rating.getMovieId());
        userNeoRepository.createRatedRelationship(
                rating.getUserId(),
                rating.getMovieId(),
                rating.getRating(),
                rating.getTimestamp()
        );
    }

    
    @Transactional
    public void syncUsersAndRatings() {
        log.info("Starting user and ratings sync");
        
        List<User> unsyncedUsers = userRepository.findUsersBySyncedIs(false);
        if (unsyncedUsers.isEmpty()) {
            log.info("No unsynced users found");
            return;
        }
        
        for (User user : unsyncedUsers) {
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
                }
            }
            
            userNeo.setRatings(ratedRelationships);
            userNeoRepository.save(userNeo);
            user.setSynced(true);
        }
        
        userRepository.saveAll(unsyncedUsers);
        log.info("User and ratings sync completed");
    }
    
    @Transactional("neo4jTransactionManager")
    public void createCollaborations() {
        log.info("Creating collaboration relationships");
        movieNeoRepository.createCollaborationRelationships(5);
        
        log.info("Collaboration relationships created");
    }

}