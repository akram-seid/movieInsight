package com.seidbros.movieinsight.service;


import com.seidbros.movieinsight.dto.base.Base;
import com.seidbros.movieinsight.dto.base.BaseList;
import com.seidbros.movieinsight.model.Rating;
import com.seidbros.movieinsight.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
