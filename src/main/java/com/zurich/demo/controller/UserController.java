package com.zurich.demo.controller;

import com.zurich.demo.dto.UserResponseDTO;
import com.zurich.demo.model.User;
import com.zurich.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    /**
     * Cria um novo usuário (endpoint de registro).
     * O corpo da requisição ainda recebe a entidade User com a senha,
     * mas a resposta retorna o DTO seguro, sem a senha.
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody User user) {
        User createdUser = service.createUser(user);

        // ✅ CORREÇÃO: Mapeia o usuário criado para o DTO de resposta segura.
        UserResponseDTO responseDto = new UserResponseDTO(
                createdUser.getId(),
                createdUser.getUsername(),
                createdUser.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * Retorna uma lista de todos os usuários.
     */
    @GetMapping
    public List<UserResponseDTO> findAll() {
        // ✅ CORREÇÃO: Converte a lista de entidades User para uma lista de DTOs seguros.
        return service.getAllUsers().stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
    }

    /**
     * Encontra um usuário pelo seu ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        // ✅ CORREÇÃO: Mapeia o Optional<User> para um Optional<UserResponseDTO> antes de criar a resposta.
        return service.getUserById(id)
                .map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deleta um usuário pelo seu ID.
     * Este método não precisou de alterações, pois não retorna dados do usuário.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}