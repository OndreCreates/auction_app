package com.ondrecreates.auctionapp.watchlist;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionRepository;
import com.ondrecreates.auctionapp.bid.Bid;
import com.ondrecreates.auctionapp.bid.BidPlacedEvent;
import com.ondrecreates.auctionapp.notification.NotificationClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatchlistNotificationListenerTest {

    @Mock
    private WatchlistService watchlistService;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private NotificationClient notificationClient;

    private WatchlistNotificationListener listener;

    @BeforeEach
    void setUp() {
        listener = new WatchlistNotificationListener(watchlistService, auctionRepository, notificationClient);
    }

    @Test
    void onBidPlaced_notifiesWatchersExceptTheBidder() {
        Bid bid = Bid.builder().auctionId(1L).bidderId("bidder@example.com").amount(new BigDecimal("110.00")).build();
        Auction auction = Auction.builder().id(1L).title("Vintage Camera").build();
        when(watchlistService.getWatcherEmails(1L))
                .thenReturn(List.of("watcher@example.com", "bidder@example.com"));
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));

        listener.onBidPlaced(new BidPlacedEvent(bid));

        verify(notificationClient).sendBidUpdateNotification("watcher@example.com", "Vintage Camera", new BigDecimal("110.00"));
        verify(notificationClient, never()).sendBidUpdateNotification(eq("bidder@example.com"), eq("Vintage Camera"), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void onBidPlaced_skipsWhenNoWatchers() {
        Bid bid = Bid.builder().auctionId(1L).bidderId("bidder@example.com").amount(new BigDecimal("110.00")).build();
        when(watchlistService.getWatcherEmails(1L)).thenReturn(List.of());

        listener.onBidPlaced(new BidPlacedEvent(bid));

        verifyNoInteractions(notificationClient);
        verify(auctionRepository, never()).findById(1L);
    }
}
