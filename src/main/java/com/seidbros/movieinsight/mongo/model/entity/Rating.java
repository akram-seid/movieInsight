package com.seidbros.movieinsight.mongo.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "ratings")
public class Rating {
    @Id
    private String id;
    private String movieId;
    private String userId;
    private Integer rating;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date timestamp;
    private boolean synced;
}
