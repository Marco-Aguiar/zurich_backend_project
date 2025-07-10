package com.zurich.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Contém os links para as imagens da capa de um livro em diferentes tamanhos.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ImageLinks(String smallThumbnail, String thumbnail) {}