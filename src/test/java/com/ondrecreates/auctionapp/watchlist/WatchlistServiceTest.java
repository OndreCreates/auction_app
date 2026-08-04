package com.ondrecreates.auctionapp.watchlist;

import com.ondrecreates.auctionapp.auction.AuctionNotFoundException;
import com.ondrecreates.auctionapp.auction.AuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {

    @Mock
    private WatchlistItemRepository watchlistItemRepository;

    @Mock
    private AuctionRepository auctionRepository;

    private WatchlistService watchlistService;

    @BeforeEach
    void setUp() {
        watchlistService = new WatchlistService(watchlistItemRepository, auctionRepository);
    }

    @Test
    void addToWatchlist_throwsWhenAuctionDoesNotExist() {
        when(auctionRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> watchlistService.addToWatchlist("buyer@example.com", 999L))
                .isInstanceOf(AuctionNotFoundException.class);
    }

    @Test
    void addToWatchlist_isIdempotent() {
        when(auctionRepository.existsById(1L)).thenReturn(true);
        when(watchlistItemRepository.existsByUserIdAndAuctionId("buyer@example.com", 1L)).thenReturn(true);

        watchlistService.addToWatchlist("buyer@example.com", 1L);

        verify(watchlistItemRepository, never()).save(any());
    }

    @Test
    void addToWatchlist_savesWhenNotAlreadyWatching() {
        when(auctionRepository.existsById(1L)).thenReturn(true);
        when(watchlistItemRepository.existsByUserIdAndAuctionId("buyer@example.com", 1L)).thenReturn(false);

        watchlistService.addToWatchlist("buyer@example.com", 1L);

        verify(watchlistItemRepository).save(any(WatchlistItem.class));
    }

    @Test
    void removeFromWatchlist_isSafeWhenNotWatching() {
        when(watchlistItemRepository.findByUserIdAndAuctionId("buyer@example.com", 1L)).thenReturn(Optional.empty());

        watchlistService.removeFromWatchlist("buyer@example.com", 1L);

        verify(watchlistItemRepository, never()).delete(any());
    }

    @Test
    void getWatcherEmails_returnsUserIdsWatchingAuction() {
        WatchlistItem item = WatchlistItem.builder().userId("buyer@example.com").auctionId(1L).build();
        when(watchlistItemRepository.findByAuctionId(1L)).thenReturn(List.of(item));

        List<String> watchers = watchlistService.getWatcherEmails(1L);

        assertThat(watchers).containsExactly("buyer@example.com");
    }
}
