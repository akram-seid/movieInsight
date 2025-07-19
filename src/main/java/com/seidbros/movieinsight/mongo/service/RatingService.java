package com.seidbros.movieinsight.mongo.service;


import com.seidbros.movieinsight.common.dto.base.BaseList;
import com.seidbros.movieinsight.mongo.model.entity.Rating;
import com.seidbros.movieinsight.mongo.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final BaseService baseService;

    public BaseList<?> getRating(Date from, Date to) {
        List<Rating> ratingsPerYearByYear = ratingRepository.findRatingsByTimestampBetween(from, to);
        return baseService.success(ratingsPerYearByYear);
    }
}
