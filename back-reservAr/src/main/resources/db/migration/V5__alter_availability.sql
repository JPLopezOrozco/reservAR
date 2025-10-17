DROP TRIGGER IF EXISTS trg_check_overlap ON restaurant_availability;
DROP FUNCTION IF EXISTS check_availability_overlap() CASCADE;

CREATE OR REPLACE FUNCTION check_availability_overlap()
    RETURNS trigger AS $$
BEGIN
    IF EXISTS(
        SELECT 1
        FROM restaurant_availability a
        WHERE a.restaurant_id = NEW.restaurant_id
          AND a.day_of_week = NEW.day_of_week
          AND a.start_time < NEW.end_time
          AND a.end_time > NEW.start_time
    ) THEN
        RAISE EXCEPTION 'Overlap for restaurant availability fot % day %', NEW.restaurant_id, NEW.day_of_week
            USING ERRCODE = 'unique violation';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE  TRIGGER trg_check_overlap
    BEFORE INSERT OR UPDATE ON restaurant_availability
    FOR EACH ROW EXECUTE FUNCTION check_availability_overlap();

ALTER TABLE restaurant_availability
    DROP CONSTRAINT IF EXISTS check_start_before;

ALTER TABLE restaurant_availability
    ADD CONSTRAINT check_start_before CHECK (start_time < end_time);

