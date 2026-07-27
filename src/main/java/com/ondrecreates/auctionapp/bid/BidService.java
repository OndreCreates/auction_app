package com.ondrecreates.auctionapp.bid;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionService auctionService;
    private final BidValidator bidValidator;
    private final ApplicationEventPublisher eventPublisher;

    // Concurrency safety relies on Auction.version (optimistic locking), not a try/catch here.
    // The version check runs as part of the UPDATE at commit time, after this method returns,
    // so a losing bid surfaces as ConcurrencyFailureException from the transaction proxy and is
    // translated to 409 in GlobalExceptionHandler. Because the bid insert and the auction price
    // update share this one @Transactional, a losing bid's row is rolled back too - no orphan bids.
    @Transactional
    public Bid placeBid(Long auctionId, Long bidderId, BigDecimal amount) {
        Auction auction = auctionService.getById(auctionId);

        bidValidator.validate(auction, amount);

        Bid bid = Bid.builder()
                .auctionId(auctionId)
                .bidderId(bidderId)
                .amount(amount)
                .build();
        Bid savedBid = bidRepository.save(bid);

        auction.setCurrentPrice(amount);

        eventPublisher.publishEvent(new BidPlacedEvent(savedBid));

        return savedBid;
    }
}
