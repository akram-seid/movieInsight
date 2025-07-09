package com.seidbros.movieinsight.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "ratings")
public class Ratings {
    @Id
    private String id;
    private String movieId;
    private String userId;
    private Long rating;
    private Long timestamp;
}
