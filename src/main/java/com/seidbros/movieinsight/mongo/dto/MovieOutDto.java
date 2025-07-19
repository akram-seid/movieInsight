package com.seidbros.movieinsight.mongo.dto;

import com.seidbros.movieinsight.mongo.model.Genre;

import java.util.List;



 public record MovieOutDto(String movieId,String title, List<Genre> genres,String posterPath, Double voteAverage,String releaseDate ) {

}
