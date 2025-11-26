-- Drop the problematic unique constraint that's preventing artists from applying to multiple jobs
ALTER TABLE job_applications DROP INDEX unique_application;
