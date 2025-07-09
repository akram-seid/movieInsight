package com.seidbros.movieinsight.dto;

public record DeleteReplyInDto(String replyId, String postId,String userId, String moderator,String reason) {
}
