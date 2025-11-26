-- Create jobs table
CREATE TABLE IF NOT EXISTS jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recruiter_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    requirements TEXT,
    location VARCHAR(255),
    job_type ENUM('FULL_TIME', 'PART_TIME', 'CONTRACT', 'FREELANCE', 'INTERNSHIP', 'PROJECT_BASED') NOT NULL,
    experience_level ENUM('ENTRY_LEVEL', 'MID_LEVEL', 'SENIOR_LEVEL', 'EXPERT_LEVEL') NOT NULL,
    budget_min DECIMAL(10,2),
    budget_max DECIMAL(10,2),
    currency VARCHAR(3) DEFAULT 'INR',
    duration_days INT,
    start_date DATE,
    end_date DATE,
    application_deadline DATE,
    is_remote BOOLEAN DEFAULT FALSE,
    is_urgent BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
    status ENUM('DRAFT', 'ACTIVE', 'PAUSED', 'CLOSED', 'CANCELLED') DEFAULT 'ACTIVE',
    views_count INT DEFAULT 0,
    applications_count INT DEFAULT 0,
    tags JSON,
    skills_required JSON,
    benefits TEXT,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    published_at TIMESTAMP,
    closed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (recruiter_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_recruiter_id (recruiter_id),
    INDEX idx_status (status),
    INDEX idx_job_type (job_type),
    INDEX idx_experience_level (experience_level),
    INDEX idx_location (location),
    INDEX idx_published_at (published_at),
    INDEX idx_application_deadline (application_deadline)
);

-- job_applications table already exists from V1, will be updated in V13

-- Create bookmarked_jobs table
CREATE TABLE IF NOT EXISTS bookmarked_jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    artist_id BIGINT NOT NULL,
    bookmarked_at TIMESTAMP NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artist_profiles(id) ON DELETE CASCADE,
    UNIQUE KEY unique_job_artist_bookmark (job_id, artist_id),
    INDEX idx_job_id (job_id),
    INDEX idx_artist_id (artist_id),
    INDEX idx_bookmarked_at (bookmarked_at)
);

-- Insert sample job data
INSERT INTO jobs (recruiter_id, title, description, requirements, location, job_type, experience_level, budget_min, budget_max, currency, duration_days, start_date, end_date, application_deadline, is_remote, is_urgent, is_featured, status, views_count, applications_count, tags, skills_required, benefits, contact_email, contact_phone, published_at) VALUES
(1, 'Lead Actor for TV Commercial', 'We are looking for a charismatic lead actor for our upcoming TV commercial. The role requires strong screen presence and ability to deliver dialogue naturally.', 'Minimum 2 years of acting experience, good communication skills, available for 3-day shoot', 'Mumbai', 'CONTRACT', 'MID_LEVEL', 50000.00, 75000.00, 'INR', 3, '2024-11-01', '2024-11-03', '2024-10-25', FALSE, FALSE, TRUE, 'ACTIVE', 0, 0, '["TV Commercial", "Lead Role", "Mumbai"]', '["Acting", "Screen Presence", "Dialogue Delivery"]', 'Professional shoot environment, experienced crew, good exposure', 'casting@example.com', '+91-9876543210', NOW()),
(1, 'Background Actor for Movie', 'Looking for background actors for a Bollywood movie shoot. No dialogue required, just natural expressions and reactions.', 'No prior experience required, punctual, available for full day shoots', 'Delhi', 'CONTRACT', 'ENTRY_LEVEL', 2000.00, 3000.00, 'INR', 1, '2024-11-15', '2024-11-15', '2024-11-10', FALSE, FALSE, FALSE, 'ACTIVE', 0, 0, '["Background Actor", "Movie", "Delhi"]', '["Natural Expressions", "Punctuality"]', 'Good learning experience, networking opportunities', 'background@example.com', '+91-9876543211', NOW()),
(1, 'Model for Fashion Shoot', 'Fashion brand looking for models for their winter collection shoot. Experience in fashion modeling preferred.', 'Fashion modeling experience, height 5\'6" or above, photogenic', 'Bangalore', 'CONTRACT', 'MID_LEVEL', 30000.00, 50000.00, 'INR', 2, '2024-11-20', '2024-11-21', '2024-11-15', FALSE, TRUE, FALSE, 'ACTIVE', 0, 0, '["Fashion", "Modeling", "Winter Collection"]', '["Fashion Modeling", "Photogenic", "Height Requirements"]', 'Professional portfolio shots, brand exposure', 'fashion@example.com', '+91-9876543212', NOW());
