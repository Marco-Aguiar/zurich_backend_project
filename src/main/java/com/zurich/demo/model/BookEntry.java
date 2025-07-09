package com.zurich.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "book_entries")
@Data
public class BookEntry {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String googleBookId;
    private String title;
    private String authors;
    private String subject;
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
