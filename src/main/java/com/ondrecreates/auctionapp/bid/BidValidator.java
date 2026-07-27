package com.ondrecreates.auctionapp.bid;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
class BidValidator {

    void validate(Auction auction, BigDecimal amount) {
        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new AuctionNotActiveException(auction.getId());
        }

        BigDecimal minAcceptedAmount = auction.getCurrentPrice().add(auction.getMinIncrement());
        if (amount.compareTo(minAcceptedAmount) < 0) {
            throw new BidTooLowException(
                    "Bid must be at least " + minAcceptedAmount + " for auction " + auction.getId());
        }
    }
}
