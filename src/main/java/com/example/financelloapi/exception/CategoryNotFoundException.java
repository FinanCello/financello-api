package com.example.financelloapi.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Integer categoryId) {
        super("Could not find category " + categoryId);
    }
}
