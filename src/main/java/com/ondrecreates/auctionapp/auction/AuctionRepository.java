package com.ondrecreates.auctionapp.auction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatusAndEndTimeBefore(AuctionStatus status, LocalDateTime endTime);

    List<Auction> findByCategoryId(Long categoryId, Sort sort);

    // "Won" = closed, and this user's bid matches the final price - bids only ever
    // increase, so the bid equal to currentPrice is definitionally the winning one.
    @Query("""
            SELECT a FROM Auction a WHERE a.status = 'CLOSED' AND EXISTS (
                SELECT 1 FROM Bid b
                WHERE b.auctionId = a.id AND b.bidderId = :userId AND b.amount = a.currentPrice
            )""")
    Page<Auction> findWonAuctions(@Param("userId") String userId, Pageable pageable);
}
