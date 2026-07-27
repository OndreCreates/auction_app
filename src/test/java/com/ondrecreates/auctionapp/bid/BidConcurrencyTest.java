package com.ondrecreates.auctionapp.bid;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionRepository;
import com.ondrecreates.auctionapp.auction.AuctionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.ConcurrencyFailureException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BidConcurrencyTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private BidService bidService;

    @Test
    void onlyOneBidWinsWhenMultipleUsersBidTheSameAmountConcurrently() throws InterruptedException {
        Auction auction = auctionRepository.save(Auction.builder()
                .title("Concurrency Test Auction")
                .sellerId("seller@example.com")
                .startingPrice(new BigDecimal("100.00"))
                .currentPrice(new BigDecimal("100.00"))
                .minIncrement(new BigDecimal("5.00"))
                .status(AuctionStatus.ACTIVE)
                .startTime(LocalDateTime.now().minusMinutes(1))
                .endTime(LocalDateTime.now().plusDays(1))
                .build());
        Long auctionId = auction.getId();

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger rejectedCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            String bidderId = "bidder" + i + "@example.com";
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    bidService.placeBid(auctionId, bidderId, new BigDecimal("110.00"));
                    successCount.incrementAndGet();
                } catch (ConcurrencyFailureException | BidTooLowException e) {
                    // A thread that starts its read after the winner already committed sees the
                    // new currentPrice and correctly gets rejected by validation instead of a
                    // lock conflict - both mean "you didn't win the race" for this assertion.
                    rejectedCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        boolean finished = doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(finished).isTrue();
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(rejectedCount.get()).isEqualTo(threadCount - 1);

        Auction reloaded = auctionRepository.findById(auctionId).orElseThrow();
        assertThat(reloaded.getCurrentPrice()).isEqualByComparingTo("110.00");
        assertThat(bidRepository.countByAuctionId(auctionId)).isEqualTo(1);
    }
}
