package com.seidbros.movieinsight.mapper;

import com.seidbros.movieinsight.model.Rating;
import com.seidbros.movieinsight.model.RecentRating;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface RatingMapper {

    Rating fromRecentRating(RecentRating rating);
}
