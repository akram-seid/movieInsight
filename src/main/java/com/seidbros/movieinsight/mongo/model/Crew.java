package com.seidbros.movieinsight.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Crew {
    private String creditId;
    private String department;
    private Integer gender;
    private Integer id;
    private String job;
    private String name;
    private String profilePath;
}
