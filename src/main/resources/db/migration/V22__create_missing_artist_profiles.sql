-- Flyway Migration: V22 - Create missing artist profiles for existing ARTIST users
-- This migration creates ArtistProfile records for ARTIST users who don't have one

-- Insert ArtistProfile for existing ARTIST users who don't have a profile
-- Uses the "OTHER" artist type as default, or falls back to the first available artist type
INSERT INTO artist_profiles (
    user_id,
    artist_type_id,
    first_name,
    last_name,
    is_verified_badge,
    is_profile_complete,
    total_applications,
    successful_hires,
    created_at,
    updated_at
)
SELECT
    u.id AS user_id,
    COALESCE(
        (SELECT id FROM artist_types WHERE name = 'OTHER' LIMIT 1),
        (SELECT id FROM artist_types WHERE is_active = TRUE ORDER BY sort_order LIMIT 1)
    ) AS artist_type_id,
    COALESCE(u.first_name, 'Unknown') AS first_name,
    COALESCE(u.last_name, 'Artist') AS last_name,
    FALSE AS is_verified_badge,
    FALSE AS is_profile_complete,
    0 AS total_applications,
    0 AS successful_hires,
    NOW() AS created_at,
    NOW() AS updated_at
FROM users u
WHERE u.role = 'ARTIST'
  AND u.id NOT IN (SELECT user_id FROM artist_profiles);