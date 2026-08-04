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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;
    private final AuctionImageRepository auctionImageRepository;

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
        Auction auction = auctionService.getById(id);
        List<String> imageUrls = auctionImageRepository.findByAuctionIdOrderBySortOrderAsc(id).stream()
                .map(AuctionImage::getUrl)
                .toList();
        return AuctionResponse.from(auction, imageUrls);
    }

    @GetMapping
    public List<AuctionResponse> getAll(@RequestParam(required = false) String category) {
        List<Auction> auctions = auctionService.getAll(category);

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
