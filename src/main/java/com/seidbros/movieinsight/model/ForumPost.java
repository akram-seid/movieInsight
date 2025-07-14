package com.seidbros.movieinsight.model;


import org.springframework.data.annotation.Id;import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "forum_posts")
public class ForumPost {
    @Id
    private String id;
    private String title;
    private String content;
    private List<Reply> replies;
    private List<DeletedReply> deletedReplies;
    private String movieId;
    private String movieTitle;
    private String createdBy;
    private String postId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime timestamp;
}
