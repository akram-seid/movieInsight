package com.seidbros.movieinsight.mongo.dto;

public record DeleteReplyInDto(String replyId, String postId,String userId, String moderator,String reason) {
}
