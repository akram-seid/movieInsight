package com.seidbros.movieinsight.dto.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Base<T> {
    private T ob;
    private boolean status;
    private String message;
}
