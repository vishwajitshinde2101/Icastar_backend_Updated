-- Flyway Migration: V1 - Initial Schema

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    mobile VARCHAR(15) UNIQUE NOT NULL,
    password VARCHAR(255),
    role ENUM('ADMIN', 'ARTIST', 'RECRUITER') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    last_login DATETIME,
    failed_login_attempts INT DEFAULT 0,
    account_locked_until DATETIME,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    account_status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'BANNED', 'PENDING_VERIFICATION') NOT NULL DEFAULT 'ACTIVE',
    deactivated_at DATETIME NULL,
    deactivated_by BIGINT NULL,
    deactivation_reason TEXT NULL,
    reactivated_at DATETIME NULL,
    reactivated_by BIGINT NULL,
    reactivation_reason TEXT NULL,
    last_activity DATETIME NULL,
    login_attempts INT DEFAULT 0,
    locked_until DATETIME NULL
);

-- Artist Types table
CREATE TABLE artist_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Recruiter Categories table
CREATE TABLE recruiter_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Subscription Plans table
CREATE TABLE subscription_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    plan_type ENUM('FREE', 'BASIC', 'PREMIUM', 'PROFESSIONAL', 'ENTERPRISE') NOT NULL,
    user_role ENUM('ARTIST', 'RECRUITER', 'BOTH') NOT NULL DEFAULT 'BOTH',
    price DECIMAL(10,2) NOT NULL,
    billing_cycle ENUM('MONTHLY', 'YEARLY', 'ONE_TIME') NOT NULL,
    max_auditions INT DEFAULT 0,
    unlimited_auditions BOOLEAN NOT NULL DEFAULT FALSE,
    max_applications INT DEFAULT 0,
    unlimited_applications BOOLEAN NOT NULL DEFAULT FALSE,
    max_portfolio_items INT DEFAULT 0,
    unlimited_portfolio BOOLEAN NOT NULL DEFAULT FALSE,
    profile_verification BOOLEAN NOT NULL DEFAULT FALSE,
    priority_verification BOOLEAN NOT NULL DEFAULT FALSE,
    featured_profile BOOLEAN NOT NULL DEFAULT FALSE,
    advanced_analytics BOOLEAN NOT NULL DEFAULT FALSE,
    max_job_posts INT DEFAULT 0,
    unlimited_job_posts BOOLEAN NOT NULL DEFAULT FALSE,
    max_messages INT DEFAULT 0,
    unlimited_messages BOOLEAN NOT NULL DEFAULT FALSE,
    max_candidates_view INT DEFAULT 0,
    unlimited_candidates BOOLEAN NOT NULL DEFAULT FALSE,
    job_boost_credits INT DEFAULT 0,
    advanced_search BOOLEAN NOT NULL DEFAULT FALSE,
    candidate_verification BOOLEAN NOT NULL DEFAULT FALSE,
    priority_support BOOLEAN NOT NULL DEFAULT FALSE,
    max_file_uploads INT DEFAULT 0,
    unlimited_uploads BOOLEAN NOT NULL DEFAULT FALSE,
    max_file_size_mb INT DEFAULT 10,
    custom_branding BOOLEAN NOT NULL DEFAULT FALSE,
    api_access BOOLEAN NOT NULL DEFAULT FALSE,
    white_label BOOLEAN NOT NULL DEFAULT FALSE,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    is_popular BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Job Posting Fields table
CREATE TABLE job_posting_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    field_name VARCHAR(100) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    field_type ENUM('TEXT', 'TEXTAREA', 'NUMBER', 'EMAIL', 'PHONE', 'URL', 'DATE', 'BOOLEAN', 'SELECT', 'MULTI_SELECT', 'CHECKBOX', 'RADIO', 'FILE', 'JSON') NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    is_searchable BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    validation_rules JSON,
    options JSON,
    placeholder VARCHAR(255),
    help_text VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Artist Type Fields table
CREATE TABLE artist_type_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artist_type_id BIGINT NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    field_type ENUM('TEXT', 'TEXTAREA', 'NUMBER', 'EMAIL', 'PHONE', 'URL', 'DATE', 'BOOLEAN', 'SELECT', 'MULTI_SELECT', 'CHECKBOX', 'RADIO', 'FILE', 'JSON') NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    is_searchable BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    validation_rules JSON,
    options JSON,
    placeholder VARCHAR(255),
    help_text VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (artist_type_id) REFERENCES artist_types(id) ON DELETE CASCADE
);

