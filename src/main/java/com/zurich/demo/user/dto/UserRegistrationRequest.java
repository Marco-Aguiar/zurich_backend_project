package com.zurich.demo.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for user registration.")
public class UserRegistrationRequest {

    @NotBlank(message = "Username cannot be empty")
    @Schema(description = "User's desired unique username", example = "new_user_1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Schema(description = "User's email address", example = "new.user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "User's password (min 8 characters)", example = "MyStrongP@ssw0rd!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}