package com.seidbros.movieinsight.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cast {
    private Integer castId;
    private String character;
    private String creditId;
    private Integer gender;
    private Integer id;
    private String name;
    private Integer order;
    private String profilePath;
}
