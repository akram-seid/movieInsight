package com.seidbros.movieinsight.neo4j.controller;

import com.seidbros.movieinsight.common.dto.base.BaseList;
import com.seidbros.movieinsight.mongo.service.BaseService;
import com.seidbros.movieinsight.neo4j.dto.FrequentCollaboratorsDto;
import com.seidbros.movieinsight.neo4j.dto.GenreCollaboratorsDto;
import com.seidbros.movieinsight.neo4j.dto.PersonInfluenceDto;
import com.seidbros.movieinsight.neo4j.dto.RoleInGenreDto;
import com.seidbros.movieinsight.neo4j.model.MovieNeo;
import com.seidbros.movieinsight.neo4j.repository.MovieNeoRepository;
import com.seidbros.movieinsight.neo4j.service.MovieNeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("movieNeo")
@RequiredArgsConstructor
public class MovieNeoController {

    private final MovieNeoService movieNeoService;
    private final BaseService baseService;


    @GetMapping("frqntCollabs")
    public ResponseEntity<?> frqntCollabs() {
        BaseList<?> frequentCollabs = movieNeoService.findFrequentCollabs();
        return baseService.restList(frequentCollabs);
    }


    @GetMapping("frqntCollabsByGenre")
    public ResponseEntity<?> frqntCollabs(@RequestParam("genre") String genre) {
        BaseList<?> frequentCollabs = movieNeoService.findFrequentCollabsByGenre(genre);
        return baseService.restList(frequentCollabs);
    }

    @GetMapping("findTopPeopleBYRoleInGenre")
    public ResponseEntity<?> findTopPeopleByRoleInGenre(@RequestParam("role") String role,@RequestParam("genre") String genre) {
        BaseList<?> topPeopleByRoleInGenre = movieNeoService.findTopPeopleByRoleInGenre(role, genre);
        return baseService.restList(topPeopleByRoleInGenre);
    }

    @GetMapping("topRatedMovies")
    public ResponseEntity<?> frqntCollabs(@RequestParam("genre") String genre, @RequestParam("rating") int rating, @RequestParam("limit") int limit) {
        BaseList<?> topRatedMoviesByGenre = movieNeoService.getTopRatedMoviesByGenre(genre, rating, limit);
        return baseService.restList(topRatedMoviesByGenre);
    }

    @GetMapping("mostConnectedPeople")
    public ResponseEntity<?> mostConnectedPeople() {
        BaseList<?> mostConnectedPeople = movieNeoService.findMostConnectedPeople();
        return baseService.restList(mostConnectedPeople);
    }


}
