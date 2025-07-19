package com.seidbros.movieinsight.mongo.repository;

import com.seidbros.movieinsight.mongo.dto.AverageRuntime;
import com.seidbros.movieinsight.mongo.dto.GenreTitleCount;
import com.seidbros.movieinsight.mongo.dto.MovieFinanceByYear;
import com.seidbros.movieinsight.mongo.dto.TopNMovies;
import com.seidbros.movieinsight.mongo.model.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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



    @Aggregation(pipeline = {
            "{ $unwind: '$genres' }",
            "{$group: {_id: '$genres.name', titleCount: { $sum: 1 }  } }",
            "{ $sort: { titleCount: -1 } }"
    })
    List<GenreTitleCount> getGenreTitleCount();


    @Aggregation(pipeline = {
            "{ $match: { release_date: { $exists: true, $not: { $in: ['', 'nan', null] } }, $expr: { $regexMatch: { input: { $toString: '$release_date' }, regex: '^[0-9]{4}-[0-9]{2}-[0-9]{2}T00:00:00.000Z$' } } } }",
            "{$unwind:  '$genres'}",
            "{$sort: {vote_count: -1 }}",
            "{ $project: {year: { $year: { $toDate: '$release_date' } },title: 1, vote_average: 1,genres: 1,release_date: 1} }",
            "{ $group:  {_id: '$genres.name', summaries: {$push:  {title: '$title', year:  '$release_date' ,vote: '$vote_average'} } }}",
            "{$project:  { genre: '$_id', _id:  0, summaries: {$slice:  ['$summaries',?#{[0]}]}}}"
    })
    List<TopNMovies> getTopNMovies(int limit);

    Page<Movie> findMoviesBySyncedIs(boolean synced, Pageable pageable);


}
