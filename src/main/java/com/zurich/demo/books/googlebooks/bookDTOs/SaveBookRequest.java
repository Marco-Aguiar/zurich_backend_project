package com.zurich.demo.books.googlebooks.bookDTOs;

import com.zurich.demo.books.bookentry.model.BookStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SaveBookRequest {

    @NotBlank(message = "The book ID (googleBookId) is required.")
    private String googleBookId;

    @NotBlank(message = "The title is required.")
    private String title;

    private List<String> authors;
    private String subject;
    private String thumbnailUrl;
    private Double averageRating;

    @NotNull(message = "The book status is required.")
    @Schema(description = "Book status", example = "PLAN_TO_READ")
    private BookStatus status;
}