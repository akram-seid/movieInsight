package com.seidbros.movieinsight.service;


import com.seidbros.movieinsight.dto.RatingInDto;
import com.seidbros.movieinsight.dto.base.Base;
import com.seidbros.movieinsight.dto.base.BaseList;
import com.seidbros.movieinsight.mapper.MovieMapper;
import com.seidbros.movieinsight.model.Movie;
import com.seidbros.movieinsight.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final BaseService baseService;
    private final MongoTemplate mongoTemplate;
    private final MovieMapper movieMapper;

    public BaseList<?> getFilteredMovies(String queryString, String language, String genre, String year,  int page, int size) {
        Query query = new Query();

        if (queryString != null && !queryString.isEmpty()) {
            query.addCriteria(
                    new Criteria().orOperator(
                            Criteria.where("title").regex(queryString, "i"),
                            Criteria.where("credits.cast.name").regex(queryString, "i"),
                            Criteria.where("credits.crew.name").regex(queryString, "i"),
                            Criteria.where("keywords.name").regex(queryString, "i")
                    )
            );
        }

        if (language != null && !language.isEmpty()) {
            query.addCriteria(Criteria.where("spoken_languages.name")
                    .regex("^" + Pattern.quote(language) + "$", "i"));
        }

        if (genre != null && !genre.isEmpty()) {
            query.addCriteria(Criteria.where("genres.name").is(genre));
        }

        if (year != null && !year.isEmpty()) {
            query.addCriteria(Criteria.where("release_date").regex(year, "i"));
        }

        long total = mongoTemplate.count(query, Movie.class);
        long pages = total / size + (total % size == 0 ? 0 : 1);

        query.with(PageRequest.of(page, size));

        List<Movie> movies = mongoTemplate.find(query, Movie.class);
        return  baseService.success(movies, total, pages);
    }

    public BaseList<?> getMostPopularMovies( int page, int size) {
        Page<Movie> all = movieRepository.findAll(
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, "vote_count")
                )
        );
        if (all.hasContent()) {
            return  baseService.success(all.map(movieMapper::toDto).getContent(), all.getTotalElements(),all.getTotalPages());
        }

        return baseService.listError();

    }


    public Base<?> getMovie(String movieId) {

        Optional<Movie> movieByMovieId = movieRepository.findMovieByMovieId(movieId);
        if (movieByMovieId.isPresent()) {
            return  baseService.success(movieByMovieId.get());
        }
        return baseService.error("Movie not found");
    }


}
