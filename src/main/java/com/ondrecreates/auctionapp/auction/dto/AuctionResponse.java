package com.ondrecreates.auctionapp.auction.dto;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionResponse(
        Long id,
        String title,
        String description,
        Long sellerId,
        BigDecimal startingPrice,
        BigDecimal minIncrement,
        BigDecimal currentPrice,
        AuctionStatus status,
        LocalDateTime startTime,
        LocalDateTime endTime
) {

    public static AuctionResponse from(Auction auction) {
        return new AuctionResponse(
                auction.getId(),
                auction.getTitle(),
                auction.getDescription(),
                auction.getSellerId(),
                auction.getStartingPrice(),
                auction.getMinIncrement(),
                auction.getCurrentPrice(),
                auction.getStatus(),
                auction.getStartTime(),
                auction.getEndTime()
        );
    }
}
