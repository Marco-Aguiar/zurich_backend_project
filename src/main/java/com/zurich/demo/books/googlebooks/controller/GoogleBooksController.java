package com.zurich.demo.books.googlebooks.controller;

import com.zurich.demo.books.googlebooks.service.GoogleBooksDetailsService;
import com.zurich.demo.books.googlebooks.service.GoogleBooksRecommendationService;
import com.zurich.demo.books.googlebooks.service.GoogleBooksSearchService;
import com.zurich.demo.books.googlebooks.bookDTOs.GoogleBookDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "External Books API", description = "Endpoints for searching books using the Google Books API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/external/books")
public class GoogleBooksController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleBooksController.class);

    private final GoogleBooksDetailsService googleBooksDetailsService;
    private final GoogleBooksRecommendationService googleBooksRecommendationService;
    private final GoogleBooksSearchService googleBooksSearchService;

    public GoogleBooksController(GoogleBooksDetailsService detailsService,
                                 GoogleBooksRecommendationService recommendationService,
                                 GoogleBooksSearchService searchService) {
        this.googleBooksDetailsService = detailsService;
        this.googleBooksRecommendationService = recommendationService;
        this.googleBooksSearchService = searchService;
    }

    @Operation(
            summary = "Search for books",
            description = "Flexible search by title and/or author. At least one parameter must be provided."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books found"),
            @ApiResponse(responseCode = "400", description = "Missing search parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @GetMapping("/search")
    public List<GoogleBookDTO> searchBooks(
            @Parameter(description = "Book title") @RequestParam(required = false) String title,
            @Parameter(description = "Book author") @RequestParam(required = false) String author) {
        logger.info("External book search with title: '{}' and author: '{}'", title, author);
        return googleBooksSearchService.searchBooks(title, author);
    }

    @Operation(
            summary = "Get Google Book details by ID",
            description = "Fetches complete details of a book from Google Books API using its Google Book ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book details retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GoogleBookDTO.class))),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @GetMapping("/{googleBookId}")
    public ResponseEntity<GoogleBookDTO> getBookDetailsById(
            @Parameter(description = "Google Book ID", example = "OEBaAAAAMAAJ") @PathVariable String googleBookId) {

        logger.info("Fetching Google Book details for ID: {}", googleBookId);
        Optional<GoogleBookDTO> bookOpt = googleBooksDetailsService.getBookDetails(googleBookId);

        return bookOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("No book details found for Google Book ID: {}", googleBookId);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(
            summary = "Get book recommendations",
            description = "Returns book recommendations based on a given title and/or subject"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recommendations found"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @GetMapping("/recommendations")
    public List<GoogleBookDTO> getRecommendations(
            @Parameter(description = "Book title", example = "The Power of Habit") @RequestParam(required = false) String title,
            @Parameter(description = "Book subject", example = "Drama") @RequestParam(required = false) String subject) {
        logger.info("Getting recommendations for title: '{}' or subject: '{}'", title, subject);
        return googleBooksRecommendationService.findRecommendations(title, subject);
    }
}
