package com.seidbros.movieinsight.neo4j.dto;

public record GenreCollaboratorsDto(String person1, String person2, String genre, Long collaborations) {
}