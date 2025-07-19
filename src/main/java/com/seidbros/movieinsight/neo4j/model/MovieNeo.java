package com.seidbros.movieinsight.neo4j.model;

import com.seidbros.movieinsight.neo4j.relationship.ActedInRelationship;
import com.seidbros.movieinsight.neo4j.relationship.RatedRelationship;
import com.seidbros.movieinsight.neo4j.relationship.WorkedAsRelationship;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapping;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Node("Movie")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieNeo {
    @Id@GeneratedValue
    private Long id;
    private String movieId;
    private String title;
    private BigDecimal budget;
    private BigDecimal revenue;
    private String releaseDate;

    @Relationship(type = "IN_GENRE", direction = Relationship.Direction.OUTGOING)
    private Set<GenreNeo> genreNeos = new HashSet<>();
}