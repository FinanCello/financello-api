package com.example.financelloapi.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String category) {
        super("Could not find category " + category);
    }
}
