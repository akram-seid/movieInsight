package com.seidbros.movieinsight.mongo.dto;

public record RatingsPerYear (
     int year,
     Integer rating1,
     Integer rating2,
     Integer rating3,
     Integer rating4,
     Integer rating5){
}
