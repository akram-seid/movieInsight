package com.seidbros.movieinsight.mongo.dto;


import com.seidbros.movieinsight.mongo.enums.Role;

public record UserOutDto(String username, String token, String userId, Role role) {
}
