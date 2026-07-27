package com.ondrecreates.auctionapp.bid;

import com.ondrecreates.auctionapp.bid.dto.BidResponse;
import com.ondrecreates.auctionapp.bid.dto.PlaceBidRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auctions/{auctionId}/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BidResponse placeBid(@PathVariable Long auctionId, @Valid @RequestBody PlaceBidRequest request) {
        Bid bid = bidService.placeBid(auctionId, request.bidderId(), request.amount());
        return BidResponse.from(bid);
    }
}
