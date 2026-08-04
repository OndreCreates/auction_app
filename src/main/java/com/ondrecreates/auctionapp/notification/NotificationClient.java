package com.ondrecreates.auctionapp.notification;

import java.math.BigDecimal;

// Interface exists so tests can mock it and so the fire-and-forget failure handling
// (see RestNotificationClient) is the only place that talks to notification_center_app.
public interface NotificationClient {

    void sendBidUpdateNotification(String recipientEmail, String auctionTitle, BigDecimal amount);
}
