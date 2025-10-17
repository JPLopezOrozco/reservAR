ALTER TABLE restaurant_availability
    RENAME COLUMN "end" TO end_time;

ALTER TABLE restaurant_availability
    RENAME COLUMN start TO start_time;