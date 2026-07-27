package com.ondrecreates.auctionapp.bid;

import com.ondrecreates.auctionapp.bid.dto.BidResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
class BidBroadcastListener {

    private final SimpMessagingTemplate messagingTemplate;

    // Runs after the bid transaction commits, so a broadcast never fires for a bid that
    // ultimately lost the optimistic-lock race, and a broker/WebSocket failure here can
    // never roll back or fail a request that already succeeded.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBidPlaced(BidPlacedEvent event) {
        Bid bid = event.bid();
        try {
            messagingTemplate.convertAndSend("/topic/auctions/" + bid.getAuctionId(), BidResponse.from(bid));
        } catch (Exception e) {
            log.error("Failed to broadcast bid {} for auction {}", bid.getId(), bid.getAuctionId(), e);
        }
    }
}
