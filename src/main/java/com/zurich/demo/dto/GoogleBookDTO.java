package com.zurich.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class GoogleBookDTO {
    private String id;
    private String title;
    private List<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private Integer pageCount;
    private String printType;
    private List<String> categories;
    private Double averageRating;
    private Integer ratingsCount;
    private String language;
    private String previewLink;
    private String infoLink;
    private String canonicalVolumeLink;
    private String thumbnailUrl;
    private String isbn13;
}
