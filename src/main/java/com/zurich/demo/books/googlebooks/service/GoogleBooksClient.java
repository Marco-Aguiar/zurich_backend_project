package com.zurich.demo.books.googlebooks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zurich.demo.books.googlebooks.bookDTOs.BookApiResponse;
import com.zurich.demo.exception.ExternalApiException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GoogleBooksClient {

    private static final Logger logger = LoggerFactory.getLogger(GoogleBooksClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Getter
    private final String apiKey;
    @Getter
    private final String baseUrl;

    public GoogleBooksClient(RestTemplate restTemplate,
                             ObjectMapper objectMapper,
                             @Value("${google.books.api.key}") String apiKey,
                             @Value("${google.books.api.baseUrl}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public BookApiResponse executeQuery(String query, int maxResults, String country) {
        String uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", query)
                .queryParam("country", country)
                .queryParam("maxResults", maxResults)
                .queryParam("printType", "books")
                .queryParam("key", apiKey)
                .encode()
                .toUriString();

        logger.info("Executing Google Books API call. URI: '{}'", uri);

        try {
            String rawJsonResponse = restTemplate.getForObject(uri, String.class);
            return objectMapper.readValue(rawJsonResponse, BookApiResponse.class);
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            logger.error("Google Books API error: {}", responseBody, e);
            if (responseBody.contains("API key not valid")) {
                throw new ExternalApiException("Invalid or expired API key.", e);
            }
            throw new ExternalApiException("Failed to retrieve data from Google Books API.", e);
        } catch (Exception e) {
            logger.error("Unexpected error calling Google Books API", e);
            throw new ExternalApiException("Unexpected error from Google Books API.", e);
        }
    }

}
