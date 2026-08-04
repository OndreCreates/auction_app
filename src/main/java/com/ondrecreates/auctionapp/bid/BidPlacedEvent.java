package com.ondrecreates.auctionapp.bid;

// Public: both BidBroadcastListener (this package) and WatchlistNotificationListener
// (watchlist package) react to it - a genuine cross-feature domain event, not an
// implementation detail of the bid feature anymore.
public record BidPlacedEvent(Bid bid) {
}
