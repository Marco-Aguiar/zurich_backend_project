package com.zurich.demo.books.googlebooks.service;

import com.zurich.demo.books.googlebooks.bookDTOs.GoogleBookDTO;
import com.zurich.demo.books.googlebooks.bookDTOs.BookApiResponse;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GoogleBooksRecommendationService {

    private final GoogleBooksClient client;

    public GoogleBooksRecommendationService(GoogleBooksClient client) {
        this.client = client;
    }

    public List<GoogleBookDTO> findRecommendations(String title, String subject) {
        if (subject != null && !subject.isBlank()) {
            return findBySubject(subject);
        }
        if (title != null && !title.isBlank()) {
            return findByTitleCategory(title);
        }
        return Collections.emptyList();
    }

    private List<GoogleBookDTO> findBySubject(String subject) {
        String query = "subject:" + subject;
        BookApiResponse response = client.executeQuery(query, 10, "US");
        if (response == null || response.items() == null) return Collections.emptyList();
        return response.items().stream()
                .map(GoogleBooksMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<GoogleBookDTO> findByTitleCategory(String title) {
        String query = "intitle:" + title;
        BookApiResponse initial = client.executeQuery(query, 1, "US");

        Optional<String> categoryOpt = Optional.ofNullable(initial)
                .flatMap(r -> Optional.ofNullable(r.items()))
                .flatMap(items -> items.stream().findFirst())
                .flatMap(v -> Optional.ofNullable(v.volumeInfo().categories()))
                .flatMap(c -> c.stream().findFirst());

        if (categoryOpt.isEmpty()) return Collections.emptyList();

        String recommendationQuery = "subject:" + categoryOpt.get();
        BookApiResponse recommendations = client.executeQuery(recommendationQuery, 10, "US");

        if (recommendations == null || recommendations.items() == null) return Collections.emptyList();

        return recommendations.items().stream()
                .filter(v -> !v.volumeInfo().title().equalsIgnoreCase(title))
                .map(GoogleBooksMapper::toDto)
                .collect(Collectors.toList());
    }
}
