package com.ondrecreates.auctionapp.bid;

import com.ondrecreates.auctionapp.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BidController.class)
@Import(SecurityConfig.class)
class BidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BidService bidService;

    @Test
    void placeBid_withoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(post("/auctions/1/bids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":110.00}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void placeBid_withToken_usesJwtSubjectAsBidderId() throws Exception {
        Bid bid = Bid.builder()
                .id(1L)
                .auctionId(1L)
                .bidderId("bidder@example.com")
                .amount(new BigDecimal("110.00"))
                .build();
        when(bidService.placeBid(eq(1L), eq("bidder@example.com"), any(BigDecimal.class))).thenReturn(bid);

        mockMvc.perform(post("/auctions/1/bids")
                        .with(jwt().jwt(builder -> builder.subject("bidder@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":110.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bidderId").value("bidder@example.com"));

        verify(bidService).placeBid(1L, "bidder@example.com", new BigDecimal("110.00"));
    }
}
