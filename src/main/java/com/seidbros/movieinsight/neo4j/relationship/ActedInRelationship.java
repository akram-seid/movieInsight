package com.seidbros.movieinsight.neo4j.relationship;

import com.seidbros.movieinsight.neo4j.model.MovieNeo;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

@RelationshipProperties
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ActedInRelationship {
    @RelationshipId
    private Long id;
    private String character;
    private Integer order;

    @TargetNode
    private MovieNeo movieNeo;


}