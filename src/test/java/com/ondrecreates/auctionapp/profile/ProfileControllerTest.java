package com.ondrecreates.auctionapp.profile;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionRepository;
import com.ondrecreates.auctionapp.auction.AuctionResponseAssembler;
import com.ondrecreates.auctionapp.bid.BidRepository;
import com.ondrecreates.auctionapp.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@Import(SecurityConfig.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BidRepository bidRepository;

    @MockitoBean
    private AuctionRepository auctionRepository;

    @MockitoBean
    private AuctionResponseAssembler auctionResponseAssembler;

    @Test
    void getBidHistory_withoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(get("/profile/bids")).andExpect(status().isUnauthorized());
    }

    @Test
    void getWonAuctions_withoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(get("/profile/won")).andExpect(status().isUnauthorized());
    }

    @Test
    void getBidHistory_withToken_usesJwtSubjectAsBidderId() throws Exception {
        when(bidRepository.findByBidderId(eq("buyer@example.com"), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/profile/bids").with(jwt().jwt(builder -> builder.subject("buyer@example.com"))))
                .andExpect(status().isOk());

        verify(bidRepository).findByBidderId(eq("buyer@example.com"), any());
    }

    @Test
    void getWonAuctions_withToken_usesJwtSubjectAsUserId() throws Exception {
        when(auctionRepository.findWonAuctions(eq("buyer@example.com"), any()))
                .thenReturn(new PageImpl<Auction>(List.of()));
        when(auctionResponseAssembler.toResponseList(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/profile/won").with(jwt().jwt(builder -> builder.subject("buyer@example.com"))))
                .andExpect(status().isOk());

        verify(auctionRepository).findWonAuctions(eq("buyer@example.com"), any());
    }
}
