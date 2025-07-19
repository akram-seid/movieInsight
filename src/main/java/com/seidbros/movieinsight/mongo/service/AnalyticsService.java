package com.seidbros.movieinsight.mongo.service;


import com.seidbros.movieinsight.common.dto.base.BaseList;
import com.seidbros.movieinsight.mongo.dto.*;
import com.seidbros.movieinsight.mongo.repository.MovieRepository;
import com.seidbros.movieinsight.mongo.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final RatingRepository ratingRepository;
    private final MovieRepository movieRepository;
    private final BaseService baseService;

    public BaseList<?> getRatingsAnalytics(Date startDate, Date endDate) {

        List<RatingsPerYear> results = ratingRepository.findRatingCountsPerYear(startDate, endDate);
        return baseService.success(results);
    }

    public BaseList<?> getAverageRatingsAnalytics(Date startDate, Date endDate) {


        List<YearlyAverageRating> averageRatingByYear = ratingRepository.findAverageRatingByYear(startDate, endDate);
        return baseService.success(averageRatingByYear);
    }

    public BaseList<?> getRevenueBudgetAnalytics(LocalDate startDate, LocalDate endDate) {
        List<MovieFinanceByYear> budgetVsRevenueByYear = movieRepository.getBudgetVsRevenueByYear(startDate,endDate);
        return baseService.success(budgetVsRevenueByYear);
    }

    public BaseList<?> getAverageRuntime() {
        List<AverageRuntime> averageRuntimeByYear = movieRepository.getAverageRuntimeByYear();
        return baseService.success(averageRuntimeByYear);
    }
    public BaseList<?> getGenreTitleCount() {
        List<GenreTitleCount> genreTitleCount = movieRepository.getGenreTitleCount();
        return baseService.success(genreTitleCount);
    }

    public BaseList<?> getTopNMovies(int limit) {
        List<String> topGenres = List.of(
                "Drama",
                "Comedy",
                "Thriller",
                "Romance",
                "Action",
                "Horror",
                "Crime",
                "Documentary",
                "Adventure",
                "Science Fiction"
        );

        List<TopNMovies> topNMovies = movieRepository.getTopNMovies(limit);
        List<TopNMovies> filtered = topNMovies.stream()
                .filter(movie -> topGenres.contains(movie.getGenre()))
                .collect(Collectors.toList());
        return baseService.success(filtered);
    }

}
