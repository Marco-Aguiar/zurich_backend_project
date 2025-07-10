package com.zurich.demo.exception; // Coloque no mesmo pacote do seu GlobalExceptionHandler

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Este método é chamado quando um usuário não autenticado tenta acessar um recurso protegido.
     * Nós retornamos um erro 401 Unauthorized.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, String> body = new HashMap<>();
        body.put("error", "Acesso não autorizado.");
        body.put("message", "Token JWT ausente, inválido ou expirado. Por favor, faça o login novamente.");

        objectMapper.writeValue(response.getWriter(), body);
    }

    /**
     * Este método é chamado quando um usuário autenticado tenta acessar um recurso
     * para o qual ele não tem permissão. Nós retornamos um erro 403 Forbidden.
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, String> body = new HashMap<>();
        body.put("error", "Acesso Negado.");
        body.put("message", "Você não tem permissão para acessar este recurso.");

        objectMapper.writeValue(response.getWriter(), body);
    }
}