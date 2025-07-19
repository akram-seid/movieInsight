package com.seidbros.movieinsight.neo4j.relationship;

import com.seidbros.movieinsight.neo4j.model.MovieNeo;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;
import java.util.Date;

@RelationshipProperties
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatedRelationship {
    @RelationshipId
    private Long id;
    private Integer rating;
    private Date timestamp;

    @TargetNode
    private MovieNeo movieNeo;

}