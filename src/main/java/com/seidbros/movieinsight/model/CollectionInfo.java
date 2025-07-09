package com.seidbros.movieinsight.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionInfo {
    private int id;
    private String name;
    private String poster_path;
    private String backdrop_path;
}