-- Recruiter Category Fields table
CREATE TABLE recruiter_category_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recruiter_category_id BIGINT NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    field_type ENUM('TEXT', 'TEXTAREA', 'NUMBER', 'EMAIL', 'PHONE', 'URL', 'DATE', 'BOOLEAN', 'SELECT', 'MULTI_SELECT', 'CHECKBOX', 'RADIO', 'FILE', 'JSON') NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    is_searchable BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    validation_rules JSON,
    options JSON,
    placeholder VARCHAR(255),
    help_text VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (recruiter_category_id) REFERENCES recruiter_categories(id) ON DELETE CASCADE
);

-- Recruiter Profiles table
CREATE TABLE recruiter_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    contact_person_name VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(15),
    designation VARCHAR(100),
    company_description TEXT,
    company_website VARCHAR(255),
    company_logo_url VARCHAR(500),
    industry VARCHAR(100),
    company_size VARCHAR(50),
    location VARCHAR(255),
    is_verified_company BOOLEAN NOT NULL DEFAULT FALSE,
    total_jobs_posted INT DEFAULT 0,
    successful_hires INT DEFAULT 0,
    chat_credits INT DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    recruiter_category_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recruiter_category_id) REFERENCES recruiter_categories(id) ON DELETE SET NULL
);

-- Artist Profiles table
CREATE TABLE artist_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    artist_type_id BIGINT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    stage_name VARCHAR(100),
    bio TEXT,
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER', 'PREFER_NOT_TO_SAY'),
    location VARCHAR(255),
    profile_image_url VARCHAR(500),
    portfolio_urls JSON,
    skills JSON,
    experience_years INT,
    hourly_rate DECIMAL(10,2),
    is_verified_badge BOOLEAN NOT NULL DEFAULT FALSE,
    verification_requested_at DATE,
    verification_approved_at DATE,
    total_applications INT DEFAULT 0,
    successful_hires INT DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_type_id) REFERENCES artist_types(id)
);

-- Subscriptions table
CREATE TABLE subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subscription_plan_id BIGINT NOT NULL,
    status ENUM('ACTIVE', 'EXPIRED', 'CANCELLED', 'SUSPENDED', 'TRIAL') NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME,
    auto_renew BOOLEAN NOT NULL DEFAULT FALSE,
    amount_paid DECIMAL(10,2),
    payment_reference VARCHAR(255),
    auditions_used INT DEFAULT 0,
    applications_used INT DEFAULT 0,
    job_posts_used INT DEFAULT 0,
    messages_used INT DEFAULT 0,
    candidates_viewed INT DEFAULT 0,
    job_boosts_used INT DEFAULT 0,
    file_uploads_used INT DEFAULT 0,
    is_trial BOOLEAN NOT NULL DEFAULT FALSE,
    trial_end_date DATETIME,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans(id)
);

-- Artist Profile Fields table
CREATE TABLE artist_profile_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artist_profile_id BIGINT NOT NULL,
    artist_type_field_id BIGINT NOT NULL,
    field_value TEXT,
    file_url VARCHAR(500),
    file_name VARCHAR(255),
    file_size BIGINT,
    mime_type VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT unique_profile_field UNIQUE (artist_profile_id, artist_type_field_id),
    FOREIGN KEY (artist_profile_id) REFERENCES artist_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_type_field_id) REFERENCES artist_type_fields(id) ON DELETE CASCADE
);

-- Recruiter Profile Fields table
CREATE TABLE recruiter_profile_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recruiter_profile_id BIGINT NOT NULL,
    recruiter_category_field_id BIGINT NOT NULL,
    field_value TEXT,
    file_url VARCHAR(500),
    file_name VARCHAR(255),
    file_size BIGINT,
    mime_type VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (recruiter_profile_id) REFERENCES recruiter_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (recruiter_category_field_id) REFERENCES recruiter_category_fields(id) ON DELETE CASCADE
);

