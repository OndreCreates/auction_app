-- identity_server_app puts the user's email in the JWT sub claim, no numeric
-- AppUser id (see incident_management_system_app for the same convention).
-- External user references move from BIGINT to VARCHAR.
ALTER TABLE auctions MODIFY seller_id VARCHAR(255) NOT NULL;
ALTER TABLE bids MODIFY bidder_id VARCHAR(255) NOT NULL;
