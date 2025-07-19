package com.zurich.demo.books.bookentry.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the status of a book in a user's collection.")
public enum BookStatus {
    WISHLIST,
    PLAN_TO_READ,
    READING,
    PAUSED,
    DROPPED,
    READ,
    RECOMMENDED;

    @JsonCreator
    public static BookStatus fromValue(String value) {
        return BookStatus.valueOf(value.toUpperCase());
    }
}