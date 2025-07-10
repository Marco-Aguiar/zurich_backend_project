package com.zurich.demo.config;

import com.zurich.demo.repository.UserRepository;
import com.zurich.demo.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info(">>>> SecurityFilter: Iniciando filtro para a requisição: {}", request.getRequestURI());

        var token = this.recoverToken(request);
        if (token != null) {
            logger.info(">>>> SecurityFilter: Token JWT encontrado no cabeçalho.");
            var username = tokenService.validateToken(token);
            logger.info(">>>> SecurityFilter: Token validado. Subject (username): {}", username);

            if (username != null && !username.isEmpty()) {
                userRepository.findByUsername(username).ifPresent(user -> {
                    logger.info(">>>> SecurityFilter: Usuário '{}' encontrado no banco.", user.getUsername());
                    logger.info(">>>> SecurityFilter: Autoridades do usuário: {}", user.getAuthorities());

                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.info(">>>> SecurityFilter: Usuário autenticado e CONTEXTO DE SEGURANÇA DEFINIDO.");
                });
            } else {
                logger.warn(">>>> SecurityFilter: Token JWT é inválido ou expirado.");
            }
        } else {
            logger.info(">>>> SecurityFilter: Nenhum token JWT encontrado no cabeçalho Authorization.");
        }

        filterChain.doFilter(request, response);
        logger.info(">>>> SecurityFilter: Finalizando filtro para a requisição: {}", request.getRequestURI());
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}