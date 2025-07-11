package com.seidbros.movieinsight.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record ForumPostOutDto (
     String title,
     String content,
     String movieId,
     String createdBy,
     String postId,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
     LocalDateTime timestamp){
}
