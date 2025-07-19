package com.seidbros.movieinsight.neo4j.repository;

import com.seidbros.movieinsight.neo4j.model.UserNeo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.time.LocalDateTime;
import java.util.Date;

public interface UserNeoRepository extends Neo4jRepository<UserNeo, String> {

    @Query("MATCH (u:User {userId: $userId}), (m:Movie {movieId: $movieId}) " +
            "MERGE (u)-[r:RATED]->(m) " +
            "ON CREATE SET r.rating = $rating, r.timestamp = $timestamp " +
            "ON MATCH SET r.rating = $rating, r.timestamp = $timestamp")
    void createRatedRelationship(String userId, String movieId, Integer rating, Date timestamp);


}
