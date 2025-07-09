package com.zurich.demo.controller;

import com.zurich.demo.dto.GoogleBookDTO;
import com.zurich.demo.service.GoogleBooksService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/external/books")
public class GoogleBooksController {

    private final GoogleBooksService googleBooksService;

    public GoogleBooksController(GoogleBooksService googleBooksService) {
        this.googleBooksService = googleBooksService;
    }

    @GetMapping("/search")
    public List<GoogleBookDTO> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String subject) {
        return googleBooksService.searchBooks(title, author, subject);
    }
}
