package com.zurich.demo.service;

import com.zurich.demo.dto.BookApiResponse;
import com.zurich.demo.dto.GoogleBookDTO;
import com.zurich.demo.dto.SaleInfo;
import com.zurich.demo.dto.Volume;
import com.zurich.demo.dto.VolumeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.ArrayList;
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

    public GoogleBooksService(RestTemplate restTemplate,
                              @Value("${google.books.api.key}") String apiKey,
                              @Value("${google.books.api.baseUrl}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;

        if (this.apiKey == null || this.apiKey.isBlank() || apiKey.startsWith("YOUR_KEY")) {
            throw new IllegalStateException("The 'google.books.api.key' property is not defined. Please check your application.properties and environment variables.");
        }
    }

    public List<GoogleBookDTO> searchBooks(String title, String author, String subject) {
        List<String> queryParts = new ArrayList<>();
        if (title != null && !title.isBlank()) queryParts.add("intitle:" + title);
        if (author != null && !author.isBlank()) queryParts.add("inauthor:" + author);
        if (subject != null && !subject.isBlank()) queryParts.add("subject:" + subject);

        if (queryParts.isEmpty()) {
            throw new IllegalArgumentException("At least one search criterion must be provided.");
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

    public Optional<SaleInfo> findBookPriceByIsbn(String isbn) {
        String query = "isbn:" + isbn;
        BookApiResponse response = executeQuery(query, 1);

        return Optional.ofNullable(response)
                .flatMap(res -> Optional.ofNullable(res.items()))
                .flatMap(items -> items.stream().findFirst())
                .map(Volume::saleInfo);
    }

    public List<GoogleBookDTO> findRecommendationsByTitle(String title) {
        String query = "intitle:\"" + title + "\"";
        BookApiResponse initialResponse = executeQuery(query, 1);

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

    private BookApiResponse executeQuery(String query, int maxResults) {
        String uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", query)
                .queryParam("maxResults", maxResults)
                .queryParam("printType", "books")
                .queryParam("key", apiKey)
                .encode()
                .toUriString();

        logger.info("Executing Google Books API call. Query: '{}'", query);
        try {
            return restTemplate.getForObject(uri, BookApiResponse.class);
        } catch (Exception e) {
            logger.error("Error calling Google Books API for query: '{}'", query, e);
            return null;
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