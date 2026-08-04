package com.ondrecreates.auctionapp.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Map;

// Same convention as incident_management_system_app's NotificationClient: X-API-Key auth,
// fire-and-forget. A down/misconfigured notification service must never fail a bid.
@Slf4j
@Component
public class RestNotificationClient implements NotificationClient {

    private final RestClient restClient;
    private final String apiKey;

    public RestNotificationClient(
            @Value("${notification.api-url}") String apiUrl,
            @Value("${notification.api-key:}") String apiKey) {
        this.restClient = RestClient.builder().baseUrl(apiUrl).build();
        this.apiKey = apiKey;
    }

    @Override
    public void sendBidUpdateNotification(String recipientEmail, String auctionTitle, BigDecimal amount) {
        if (apiKey.isBlank()) {
            log.warn("notification.api-key not configured, skipping watchlist notification to {}", recipientEmail);
            return;
        }

        try {
            restClient.post()
                    .uri("/api/v1/notifications")
                    .header("X-API-Key", apiKey)
                    .body(Map.of(
                            "channel", "EMAIL",
                            "recipient", recipientEmail,
                            "subject", "New bid on " + auctionTitle,
                            "body", "A new highest bid of " + amount + " was placed on \"" + auctionTitle + "\"."
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.warn("Failed to send watchlist notification to {} for auction '{}'", recipientEmail, auctionTitle, e);
        }
    }
}
