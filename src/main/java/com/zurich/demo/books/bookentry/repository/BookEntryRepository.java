package com.zurich.demo.books.bookentry.repository;

import com.zurich.demo.books.bookentry.model.BookEntry;
import com.zurich.demo.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookEntryRepository extends JpaRepository<BookEntry, Long> {

    List<BookEntry> findByUser(User user);

    Optional<BookEntry> findByUserIdAndGoogleBookId(Long userId, String googleBookId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BookEntry b WHERE b.user.id = :userId")
    void deleteByUserId(Long userId);
}
