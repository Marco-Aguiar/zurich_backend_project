package com.zurich.demo.controller;

import com.zurich.demo.dto.GoogleBookDTO;
import com.zurich.demo.dto.SaleInfo;
import com.zurich.demo.service.GoogleBooksService;
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

@Tag(name = "External Books API", description = "Endpoints for searching books using the Google Books API.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/external/books")
public class GoogleBooksController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleBooksController.class);
    private final GoogleBooksService googleBooksService;

    public GoogleBooksController(GoogleBooksService googleBooksService) {
        this.googleBooksService = googleBooksService;
    }

    @Operation(summary = "Search for books",
            description = "Flexible search for books by title, author, or subject. At least one parameter is required.")
    @ApiResponse(responseCode = "200", description = "A list of books matching the criteria")
    @GetMapping("/search")
    public List<GoogleBookDTO> searchBooks(
            @Parameter(description = "Book title") @RequestParam(required = false) String title,
            @Parameter(description = "Book author") @RequestParam(required = false) String author,
            @Parameter(description = "Book subject/genre") @RequestParam(required = false) String subject) {
        logger.info("External book search initiated with title: '{}', author: '{}', subject: '{}'", title, author, subject);
        return googleBooksService.searchBooks(title, author, subject);
    }

    @Operation(summary = "Get book price by ISBN",
            description = "Finds sale information, including price, for a book using its ISBN-13.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale information found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleInfo.class))),
            @ApiResponse(responseCode = "404", description = "No sale information found for the given ISBN", content = @Content)
    })
    @GetMapping("/price")
    public ResponseEntity<SaleInfo> getBookPriceByIsbn(
            @Parameter(description = "The 13-digit ISBN of the book", example = "9788532530837") @RequestParam String isbn,
            @RequestParam(defaultValue = "US") String country) {
        logger.info("Searching for price information for ISBN: {} in country: {}", isbn, country);
        Optional<SaleInfo> saleInfoOpt = googleBooksService.findBookPriceByIsbn(isbn, country);

        saleInfoOpt.ifPresent(saleInfo -> logger.info("Price found for ISBN {}: {}", isbn, saleInfo.retailPrice()));

        return saleInfoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("No price information found for ISBN: {}", isbn);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Get book recommendations",
            description = "Finds book recommendations based on a given book title.")
    @ApiResponse(responseCode = "200", description = "A list of recommended books")
    @GetMapping("/recommendations")
    public List<GoogleBookDTO> getRecommendations(@Parameter(description = "The title of the book to get recommendations for", example = "The Power of Habit") @RequestParam String title) {
        logger.info("Fetching recommendations for title: '{}'", title);
        return googleBooksService.findRecommendationsByTitle(title);
    }
}
