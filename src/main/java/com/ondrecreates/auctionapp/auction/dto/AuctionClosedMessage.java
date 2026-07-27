package com.ondrecreates.auctionapp.auction.dto;

import com.ondrecreates.auctionapp.auction.Auction;

import java.math.BigDecimal;

public record AuctionClosedMessage(
        Long auctionId,
        String status,
        BigDecimal finalPrice
) {

    public static AuctionClosedMessage from(Auction auction) {
        return new AuctionClosedMessage(auction.getId(), auction.getStatus().name(), auction.getCurrentPrice());
    }
}
