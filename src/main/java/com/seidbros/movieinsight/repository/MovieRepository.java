package com.seidbros.movieinsight.repository;

import com.seidbros.movieinsight.dto.AverageRuntime;
import com.seidbros.movieinsight.dto.MovieFinanceByYear;
import com.seidbros.movieinsight.model.Movie;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {

    Optional<Movie> findMovieByMovieId(String movieId);

    @Aggregation(pipeline = {
            "{ $match: { budget: { $gt: 0 }, revenue: { $gt: 0 }, release_date: { $gte: ?#{[0]}, $lte: ?#{[1]} } } }",

            "{ $project: { year: { $year: { $toDate: '$release_date' } },budget: 1, revenue: 1 } }",

            "{ $group: { _id: '$year', expense: { $sum: '$budget' }, income: { $sum: '$revenue' }, count: { $sum: 1 } } }",

            "{ $project: { _id: 0, year: '$_id', expense: 1, income: 1, count: 1 } }",

            "{ $sort: { year: 1 } }"
    })
    List<MovieFinanceByYear> getBudgetVsRevenueByYear(LocalDate from, LocalDate to);


    @Aggregation(pipeline = {
            "{ $match: { runtime: { $gt: 0 }, release_date: { $exists: true, $ne: 'nan' } } }",

            "{ $project: { year: { $year: { $toDate: '$release_date' } }, runtime: 1 } }",

            "{ $group: { _id: '$year', totalRuntime: { $sum: '$runtime' }, movieCount: { $sum: 1 } } }",

            "{ $project: { _id: 0, year: '$_id', time: { $divide: ['$totalRuntime', '$movieCount'] } } }",

            "{ $sort: { year: 1 } }"
    })
    List<AverageRuntime> getAverageRuntimeByYear();


}
