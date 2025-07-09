package com.zurich.demo.service;

import com.zurich.demo.dto.GoogleBookDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class GoogleBooksService {

    private final String apiKey = System.getenv("GOOGLE_BOOKS_API_KEY");
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    public List<GoogleBookDTO> searchBooks(String title, String author, String subject) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("API key do Google Books não está definida.");
        }

        // Monta query
        StringBuilder queryBuilder = new StringBuilder();
        if (title != null && !title.isBlank()) queryBuilder.append("intitle:").append(title).append(" ");
        if (author != null && !author.isBlank()) queryBuilder.append("inauthor:").append(author).append(" ");
        if (subject != null && !subject.isBlank()) queryBuilder.append("subject:").append(subject);

        String query = queryBuilder.toString().trim().replace(" ", "+");

        if (query.isBlank()) {
            throw new IllegalArgumentException("At least one filter (title, author, subject) must be provided.");
        }

        // Monta URI segura
        String uri = UriComponentsBuilder
                .fromHttpUrl(BASE_URL)
                .queryParam("q", query)
                .queryParam("key", apiKey)
                .build()
                .encode()
                .toUriString();

        System.out.println("📡 URL Final: " + uri);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(uri, Map.class);

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        if (items == null) return Collections.emptyList();

        List<GoogleBookDTO> results = new ArrayList<>();

        for (Map<String, Object> item : items) {
            Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");

            GoogleBookDTO dto = new GoogleBookDTO();
            dto.setId((String) item.get("id"));
            dto.setTitle((String) volumeInfo.get("title"));
            dto.setAuthors((List<String>) volumeInfo.getOrDefault("authors", List.of()));
            dto.setCategories((List<String>) volumeInfo.getOrDefault("categories", List.of()));

            Map<String, String> imageLinks = (Map<String, String>) volumeInfo.get("imageLinks");
            if (imageLinks != null) {
                dto.setThumbnailUrl(imageLinks.get("thumbnail"));
            }

            results.add(dto);
        }

        return results;
    }
}
