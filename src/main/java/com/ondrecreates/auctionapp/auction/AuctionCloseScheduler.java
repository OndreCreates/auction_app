package com.ondrecreates.auctionapp.auction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
class AuctionCloseScheduler {

    private final AuctionRepository auctionRepository;
    private final AuctionCloseService auctionCloseService;
    private final Clock clock;

    @Scheduled(fixedRateString = "${auction.close-scheduler.fixed-rate-ms:30000}")
    public void closeExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Auction> expired = auctionRepository.findByStatusAndEndTimeBefore(AuctionStatus.ACTIVE, now);

        for (Auction auction : expired) {
            try {
                auctionCloseService.close(auction.getId());
            } catch (Exception e) {
                log.error("Failed to close expired auction {}", auction.getId(), e);
            }
        }
    }
}
