package com.seidbros.movieinsight.neo4j.relationship;

import com.seidbros.movieinsight.neo4j.model.MovieNeo;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

@RelationshipProperties
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkedAsRelationship {
    @RelationshipId
    private Long id;
    private String job;
    private String department;

    @TargetNode
    private MovieNeo movieNeo;


}