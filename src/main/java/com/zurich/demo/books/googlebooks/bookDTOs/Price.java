package com.zurich.demo.books.googlebooks.bookDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Price(double amount, String currencyCode) {}