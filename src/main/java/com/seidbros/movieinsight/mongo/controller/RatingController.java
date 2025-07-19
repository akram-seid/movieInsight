package com.seidbros.movieinsight.mongo.controller;


import com.seidbros.movieinsight.mongo.service.BaseService;
import com.seidbros.movieinsight.mongo.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;
    private final BaseService baseService;

    @GetMapping("list")
    public ResponseEntity<?> getRating(@RequestParam("from")
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from, @RequestParam("to")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to) {
        return baseService.restList(ratingService.getRating(from, to));
    }

}
