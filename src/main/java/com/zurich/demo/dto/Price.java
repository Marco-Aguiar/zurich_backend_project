package com.zurich.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representa um valor monetário, incluindo o montante e o código da moeda.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Price(double amount, String currencyCode) {}