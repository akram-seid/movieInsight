package com.seidbros.movieinsight.neo4j.repository;

import com.seidbros.movieinsight.neo4j.model.GenreNeo;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface GenreRepository extends Neo4jRepository<GenreNeo, Long> {
}
