package com.zurich.demo.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Schema(description = "Represents a user in the system, including authentication details.")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Username cannot be empty")
    @Schema(description = "User's unique username", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Column(unique = true)
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Schema(description = "User's unique email address", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "User's password (min 8 characters)", example = "secureP@ss123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

}