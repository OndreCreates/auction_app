package com.ondrecreates.auctionapp.bid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {

    long countByAuctionId(Long auctionId);

    Page<Bid> findByBidderId(String bidderId, Pageable pageable);
}
