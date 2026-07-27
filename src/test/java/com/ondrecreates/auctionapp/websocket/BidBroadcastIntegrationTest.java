package com.ondrecreates.auctionapp.websocket;

import com.ondrecreates.auctionapp.auction.Auction;
import com.ondrecreates.auctionapp.auction.AuctionRepository;
import com.ondrecreates.auctionapp.auction.AuctionStatus;
import com.ondrecreates.auctionapp.bid.BidService;
import com.ondrecreates.auctionapp.bid.dto.BidResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BidBroadcastIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidService bidService;

    @Test
    void broadcastsNewBidToAuctionTopic() throws Exception {
        Auction auction = auctionRepository.save(Auction.builder()
                .title("Broadcast Test Auction")
                .sellerId("seller@example.com")
                .startingPrice(new BigDecimal("100.00"))
                .currentPrice(new BigDecimal("100.00"))
                .minIncrement(new BigDecimal("5.00"))
                .status(AuctionStatus.ACTIVE)
                .startTime(LocalDateTime.now().minusMinutes(1))
                .endTime(LocalDateTime.now().plusDays(1))
                .build());

        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());

        BlockingQueue<BidResponse> receivedMessages = new ArrayBlockingQueue<>(1);

        StompSession session = stompClient
                .connectAsync("ws://localhost:" + port + "/ws", new StompSessionHandlerAdapter() {
                    @Override
                    public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                                 byte[] payload, Throwable exception) {
                        log.error("STOMP client error", exception);
                    }

                    @Override
                    public void handleTransportError(StompSession session, Throwable exception) {
                        log.error("STOMP transport error", exception);
                    }
                })
                .get(5, TimeUnit.SECONDS);

        session.subscribe("/topic/auctions/" + auction.getId(), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BidResponse.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedMessages.add((BidResponse) payload);
            }
        });
        // No synchronous way to know the broker registered the subscription; give it a moment.
        Thread.sleep(200);

        bidService.placeBid(auction.getId(), "bidder@example.com", new BigDecimal("110.00"));

        BidResponse message = receivedMessages.poll(5, TimeUnit.SECONDS);

        assertThat(message).isNotNull();
        assertThat(message.auctionId()).isEqualTo(auction.getId());
        assertThat(message.bidderId()).isEqualTo("bidder@example.com");
        assertThat(message.amount()).isEqualByComparingTo("110.00");

        session.disconnect();
    }
}
