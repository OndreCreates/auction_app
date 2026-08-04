package com.ondrecreates.auctionapp.auction;

import com.ondrecreates.auctionapp.auction.dto.AuctionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Shared between AuctionController and WatchlistController - both need to attach each
// auction's images without an N+1 query per auction.
@Component
@RequiredArgsConstructor
public class AuctionResponseAssembler {

    private final AuctionImageRepository auctionImageRepository;

    public AuctionResponse toResponse(Auction auction) {
        List<String> imageUrls = auctionImageRepository.findByAuctionIdOrderBySortOrderAsc(auction.getId()).stream()
                .map(AuctionImage::getUrl)
                .toList();
        return AuctionResponse.from(auction, imageUrls);
    }

    public List<AuctionResponse> toResponseList(List<Auction> auctions) {
        Map<Long, List<String>> imageUrlsByAuctionId = auctionImageRepository
                .findByAuctionIdInOrderByAuctionIdAscSortOrderAsc(auctions.stream().map(Auction::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(
                        AuctionImage::getAuctionId,
                        LinkedHashMap::new,
                        Collectors.mapping(AuctionImage::getUrl, Collectors.toList())));

        return auctions.stream()
                .map(auction -> AuctionResponse.from(auction, imageUrlsByAuctionId.getOrDefault(auction.getId(), List.of())))
                .toList();
    }
}
