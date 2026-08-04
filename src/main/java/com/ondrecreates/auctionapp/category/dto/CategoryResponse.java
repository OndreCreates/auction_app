package com.ondrecreates.auctionapp.category.dto;

import com.ondrecreates.auctionapp.category.Category;

public record CategoryResponse(
        Long id,
        String name,
        String slug
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getSlug());
    }
}
