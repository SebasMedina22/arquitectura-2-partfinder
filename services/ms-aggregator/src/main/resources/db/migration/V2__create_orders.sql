CREATE TABLE orders (
    id              VARCHAR(64)   PRIMARY KEY,
    workshop_id     VARCHAR(40)   NOT NULL,
    part_id         VARCHAR(40)   NOT NULL,
    supplier_id     VARCHAR(40)   NOT NULL,
    quantity        INT           NOT NULL,
    total_amount    DECIMAL(15,2) NOT NULL,
    total_currency  VARCHAR(3)    NOT NULL,
    status          VARCHAR(16)   NOT NULL,
    created_at      TIMESTAMP(6)  NOT NULL,
    INDEX idx_orders_workshop (workshop_id),
    INDEX idx_orders_status   (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
