package com.seidbros.movieinsight.mongo.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
@Data
public class RatingDistribution {
    @Id
    private YearRatingId id;
    private int count;
    
    // Getters and setters
    @Data
    public static class YearRatingId {
        private int year;
        private int rating;
        // Getters and setters
    }
}