package com.zurich.demo.user.service;

import com.zurich.demo.books.bookentry.repository.BookEntryRepository;
import com.zurich.demo.user.model.User;
import com.zurich.demo.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookEntryRepository bookEntryRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       BookEntryRepository bookEntryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookEntryRepository = bookEntryRepository;
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

    @Transactional
    public void deleteUser(Long id) {
        logger.info("Attempting to delete user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found for deletion with ID: {}", id);
                    return new EntityNotFoundException("User not found with id: " + id);
                });

        logger.info("Deleting associated book entries for user ID: {}", id);
        bookEntryRepository.deleteByUserId(id);

        userRepository.delete(user);
        logger.info("User with ID: {} deleted successfully.", id);
    }
}
