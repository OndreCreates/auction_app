CREATE TABLE watchlist_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    auction_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_watchlist_items_auction FOREIGN KEY (auction_id) REFERENCES auctions (id),
    CONSTRAINT uq_watchlist_items_user_auction UNIQUE (user_id, auction_id)
) ENGINE = InnoDB;

CREATE INDEX idx_watchlist_items_user_id ON watchlist_items (user_id);
