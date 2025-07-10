package com.zurich.demo.controller;

import com.zurich.demo.dto.SaveBookRequest;
import com.zurich.demo.model.BookEntry;
import com.zurich.demo.model.BookStatus;
import com.zurich.demo.model.User;
import com.zurich.demo.service.BookEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Book Entries", description = "Endpoints for managing a user's personal book entries.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/book-entries")
public class BookEntryController {

    private static final Logger logger = LoggerFactory.getLogger(BookEntryController.class);
    private final BookEntryService bookEntryService;

    public BookEntryController(BookEntryService bookEntryService) {
        this.bookEntryService = bookEntryService;
    }

    @Operation(summary = "Get all book entries for the authenticated user",
            description = "Retrieves a list of all book entries (books owned, wishlisted, etc.) associated with the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of book entries",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookEntry.class)))
    @GetMapping
    public List<BookEntry> getAllBookEntriesForUser(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        logger.info("Fetching all book entries for user: {}", user.getUsername());
        return bookEntryService.getBookEntriesForUser(user.getId());
    }

    @Operation(summary = "Save a new book entry for the authenticated user",
            description = "Adds a new book entry to the authenticated user's collection, typically from a search result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book entry created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookEntry.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data provided", content = @Content)
    })
    @PostMapping
    public ResponseEntity<BookEntry> saveBookEntryFromSearch(@Parameter(description = "Details of the book to save") @Valid @RequestBody SaveBookRequest request,
                                                             @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        logger.info("User '{}' is saving a book entry with Google Book ID: {}", user.getUsername(), request.getGoogleBookId());
        BookEntry savedBookEntry = bookEntryService.saveBookEntryFromSearch(request, user.getId());
        logger.info("Book entry '{}' saved with ID {} for user '{}'", savedBookEntry.getTitle(), savedBookEntry.getId(), user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBookEntry);
    }

    @Operation(summary = "Update the status of an existing book entry",
            description = "Modifies only the status (e.g., WISHLIST, READING, READ) of a specific book entry by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book entry status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookEntry.class))),
            @ApiResponse(responseCode = "404", description = "Book entry not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid status provided", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public BookEntry updateBookEntryStatus(@Parameter(description = "ID of the book entry to update") @PathVariable Long id,
                                           @Parameter(description = "The new status for the book entry", required = true, schema = @Schema(type = "string", allowableValues = {"WISHLIST", "PLAN_TO_READ", "READING", "PAUSED", "DROPPED", "READ", "RECOMMENDED"})) @RequestParam BookStatus status) {
        logger.info("Patching status to '{}' for book entry with ID: {}", status, id);
        return bookEntryService.updateOnlyStatus(id, status);
    }

    @Operation(summary = "Delete a book entry",
            description = "Removes a specific book entry from the authenticated user's collection by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book entry deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book entry not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookEntry(@Parameter(description = "ID of the book entry to delete") @PathVariable Long id) {
        logger.info("Attempting to delete book entry with ID: {}", id);
        bookEntryService.deleteBookEntry(id);
        logger.info("Book entry with ID {} deleted successfully.", id);
        return ResponseEntity.noContent().build();
    }
}