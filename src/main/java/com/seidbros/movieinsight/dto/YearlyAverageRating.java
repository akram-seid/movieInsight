package com.seidbros.movieinsight.dto;

import lombok.Data;

@Data
public class YearlyAverageRating {
    private Integer id;
    private Double AvgRating;
    private Integer count;
}
