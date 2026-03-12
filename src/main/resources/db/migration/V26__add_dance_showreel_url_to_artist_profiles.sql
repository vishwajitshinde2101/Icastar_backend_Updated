-- Add dance showreel URL column to artist_profiles table
-- This column stores S3 URL for uploaded dance showreel video

ALTER TABLE artist_profiles ADD COLUMN dance_showreel_url VARCHAR(500) NULL;
