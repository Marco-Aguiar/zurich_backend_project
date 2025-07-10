package com.zurich.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Representa o objeto raiz da resposta da API do Google Books para uma busca.
 * O campo principal é "items", que é uma lista de volumes (livros).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BookApiResponse(List<Volume> items) {}