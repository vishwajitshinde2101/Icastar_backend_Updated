-- Add columns for role-based (open) auditions
-- This allows recruiters to create auditions targeting specific artist types

-- Add target_artist_type_id column for role-based filtering
ALTER TABLE auditions ADD COLUMN target_artist_type_id BIGINT NULL AFTER artist_id;

-- Add title column for audition title
ALTER TABLE auditions ADD COLUMN title VARCHAR(255) AFTER target_artist_type_id;

-- Add description column for audition details
ALTER TABLE auditions ADD COLUMN description TEXT AFTER title;

-- Add is_open_audition flag to distinguish open auditions from direct auditions
ALTER TABLE auditions ADD COLUMN is_open_audition BOOLEAN NOT NULL DEFAULT FALSE AFTER description;

-- Add foreign key constraint for target_artist_type
ALTER TABLE auditions ADD CONSTRAINT fk_audition_target_artist_type
    FOREIGN KEY (target_artist_type_id) REFERENCES artist_types(id) ON DELETE SET NULL;

-- Add indexes for efficient querying of open auditions
ALTER TABLE auditions ADD INDEX idx_target_artist_type (target_artist_type_id);
ALTER TABLE auditions ADD INDEX idx_open_auditions (is_open_audition, target_artist_type_id, status);