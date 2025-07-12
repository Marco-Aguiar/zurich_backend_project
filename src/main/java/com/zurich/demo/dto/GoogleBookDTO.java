package com.zurich.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class GoogleBookDTO {
    private String id;
    private String title;
    private List<String> authors;
    private String thumbnailUrl;
    private List<String> categories;
    private Double averageRating;
    private Integer ratingsCount;
}
