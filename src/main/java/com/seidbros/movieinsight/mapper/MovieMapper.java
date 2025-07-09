package com.seidbros.movieinsight.mapper;

import com.seidbros.movieinsight.dto.MovieOutDto;
import com.seidbros.movieinsight.model.Movie;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieOutDto toDto(Movie movie);
    List<MovieOutDto> toDtoList(List<Movie> movies);
}