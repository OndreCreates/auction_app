package com.ondrecreates.auctionapp.auction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuctionCloseSchedulerTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionCloseService auctionCloseService;

    @Test
    void closesOnlyAuctionsPastEndTime() {
        LocalDateTime fixedNow = LocalDateTime.of(2026, 1, 1, 12, 0);
        Clock fixedClock = Clock.fixed(fixedNow.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        Auction expiredWithBids = auctionRepository.save(
                activeAuction(fixedNow.minusDays(2), fixedNow.minusHours(1), new BigDecimal("150.00")));
        Auction expiredWithoutBids = auctionRepository.save(
                activeAuction(fixedNow.minusDays(2), fixedNow.minusHours(1), new BigDecimal("100.00")));
        Auction stillRunning = auctionRepository.save(
                activeAuction(fixedNow.minusDays(1), fixedNow.plusDays(1), new BigDecimal("100.00")));

        new AuctionCloseScheduler(auctionRepository, auctionCloseService, fixedClock).closeExpiredAuctions();

        assertThat(auctionRepository.findById(expiredWithBids.getId()).orElseThrow().getStatus())
                .isEqualTo(AuctionStatus.CLOSED);
        // Edge case: an auction with zero bids still closes cleanly, at its starting price.
        assertThat(auctionRepository.findById(expiredWithoutBids.getId()).orElseThrow().getStatus())
                .isEqualTo(AuctionStatus.CLOSED);
        assertThat(auctionRepository.findById(expiredWithoutBids.getId()).orElseThrow().getCurrentPrice())
                .isEqualByComparingTo("100.00");
        assertThat(auctionRepository.findById(stillRunning.getId()).orElseThrow().getStatus())
                .isEqualTo(AuctionStatus.ACTIVE);
    }

    private Auction activeAuction(LocalDateTime startTime, LocalDateTime endTime, BigDecimal currentPrice) {
        return Auction.builder()
                .title("Scheduler Test Auction")
                .sellerId(1L)
                .startingPrice(new BigDecimal("100.00"))
                .currentPrice(currentPrice)
                .minIncrement(new BigDecimal("5.00"))
                .status(AuctionStatus.ACTIVE)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
