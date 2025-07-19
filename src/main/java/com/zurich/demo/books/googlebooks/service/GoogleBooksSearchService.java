package com.zurich.demo.books.googlebooks.service;

import com.zurich.demo.books.googlebooks.bookDTOs.GoogleBookDTO;
import com.zurich.demo.books.googlebooks.bookDTOs.BookApiResponse;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleBooksSearchService {

    private final GoogleBooksClient client;

    public GoogleBooksSearchService(GoogleBooksClient client) {
        this.client = client;
    }

    public List<GoogleBookDTO> searchBooks(String title, String author) {
        StringBuilder queryBuilder = new StringBuilder();
        if (title != null && !title.isBlank()) queryBuilder.append(title).append(" ");
        if (author != null && !author.isBlank()) queryBuilder.append("inauthor:").append(author);
        String query = queryBuilder.toString().trim().replace(" ", "+");

        BookApiResponse response = client.executeQuery(query, 20, "US");
        if (response == null || response.items() == null) return Collections.emptyList();

        return response.items().stream()
                .map(GoogleBooksMapper::toDto)
                .collect(Collectors.toList());
    }
}
