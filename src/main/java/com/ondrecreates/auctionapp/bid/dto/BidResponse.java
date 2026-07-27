package com.ondrecreates.auctionapp.bid.dto;

import com.ondrecreates.auctionapp.bid.Bid;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BidResponse(
        Long id,
        Long auctionId,
        Long bidderId,
        BigDecimal amount,
        LocalDateTime createdAt
) {

    public static BidResponse from(Bid bid) {
        return new BidResponse(
                bid.getId(),
                bid.getAuctionId(),
                bid.getBidderId(),
                bid.getAmount(),
                bid.getCreatedAt()
        );
    }
}
