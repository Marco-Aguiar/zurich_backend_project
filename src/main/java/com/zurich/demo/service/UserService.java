package com.zurich.demo.service;

import com.zurich.demo.model.User;
import com.zurich.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        logger.info("Attempting to create user with username: {} and email: {}", user.getUsername(), user.getEmail());

        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("User creation failed: Email '{}' is already in use.", user.getEmail());
            throw new IllegalArgumentException("Email is already in use.");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.warn("User creation failed: Username '{}' is already taken.", user.getUsername());
            throw new IllegalArgumentException("Username is already taken.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    public List<User> getAllUsers() {
        logger.info("Fetching all users.");
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        logger.info("Fetching user by ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        user.ifPresentOrElse(
                u -> logger.info("User found with ID: {}", id),
                () -> logger.warn("User not found with ID: {}", id)
        );
        return user;
    }

    public void deleteUser(Long id) {
        logger.info("Attempting to delete user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            logger.warn("User not found for deletion with ID: {}", id);
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        logger.info("User with ID: {} deleted successfully.", id);
    }
}