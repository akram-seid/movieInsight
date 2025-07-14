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
public class Reply {
    private String replyId;
    private String userId;
    private String content;
    private LocalDateTime timestamp;
}
