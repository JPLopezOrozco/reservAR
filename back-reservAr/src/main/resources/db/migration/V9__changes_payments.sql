ALTER TABLE payments
    ADD COLUMN preference_id VARCHAR(255),
    ADD COLUMN payment_provider_id VARCHAR(255),
    ADD COLUMN init_point VARCHAR(500),
    ADD COLUMN external_reference varchar(255);

