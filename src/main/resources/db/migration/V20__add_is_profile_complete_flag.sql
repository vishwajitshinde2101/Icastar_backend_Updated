-- Add is_profile_complete flag to artist_profiles table
ALTER TABLE artist_profiles ADD COLUMN is_profile_complete BOOLEAN NOT NULL DEFAULT FALSE;

