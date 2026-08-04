package com.ondrecreates.auctionapp.auction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionImageRepository extends JpaRepository<AuctionImage, Long> {

    List<AuctionImage> findByAuctionIdOrderBySortOrderAsc(Long auctionId);

    List<AuctionImage> findByAuctionIdInOrderByAuctionIdAscSortOrderAsc(List<Long> auctionIds);
}
