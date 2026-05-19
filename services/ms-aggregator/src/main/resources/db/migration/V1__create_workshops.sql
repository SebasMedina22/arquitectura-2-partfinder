CREATE TABLE workshops (
    id                          VARCHAR(40)    PRIMARY KEY,
    name                        VARCHAR(120)   NOT NULL,
    credit_limit_amount         DECIMAL(15,2)  NOT NULL,
    credit_limit_currency       VARCHAR(3)     NOT NULL,
    credit_used_amount          DECIMAL(15,2)  NOT NULL DEFAULT 0.00,
    credit_used_currency        VARCHAR(3)     NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