-- Job Posts table
CREATE TABLE job_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recruiter_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    requirements TEXT,
    skills_required JSON,
    job_type ENUM('FULL_TIME', 'PART_TIME', 'CONTRACT', 'FREELANCE', 'INTERNSHIP') NOT NULL,
    experience_level ENUM('ENTRY', 'JUNIOR', 'MID', 'SENIOR', 'EXPERT') NOT NULL,
    budget_min DECIMAL(10,2),
    budget_max DECIMAL(10,2),
    salary_min DECIMAL(10,2),
    salary_max DECIMAL(10,2),
    currency VARCHAR(3),
    start_date DATETIME,
    location VARCHAR(255),
    is_remote BOOLEAN NOT NULL DEFAULT FALSE,
    application_deadline DATETIME,
    status ENUM('ACTIVE', 'CLOSED', 'EXPIRED', 'DRAFT', 'OPEN') NOT NULL DEFAULT 'ACTIVE',
    is_boosted BOOLEAN NOT NULL DEFAULT FALSE,
    boost_expires_at DATETIME,
    total_applications INT DEFAULT 0,
    total_views INT DEFAULT 0,
    is_visible BOOLEAN NOT NULL DEFAULT TRUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (recruiter_id) REFERENCES recruiter_profiles(id) ON DELETE CASCADE
);

-- Job Posting Field Values table
CREATE TABLE job_posting_field_values (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_post_id BIGINT NOT NULL,
    job_posting_field_id BIGINT NOT NULL,
    field_value TEXT,
    file_url VARCHAR(500),
    file_name VARCHAR(255),
    file_size BIGINT,
    mime_type VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (job_post_id) REFERENCES job_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (job_posting_field_id) REFERENCES job_posting_fields(id) ON DELETE CASCADE
);

-- Job Applications table
CREATE TABLE job_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_post_id BIGINT NOT NULL,
    artist_id BIGINT NOT NULL,
    cover_letter TEXT,
    portfolio_url VARCHAR(500),
    resume_url VARCHAR(500),
    proposed_rate DECIMAL(10,2),
    availability_date DATETIME,
    status ENUM('PENDING', 'REVIEWED', 'SHORTLISTED', 'ACCEPTED', 'REJECTED', 'HIRED', 'WITHDRAWN') NOT NULL DEFAULT 'PENDING',
    applied_at DATETIME NOT NULL,
    reviewed_at DATETIME,
    notes TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT unique_application UNIQUE (job_post_id, artist_id),
    FOREIGN KEY (job_post_id) REFERENCES job_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artist_profiles(id) ON DELETE CASCADE
);

-- Bookmarked Jobs table
CREATE TABLE bookmarked_jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_post_id BIGINT NOT NULL,
    artist_id BIGINT NOT NULL,
    bookmarked_at DATETIME NOT NULL,
    notes VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT unique_bookmark UNIQUE (job_post_id, artist_id),
    FOREIGN KEY (job_post_id) REFERENCES job_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artist_profiles(id) ON DELETE CASCADE
);

-- Auditions table
CREATE TABLE auditions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_application_id BIGINT NOT NULL,
    recruiter_id BIGINT NOT NULL,
    artist_id BIGINT NOT NULL,
    audition_type ENUM('LIVE_VIDEO', 'LIVE_AUDIO', 'RECORDED_SUBMISSION', 'IN_PERSON') NOT NULL,
    scheduled_at DATETIME NOT NULL,
    duration_minutes INT,
    meeting_link VARCHAR(500),
    instructions TEXT,
    status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW') NOT NULL DEFAULT 'SCHEDULED',
    completed_at DATETIME,
    feedback TEXT,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    recording_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (job_application_id) REFERENCES job_applications(id) ON DELETE CASCADE,
    FOREIGN KEY (recruiter_id) REFERENCES recruiter_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artist_profiles(id) ON DELETE CASCADE
);

