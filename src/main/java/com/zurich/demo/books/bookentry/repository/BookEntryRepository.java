package com.zurich.demo.books.bookentry.repository;

import com.zurich.demo.books.bookentry.model.BookEntry;
import com.zurich.demo.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookEntryRepository extends JpaRepository<BookEntry, Long> {
    List<BookEntry> findByUser(User user);
    Optional<BookEntry> findByUserIdAndGoogleBookId(Long userId, String googleBookId);

}