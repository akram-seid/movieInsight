package com.seidbros.movieinsight.repository;

import com.seidbros.movieinsight.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {

    Optional<Movie> findMovieByMovieId(String movieId);

}
