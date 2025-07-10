package com.zurich.demo.dto;

import com.zurich.demo.model.BookStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveBookRequest {

    @NotBlank(message = "The book ID (googleBookId) is required.")
    private String googleBookId;

    @NotBlank(message = "The title is required.")
    private String title;

    @NotBlank(message = "The author(s) field is required.")
    private String authors;

    @NotBlank(message = "The subject is required.")
    private String subject;

    private String thumbnailUrl;

    @NotNull(message = "The book status is required.")
    @Schema(description = "Book status", example = "PLAN_TO_READ")
    private BookStatus status;
}