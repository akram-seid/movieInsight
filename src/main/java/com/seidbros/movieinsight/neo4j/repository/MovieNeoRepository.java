package com.seidbros.movieinsight.neo4j.repository;

import com.seidbros.movieinsight.neo4j.dto.FrequentCollaboratorsDto;
import com.seidbros.movieinsight.neo4j.dto.GenreCollaboratorsDto;
import com.seidbros.movieinsight.neo4j.dto.PersonInfluenceDto;
import com.seidbros.movieinsight.neo4j.dto.RoleInGenreDto;
import com.seidbros.movieinsight.neo4j.model.MovieNeo;
import com.seidbros.movieinsight.neo4j.model.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieNeoRepository extends Neo4jRepository<MovieNeo, Long> {
    
    Optional<MovieNeo> findMovieNeoByMovieId(String movieId);


    @Query("""
        MATCH (p1:Person)-[:ACTED_IN]->(m:Movie)<-[:ACTED_IN]-(p2:Person)
        WHERE p1.personId <> p2.personId
        WITH p1, p2, COUNT(m) as movieCount
        WHERE movieCount >= $minMovies
        MERGE (p1)-[r:COLLABORATED_WITH]-(p2)
        SET r.movieCount = movieCount
        """)
    void createCollaborationRelationships(@Param("minMovies") int minMovies);


    @Query("MATCH (p1:Person)-[:ACTED_IN]->(m:Movie)<-[:ACTED_IN]-(p2:Person) " +
            "WHERE id(p1) < id(p2) " +
            "WITH p1, p2, count(m) AS collaborations " +
            "ORDER BY collaborations DESC " +
            "LIMIT 10 " +
            "RETURN p1.name AS person1, p2.name AS person2, collaborations")
    List<FrequentCollaboratorsDto> findFrequentCollaborators();


    @Query("MATCH (p1:Person)-[:ACTED_IN]->(m:Movie)-[:IN_GENRE]->(g:Genre {name: $genreName}) " +
            "MATCH (p2:Person)-[:ACTED_IN]->(m) " +
            "WHERE id(p1) < id(p2) " +
            "WITH p1, p2, g, count(m) AS collaborations " +
            "ORDER BY collaborations DESC " +
            "LIMIT 10 " +
            "RETURN p1.name AS person1, p2.name AS person2, g.name as genre, collaborations")
    List<GenreCollaboratorsDto> findFrequentCollaboratorsByGenre(@Param("genreName") String genreName);

    @Query("MATCH (p:Person)-[r:ACTED_IN|WORKED_AS]->(m:Movie) " +
            "WITH p, count(r) as degree " +
            "ORDER BY degree DESC " +
            "LIMIT 10 " +
            "RETURN p.name as person, degree")
    List<PersonInfluenceDto> findMostConnectedPeople();

    @Query("MATCH (p:Person)-[r:WORKED_AS {job: $jobTitle}]->(m:Movie)-[:IN_GENRE]->(g:Genre {name: $genreName}) " +
            "WITH p, count(m) as movieCount " +
            "ORDER BY movieCount DESC " +
            "LIMIT 10 " +
            "RETURN p.name as person, movieCount")
    List<RoleInGenreDto> findTopPeopleByRoleInGenre(@Param("jobTitle") String jobTitle, @Param("genreName") String genreName);



    @Query("""
        MATCH (m:Movie)-[:IN_GENRE]->(g:Genre)
        WHERE g.name = $genreName
        OPTIONAL MATCH (m)<-[r:RATED]-(:User)
        WITH m, AVG(r.rating) as avgRating, COUNT(r) as ratingCount
        WHERE ratingCount >= $minRatings
        RETURN m
        ORDER BY avgRating DESC
        LIMIT $limit
        """)
    List<MovieNeo> getTopRatedMoviesByGenre(
        @Param("genreName") String genreName,
        @Param("minRatings") int minRatings,
        @Param("limit") int limit
    );

}