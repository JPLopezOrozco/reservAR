CREATE UNIQUE INDEX ux_restaurant_identity_ci
    ON restaurants (lower(name), lower(city), lower(address));

ALTER TABLE restaurant_availability
    DROP CONSTRAINT uc_restaurant_day;

CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE restaurant_availability
    ADD CONSTRAINT no_overlap_availability
    EXCLUDE USING gist(
        restaurant_id WITH =,
        day_of_week WITH =,
        tstzrange(start::timestamp, "end"::timestamp, '[)') WITH &&
        )