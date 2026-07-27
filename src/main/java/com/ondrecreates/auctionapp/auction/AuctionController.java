package com.ondrecreates.auctionapp.auction;

import com.ondrecreates.auctionapp.auction.dto.AuctionResponse;
import com.ondrecreates.auctionapp.auction.dto.CreateAuctionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuctionResponse create(@Valid @RequestBody CreateAuctionRequest request) {
        Auction auction = Auction.builder()
                .title(request.title())
                .description(request.description())
                .sellerId(request.sellerId())
                .startingPrice(request.startingPrice())
                .minIncrement(request.minIncrement())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();
        return AuctionResponse.from(auctionService.create(auction));
    }

    @GetMapping("/{id}")
    public AuctionResponse getById(@PathVariable Long id) {
        return AuctionResponse.from(auctionService.getById(id));
    }
}
