package com.zurich.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Contém as informações de venda de um livro, como disponibilidade e preço.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SaleInfo(String saleability, Price listPrice, Price retailPrice, String buyLink) {}