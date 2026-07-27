package com.ondrecreates.auctionapp.auction;

public class AuctionNotFoundException extends RuntimeException {

    public AuctionNotFoundException(Long id) {
        super("Auction not found: " + id);
    }
}
