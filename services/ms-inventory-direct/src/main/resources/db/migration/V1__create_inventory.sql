CREATE TABLE inventory_items (
    part_id       VARCHAR(40)  NOT NULL,
    supplier_id   VARCHAR(40)  NOT NULL,
    stock         INT          NOT NULL DEFAULT 0,
    last_updated  TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (part_id, supplier_id),
    INDEX idx_inventory_part (part_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
