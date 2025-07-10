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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "User Management", description = "Endpoints for managing user accounts.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user (registration endpoint)",
            description = "Registers a new user account. The request body contains user details including password, but the response returns a secure DTO without the password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user details supplied", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(@Parameter(description = "User details for registration") @Valid @RequestBody UserRegistrationRequest request) { // <-- CHANGE IS HERE
        logger.info("Attempting to register a new user with username: {}", request.getUsername());

        User userToCreate = new User();
        userToCreate.setUsername(request.getUsername());
        userToCreate.setEmail(request.getEmail());
        userToCreate.setPassword(request.getPassword());

        User createdUser = userService.createUser(userToCreate);

        UserResponseDTO responseDto = new UserResponseDTO(
                createdUser.getId(),
                createdUser.getUsername(),
                createdUser.getEmail()
        );

        logger.info("User registered successfully with ID: {}", createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "Get all users",
            description = "Retrieves a list of all registered users.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)))
    @GetMapping
    public List<UserResponseDTO> findAll() {
        logger.info("Fetching all users.");
        return userService.getAllUsers().stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get user by ID",
            description = "Retrieves a user's details by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@Parameter(description = "ID of the user to retrieve", example = "1") @PathVariable Long id) {
        logger.info("Searching for user with ID: {}", id);
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

    @Operation(summary = "Delete user by ID",
            description = "Deletes a user account by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID of the user to delete", example = "1") @PathVariable Long id) {
        logger.info("Attempting to delete user with ID: {}", id);
        userService.deleteUser(id);
        logger.info("User with ID: {} deleted successfully.", id);
        return ResponseEntity.noContent().build();
    }
}