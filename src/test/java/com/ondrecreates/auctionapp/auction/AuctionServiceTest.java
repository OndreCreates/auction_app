package com.ondrecreates.auctionapp.auction;

import com.ondrecreates.auctionapp.category.Category;
import com.ondrecreates.auctionapp.category.CategoryNotFoundException;
import com.ondrecreates.auctionapp.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionImageRepository auctionImageRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        auctionService = new AuctionService(auctionRepository, auctionImageRepository, categoryRepository);
    }

    @Test
    void create_setsCurrentPriceToStartingPriceAndStatusActive() {
        Auction auction = Auction.builder()
                .title("Vintage Camera")
                .sellerId("seller@example.com")
                .categoryId(1L)
                .startingPrice(new BigDecimal("100.00"))
                .minIncrement(new BigDecimal("5.00"))
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(3))
                .build();
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(auctionRepository.save(any(Auction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Auction saved = auctionService.create(auction, List.of());

        assertThat(saved.getCurrentPrice()).isEqualByComparingTo("100.00");
        assertThat(saved.getStatus()).isEqualTo(AuctionStatus.ACTIVE);
    }

    @Test
    void create_throwsWhenCategoryDoesNotExist() {
        Auction auction = Auction.builder()
                .title("Vintage Camera")
                .sellerId("seller@example.com")
                .categoryId(999L)
                .startingPrice(new BigDecimal("100.00"))
                .minIncrement(new BigDecimal("5.00"))
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(3))
                .build();
        when(categoryRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> auctionService.create(auction, List.of()))
                .isInstanceOf(CategoryNotFoundException.class);
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

    @Test
    void getAll_withoutCategory_returnsEverything() {
        auctionService.getAll(null);

        verify(auctionRepository).findAll(any(Sort.class));
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void getAll_withCategory_resolvesSlugAndFilters() {
        Category watches = Category.builder().id(1L).name("Watches").slug("watches").build();
        when(categoryRepository.findBySlug("watches")).thenReturn(Optional.of(watches));

        auctionService.getAll("watches");

        verify(auctionRepository).findByCategoryId(eq(1L), any(Sort.class));
    }

    @Test
    void getAll_withUnknownCategory_throws() {
        when(categoryRepository.findBySlug("bogus")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auctionService.getAll("bogus"))
                .isInstanceOf(CategoryNotFoundException.class);
    }
}
