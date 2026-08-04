package com.ondrecreates.auctionapp.profile;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionRepository;
import com.ondrecreates.auctionapp.auction.AuctionResponseAssembler;
import com.ondrecreates.auctionapp.auction.dto.AuctionResponse;
import com.ondrecreates.auctionapp.bid.BidRepository;
import com.ondrecreates.auctionapp.bid.dto.BidResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionResponseAssembler auctionResponseAssembler;

    @GetMapping("/bids")
    public Page<BidResponse> getBidHistory(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return bidRepository.findByBidderId(jwt.getSubject(), pageable).map(BidResponse::from);
    }

    @GetMapping("/won")
    public Page<AuctionResponse> getWonAuctions(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "endTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Auction> won = auctionRepository.findWonAuctions(jwt.getSubject(), pageable);
        return new PageImpl<>(auctionResponseAssembler.toResponseList(won.getContent()), pageable, won.getTotalElements());
    }
}
