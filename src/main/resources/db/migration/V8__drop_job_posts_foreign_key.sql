-- Drop the foreign key constraint that references job_posts table
-- This will allow the application to work with the jobs table instead

ALTER TABLE job_applications DROP FOREIGN KEY job_applications_ibfk_1;
