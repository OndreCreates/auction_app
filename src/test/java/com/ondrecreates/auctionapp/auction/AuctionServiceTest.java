package com.ondrecreates.auctionapp.auction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        auctionService = new AuctionService(auctionRepository);
    }

    @Test
    void create_setsCurrentPriceToStartingPriceAndStatusActive() {
        Auction auction = Auction.builder()
                .title("Vintage Camera")
                .sellerId("seller@example.com")
                .startingPrice(new BigDecimal("100.00"))
                .minIncrement(new BigDecimal("5.00"))
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(3))
                .build();
        when(auctionRepository.save(any(Auction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Auction saved = auctionService.create(auction);

        assertThat(saved.getCurrentPrice()).isEqualByComparingTo("100.00");
        assertThat(saved.getStatus()).isEqualTo(AuctionStatus.ACTIVE);
    }

    @Test
    void getById_returnsAuctionWhenFound() {
        Auction auction = Auction.builder().id(1L).title("Vintage Camera").build();
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));

        Auction found = auctionService.getById(1L);

        assertThat(found.getId()).isEqualTo(1L);
    }

    @Test
    void getById_throwsWhenNotFound() {
        when(auctionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auctionService.getById(999L))
                .isInstanceOf(AuctionNotFoundException.class)
                .hasMessageContaining("999");
    }
}
