package com.ondrecreates.auctionapp.bid;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BidValidatorTest {

    private final BidValidator bidValidator = new BidValidator();

    @Test
    void allowsBidThatMeetsMinIncrement() {
        Auction auction = activeAuction();

        assertThatCode(() -> bidValidator.validate(auction, new BigDecimal("105.00")))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsBidBelowMinIncrement() {
        Auction auction = activeAuction();

        assertThatThrownBy(() -> bidValidator.validate(auction, new BigDecimal("102.00")))
                .isInstanceOf(BidTooLowException.class);
    }

    @Test
    void rejectsBidOnClosedAuction() {
        Auction auction = activeAuction();
        auction.setStatus(AuctionStatus.CLOSED);

        assertThatThrownBy(() -> bidValidator.validate(auction, new BigDecimal("200.00")))
                .isInstanceOf(AuctionNotActiveException.class);
    }

    private Auction activeAuction() {
        return Auction.builder()
                .id(1L)
                .title("Test Auction")
                .sellerId(1L)
                .startingPrice(new BigDecimal("100.00"))
                .currentPrice(new BigDecimal("100.00"))
                .minIncrement(new BigDecimal("5.00"))
                .status(AuctionStatus.ACTIVE)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .build();
    }
}
