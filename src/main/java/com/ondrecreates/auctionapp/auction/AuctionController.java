package com.ondrecreates.auctionapp.auction;

import com.ondrecreates.auctionapp.auction.dto.AuctionResponse;
import com.ondrecreates.auctionapp.auction.dto.CreateAuctionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;
    private final AuctionResponseAssembler auctionResponseAssembler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuctionResponse create(@Valid @RequestBody CreateAuctionRequest request, @AuthenticationPrincipal Jwt jwt) {
        Auction auction = Auction.builder()
                .title(request.title())
                .description(request.description())
                .sellerId(jwt.getSubject())
                .categoryId(request.categoryId())
                .provenance(request.provenance())
                .startingPrice(request.startingPrice())
                .minIncrement(request.minIncrement())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();
        List<String> imageUrls = request.imageUrls() == null ? List.of() : request.imageUrls();
        Auction created = auctionService.create(auction, imageUrls);
        return AuctionResponse.from(created, imageUrls);
    }

    @GetMapping("/{id}")
    public AuctionResponse getById(@PathVariable Long id) {
        return auctionResponseAssembler.toResponse(auctionService.getById(id));
    }

    @GetMapping
    public List<AuctionResponse> getAll(@RequestParam(required = false) String category) {
        return auctionResponseAssembler.toResponseList(auctionService.getAll(category));
    }
}
