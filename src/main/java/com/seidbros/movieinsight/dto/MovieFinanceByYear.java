package com.seidbros.movieinsight.dto;


import lombok.Data;

@Data
public class MovieFinanceByYear {
    private int year;
    private long expense;
    private long income;
    private int count;
}
