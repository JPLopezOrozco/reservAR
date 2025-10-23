ALTER TABLE inventory_rule
    DROP COLUMN slot_granularity_min;

ALTER TABLE inventory_rule
    ADD COLUMN
        grace_period_min INT NOT NULL DEFAULT 15;
