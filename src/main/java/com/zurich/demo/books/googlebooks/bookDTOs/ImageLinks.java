package com.zurich.demo.books.googlebooks.bookDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ImageLinks(String smallThumbnail, String thumbnail) {}