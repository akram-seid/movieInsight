package com.seidbros.movieinsight.mongo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TopNMovies {
    private String genre;
    private List<MovieSummary> summaries;

    @Data
    public static class MovieSummary {
        private String title;
        private LocalDate year;
        private Double vote;
    }
}
