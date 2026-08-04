package com.ondrecreates.auctionapp.watchlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {

    Optional<WatchlistItem> findByUserIdAndAuctionId(String userId, Long auctionId);

    boolean existsByUserIdAndAuctionId(String userId, Long auctionId);

    List<WatchlistItem> findByUserId(String userId);

    List<WatchlistItem> findByAuctionId(Long auctionId);
}
