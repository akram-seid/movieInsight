package com.seidbros.movieinsight.neo4j.repository;

import com.seidbros.movieinsight.neo4j.model.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface PersonRepository extends Neo4jRepository<Person, Long> {
    Optional<Person> findByPersonId(String id);
}
