package com.zurich.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "📚 Book Reader API",
                version = "v1.0",
                description = """
            Backend API for the Book Reader system.

            Users can search books via Google Books, manage personal collections, \
            track reading status, and fetch pricing info via ISBN.

            All endpoints are secured via JWT bearer tokens.""",
                contact = @Contact(name = "Book Reader Support", email = "marcoaguiar14@gmail.com")
        ),
        tags = {
                @Tag(name = "Authentication", description = "Endpoints for user authentication"),
                @Tag(name = "User Management", description = "Endpoints for managing user accounts"),
                @Tag(name = "Book Entries", description = "Endpoints for managing a user's personal book entries"),
                @Tag(name = "External Books API", description = "Endpoints for searching books using the Google Books API")
        }
)
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste your JWT token here.")
                ));
    }
}
