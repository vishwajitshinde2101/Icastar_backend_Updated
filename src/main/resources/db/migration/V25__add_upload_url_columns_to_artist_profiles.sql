-- Add cover photo and ID proof columns to artist_profiles table
-- These columns store S3 URLs for uploaded files

ALTER TABLE artist_profiles ADD COLUMN cover_photo_url VARCHAR(500) NULL;
ALTER TABLE artist_profiles ADD COLUMN id_proof_url VARCHAR(500) NULL;
ALTER TABLE artist_profiles ADD COLUMN id_proof_verified BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE artist_profiles ADD COLUMN id_proof_uploaded_at TIMESTAMP NULL;