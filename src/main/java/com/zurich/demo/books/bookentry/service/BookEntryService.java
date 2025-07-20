package com.zurich.demo.books.bookentry.service;

import com.zurich.demo.books.bookentry.model.BookStatus;
import com.zurich.demo.books.bookentry.model.BookEntry;
import com.zurich.demo.books.bookentry.repository.BookEntryRepository;
import com.zurich.demo.books.googlebooks.bookDTOs.SaveBookRequest;
import com.zurich.demo.exception.DuplicateBookEntryException;
import com.zurich.demo.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookEntryService {

    private static final Logger logger = LoggerFactory.getLogger(BookEntryService.class);
    private final BookEntryRepository bookEntryRepository;

    public BookEntryService(BookEntryRepository bookEntryRepository) {
        this.bookEntryRepository = bookEntryRepository;
    }

    public List<BookEntry> getBookEntriesForUser(Long userId) {
        if (userId == null || userId <= 0) {
            logger.warn("Attempted to get book entries with an invalid user ID: {}", userId);
            throw new IllegalArgumentException("Invalid user ID.");
        }
        User user = new User();
        user.setId(userId);
        logger.info("Fetching book entries for user ID: {}", userId);
        return bookEntryRepository.findByUser(user);
    }

    public BookEntry updateOnlyStatus(Long bookId, BookStatus status) {
        logger.info("Attempting to update status for book entry ID: {} to status: {}", bookId, status);
        BookEntry bookEntry = bookEntryRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.warn("Book entry not found with ID: {}", bookId);
                    return new EntityNotFoundException("Book entry not found with id: " + bookId);
                });
        bookEntry.setStatus(status);
        BookEntry updatedEntry = bookEntryRepository.save(bookEntry);
        logger.info("Successfully updated status for book entry ID: {} to status: {}", bookId, status);
        return updatedEntry;
    }

    public void deleteBookEntry(Long id) {
        logger.info("Attempting to delete book entry with ID: {}", id);
        if (!bookEntryRepository.existsById(id)) {
            logger.warn("Book entry not found for deletion with ID: {}", id);
            throw new EntityNotFoundException("Book entry not found with id: " + id);
        }
        bookEntryRepository.deleteById(id);
        logger.info("Book entry with ID: {} deleted successfully.", id);
    }

    public BookEntry saveBookEntryFromSearch(SaveBookRequest request, Long userId) {
        logger.info("Saving book entry from search for user ID: {} with Google Book ID: {}", userId, request.getGoogleBookId());

        Optional<BookEntry> existingEntry = bookEntryRepository.findByUserIdAndGoogleBookId(userId, request.getGoogleBookId());
        if (existingEntry.isPresent()) {
            throw new DuplicateBookEntryException("This book has already been saved by the user.");
        }

        BookEntry entry = new BookEntry();
        entry.setGoogleBookId(request.getGoogleBookId());
        entry.setTitle(request.getTitle());
        entry.setAuthors(String.join(", ", request.getAuthors()));
        entry.setSubject(request.getSubject() != null ? request.getSubject() : List.of()); // CORREÇÃO AQUI
        entry.setThumbnailUrl(request.getThumbnailUrl());
        entry.setStatus(request.getStatus());
        entry.setAverageRating(request.getAverageRating());

        User user = new User();
        user.setId(userId);
        entry.setUser(user);

        BookEntry savedEntry = bookEntryRepository.save(entry);
        logger.info("Book entry saved successfully with ID: {}", savedEntry.getId());
        return savedEntry;
    }
}
