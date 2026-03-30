-- Migration to allow multiple confirmation records for a single reservation
-- This is necessary to support sending both an Email and an SMS for the same booking.

DO $$
BEGIN
    -- Drop the unique constraint if it exists
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'confirmations_reservation_id_key') THEN
        ALTER TABLE confirmations DROP CONSTRAINT confirmations_reservation_id_key;
    END IF;
    
    -- Drop the unique index if Postgres created one automatically
    DROP INDEX IF EXISTS confirmations_reservation_id_key;
END $$;
