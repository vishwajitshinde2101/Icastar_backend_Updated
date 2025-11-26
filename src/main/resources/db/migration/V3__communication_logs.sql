-- Create communication_logs table
CREATE TABLE communication_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    communication_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    recipient_email VARCHAR(255),
    recipient_mobile VARCHAR(20),
    subject VARCHAR(500),
    message TEXT,
    template_name VARCHAR(100),
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    failed_at TIMESTAMP,
    error_message TEXT,
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    next_retry_at TIMESTAMP,
    external_id VARCHAR(255),
    user_id BIGINT,
    metadata TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_communication_type (communication_type),
    INDEX idx_status (status),
    INDEX idx_recipient_email (recipient_email),
    INDEX idx_recipient_mobile (recipient_mobile),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_sent_at (sent_at),
    INDEX idx_external_id (external_id),
    INDEX idx_retryable (status, retry_count, next_retry_at)
);

-- Add comments for documentation
ALTER TABLE communication_logs COMMENT = 'Logs all email, SMS, and other communications sent by the system';

-- Insert some sample data for testing (optional)
-- INSERT INTO communication_logs (communication_type, status, recipient_email, subject, message, template_name, sent_at) VALUES
-- ('EMAIL', 'SENT', 'test@example.com', 'Test Email', 'This is a test email', 'TEST_TEMPLATE', NOW()),
-- ('SMS', 'DELIVERED', NULL, 'Test SMS', 'This is a test SMS', 'TEST_SMS_TEMPLATE', NOW());
