package com.zurich.demo.books.googlebooks.bookDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VolumeInfo(
        String title,
        List<String> authors,
        String publisher,
        String publishedDate,
        String description,
        Integer pageCount,
        String printType,
        List<String> categories,
        Double averageRating,
        Integer ratingsCount,
        String language,
        String previewLink,
        String infoLink,
        String canonicalVolumeLink,
        List<IndustryIdentifier> industryIdentifiers,
        ImageLinks imageLinks
) {}
