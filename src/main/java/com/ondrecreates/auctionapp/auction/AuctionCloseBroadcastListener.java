package com.ondrecreates.auctionapp.auction;

import com.ondrecreates.auctionapp.auction.dto.AuctionClosedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
class AuctionCloseBroadcastListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAuctionClosed(AuctionClosedEvent event) {
        Auction auction = event.auction();
        try {
            messagingTemplate.convertAndSend(
                    "/topic/auctions/" + auction.getId(),
                    AuctionClosedMessage.from(auction));
        } catch (Exception e) {
            log.error("Failed to broadcast auction-closed for auction {}", auction.getId(), e);
        }
    }
}
