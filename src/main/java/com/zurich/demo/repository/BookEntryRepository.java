package com.zurich.demo.repository;

import com.zurich.demo.model.BookEntry;
import com.zurich.demo.model.BookStatus;
import com.zurich.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookEntryRepository extends JpaRepository<BookEntry, Long> {
    List<BookEntry> findByUser(User user);
    List<BookEntry> findByUserAndStatus(User user, BookStatus status);
}
