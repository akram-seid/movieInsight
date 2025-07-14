package com.seidbros.movieinsight.service;


import com.seidbros.movieinsight.dto.*;
import com.seidbros.movieinsight.dto.base.BaseList;
import com.seidbros.movieinsight.repository.MovieRepository;
import com.seidbros.movieinsight.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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


        List<RatingRepository.YearlyAverageRating> averageRatingByYear = ratingRepository.findAverageRatingByYear(startDate, endDate);
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
}
