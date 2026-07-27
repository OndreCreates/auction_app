ALTER TABLE auctions
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

CREATE TABLE bids (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    bidder_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bids_auction FOREIGN KEY (auction_id) REFERENCES auctions (id)
) ENGINE = InnoDB;

CREATE INDEX idx_bids_auction_id ON bids (auction_id);
