package com.zurich.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SaleInfo(String saleability, Price listPrice, Price retailPrice, String buyLink) {}