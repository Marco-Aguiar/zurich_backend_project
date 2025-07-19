package com.zurich.demo.books.googlebooks.bookDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Volume(String id, VolumeInfo volumeInfo, SaleInfo saleInfo) {}