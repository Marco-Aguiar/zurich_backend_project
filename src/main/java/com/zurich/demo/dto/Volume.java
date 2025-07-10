package com.zurich.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Volume(String id, VolumeInfo volumeInfo, SaleInfo saleInfo) {}