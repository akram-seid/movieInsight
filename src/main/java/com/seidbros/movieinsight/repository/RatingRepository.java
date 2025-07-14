package com.seidbros.movieinsight.repository;

import com.seidbros.movieinsight.dto.RatingsPerYear;
import com.seidbros.movieinsight.model.Rating;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface RatingRepository extends MongoRepository<Rating, String> {

    @Aggregation(pipeline = {
            "{ $match: { timestamp: { $lte: ?#{[1]}, $gte: ?#{[0]} }, rating: { $exists: true } } }",
            "{ $project: { year: { $year: { $toDate: '$timestamp' } }, rating: 1 } }",
            "{ $group: { _id: { year: '$year', rating: '$rating' }, count: { $sum: 1 } } }",
            "{ $group: { _id: '$_id.year', " +
                    "rating1: { $sum: { $cond: [ { $eq: ['$_id.rating', 1] }, '$count', 0 ] } }, " +
                    "rating2: { $sum: { $cond: [ { $eq: ['$_id.rating', 2] }, '$count', 0 ] } }, " +
                    "rating3: { $sum: { $cond: [ { $eq: ['$_id.rating', 3] }, '$count', 0 ] } }, " +
                    "rating4: { $sum: { $cond: [ { $eq: ['$_id.rating', 4] }, '$count', 0 ] } }, " +
                    "rating5: { $sum: { $cond: [ { $eq: ['$_id.rating', 5] }, '$count', 0 ] } } } }",

            "{ $project: { _id: 0, year: '$_id', rating1: 1, rating2: 1, rating3: 1, rating4: 1, rating5: 1 } }",

            "{ $sort: { year: 1 } }"
    })
    List<RatingsPerYear> findRatingCountsPerYear(Date minDate, Date maxDate);


    @Aggregation(pipeline = {
            "{ $match: { timestamp: { $lte: ?#{[1]}, $gte: ?#{[0]} }, rating: { $exists: true } } }",
            "{ $project: { year: { $year: { $toDate: '$timestamp' } }, rating: 1 } }",
            "{ $group: { _id: '$year', avgRating: { $avg: '$rating' }, count: { $sum: 1 } } }",
            "{ $sort: { _id: 1 } }"
    })
    List<YearlyAverageRating> findAverageRatingByYear(Date startDate, Date endDate);


    public interface YearlyAverageRating {
        Integer get_id();
        Double getAvgRating();
        Integer getCount();
    }


    List<Rating> findRatingsByTimestampBetween(Date startDate, Date endDate);
}
