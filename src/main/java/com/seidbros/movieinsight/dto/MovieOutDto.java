package com.seidbros.movieinsight.dto;

import com.seidbros.movieinsight.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;



 public record MovieOutDto(String movieId,String title, List<Genre> genres,String posterPath, Double voteAverage,String releaseDate ) {

}
