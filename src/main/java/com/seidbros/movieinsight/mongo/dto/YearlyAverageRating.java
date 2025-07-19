package com.seidbros.movieinsight.mongo.dto;

public interface YearlyAverageRating {
        Integer get_id();
        Double getAvgRating();
        Integer getCount();
    }