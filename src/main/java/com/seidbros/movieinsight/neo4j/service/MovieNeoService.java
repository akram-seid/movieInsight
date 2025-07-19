package com.seidbros.movieinsight.neo4j.service;

import com.seidbros.movieinsight.common.dto.base.BaseList;
import com.seidbros.movieinsight.mongo.service.BaseService;
import com.seidbros.movieinsight.neo4j.dto.FrequentCollaboratorsDto;
import com.seidbros.movieinsight.neo4j.dto.GenreCollaboratorsDto;
import com.seidbros.movieinsight.neo4j.dto.PersonInfluenceDto;
import com.seidbros.movieinsight.neo4j.dto.RoleInGenreDto;
import com.seidbros.movieinsight.neo4j.model.MovieNeo;
import com.seidbros.movieinsight.neo4j.repository.MovieNeoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieNeoService {

    private final MovieNeoRepository movieNeoRepository;
    private final BaseService baseService;

    public BaseList<?> findFrequentCollabs(){
        List<FrequentCollaboratorsDto> frequentCollaborators = movieNeoRepository.findFrequentCollaborators();
        if (frequentCollaborators.isEmpty()) {
            return baseService.listError("FrequentCollaborators not found");
        }
        return baseService.success(frequentCollaborators);
    }


    public BaseList<?> findFrequentCollabsByGenre(String genre){
        List<GenreCollaboratorsDto> frequentCollaboratorsByGenre = movieNeoRepository.findFrequentCollaboratorsByGenre(genre);
        if (frequentCollaboratorsByGenre.isEmpty()) {
            return baseService.listError("FrequentCollaborators by Genre not found");
        }
        return baseService.success(frequentCollaboratorsByGenre);
    }

    public BaseList<?> findMostConnectedPeople(){
        List<PersonInfluenceDto> mostConnectedPeople = movieNeoRepository.findMostConnectedPeople();
        if (mostConnectedPeople.isEmpty()) {
            return baseService.listError("Most connected people not found");
        }
        return baseService.success(mostConnectedPeople);
    }

    public BaseList<?> findTopPeopleByRoleInGenre(String jobTitle, String genre){
        List<RoleInGenreDto> topPeopleByRoleInGenre = movieNeoRepository.findTopPeopleByRoleInGenre(jobTitle, genre);
        if (topPeopleByRoleInGenre.isEmpty()) {
            return baseService.listError("Top People not found");
        }
        return baseService.success(topPeopleByRoleInGenre);
    }

    public BaseList<?> getTopRatedMoviesByGenre(String genre, int limit, int offset){
        List<MovieNeo> topRatedMoviesByGenre = movieNeoRepository.getTopRatedMoviesByGenre(genre, limit, offset);
        if (topRatedMoviesByGenre.isEmpty()) {
            return baseService.listError("Top Rated Movies not found");
        }
        return baseService.success(topRatedMoviesByGenre);
    }


}
