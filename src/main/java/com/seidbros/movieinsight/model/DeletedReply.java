package com.seidbros.movieinsight.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeletedReply {
    private String originalAuthor;
    private String deletionReason;
    private LocalDateTime deletedAt = LocalDateTime.now();
    private String moderator;
    private String originalMessage;
}
