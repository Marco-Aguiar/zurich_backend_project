package com.zurich.demo.auth.controller;

import com.zurich.demo.auth.service.TokenService;
import com.zurich.demo.user.dto.LoginRequestDTO;
import com.zurich.demo.user.dto.LoginResponseDTO;
import com.zurich.demo.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Endpoints for user authentication")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Operation(
            summary = "Authenticate user and return JWT",
            description = "Authenticates the user using email and password and returns a JWT on success"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO data) {
        logger.info("Authentication attempt for user: {}", data.email());

        try {
            var authToken = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            var auth = authenticationManager.authenticate(authToken);
            var user = (User) auth.getPrincipal();
            var token = tokenService.generateToken(user);

            logger.info("Authentication successful for: {}", user.getEmail());
            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for '{}': {}", data.email(), e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        } catch (Exception e) {
            logger.error("Unexpected error during login for '{}'", data.email(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
