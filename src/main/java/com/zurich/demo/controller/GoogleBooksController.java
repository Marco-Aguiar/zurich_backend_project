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

@Tag(name = "External Books API", description = "Endpoints for searching books using the Google Books API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/external/books")
public class GoogleBooksController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleBooksController.class);
    private final GoogleBooksService googleBooksService;

    public GoogleBooksController(GoogleBooksService googleBooksService) {
        this.googleBooksService = googleBooksService;
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
        return googleBooksService.searchBooks(title, author);
    }

    @Operation(
            summary = "Get book price by ISBN",
            description = "Returns sale information for a book using its ISBN-13"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale info found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleInfo.class))),
            @ApiResponse(responseCode = "404", description = "Sale info not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @GetMapping("/price")
    public ResponseEntity<SaleInfo> getBookPriceByIsbn(
            @Parameter(description = "13-digit ISBN", example = "9788532530837") @RequestParam String isbn,
            @Parameter(description = "Country code for price lookup", example = "US") @RequestParam(defaultValue = "US") String country) {

        logger.info("Searching price for ISBN: {} in country: {}", isbn, country);
        Optional<SaleInfo> saleInfoOpt = googleBooksService.findBookPriceByIsbn(isbn, country);

        return saleInfoOpt
                .map(info -> {
                    logger.info("Price found for ISBN {}: {}", isbn, info.retailPrice());
                    return ResponseEntity.ok(info);
                })
                .orElseGet(() -> {
                    logger.warn("No price information found for ISBN: {}", isbn);
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
        return googleBooksService.findRecommendations(title, subject);
    }
}