-- Messages table
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    recruiter_id BIGINT,
    artist_id BIGINT,
    content TEXT NOT NULL,
    message_type ENUM('TEXT', 'IMAGE', 'FILE', 'AUDIO', 'VIDEO') NOT NULL DEFAULT 'TEXT',
    sent_at DATETIME NOT NULL,
    read_at DATETIME,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    attachment_url VARCHAR(500),
    is_paid_message BOOLEAN NOT NULL DEFAULT FALSE,
    payment_amount DECIMAL(10,2),
    payment_reference VARCHAR(255),
    is_deleted_by_sender BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted_by_recipient BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recruiter_id) REFERENCES recruiter_profiles(id) ON DELETE SET NULL,
    FOREIGN KEY (artist_id) REFERENCES artist_profiles(id) ON DELETE SET NULL
);

-- Payments table
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subscription_id BIGINT,
    payment_type ENUM('SUBSCRIPTION', 'MESSAGE_UNLOCK', 'JOB_BOOST', 'COMMISSION', 'REFUND') NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    payment_method ENUM('RAZORPAY', 'STRIPE', 'UPI', 'CARD', 'NET_BANKING', 'WALLET') NOT NULL,
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED', 'REFUNDED', 'PARTIALLY_REFUNDED') NOT NULL DEFAULT 'PENDING',
    payment_reference VARCHAR(255) UNIQUE,
    gateway_transaction_id VARCHAR(255),
    gateway_response JSON,
    paid_at DATETIME,
    failure_reason VARCHAR(500),
    refund_amount DECIMAL(10,2),
    refund_reference VARCHAR(255),
    refunded_at DATETIME,
    commission_amount DECIMAL(10,2),
    net_amount DECIMAL(10,2),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL
);

-- OTPs table
CREATE TABLE otps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mobile VARCHAR(15) NOT NULL,
    email VARCHAR(255),
    otp_code VARCHAR(10) NOT NULL,
    otp_type ENUM('LOGIN', 'REGISTRATION', 'FORGOT_PASSWORD', 'VERIFICATION') NOT NULL,
    status ENUM('PENDING', 'VERIFIED', 'EXPIRED', 'FAILED') NOT NULL DEFAULT 'PENDING',
    expires_at DATETIME NOT NULL,
    verified_at DATETIME,
    attempts INT NOT NULL DEFAULT 0,
    max_attempts INT NOT NULL DEFAULT 3,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Audit Logs table
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    old_values JSON,
    new_values JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    session_id VARCHAR(255),
    performed_at DATETIME NOT NULL,
    action_type ENUM('CREATE', 'READ', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'EXPORT', 'IMPORT') NOT NULL,
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Notifications table
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type ENUM('JOB_ALERT', 'APPLICATION_RECEIVED', 'AUDITION_SCHEDULED', 'PAYMENT_SUCCESS', 'SUBSCRIPTION_EXPIRY', 'VERIFICATION_APPROVED', 'MESSAGE_RECEIVED', 'SYSTEM_UPDATE') NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') NOT NULL DEFAULT 'MEDIUM',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at DATETIME,
    sent_at DATETIME NOT NULL,
    action_url VARCHAR(500),
    metadata JSON,
    email_sent BOOLEAN NOT NULL DEFAULT FALSE,
    push_sent BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Account Management Log table
CREATE TABLE account_management_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    admin_id BIGINT NOT NULL,
    action ENUM('ACTIVATE', 'DEACTIVATE', 'SUSPEND', 'UNSUSPEND', 'BAN', 'UNBAN', 'VERIFY', 'UNVERIFY') NOT NULL,
    previous_status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'BANNED', 'PENDING_VERIFICATION') NOT NULL,
    new_status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'BANNED', 'PENDING_VERIFICATION') NOT NULL,
    reason TEXT,
    admin_notes TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Account Status History table
CREATE TABLE account_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'BANNED', 'PENDING_VERIFICATION') NOT NULL,
    changed_by BIGINT NOT NULL,
    reason TEXT,
    notes TEXT,
    effective_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Admin Permissions table
