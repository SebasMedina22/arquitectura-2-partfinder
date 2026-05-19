CREATE TABLE failed_searches (
    event_id    VARCHAR(64)  PRIMARY KEY,
    part_query  VARCHAR(200) NOT NULL,
    workshop_id VARCHAR(40)  NOT NULL,
    searched_at TIMESTAMP(6) NOT NULL
);

CREATE INDEX idx_failed_searches_part_query ON failed_searches (part_query);
CREATE INDEX idx_failed_searches_workshop   ON failed_searches (workshop_id);
CREATE INDEX idx_failed_searches_searched   ON failed_searches (searched_at);
