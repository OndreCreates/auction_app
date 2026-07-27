package com.ondrecreates.auctionapp.bid;

public class AuctionNotActiveException extends RuntimeException {

    public AuctionNotActiveException(Long auctionId) {
        super("Auction is not active: " + auctionId);
    }
}
