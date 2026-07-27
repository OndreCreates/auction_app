package com.ondrecreates.auctionapp.auction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatusAndEndTimeBefore(AuctionStatus status, LocalDateTime endTime);
}
