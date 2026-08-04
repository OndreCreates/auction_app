package com.ondrecreates.auctionapp.watchlist;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionNotFoundException;
import com.ondrecreates.auctionapp.auction.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchlistService {

    private final WatchlistItemRepository watchlistItemRepository;
    private final AuctionRepository auctionRepository;

    @Transactional
    public void addToWatchlist(String userId, Long auctionId) {
        if (!auctionRepository.existsById(auctionId)) {
            throw new AuctionNotFoundException(auctionId);
        }
        if (watchlistItemRepository.existsByUserIdAndAuctionId(userId, auctionId)) {
            return;
        }
        watchlistItemRepository.save(WatchlistItem.builder()
                .userId(userId)
                .auctionId(auctionId)
                .build());
    }

    @Transactional
    public void removeFromWatchlist(String userId, Long auctionId) {
        watchlistItemRepository.findByUserIdAndAuctionId(userId, auctionId)
                .ifPresent(watchlistItemRepository::delete);
    }

    public List<Auction> getWatchedAuctions(String userId) {
        List<Long> auctionIds = watchlistItemRepository.findByUserId(userId).stream()
                .map(WatchlistItem::getAuctionId)
                .toList();
        return auctionRepository.findAllById(auctionIds);
    }

    public List<String> getWatcherEmails(Long auctionId) {
        return watchlistItemRepository.findByAuctionId(auctionId).stream()
                .map(WatchlistItem::getUserId)
                .toList();
    }
}
