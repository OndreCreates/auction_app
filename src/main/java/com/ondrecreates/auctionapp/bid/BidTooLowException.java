package com.ondrecreates.auctionapp.bid;

public class BidTooLowException extends RuntimeException {

    public BidTooLowException(String message) {
        super(message);
    }
}
