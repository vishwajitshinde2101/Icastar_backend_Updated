-- Create auditions table
CREATE TABLE IF NOT EXISTS auditions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_application_id BIGINT NULL,
    recruiter_id BIGINT NOT NULL,
    artist_id BIGINT NULL,
    audition_type ENUM('LIVE_VIDEO', 'LIVE_AUDIO', 'RECORDED_SUBMISSION', 'IN_PERSON') NOT NULL,
    scheduled_at TIMESTAMP NOT NULL,
    duration_minutes INT,
    meeting_link VARCHAR(500),
    instructions TEXT,
    status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW') NOT NULL DEFAULT 'SCHEDULED',
    completed_at TIMESTAMP NULL,
    feedback TEXT,
    rating INT,
    recording_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (job_application_id) REFERENCES job_applications(id) ON DELETE CASCADE,
    FOREIGN KEY (recruiter_id) REFERENCES recruiter_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artist_profiles(id) ON DELETE CASCADE,

    INDEX idx_artist_id (artist_id),
    INDEX idx_recruiter_id (recruiter_id),
    INDEX idx_job_application_id (job_application_id),
    INDEX idx_status (status),
    INDEX idx_scheduled_at (scheduled_at),
    INDEX idx_artist_scheduled (artist_id, scheduled_at, status)
);