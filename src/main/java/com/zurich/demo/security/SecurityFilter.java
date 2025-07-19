package com.zurich.demo.security;

import com.zurich.demo.user.repository.UserRepository;
import com.zurich.demo.auth.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        logger.debug("Request to path: {}", path);

        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)
                || "/api/auth/login".equals(path)
                || "/api/users".equals(path)
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")) {
            logger.debug("Skipping filter for public or preflight request: {} {}", method, path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = this.recoverToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = tokenService.validateToken(token);

            if (username != null && !username.isEmpty()) {
                userRepository.findByUsername(username).ifPresent(user -> {
                    var authentication = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("User '{}' authenticated successfully.", user.getUsername());
                });
            } else {
                logger.warn("Invalid or expired token.");
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}
