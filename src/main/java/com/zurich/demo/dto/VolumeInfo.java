package com.zurich.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VolumeInfo(
        String title,
        List<String> authors,
        List<String> categories,
        String description,
        ImageLinks imageLinks,
        Double averageRating,
        Integer ratingsCount
) {}
