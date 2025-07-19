package com.seidbros.movieinsight.neo4j.model;

import com.seidbros.movieinsight.neo4j.relationship.RatedRelationship;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNeo {
    @Id
    private String id;
    private String userId;
    private String username;

    @Relationship(type = "RATED", direction = Relationship.Direction.OUTGOING)
    private Set<RatedRelationship> ratings = new HashSet<>();
}