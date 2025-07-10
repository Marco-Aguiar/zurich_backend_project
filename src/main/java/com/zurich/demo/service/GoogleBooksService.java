package com.zurich.demo.service;

import com.zurich.demo.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoogleBooksService {

    // ✅ MELHORIA: Usando um logger profissional em vez de System.out
    private static final Logger logger = LoggerFactory.getLogger(GoogleBooksService.class);

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    public GoogleBooksService(RestTemplate restTemplate,
                              @Value("${google.books.api.key}") String apiKey,
                              @Value("${google.books.api.baseUrl}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;

        if (this.apiKey == null || this.apiKey.isBlank() || apiKey.startsWith("SUA_CHAVE")) {
            throw new IllegalStateException("A propriedade 'google.books.api.key' não está definida. Verifique seu application.properties e as variáveis de ambiente.");
        }
    }

    /**
     * Busca flexível de livros.
     */
    public List<GoogleBookDTO> searchBooks(String title, String author, String subject) {
        // ✅ MELHORIA: Montagem de query um pouco mais limpa
        List<String> queryParts = new ArrayList<>();
        if (title != null && !title.isBlank()) queryParts.add("intitle:" + title);
        if (author != null && !author.isBlank()) queryParts.add("inauthor:" + author);
        if (subject != null && !subject.isBlank()) queryParts.add("subject:" + subject);

        if (queryParts.isEmpty()) {
            throw new IllegalArgumentException("Pelo menos um critério de busca deve ser fornecido.");
        }
        String query = String.join("+", queryParts);

        BookApiResponse response = executeQuery(query, 20);

        if (response == null || response.items() == null) {
            return Collections.emptyList();
        }

        return response.items().stream()
                .map(this::convertToGoogleBookDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca o preço de um livro pelo ISBN.
     */
    public Optional<SaleInfo> findBookPriceByIsbn(String isbn) {
        String query = "isbn:" + isbn;
        BookApiResponse response = executeQuery(query, 1);

        // ✅ CORREÇÃO: Lógica segura que evita NullPointerException se "items" for nulo.
        return Optional.ofNullable(response)
                .flatMap(res -> Optional.ofNullable(res.items())) // Lida com 'items' nulo
                .flatMap(items -> items.stream().findFirst())     // Pega o primeiro item da lista
                .map(Volume::saleInfo);                           // Pega as informações de venda
    }

    /**
     * Busca recomendações com base no título de um livro.
     */
    public List<GoogleBookDTO> findRecommendationsByTitle(String title) {
        String query = "intitle:\"" + title + "\"";
        BookApiResponse initialResponse = executeQuery(query, 1);

        // ✅ CORREÇÃO: Lógica segura para extrair a categoria, evitando NullPointerException.
        Optional<String> categoryOpt = Optional.ofNullable(initialResponse)
                .flatMap(res -> Optional.ofNullable(res.items()))
                .flatMap(items -> items.stream().findFirst())
                .flatMap(volume -> Optional.ofNullable(volume.volumeInfo().categories()))
                .flatMap(categories -> categories.stream().findFirst());

        if (categoryOpt.isEmpty()) {
            return Collections.emptyList();
        }

        String recommendationQuery = "subject:\"" + categoryOpt.get() + "\"";
        BookApiResponse recommendationResponse = executeQuery(recommendationQuery, 10);

        if (recommendationResponse == null || recommendationResponse.items() == null) {
            return Collections.emptyList();
        }

        return recommendationResponse.items().stream()
                .filter(volume -> !volume.volumeInfo().title().equalsIgnoreCase(title))
                .map(this::convertToGoogleBookDTO)
                .collect(Collectors.toList());
    }


    // --- MÉTODOS PRIVADOS AUXILIARES ---

    /**
     * Centraliza a chamada à API.
     */
    private BookApiResponse executeQuery(String query, int maxResults) {
        String uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", query)
                .queryParam("maxResults", maxResults)
                .queryParam("printType", "books")
                .queryParam("key", apiKey)
                .encode()
                .toUriString();

        logger.info("Executando chamada para a API do Google Books. Query: '{}'", query);
        try {
            return restTemplate.getForObject(uri, BookApiResponse.class);
        } catch (Exception e) {
            // ✅ MELHORIA: Log de erro mais informativo
            logger.error("Erro ao chamar a API do Google Books para a query: '{}'", query, e);
            return null;
        }
    }

    /**
     * Converte o DTO da API para o DTO do seu sistema.
     */
    private GoogleBookDTO convertToGoogleBookDTO(Volume volume) {
        VolumeInfo info = volume.volumeInfo();
        GoogleBookDTO dto = new GoogleBookDTO();
        dto.setId(volume.id());
        dto.setTitle(info.title());
        dto.setAuthors(info.authors() != null ? info.authors() : Collections.emptyList());
        dto.setCategories(info.categories() != null ? info.categories() : Collections.emptyList());
        if (info.imageLinks() != null) {
            dto.setThumbnailUrl(info.imageLinks().thumbnail());
        }
        return dto;
    }
}