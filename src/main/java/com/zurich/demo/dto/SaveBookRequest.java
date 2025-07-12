package com.zurich.demo.dto;

import com.zurich.demo.model.BookStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SaveBookRequest {

    @NotBlank(message = "The book ID (googleBookId) is required.")
    private String googleBookId;

    @NotBlank(message = "The title is required.")
    private String title;

    @NotEmpty(message = "Authors list must not be empty")
    @Size(min = 1, message = "There must be at least one author")
    private List<@NotBlank(message = "Author name must not be blank") String> authors;


    @NotBlank(message = "The subject is required.")
    private String subject;

    private String thumbnailUrl;
    private Double averageRating;


    @NotNull(message = "The book status is required.")
    @Schema(description = "Book status", example = "PLAN_TO_READ")
    private BookStatus status;
}