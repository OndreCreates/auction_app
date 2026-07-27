package com.ondrecreates.auctionapp.bid;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionService;
import com.ondrecreates.auctionapp.auction.AuctionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionService auctionService;

    @Transactional
    public Bid placeBid(Long auctionId, Long bidderId, BigDecimal amount) {
        Auction auction = auctionService.getById(auctionId);

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new AuctionNotActiveException(auctionId);
        }

        BigDecimal minAcceptedAmount = auction.getCurrentPrice().add(auction.getMinIncrement());
        if (amount.compareTo(minAcceptedAmount) < 0) {
            throw new BidTooLowException(
                    "Bid must be at least " + minAcceptedAmount + " for auction " + auctionId);
        }

        Bid bid = Bid.builder()
                .auctionId(auctionId)
                .bidderId(bidderId)
                .amount(amount)
                .build();
        Bid savedBid = bidRepository.save(bid);

        auction.setCurrentPrice(amount);

        return savedBid;
    }
}
