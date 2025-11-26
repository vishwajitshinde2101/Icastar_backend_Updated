-- Fix the status column size to prevent truncation
-- Change the status column to VARCHAR with sufficient size

ALTER TABLE job_applications MODIFY COLUMN status VARCHAR(50) DEFAULT 'APPLIED' NOT NULL;
