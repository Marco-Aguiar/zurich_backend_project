package com.zurich.demo.dto;

import com.zurich.demo.model.BookStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveBookRequest {

    @NotBlank(message = "O ID do livro (googleBookId) é obrigatório.")
    private String googleBookId;

    @NotBlank(message = "O título é obrigatório.")
    private String title;

    @NotBlank(message = "O(s) autor(es) é(são) obrigatório(s).")
    private String authors;

    @NotBlank(message = "O assunto (subject) é obrigatório.")
    private String subject;

    private String thumbnailUrl;

    @NotNull(message = "O status do livro é obrigatório.")
    @Schema(description = "Book status", example = "PLAN_TO_READ")
    private BookStatus status;
}
