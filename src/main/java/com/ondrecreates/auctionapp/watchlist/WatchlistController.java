package com.ondrecreates.auctionapp.watchlist;

import com.ondrecreates.auctionapp.auction.AuctionResponseAssembler;
import com.ondrecreates.auctionapp.auction.dto.AuctionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final AuctionResponseAssembler auctionResponseAssembler;

    @PostMapping("/{auctionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(@PathVariable Long auctionId, @AuthenticationPrincipal Jwt jwt) {
        watchlistService.addToWatchlist(jwt.getSubject(), auctionId);
    }

    @DeleteMapping("/{auctionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long auctionId, @AuthenticationPrincipal Jwt jwt) {
        watchlistService.removeFromWatchlist(jwt.getSubject(), auctionId);
    }

    @GetMapping
    public List<AuctionResponse> getAll(@AuthenticationPrincipal Jwt jwt) {
        return auctionResponseAssembler.toResponseList(watchlistService.getWatchedAuctions(jwt.getSubject()));
    }
}
