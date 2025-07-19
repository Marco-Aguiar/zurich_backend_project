package com.zurich.demo.books.googlebooks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zurich.demo.books.googlebooks.bookDTOs.GoogleBookDTO;
import com.zurich.demo.books.googlebooks.bookDTOs.Volume;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
public class GoogleBooksDetailsService {

    private final GoogleBooksClient client;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    public GoogleBooksDetailsService(GoogleBooksClient client, RestTemplate restTemplate, ObjectMapper mapper) {
        this.client = client;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    public Optional<GoogleBookDTO> getBookDetails(String googleBookId) {
        String uri = UriComponentsBuilder.fromUriString(client.getBaseUrl() + "/" + googleBookId)
                .queryParam("key", client.getApiKey())
                .encode()
                .toUriString();

        try {
            String raw = restTemplate.getForObject(uri, String.class);
            Volume volume = mapper.readValue(raw, Volume.class);
            return Optional.of(GoogleBooksMapper.toDto(volume));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
