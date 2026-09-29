CREATE TABLE products
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    quantity INT NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_products
        PRIMARY KEY (id),

    CONSTRAINT chk_products_quantity
        CHECK (quantity >= 0),

    CONSTRAINT chk_products_price
        CHECK (price > 0)
);

CREATE INDEX idx_products_name
    ON products(name);