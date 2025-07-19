package com.zurich.demo.books.googlebooks.bookDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookApiResponse(int totalItems, List<Volume> items) {}