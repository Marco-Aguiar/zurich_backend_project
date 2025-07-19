package com.zurich.demo.books.bookentry.model;

import com.zurich.demo.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "book_entries")
@Data
@Schema(description = "Represents a user's book entry, tracking books they own or wish to acquire.")
public class BookEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the book entry", example = "1")
    private Long id;

    @Column(name = "google_book_id")
    @Schema(description = "The ID of the book from the Google Books API", example = "zyTCAlADqUcC")
    private String googleBookId;

    @Schema(description = "The title of the book", example = "The Hitchhiker's Guide to the Galaxy")
    private String title;

    @Schema(description = "Authors of the book, comma-separated", example = "Douglas Adams")
    private String authors;

    @Schema(description = "Subject or genre of the book", example = "Science Fiction")
    private String subject;

    @Column(name = "thumbnail_url")
    @Schema(description = "URL to the book's thumbnail image", example = "http://books.google.com/books/content?id=zyTCAlADqUcC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api")
    private String thumbnailUrl;

    @Schema(description = "Average user rating from Google Books API", example = "4.3")
    @Column(name = "average_rating")
    private Double averageRating;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Current status of the book (e.g., OWNED, WISHLIST, READING)", example = "OWNED")
    private BookStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema(description = "The user associated with this book entry")
    private User user;
}
