package com.zurich.demo.controller;

import com.zurich.demo.dto.SaveBookRequest;
import com.zurich.demo.model.BookEntry;
import com.zurich.demo.model.BookStatus;
import com.zurich.demo.service.BookEntryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookEntryService bookEntryService;

    public BookController(BookEntryService bookEntryService) {
        this.bookEntryService = bookEntryService;
    }

    @GetMapping
    public List<BookEntry> getAllBooksForUser() {
        // Em breve: substituir por ID do usuário autenticado
        Long userId = 1L;
        return bookEntryService.getBooksForUser(userId);
    }

    @PostMapping
    public ResponseEntity<BookEntry> saveBookFromSearch(@Valid @RequestBody SaveBookRequest request) {
        Long userId = 1L;
        BookEntry saved = bookEntryService.saveBookFromSearch(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public BookEntry updateBookStatus(@PathVariable Long id, @RequestBody BookEntry updated) {
        return bookEntryService.updateStatus(id, updated);
    }

    @PatchMapping("/{id}/status")
    public BookEntry updateStatusOnly(@PathVariable Long id, @RequestParam BookStatus status) {
        return bookEntryService.updateOnlyStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookEntryService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
