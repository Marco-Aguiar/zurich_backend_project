package com.zurich.demo.service;

import com.zurich.demo.dto.SaveBookRequest;
import com.zurich.demo.model.BookEntry;
import com.zurich.demo.model.BookStatus;
import com.zurich.demo.model.User;
import com.zurich.demo.repository.BookEntryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookEntryService {

    private final BookEntryRepository repository;

    public BookEntryService(BookEntryRepository repository) {
        this.repository = repository;
    }

    public List<BookEntry> getBooksForUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        User user = new User();
        user.setId(userId);
        return repository.findByUser(user);
    }

    public BookEntry addBook(BookEntry entry) {
        if (entry == null || entry.getTitle() == null || entry.getUser() == null) {
            throw new IllegalArgumentException("Missing book details or user.");
        }
        return repository.save(entry);
    }


    public BookEntry updateOnlyStatus(Long bookId, BookStatus status) {
        BookEntry book = repository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        book.setStatus(status);
        return repository.save(book);
    }

    public BookEntry updateStatus(Long id, BookEntry newStatus) {
        BookEntry book = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        book.setStatus(newStatus.getStatus());
        return repository.save(book);
    }

    public void deleteBook(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Book not found with id: " + id);
        }
        repository.deleteById(id);
    }

    public BookEntry saveBookFromSearch(SaveBookRequest request, Long userId) {
        BookEntry entry = new BookEntry();
        entry.setGoogleBookId(request.getGoogleBookId());
        entry.setTitle(request.getTitle());
        entry.setAuthors(request.getAuthors());
        entry.setSubject(request.getSubject());
        entry.setThumbnailUrl(request.getThumbnailUrl());
        entry.setStatus(request.getStatus());

        User user = new User();
        user.setId(userId);
        entry.setUser(user);

        return repository.save(entry);
    }
}
