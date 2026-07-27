package com.ondrecreates.auctionapp.auction;

import com.ondrecreates.auctionapp.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionController.class)
@Import(SecurityConfig.class)
class AuctionControllerTest {

    private static final String VALID_BODY = """
            {"title":"Vintage Camera","startingPrice":100.00,"minIncrement":5.00,
             "startTime":"%s","endTime":"%s"}
            """.formatted(
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    LocalDateTime.now().plusDays(4).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuctionService auctionService;

    @Test
    void create_withoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(post("/auctions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_withToken_usesJwtSubjectAsSellerId() throws Exception {
        when(auctionService.create(any(Auction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/auctions")
                        .with(jwt().jwt(builder -> builder.subject("seller@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sellerId").value("seller@example.com"));
    }
}
