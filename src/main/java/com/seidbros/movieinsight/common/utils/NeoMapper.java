package com.seidbros.movieinsight.common.utils;


import com.seidbros.movieinsight.mongo.model.Genre;
import com.seidbros.movieinsight.mongo.model.entity.Movie;
import com.seidbros.movieinsight.mongo.model.entity.User;
import com.seidbros.movieinsight.neo4j.model.GenreNeo;
import com.seidbros.movieinsight.neo4j.model.MovieNeo;
import com.seidbros.movieinsight.neo4j.model.UserNeo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NeoMapper {
    @Mapping(target = "id", ignore = true)
    MovieNeo maptoMovie(Movie movie);
    GenreNeo maptoGenre(Genre genre);
    UserNeo maptoUser(User user);
}

