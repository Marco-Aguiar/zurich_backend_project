package com.zurich.demo.exception;

public class DuplicateBookEntryException extends RuntimeException {
    public DuplicateBookEntryException(String message) {
        super(message);
    }
}
