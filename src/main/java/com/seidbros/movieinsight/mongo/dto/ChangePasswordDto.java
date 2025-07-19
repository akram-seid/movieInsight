package com.seidbros.movieinsight.mongo.dto;

public record ChangePasswordDto(String username, String oldPassword, String newPassword) {
}
