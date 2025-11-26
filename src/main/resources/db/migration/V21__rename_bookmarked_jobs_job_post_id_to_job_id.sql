-- Rename job_post_id to job_id in bookmarked_jobs table
-- This migration updates the existing bookmarked_jobs table to use job_id instead of job_post_id

-- First, drop the existing foreign key constraint on job_post_id (if it exists)
SET @constraint_name_job_post_id = (
    SELECT CONSTRAINT_NAME 
    FROM information_schema.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'bookmarked_jobs' 
    AND COLUMN_NAME = 'job_post_id' 
    AND REFERENCED_TABLE_NAME IS NOT NULL
    LIMIT 1
);

SET @sql_drop_fk_job_post_id = IF(@constraint_name_job_post_id IS NOT NULL, 
    CONCAT('ALTER TABLE bookmarked_jobs DROP FOREIGN KEY ', @constraint_name_job_post_id), 
    'SELECT "No foreign key constraint found on job_post_id" as message'
);

PREPARE stmt_drop_fk_job_post_id FROM @sql_drop_fk_job_post_id;
EXECUTE stmt_drop_fk_job_post_id;
DEALLOCATE PREPARE stmt_drop_fk_job_post_id;

-- Drop the old unique constraint if it exists
SET @old_constraint = (
    SELECT CONSTRAINT_NAME 
    FROM information_schema.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'bookmarked_jobs' 
    AND CONSTRAINT_NAME = 'unique_bookmark'
);

SET @drop_sql = IF(@old_constraint IS NOT NULL, 
    CONCAT('ALTER TABLE bookmarked_jobs DROP INDEX unique_bookmark'), 
    'SELECT "No old constraint to drop" as message'
);

PREPARE drop_stmt FROM @drop_sql;
EXECUTE drop_stmt;
DEALLOCATE PREPARE drop_stmt;

-- Check if column job_post_id exists
SET @job_post_id_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'bookmarked_jobs' 
    AND COLUMN_NAME = 'job_post_id'
);

-- Check if column job_id already exists
SET @job_id_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'bookmarked_jobs' 
    AND COLUMN_NAME = 'job_id'
);

-- Handle column rename or drop based on what exists
-- Case 1: job_id doesn't exist but job_post_id does - rename it
SET @rename_sql = IF(@job_id_exists = 0 AND @job_post_id_exists > 0, 
    'ALTER TABLE bookmarked_jobs CHANGE COLUMN job_post_id job_id BIGINT NOT NULL',
    'SELECT "Skipping rename - checking for drop instead" as message'
);

PREPARE rename_stmt FROM @rename_sql;
EXECUTE rename_stmt;
DEALLOCATE PREPARE rename_stmt;

-- Case 2: Both columns exist - drop any remaining constraints on job_post_id, then drop column
-- Drop any unique/index constraints that use job_post_id
SET @constraint_on_job_post_id = (
    SELECT CONSTRAINT_NAME 
    FROM information_schema.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'bookmarked_jobs' 
    AND COLUMN_NAME = 'job_post_id' 
    AND CONSTRAINT_NAME != 'PRIMARY'
    LIMIT 1
);

SET @drop_constraint_sql = IF(@constraint_on_job_post_id IS NOT NULL AND @job_post_id_exists > 0 AND @job_id_exists > 0,
    CONCAT('ALTER TABLE bookmarked_jobs DROP INDEX ', @constraint_on_job_post_id),
    'SELECT "No constraint to drop on job_post_id" as message'
);

PREPARE drop_constraint_stmt FROM @drop_constraint_sql;
EXECUTE drop_constraint_stmt;
DEALLOCATE PREPARE drop_constraint_stmt;

-- Now drop the job_post_id column if both columns exist
SET @drop_sql = IF(@job_post_id_exists > 0 AND @job_id_exists > 0,
    'ALTER TABLE bookmarked_jobs DROP COLUMN job_post_id',
    'SELECT "No drop needed" as message'
);

PREPARE drop_stmt FROM @drop_sql;
EXECUTE drop_stmt;
DEALLOCATE PREPARE drop_stmt;

-- Add the foreign key constraint to reference jobs table
SET @fk_exists = (
    SELECT COUNT(*) 
    FROM information_schema.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'bookmarked_jobs' 
    AND CONSTRAINT_NAME = 'fk_bookmarked_jobs_job_id'
);

SET @add_fk_sql = IF(@fk_exists = 0, 
    'ALTER TABLE bookmarked_jobs ADD CONSTRAINT fk_bookmarked_jobs_job_id FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE', 
    'SELECT "Foreign key already exists" as message'
);

PREPARE add_fk_stmt FROM @add_fk_sql;
EXECUTE add_fk_stmt;
DEALLOCATE PREPARE add_fk_stmt;

-- Re-add the unique constraint if it doesn't exist
SET @unique_constraint = (
    SELECT CONSTRAINT_NAME 
    FROM information_schema.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'bookmarked_jobs' 
    AND CONSTRAINT_NAME = 'unique_job_artist_bookmark'
);

SET @add_unique_sql = IF(@unique_constraint IS NULL, 
    'ALTER TABLE bookmarked_jobs ADD CONSTRAINT unique_job_artist_bookmark UNIQUE (job_id, artist_id)', 
    'SELECT "Unique constraint already exists" as message'
);

PREPARE add_unique_stmt FROM @add_unique_sql;
EXECUTE add_unique_stmt;
DEALLOCATE PREPARE add_unique_stmt;

