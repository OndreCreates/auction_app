package com.ondrecreates.auctionapp.watchlist;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionRepository;
import com.ondrecreates.auctionapp.bid.Bid;
import com.ondrecreates.auctionapp.bid.BidPlacedEvent;
import com.ondrecreates.auctionapp.notification.NotificationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

// Runs after the bid transaction commits, same as BidBroadcastListener - a losing bid never
// reaches here, and a notification_center_app failure can never affect the bid itself (see
// RestNotificationClient, which swallows and logs its own failures per recipient).
@Component
@RequiredArgsConstructor
class WatchlistNotificationListener {

    private final WatchlistService watchlistService;
    private final AuctionRepository auctionRepository;
    private final NotificationClient notificationClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBidPlaced(BidPlacedEvent event) {
        Bid bid = event.bid();

        List<String> watchers = watchlistService.getWatcherEmails(bid.getAuctionId()).stream()
                .filter(email -> !email.equals(bid.getBidderId()))
                .toList();
        if (watchers.isEmpty()) {
            return;
        }

        Auction auction = auctionRepository.findById(bid.getAuctionId()).orElse(null);
        if (auction == null) {
            return;
        }

        for (String watcher : watchers) {
            notificationClient.sendBidUpdateNotification(watcher, auction.getTitle(), bid.getAmount());
        }
    }
}
