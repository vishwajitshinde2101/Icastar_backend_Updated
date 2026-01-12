package com.icastar.platform.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Temporary migration to fix default values for boolean columns.
 * This can be removed after running once successfully.
 */
@Component
@Slf4j
public class DatabaseMigration {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void migrate() {
        try {
            log.info("Running database migration to fix default values...");

            // Fix is_active column
            jdbcTemplate.execute("ALTER TABLE users MODIFY COLUMN is_active BOOLEAN NOT NULL DEFAULT true");
            log.info("Updated is_active column");

            // Fix is_verified column
            jdbcTemplate.execute("ALTER TABLE users MODIFY COLUMN is_verified BOOLEAN NOT NULL DEFAULT false");
            log.info("Updated is_verified column");

            // Fix is_onboarding_complete column
            jdbcTemplate.execute("ALTER TABLE users MODIFY COLUMN is_onboarding_complete BOOLEAN NOT NULL DEFAULT false");
            log.info("Updated is_onboarding_complete column");

            // Update any NULL values
            jdbcTemplate.execute("UPDATE users SET is_active = true WHERE is_active IS NULL");
            jdbcTemplate.execute("UPDATE users SET is_verified = false WHERE is_verified IS NULL");
            jdbcTemplate.execute("UPDATE users SET is_onboarding_complete = false WHERE is_onboarding_complete IS NULL");

            log.info("Database migration completed successfully!");
            log.warn("IMPORTANT: You can now delete this DatabaseMigration.java file");

        } catch (Exception e) {
            log.error("Migration failed (this might be normal if already applied): {}", e.getMessage());
        }
    }
}