CREATE TABLE admin_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    permission_type ENUM('ACCOUNT_MANAGEMENT', 'USER_MANAGEMENT', 'CONTENT_MODERATION', 'PAYMENT_MANAGEMENT', 'SYSTEM_ADMIN') NOT NULL,
    permission_level ENUM('READ', 'WRITE', 'ADMIN', 'SUPER_ADMIN') NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    granted_by BIGINT NOT NULL,
    granted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (granted_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Plan Features table
CREATE TABLE plan_features (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_plan_id BIGINT NOT NULL,
    feature_name VARCHAR(100) NOT NULL,
    feature_description TEXT,
    feature_type ENUM('LIMIT', 'BOOLEAN', 'NUMBER', 'TEXT') NOT NULL,
    feature_value VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans(id) ON DELETE CASCADE
);

-- Usage Tracking table
CREATE TABLE usage_tracking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subscription_id BIGINT NOT NULL,
    feature_name VARCHAR(100) NOT NULL,
    usage_count INT DEFAULT 1,
    usage_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metadata JSON,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE CASCADE
);

-- Subscription Changes table
CREATE TABLE subscription_changes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    from_plan_id BIGINT,
    to_plan_id BIGINT NOT NULL,
    change_type ENUM('UPGRADE', 'DOWNGRADE', 'RENEWAL', 'CANCELLATION') NOT NULL,
    change_reason TEXT,
    prorated_amount DECIMAL(10,2),
    effective_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (from_plan_id) REFERENCES subscription_plans(id),
    FOREIGN KEY (to_plan_id) REFERENCES subscription_plans(id)
);

-- Add foreign keys for users table
ALTER TABLE users 
ADD CONSTRAINT fk_users_deactivated_by FOREIGN KEY (deactivated_by) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE users 
ADD CONSTRAINT fk_users_reactivated_by FOREIGN KEY (reactivated_by) REFERENCES users(id) ON DELETE SET NULL;

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_mobile ON users(mobile);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_account_status ON users(account_status);
CREATE INDEX idx_users_deactivated_at ON users(deactivated_at);

CREATE INDEX idx_job_posts_recruiter ON job_posts(recruiter_id);
CREATE INDEX idx_job_posts_status ON job_posts(status);
CREATE INDEX idx_job_posts_created_at ON job_posts(created_at);

CREATE INDEX idx_job_applications_job_post ON job_applications(job_post_id);
CREATE INDEX idx_job_applications_artist ON job_applications(artist_id);
CREATE INDEX idx_job_applications_status ON job_applications(status);

CREATE INDEX idx_messages_sender ON messages(sender_id);
CREATE INDEX idx_messages_recipient ON messages(recipient_id);
CREATE INDEX idx_messages_sent_at ON messages(sent_at);

CREATE INDEX idx_payments_user ON payments(user_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_payment_reference ON payments(payment_reference);

CREATE INDEX idx_otps_mobile ON otps(mobile);
CREATE INDEX idx_otps_status ON otps(status);
CREATE INDEX idx_otps_expires_at ON otps(expires_at);

CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_performed_at ON audit_logs(performed_at);

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);

CREATE INDEX idx_artist_types_name ON artist_types(name);
CREATE INDEX idx_artist_types_active ON artist_types(is_active);
CREATE INDEX idx_artist_types_sort_order ON artist_types(sort_order);

CREATE INDEX idx_artist_type_fields_type ON artist_type_fields(artist_type_id);
CREATE INDEX idx_artist_type_fields_name ON artist_type_fields(field_name);
CREATE INDEX idx_artist_type_fields_searchable ON artist_type_fields(is_searchable);
CREATE INDEX idx_artist_type_fields_sort_order ON artist_type_fields(sort_order);

CREATE INDEX idx_artist_profiles_type ON artist_profiles(artist_type_id);
CREATE INDEX idx_artist_profiles_verified ON artist_profiles(is_verified_badge);

CREATE INDEX idx_artist_profile_fields_profile ON artist_profile_fields(artist_profile_id);
CREATE INDEX idx_artist_profile_fields_type_field ON artist_profile_fields(artist_type_field_id);

CREATE INDEX idx_account_management_log_user_id ON account_management_log(user_id);
CREATE INDEX idx_account_management_log_admin_id ON account_management_log(admin_id);
CREATE INDEX idx_account_management_log_action ON account_management_log(action);

CREATE INDEX idx_account_status_history_user_id ON account_status_history(user_id);

CREATE INDEX idx_admin_permissions_user_id ON admin_permissions(user_id);
CREATE INDEX idx_admin_permissions_permission_type ON admin_permissions(permission_type);
