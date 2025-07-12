package com.zurich.demo.controller;

import com.zurich.demo.dto.UserRegistrationRequest;
import com.zurich.demo.dto.UserResponseDTO;
import com.zurich.demo.model.User;
import com.zurich.demo.service.UserService;
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
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "User Management", description = "Endpoints for managing user accounts")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with username, email and password"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user details", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(
            @Parameter(description = "User registration data")
            @Valid @RequestBody UserRegistrationRequest request) {

        logger.info("Registering user: {}", request.getUsername());

        User userToCreate = new User();
        userToCreate.setUsername(request.getUsername());
        userToCreate.setEmail(request.getEmail());
        userToCreate.setPassword(request.getPassword());

        User createdUser = userService.createUser(userToCreate);

        UserResponseDTO response = new UserResponseDTO(
                createdUser.getId(),
                createdUser.getUsername(),
                createdUser.getEmail()
        );

        logger.info("User registered with ID: {}", createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get all users",
            description = "Returns a list of all registered users"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User list retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @GetMapping
    public List<UserResponseDTO> findAll() {
        logger.info("Fetching all users");
        return userService.getAllUsers().stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
    }

    @Operation(
            summary = "Get user by ID",
            description = "Returns user details for a given user ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(
            @Parameter(description = "User ID", example = "1") @PathVariable Long id) {

        logger.info("Looking up user with ID: {}", id);

        return userService.getUserById(id)
                .map(user -> {
                    logger.info("User found with ID: {}", id);
                    return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail());
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(
            summary = "Delete user by ID",
            description = "Deletes a user account for a given ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "User ID", example = "1") @PathVariable Long id) {

        logger.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        logger.info("User with ID: {} deleted", id);

        return ResponseEntity.noContent().build();
    }
}
