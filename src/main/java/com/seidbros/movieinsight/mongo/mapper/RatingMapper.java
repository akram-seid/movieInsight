package com.seidbros.movieinsight.mongo.mapper;

import com.seidbros.movieinsight.mongo.model.entity.Rating;
import com.seidbros.movieinsight.mongo.model.RecentRating;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface RatingMapper {

    Rating fromRecentRating(RecentRating rating);
}
