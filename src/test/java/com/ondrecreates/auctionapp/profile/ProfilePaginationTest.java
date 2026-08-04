package com.ondrecreates.auctionapp.profile;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionRepository;
import com.ondrecreates.auctionapp.auction.AuctionStatus;
import com.ondrecreates.auctionapp.bid.Bid;
import com.ondrecreates.auctionapp.bid.BidRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

// Proves pagination actually paginates (findByBidderId/findWonAuctions use Pageable, not
// findAll()) - the acceptance criterion is data spanning more than one page.
@SpringBootTest
class ProfilePaginationTest {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Test
    void bidHistory_paginatesAcrossMultiplePages() {
        String bidderId = "paginationtest+" + System.nanoTime() + "@example.com";
        Auction auction = auctionRepository.save(Auction.builder()
                .title("Pagination Test Auction")
                .sellerId("seller@example.com")
                .startingPrice(new BigDecimal("100.00"))
                .currentPrice(new BigDecimal("100.00"))
                .minIncrement(new BigDecimal("1.00"))
                .status(AuctionStatus.ACTIVE)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(1))
                .build());

        for (int i = 0; i < 25; i++) {
            bidRepository.save(Bid.builder()
                    .auctionId(auction.getId())
                    .bidderId(bidderId)
                    .amount(new BigDecimal("100.00").add(BigDecimal.valueOf(i)))
                    .build());
        }

        Page<Bid> firstPage = bidRepository.findByBidderId(bidderId, PageRequest.of(0, 10));
        Page<Bid> lastPage = bidRepository.findByBidderId(bidderId, PageRequest.of(2, 10));

        assertThat(firstPage.getTotalElements()).isEqualTo(25);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
        assertThat(firstPage.getContent()).hasSize(10);
        assertThat(lastPage.getContent()).hasSize(5);
    }

    @Test
    void wonAuctions_onlyIncludesClosedAuctionsWithMatchingWinningBid() {
        String userId = "winner+" + System.nanoTime() + "@example.com";

        Auction won = auctionRepository.save(Auction.builder()
                .title("Won Auction")
                .sellerId("seller@example.com")
                .startingPrice(new BigDecimal("100.00"))
                .currentPrice(new BigDecimal("150.00"))
                .minIncrement(new BigDecimal("5.00"))
                .status(AuctionStatus.CLOSED)
                .startTime(LocalDateTime.now().minusDays(2))
                .endTime(LocalDateTime.now().minusDays(1))
                .build());
        bidRepository.save(Bid.builder().auctionId(won.getId()).bidderId(userId).amount(new BigDecimal("150.00")).build());

        Auction outbid = auctionRepository.save(Auction.builder()
                .title("Outbid Auction")
                .sellerId("seller@example.com")
                .startingPrice(new BigDecimal("100.00"))
                .currentPrice(new BigDecimal("200.00"))
                .minIncrement(new BigDecimal("5.00"))
                .status(AuctionStatus.CLOSED)
                .startTime(LocalDateTime.now().minusDays(2))
                .endTime(LocalDateTime.now().minusDays(1))
                .build());
        bidRepository.save(Bid.builder().auctionId(outbid.getId()).bidderId(userId).amount(new BigDecimal("150.00")).build());
        bidRepository.save(Bid.builder().auctionId(outbid.getId()).bidderId("someoneelse@example.com").amount(new BigDecimal("200.00")).build());

        Auction stillActive = auctionRepository.save(Auction.builder()
                .title("Still Active Auction")
                .sellerId("seller@example.com")
                .startingPrice(new BigDecimal("100.00"))
                .currentPrice(new BigDecimal("150.00"))
                .minIncrement(new BigDecimal("5.00"))
                .status(AuctionStatus.ACTIVE)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(1))
                .build());
        bidRepository.save(Bid.builder().auctionId(stillActive.getId()).bidderId(userId).amount(new BigDecimal("150.00")).build());

        Page<Auction> wonAuctions = auctionRepository.findWonAuctions(userId, PageRequest.of(0, 10));

        assertThat(wonAuctions.getContent()).extracting(Auction::getId).containsExactly(won.getId());
    }
}
