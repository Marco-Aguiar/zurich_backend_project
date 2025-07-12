package com.zurich.demo.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zurich.demo.dto.BookApiResponse;
import com.zurich.demo.dto.GoogleBookDTO;
import com.zurich.demo.dto.SaleInfo;
import com.zurich.demo.dto.Volume;
import com.zurich.demo.dto.VolumeInfo;
import com.zurich.demo.exception.ExternalApiException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GoogleBooksService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleBooksService.class);

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public GoogleBooksService(RestTemplate restTemplate,
                              @Value("${google.books.api.key}") String apiKey,
                              @Value("${google.books.api.baseUrl}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        if (this.apiKey == null || this.apiKey.isBlank() || apiKey.startsWith("YOUR_KEY")) {
            throw new IllegalStateException("The 'google.books.api.key' property is not defined.");
        }
    }

    @PostConstruct
    public void logApiKeyPrefix() {
        logger.info("GOOGLE_BOOKS_API_KEY received by Spring: '{}'", apiKey != null ? apiKey.substring(0, 10) + "..." : "null");
    }

    public List<GoogleBookDTO> searchBooks(String title, String author) {
        StringBuilder queryBuilder = new StringBuilder();

        if (title != null && !title.isBlank()) {
            queryBuilder.append(title).append(" ");
        }
        if (author != null && !author.isBlank()) {
            queryBuilder.append("inauthor:").append(author).append(" ");
        }

        String query = queryBuilder.toString().trim().replace(" ", "+");

        logger.info("Executing Google Books API call with query: '{}'", query);

        BookApiResponse response = executeQuery(query, 20, "US");

        if (response == null || response.items() == null) {
            logger.warn("No items found or null response received for query: '{}'", query);
            return Collections.emptyList();
        }

        return response.items().stream()
                .map(this::convertToGoogleBookDTO)
                .collect(Collectors.toList());
    }

    public Optional<SaleInfo> findBookPriceByIsbn(String isbn, String country) {
        String query = "isbn:" + isbn;
        BookApiResponse response = executeQuery(query, 1, country);

        if (response == null || response.items() == null) {
            logger.warn("No sale info found or null response received for ISBN: '{}'", isbn);
            return Optional.empty();
        }

        return Optional.ofNullable(response)
                .flatMap(res -> Optional.ofNullable(res.items()))
                .flatMap(items -> items.stream().findFirst())
                .map(Volume::saleInfo);
    }

    public List<GoogleBookDTO> findRecommendations(String title, String subject) {
        if (subject != null && !subject.isBlank()) {
            String recommendationQuery = "subject:" + subject;
            BookApiResponse response = executeQuery(recommendationQuery, 10, "US");

            if (response == null || response.items() == null) {
                logger.warn("No recommendations found or null response received for subject: '{}'", subject);
                return Collections.emptyList();
            }

            return response.items().stream()
                    .map(this::convertToGoogleBookDTO)
                    .collect(Collectors.toList());
        }

        if (title != null && !title.isBlank()) {
            String query = "intitle:" + title;
            BookApiResponse initialResponse = executeQuery(query, 1, "US");

            Optional<String> categoryOpt = Optional.ofNullable(initialResponse)
                    .flatMap(res -> Optional.ofNullable(res.items()))
                    .flatMap(items -> items.stream().findFirst())
                    .flatMap(volume -> Optional.ofNullable(volume.volumeInfo().categories()))
                    .flatMap(categories -> categories.stream().findFirst());

            if (categoryOpt.isEmpty()) {
                logger.warn("No category found for title '{}' to base recommendations on.", title);
                return Collections.emptyList();
            }

            String recommendationQuery = "subject:" + categoryOpt.get();
            BookApiResponse recommendationResponse = executeQuery(recommendationQuery, 10, "US");

            if (recommendationResponse == null || recommendationResponse.items() == null) {
                logger.warn("No recommendations found or null response received for category: '{}'", categoryOpt.get());
                return Collections.emptyList();
            }

            return recommendationResponse.items().stream()
                    .filter(volume -> !volume.volumeInfo().title().equalsIgnoreCase(title))
                    .map(this::convertToGoogleBookDTO)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }


    private BookApiResponse executeQuery(String query, int maxResults, String country) {
        String uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", query)
                .queryParam("country", country)
                .queryParam("maxResults", maxResults)
                .queryParam("printType", "books")
                .queryParam("key", apiKey)
                .encode()
                .toUriString();

        logger.info("Executing Google Books API call. Full URI: '{}'", uri);

        try {
            String rawJsonResponse = restTemplate.getForObject(uri, String.class);
            logger.info("Raw JSON response received...");
            return objectMapper.readValue(rawJsonResponse, BookApiResponse.class);

        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            logger.error("Google Books API error: {}", responseBody, e);

            // Detecta se o erro é relacionado à API Key
            if (responseBody != null && responseBody.contains("API key not valid")) {
                throw new ExternalApiException("❌ External service error: Your API key is invalid or expired. Please update it and try again.", e);
            }

            // Mensagem genérica para outros erros HTTP 4xx
            throw new ExternalApiException("❌ External service error: Failed to retrieve data from Google Books API. Please try again later.", e);

        } catch (Exception e) {
            logger.error("Unexpected error calling Google Books API", e);
            throw new ExternalApiException("❌ Unexpected error while calling Google Books API. Please try again later.", e);
        }
    }


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
