-- Remove job_posts table and related references
-- This migration removes the job_posts table since we're using the jobs table instead

-- Drop foreign key constraints that reference job_posts
-- Note: We'll handle this carefully since the constraint might not exist
SET @constraint_name = (
    SELECT CONSTRAINT_NAME 
    FROM information_schema.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'job_posting_field_values' 
    AND CONSTRAINT_NAME = 'job_posting_field_values_ibfk_1'
);

SET @sql = IF(@constraint_name IS NOT NULL, 
    CONCAT('ALTER TABLE job_posting_field_values DROP FOREIGN KEY ', @constraint_name), 
    'SELECT "No foreign key constraint found" as message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Drop the job_posting_field_values table (it references job_posts)
DROP TABLE IF EXISTS job_posting_field_values;

-- Drop foreign key constraint from bookmarked_jobs that references job_posts
SET @constraint_name = (
    SELECT CONSTRAINT_NAME 
    FROM information_schema.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'bookmarked_jobs' 
    AND CONSTRAINT_NAME = 'bookmarked_jobs_ibfk_1'
);

SET @sql = IF(@constraint_name IS NOT NULL, 
    CONCAT('ALTER TABLE bookmarked_jobs DROP FOREIGN KEY ', @constraint_name), 
    'SELECT "No foreign key constraint found" as message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Drop the job_posts table
DROP TABLE IF EXISTS job_posts;

-- Drop the job_skills table if it exists (it was referenced by job_posts)
DROP TABLE IF EXISTS job_skills;
