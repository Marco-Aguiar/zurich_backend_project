package com.zurich.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representa um único livro (volume) na resposta da API.
 * Contém o ID do livro, as informações detalhadas (volumeInfo) e os dados de venda (saleInfo).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Volume(String id, VolumeInfo volumeInfo, SaleInfo saleInfo) {}