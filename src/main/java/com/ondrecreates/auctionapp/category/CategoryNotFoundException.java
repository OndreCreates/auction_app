package com.ondrecreates.auctionapp.category;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("Category not found: " + id);
    }

    public CategoryNotFoundException(String slug) {
        super("Category not found: " + slug);
    }
}
