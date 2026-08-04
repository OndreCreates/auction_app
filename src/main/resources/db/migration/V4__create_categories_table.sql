CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

INSERT INTO categories (name, slug) VALUES
    ('Watches', 'watches'),
    ('Art', 'art'),
    ('Cars', 'cars'),
    ('Golf', 'golf'),
    ('Jewelry', 'jewelry');
