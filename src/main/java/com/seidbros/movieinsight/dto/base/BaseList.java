package com.seidbros.movieinsight.dto.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseList<T> {
    private List<T> ts;
    private boolean status;
    private long count;
    private long pages;
    private String message;
}
