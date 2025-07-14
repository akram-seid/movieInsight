package com.seidbros.movieinsight.controller;


import com.seidbros.movieinsight.dto.base.BaseList;
import com.seidbros.movieinsight.service.AnalyticsService;
import com.seidbros.movieinsight.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    private final BaseService baseService;


    @GetMapping("ratingTrend")
    public ResponseEntity<?> ratingDistribution(@RequestParam("from")
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from, @RequestParam("to")
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to) {
        BaseList<?> ratingsAnalytics = analyticsService.getAverageRatingsAnalytics(from, to);
        return baseService.restList(ratingsAnalytics);
    }

    @GetMapping("ratingDistribution")
    public ResponseEntity<?> ratingTrend(@RequestParam("from")
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from, @RequestParam("to")
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to) {
        BaseList<?> ratingsAnalytics = analyticsService.getRatingsAnalytics(from, to);
        return baseService.restList(ratingsAnalytics);
    }

    @GetMapping("budgetVsRevenue")
    public ResponseEntity<?> getBudgetVsRevenue(@RequestParam("from")
                                                    LocalDate from, @RequestParam("to")
    LocalDate to) {
        BaseList<?> ratingsAnalytics = analyticsService.getRevenueBudgetAnalytics(from, to);
        return baseService.restList(ratingsAnalytics);
    }

    @GetMapping("avgRuntime")
    public ResponseEntity<?> avgRuntime() {
        BaseList<?> analyticsServiceRuntime = analyticsService.getAverageRuntime();
        return baseService.restList(analyticsServiceRuntime);
    }
}
