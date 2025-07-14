package com.seidbros.movieinsight.controller;


import com.seidbros.movieinsight.dto.RatingInDto;
import com.seidbros.movieinsight.dto.base.Base;
import com.seidbros.movieinsight.dto.base.BaseList;
import com.seidbros.movieinsight.model.Movie;
import com.seidbros.movieinsight.service.BaseService;
import com.seidbros.movieinsight.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("movies")
@RequiredArgsConstructor
public class MovieController {

        private final MovieService movieService;
        private final BaseService baseService;

    @GetMapping("/filtered-movies")
    public ResponseEntity<?> getFilteredMovies(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        BaseList<?> filteredMovies = movieService.getFilteredMovies(query, language, genre, year, page, size);
        return baseService.restList(filteredMovies);
    }

    @GetMapping("popular")
    public ResponseEntity<?> getPopularMovies(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {

        BaseList<?> mostPopularMovies = movieService.getMostPopularMovies(page, size);
        return baseService.restList(mostPopularMovies);

    }

    @GetMapping("byMovieId")
    public ResponseEntity<?> getMovieByMovieId(@RequestParam String movieId) {
        Base<?> movie = movieService.getMovie(movieId);
        return baseService.rest(movie);

    }

    @PostMapping("rating")
    public ResponseEntity<?> rateMovie(@RequestBody RatingInDto ratingInDto) {
        Base<?> base = movieService.rateMovie(ratingInDto);
        return baseService.rest(base);
    }
}
