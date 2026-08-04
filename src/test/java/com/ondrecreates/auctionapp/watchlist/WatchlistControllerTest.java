package com.ondrecreates.auctionapp.watchlist;

import com.ondrecreates.auctionapp.auction.AuctionResponseAssembler;
import com.ondrecreates.auctionapp.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WatchlistController.class)
@Import(SecurityConfig.class)
class WatchlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WatchlistService watchlistService;

    @MockitoBean
    private AuctionResponseAssembler auctionResponseAssembler;

    @Test
    void getAll_withoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(get("/watchlist")).andExpect(status().isUnauthorized());
    }

    @Test
    void add_withToken_usesJwtSubjectAsUserId() throws Exception {
        mockMvc.perform(post("/watchlist/1").with(jwt().jwt(builder -> builder.subject("buyer@example.com"))))
                .andExpect(status().isNoContent());

        verify(watchlistService).addToWatchlist("buyer@example.com", 1L);
    }

    @Test
    void remove_withToken_usesJwtSubjectAsUserId() throws Exception {
        mockMvc.perform(delete("/watchlist/1").with(jwt().jwt(builder -> builder.subject("buyer@example.com"))))
                .andExpect(status().isNoContent());

        verify(watchlistService).removeFromWatchlist("buyer@example.com", 1L);
    }

    @Test
    void getAll_withToken_returnsWatchedAuctions() throws Exception {
        when(watchlistService.getWatchedAuctions("buyer@example.com")).thenReturn(List.of());
        when(auctionResponseAssembler.toResponseList(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/watchlist").with(jwt().jwt(builder -> builder.subject("buyer@example.com"))))
                .andExpect(status().isOk());
    }
}
