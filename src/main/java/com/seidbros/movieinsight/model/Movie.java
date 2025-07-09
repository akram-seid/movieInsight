package com.seidbros.movieinsight.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "movies")
public class Movie {
    @Id
    private String id;
    private String movieId;
    private String title;
    private String originalTitle;
    private String language;
    private String status;
    private Long runtime;
    private Long voteCount;
    private Double voteAverage;
    private Double popularity;
    private BigDecimal budget;
    private BigDecimal revenue;
    private String imdbId;
    private String tagline;
    private String overview;
    private String posterPath;

    private Boolean adult;
    private Boolean video;
    private String homepage;
    private String releaseDate;

    private List<Genre> genres;
    private List<Company> productionCompanies;
    private List<Country> productionCountries;
    private List<Language> spokenLanguages;
    private List<Keyword> keywords;
    private Credit credits;
    private CollectionInfo belongsToCollection;
    private List<Rating> recentRatings;
    private Link links;
    private Double avgRating;
    private Integer ratingCount;
    private Map<Double, Integer> ratingDistribution;


}
