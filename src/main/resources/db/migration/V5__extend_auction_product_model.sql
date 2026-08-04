-- category_id is nullable at the DB level so this migration stays safe against rows that
-- predate categories; CreateAuctionRequest requires it for every new auction from now on.
ALTER TABLE auctions
    ADD COLUMN category_id BIGINT NULL,
    ADD COLUMN provenance TEXT NULL,
    ADD COLUMN verified BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE auctions
    ADD CONSTRAINT fk_auctions_category FOREIGN KEY (category_id) REFERENCES categories (id);

CREATE INDEX idx_auctions_category_id ON auctions (category_id);

CREATE TABLE auction_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    url VARCHAR(1000) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_auction_images_auction FOREIGN KEY (auction_id) REFERENCES auctions (id)
) ENGINE = InnoDB;

CREATE INDEX idx_auction_images_auction_id ON auction_images (auction_id);
