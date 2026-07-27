package com.ondrecreates.auctionapp.auction;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AuctionCloseService {

    private final AuctionRepository auctionRepository;
    private final ApplicationEventPublisher eventPublisher;

    // Each auction is closed in its own transaction so one failure doesn't roll back
    // the others when the scheduler processes a batch of expired auctions.
    @Transactional
    public void close(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            return;
        }

        auction.setStatus(AuctionStatus.CLOSED);

        eventPublisher.publishEvent(new AuctionClosedEvent(auction));
    }
}
