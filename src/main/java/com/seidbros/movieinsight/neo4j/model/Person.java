package com.seidbros.movieinsight.neo4j.model;

import com.seidbros.movieinsight.neo4j.relationship.ActedInRelationship;
import com.seidbros.movieinsight.neo4j.relationship.WorkedAsRelationship;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Node("Person")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Person {
    @Id @GeneratedValue
    private Long id;
    private String personId;
    private String name;
    private Integer gender;
    private String role;

    @Relationship(type = "ACTED_IN", direction = Relationship.Direction.OUTGOING)
    private Set<ActedInRelationship> actedInMovies = new HashSet<>();

    @Relationship(type = "WORKED_AS", direction = Relationship.Direction.OUTGOING)
    private Set<WorkedAsRelationship> workedAsMovies = new HashSet<>();

    @Relationship(type = "COLLABORATED_WITH", direction = Relationship.Direction.OUTGOING)
    private Set<Person> collaborations = new HashSet<>();

}