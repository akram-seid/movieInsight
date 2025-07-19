package com.seidbros.movieinsight.mongo.mapper;

import com.seidbros.movieinsight.mongo.dto.MovieOutDto;
import com.seidbros.movieinsight.mongo.model.entity.Movie;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieOutDto toDto(Movie movie);
    List<MovieOutDto> toDtoList(List<Movie> movies);
}