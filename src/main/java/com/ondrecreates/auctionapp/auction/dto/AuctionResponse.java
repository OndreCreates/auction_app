package com.ondrecreates.auctionapp.auction.dto;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AuctionResponse(
        Long id,
        String title,
        String description,
        String sellerId,
        Long categoryId,
        String provenance,
        boolean verified,
        List<String> imageUrls,
        BigDecimal startingPrice,
        BigDecimal minIncrement,
        BigDecimal currentPrice,
        AuctionStatus status,
        LocalDateTime startTime,
        LocalDateTime endTime
) {

    public static AuctionResponse from(Auction auction, List<String> imageUrls) {
        return new AuctionResponse(
                auction.getId(),
                auction.getTitle(),
                auction.getDescription(),
                auction.getSellerId(),
                auction.getCategoryId(),
                auction.getProvenance(),
                auction.isVerified(),
                imageUrls,
                auction.getStartingPrice(),
                auction.getMinIncrement(),
                auction.getCurrentPrice(),
                auction.getStatus(),
                auction.getStartTime(),
                auction.getEndTime()
        );
    }
}
