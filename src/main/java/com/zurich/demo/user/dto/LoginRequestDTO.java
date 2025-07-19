package com.zurich.demo.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "Request body for user login.")
public record LoginRequestDTO(
        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        @Schema(description = "User's email address for login", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Schema(description = "User's password for login", example = "SecurePassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {}