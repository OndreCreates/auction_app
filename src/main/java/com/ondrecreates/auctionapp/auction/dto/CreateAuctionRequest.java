package com.ondrecreates.auctionapp.auction.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateAuctionRequest(
        @NotBlank String title,
        String description,
        @NotNull Long sellerId,
        @NotNull @Positive BigDecimal startingPrice,
        @NotNull @Positive BigDecimal minIncrement,
        @NotNull LocalDateTime startTime,
        @NotNull @Future LocalDateTime endTime
) {
}
