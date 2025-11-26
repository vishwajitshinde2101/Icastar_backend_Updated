-- Flyway Migration: V2 - Initial Data Seeding

-- Insert default admin user (password: admin123)
INSERT IGNORE INTO users (id, email, mobile, password, role, status, is_verified) VALUES
(1, 'admin@icastar.com', '+919876543210', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN', 'ACTIVE', TRUE);

-- Insert basic artist types
INSERT IGNORE INTO artist_types (id, name, display_name, description, icon_url, is_active, sort_order) VALUES
(1, 'DANCER', 'Dancer', 'Professional Dancers specializing in various dance styles.', '/icons/dancer.png', TRUE, 2),
(2, 'SINGER', 'Singer', 'Professional Singers specializing in various music genres.', '/icons/singer.png', TRUE, 3),
(3, 'DIRECTOR', 'Director', 'Professional Directors specializing in various film and video production.', '/icons/director.png', TRUE, 4),
(4, 'WRITER', 'Writer', 'Professional Writers specializing in various writing forms.', '/icons/writer.png', TRUE, 5);

-- Insert recruiter categories
INSERT IGNORE INTO recruiter_categories (id, name, display_name, description, icon_url, sort_order) VALUES
(1, 'PRODUCTION_HOUSE', 'Production House', 'Film and TV production companies, studios, and production houses', '/icons/production-house.png', 1),
(2, 'CASTING_DIRECTOR', 'Casting Director', 'Professional casting directors working for production houses', '/icons/casting-director.png', 2),
(3, 'INDIVIDUAL', 'Individual Recruiter', 'Entrepreneurs, event managers, ad agencies, and individual recruiters', '/icons/individual-recruiter.png', 3);

-- Insert subscription plans
INSERT IGNORE INTO subscription_plans (name, description, plan_type, user_role, price, billing_cycle, max_auditions, unlimited_auditions, max_applications, unlimited_applications, max_portfolio_items, unlimited_portfolio, profile_verification, featured_profile, advanced_analytics, max_file_uploads, unlimited_uploads, max_file_size_mb, sort_order) VALUES
('Artist Free', 'Basic plan for new artists to get started', 'FREE', 'ARTIST', 0.00, 'MONTHLY', 5, FALSE, 10, FALSE, 5, FALSE, FALSE, FALSE, FALSE, 10, FALSE, 10, 1),
('Artist Basic', 'Essential features for growing artists', 'BASIC', 'ARTIST', 299.00, 'MONTHLY', 25, FALSE, 50, FALSE, 20, FALSE, TRUE, FALSE, FALSE, 50, FALSE, 25, 2),
('Artist Premium', 'Advanced features for professional artists', 'PREMIUM', 'ARTIST', 599.00, 'MONTHLY', 100, FALSE, 200, FALSE, 100, FALSE, TRUE, TRUE, TRUE, 200, FALSE, 50, 3),
('Artist Professional', 'Complete toolkit for established artists', 'PROFESSIONAL', 'ARTIST', 999.00, 'MONTHLY', 500, FALSE, 1000, FALSE, 500, FALSE, TRUE, TRUE, TRUE, 1000, FALSE, 100, 4),
('Artist Enterprise', 'Unlimited access for top-tier artists', 'ENTERPRISE', 'ARTIST', 1999.00, 'MONTHLY', 0, TRUE, 0, TRUE, 0, TRUE, TRUE, TRUE, TRUE, 0, TRUE, 500, 5);

INSERT IGNORE INTO subscription_plans (name, description, plan_type, user_role, price, billing_cycle, max_job_posts, unlimited_job_posts, max_messages, unlimited_messages, max_candidates_view, unlimited_candidates, job_boost_credits, advanced_search, candidate_verification, priority_support, max_file_uploads, unlimited_uploads, max_file_size_mb, sort_order) VALUES
('Recruiter Free', 'Basic plan for new recruiters', 'FREE', 'RECRUITER', 0.00, 'MONTHLY', 2, FALSE, 10, FALSE, 20, FALSE, 0, FALSE, FALSE, FALSE, 10, FALSE, 10, 6),
('Recruiter Basic', 'Essential features for growing recruiters', 'BASIC', 'RECRUITER', 499.00, 'MONTHLY', 10, FALSE, 50, FALSE, 100, FALSE, 2, FALSE, FALSE, FALSE, 50, FALSE, 25, 7),
('Recruiter Premium', 'Advanced features for professional recruiters', 'PREMIUM', 'RECRUITER', 999.00, 'MONTHLY', 50, FALSE, 200, FALSE, 500, FALSE, 5, TRUE, TRUE, TRUE, 200, FALSE, 50, 8),
('Recruiter Professional', 'Complete toolkit for established recruiters', 'PROFESSIONAL', 'RECRUITER', 1999.00, 'MONTHLY', 200, FALSE, 1000, FALSE, 2000, FALSE, 10, TRUE, TRUE, TRUE, 1000, FALSE, 100, 9),
('Recruiter Enterprise', 'Unlimited access for large organizations', 'ENTERPRISE', 'RECRUITER', 4999.00, 'MONTHLY', 0, TRUE, 0, TRUE, 0, TRUE, 25, TRUE, TRUE, TRUE, 0, TRUE, 500, 10);

INSERT IGNORE INTO subscription_plans (name, description, plan_type, user_role, price, billing_cycle, max_auditions, unlimited_auditions, max_applications, unlimited_applications, max_job_posts, unlimited_job_posts, max_messages, unlimited_messages, max_candidates_view, unlimited_candidates, job_boost_credits, advanced_search, profile_verification, featured_profile, priority_support, max_file_uploads, unlimited_uploads, max_file_size_mb, sort_order) VALUES
('Unified Basic', 'Basic features for both artists and recruiters', 'BASIC', 'BOTH', 699.00, 'MONTHLY', 15, FALSE, 30, FALSE, 5, FALSE, 30, FALSE, 50, FALSE, 1, FALSE, FALSE, FALSE, FALSE, 30, FALSE, 25, 11),
('Unified Premium', 'Advanced features for both roles', 'PREMIUM', 'BOTH', 1299.00, 'MONTHLY', 50, FALSE, 100, FALSE, 25, FALSE, 100, FALSE, 250, FALSE, 3, TRUE, TRUE, TRUE, TRUE, 100, FALSE, 50, 12),
('Unified Professional', 'Complete toolkit for professionals', 'PROFESSIONAL', 'BOTH', 2499.00, 'MONTHLY', 250, FALSE, 500, FALSE, 100, FALSE, 500, FALSE, 1000, FALSE, 8, TRUE, TRUE, TRUE, TRUE, 500, FALSE, 100, 13),
('Unified Enterprise', 'Unlimited access for organizations', 'ENTERPRISE', 'BOTH', 6999.00, 'MONTHLY', 0, TRUE, 0, TRUE, 0, TRUE, 0, TRUE, 0, TRUE, 20, TRUE, TRUE, TRUE, TRUE, 0, TRUE, 500, 14);

-- DANCER FIELDS (Artist Type ID: 1)
INSERT IGNORE INTO artist_type_fields (artist_type_id, field_name, display_name, field_type, is_required, is_searchable, sort_order) VALUES
(1, 'bio', 'Brief Yourself', 'TEXTAREA', 0, 1, 1),
(1, 'dance_style', 'Your Unique Dance Style', 'TEXT', 1, 1, 2),
(1, 'experience_years', 'Choreography Experience', 'NUMBER', 0, 1, 3),
(1, 'training', 'Training', 'TEXTAREA', 0, 1, 4),
(1, 'achievements', 'Achievements', 'TEXTAREA', 0, 1, 5),
(1, 'personal_style', 'Personal Style / Approach', 'TEXTAREA', 0, 1, 6),
(1, 'skills_strengths', 'Skills and Strengths', 'TEXTAREA', 0, 1, 7),
(1, 'dancing_video', 'Upload Dancing Video', 'FILE', 0, 0, 8),
(1, 'drive_link', 'Upload Drive Link', 'URL', 0, 0, 9),
(1, 'face_verification', 'Face Verification', 'BOOLEAN', 0, 0, 10);

-- SINGER FIELDS (Artist Type ID: 2)
INSERT IGNORE INTO artist_type_fields (artist_type_id, field_name, display_name, field_type, is_required, is_searchable, sort_order, options) VALUES
(2, 'genre', 'Select your Genre', 'MULTI_SELECT', 1, 1, 1, JSON_ARRAY('Classical','Pop','Rock','Jazz','Hip-Hop','Folk','Country','R&B','Devotional','Bollywood','Indie','Alternative','Sufi','Qawwali','Ghazal','Playback','Opera')),
(2, 'vocal_range', 'Select your Vocal Range', 'SELECT', 1, 1, 2, JSON_ARRAY('Tenor','Baritone','Bass','Soprano','Mezzo-soprano','Alto','Countertenor','Contralto')),
(2, 'languages', 'Languages you can sing', 'MULTI_SELECT', 1, 1, 3, JSON_ARRAY('Hindi','English','Marathi','Tamil','Telugu','Punjabi','Gujarati','Kannada','Bengali','Malayalam','Assamese','Odia','Bhojpuri','Rajasthani','Urdu','Sanskrit'));

-- PRODUCTION HOUSE FIELDS (Recruiter Category ID: 1)
INSERT IGNORE INTO recruiter_category_fields (recruiter_category_id, field_name, display_name, field_type, is_required, is_searchable, sort_order, placeholder, help_text) VALUES
(1, 'production_house_name', 'Name of Production House', 'TEXT', TRUE, TRUE, 1, 'Enter production house name', 'Official name of your production house or studio'),
(1, 'recruiter_name', 'Name of Recruiter', 'TEXT', TRUE, TRUE, 2, 'Enter your full name', 'Your name as the recruiter/contact person');

-- CASTING DIRECTOR FIELDS (Recruiter Category ID: 2)
INSERT IGNORE INTO recruiter_category_fields (recruiter_category_id, field_name, display_name, field_type, is_required, is_searchable, sort_order, placeholder, help_text) VALUES
(2, 'recruiter_name', 'Name of Recruiter', 'TEXT', TRUE, TRUE, 1, 'Enter your full name', 'Your name as the casting director'),
(2, 'location', 'Location', 'TEXT', TRUE, TRUE, 2, 'Enter city, state, country', 'Where are you based?');

-- INDIVIDUAL RECRUITER FIELDS (Recruiter Category ID: 3)
INSERT IGNORE INTO recruiter_category_fields (recruiter_category_id, field_name, display_name, field_type, is_required, is_searchable, sort_order, placeholder, help_text) VALUES
(3, 'name', 'Name', 'TEXT', TRUE, TRUE, 1, 'Enter your full name', 'Your full name'),
(3, 'location', 'Location', 'TEXT', TRUE, TRUE, 2, 'Enter city, state, country', 'Where are you based?');

-- JOB POSTING FIELDS
INSERT IGNORE INTO job_posting_fields (field_name, display_name, field_type, is_required, is_searchable, sort_order, options, placeholder, help_text) VALUES
('artist_category', 'Category of Artist', 'MULTI_SELECT', TRUE, TRUE, 1, JSON_ARRAY('Actor', 'Singer', 'Musician', 'Writer', 'Dancer'), 'Select artist categories', 'What type of artists are you looking for?'),
('project_type', 'Type of Your Project', 'SELECT', TRUE, TRUE, 2, JSON_ARRAY('Film', 'Television', 'Web Series', 'Commercial'), 'Select project type', 'What type of project is this?');

-- Insert default admin permissions
INSERT IGNORE INTO admin_permissions (user_id, permission_type, permission_level, granted_by) VALUES
(1, 'ACCOUNT_MANAGEMENT', 'SUPER_ADMIN', 1),
(1, 'USER_MANAGEMENT', 'SUPER_ADMIN', 1),
(1, 'CONTENT_MODERATION', 'SUPER_ADMIN', 1),
(1, 'PAYMENT_MANAGEMENT', 'SUPER_ADMIN', 1),
(1, 'SYSTEM_ADMIN', 'SUPER_ADMIN', 1);

-- Update existing users to have proper account status
UPDATE users SET account_status = 'ACTIVE' WHERE account_status IS NULL;
UPDATE users SET last_activity = updated_at WHERE last_activity IS NULL;
