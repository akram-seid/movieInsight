package com.seidbros.movieinsight.neo4j.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Genre")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreNeo {
    @Id @GeneratedValue
    private Long genreId;
    private Integer id;
    private String name;
}