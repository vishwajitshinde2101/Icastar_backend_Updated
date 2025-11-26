package com.icastar.platform.constants;

/**
 * Application-wide constants to replace hardcoded strings
 */
public final class ApplicationConstants {

    private ApplicationConstants() {
        // Utility class - prevent instantiation
    }

    // ==================== ERROR MESSAGES ====================
    public static final class ErrorMessages {
        public static final String JOB_NOT_FOUND = "Job not found";
        public static final String RECRUITER_PROFILE_NOT_FOUND = "Recruiter profile not found";
        public static final String ARTIST_PROFILE_NOT_FOUND = "Artist profile not found";
        public static final String USER_NOT_FOUND = "User not found";
        public static final String ACCESS_DENIED = "Access denied to this job";
        public static final String INVALID_CREDENTIALS = "Invalid credentials";
        public static final String ACCOUNT_LOCKED = "Account is locked";
        public static final String ACCOUNT_INACTIVE = "Account is inactive";
        public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
        public static final String MOBILE_ALREADY_EXISTS = "Mobile number already exists";
        public static final String INVALID_TOKEN = "Invalid or expired token";
        public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
        public static final String VALIDATION_FAILED = "Validation failed";
        public static final String OPERATION_FAILED = "Operation failed";
        public static final String RESOURCE_NOT_FOUND = "Resource not found";
        public static final String DUPLICATE_ENTRY = "Duplicate entry";
        public static final String CONSTRAINT_VIOLATION = "Constraint violation";
    }

    // ==================== STATUS VALUES ====================
    public static final class Status {
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";
        public static final String PENDING = "PENDING";
        public static final String COMPLETED = "COMPLETED";
        public static final String FAILED = "FAILED";
        public static final String CANCELLED = "CANCELLED";
        public static final String SUSPENDED = "SUSPENDED";
        public static final String BANNED = "BANNED";
        public static final String PENDING_VERIFICATION = "PENDING_VERIFICATION";
        public static final String SENT = "SENT";
        public static final String DELIVERED = "DELIVERED";
        public static final String RETRYING = "RETRYING";
    }

    // ==================== USER ROLES ====================
    public static final class UserRoles {
        public static final String ADMIN = "ADMIN";
        public static final String ARTIST = "ARTIST";
        public static final String RECRUITER = "RECRUITER";
    }

    // ==================== JOB STATUS ====================
    public static final class JobStatus {
        public static final String ACTIVE = "ACTIVE";
        public static final String CLOSED = "CLOSED";
        public static final String DRAFT = "DRAFT";
        public static final String EXPIRED = "EXPIRED";
        public static final String PAUSED = "PAUSED";
    }

    // ==================== JOB TYPES ====================
    public static final class JobTypes {
        public static final String FULL_TIME = "FULL_TIME";
        public static final String PART_TIME = "PART_TIME";
        public static final String CONTRACT = "CONTRACT";
        public static final String FREELANCE = "FREELANCE";
        public static final String INTERNSHIP = "INTERNSHIP";
    }

    // ==================== EXPERIENCE LEVELS ====================
    public static final class ExperienceLevels {
        public static final String ENTRY = "ENTRY";
        public static final String JUNIOR = "JUNIOR";
        public static final String MID = "MID";
        public static final String SENIOR = "SENIOR";
        public static final String EXPERT = "EXPERT";
    }

    // ==================== COMMUNICATION TYPES ====================
    public static final class CommunicationTypes {
        public static final String EMAIL = "EMAIL";
        public static final String SMS = "SMS";
        public static final String PUSH_NOTIFICATION = "PUSH_NOTIFICATION";
        public static final String IN_APP_NOTIFICATION = "IN_APP_NOTIFICATION";
    }

    // ==================== PERMISSION TYPES ====================
    public static final class PermissionTypes {
        public static final String ACCOUNT_MANAGEMENT = "ACCOUNT_MANAGEMENT";
        public static final String USER_MANAGEMENT = "USER_MANAGEMENT";
        public static final String CONTENT_MODERATION = "CONTENT_MODERATION";
        public static final String PAYMENT_MANAGEMENT = "PAYMENT_MANAGEMENT";
        public static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";
    }

    // ==================== PERMISSION LEVELS ====================
    public static final class PermissionLevels {
        public static final String SUPER_ADMIN = "SUPER_ADMIN";
        public static final String ADMIN = "ADMIN";
        public static final String MODERATOR = "MODERATOR";
        public static final String VIEWER = "VIEWER";
    }

    // ==================== SUBSCRIPTION STATUS ====================
    public static final class SubscriptionStatus {
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";
        public static final String EXPIRED = "EXPIRED";
        public static final String CANCELLED = "CANCELLED";
        public static final String PENDING = "PENDING";
    }

    // ==================== PAYMENT STATUS ====================
    public static final class PaymentStatus {
        public static final String PENDING = "PENDING";
        public static final String COMPLETED = "COMPLETED";
        public static final String FAILED = "FAILED";
        public static final String REFUNDED = "REFUNDED";
        public static final String CANCELLED = "CANCELLED";
    }

    // ==================== PAYMENT METHODS ====================
    public static final class PaymentMethods {
        public static final String CREDIT_CARD = "CREDIT_CARD";
        public static final String DEBIT_CARD = "DEBIT_CARD";
        public static final String BANK_TRANSFER = "BANK_TRANSFER";
        public static final String UPI = "UPI";
        public static final String WALLET = "WALLET";
    }

    // ==================== ARTIST TYPES ====================
    public static final class ArtistTypes {
        public static final String ACTOR = "ACTOR";
        public static final String SINGER = "SINGER";
        public static final String MUSICIAN = "MUSICIAN";
        public static final String DANCER = "DANCER";
        public static final String WRITER = "WRITER";
        public static final String DIRECTOR = "DIRECTOR";
        public static final String PRODUCER = "PRODUCER";
        public static final String COMPOSER = "COMPOSER";
        public static final String CHOREOGRAPHER = "CHOREOGRAPHER";
    }

    // ==================== RECRUITER CATEGORIES ====================
    public static final class RecruiterCategories {
        public static final String PRODUCTION_HOUSE = "PRODUCTION_HOUSE";
        public static final String CASTING_DIRECTOR = "CASTING_DIRECTOR";
        public static final String INDIVIDUAL_RECRUITER = "INDIVIDUAL_RECRUITER";
        public static final String AGENCY = "AGENCY";
    }

    // ==================== APPLICATION STATUS ====================
    public static final class ApplicationStatus {
        public static final String PENDING = "PENDING";
        public static final String REVIEWED = "REVIEWED";
        public static final String SHORTLISTED = "SHORTLISTED";
        public static final String REJECTED = "REJECTED";
        public static final String ACCEPTED = "ACCEPTED";
        public static final String WITHDRAWN = "WITHDRAWN";
    }

    // ==================== BOOKMARK STATUS ====================
    public static final class BookmarkStatus {
        public static final String ACTIVE = "ACTIVE";
        public static final String REMOVED = "REMOVED";
    }

    // ==================== DEFAULT VALUES ====================
    public static final class DefaultValues {
        public static final String SAMPLE_ARTIST_NAME = "Sample Artist";
        public static final String SAMPLE_ARTIST_EMAIL = "artist@example.com";
        public static final String SAMPLE_ARTIST_CATEGORY = "Dancer";
        public static final String DEFAULT_CURRENCY = "USD";
        public static final String DEFAULT_LOCATION = "Remote";
        public static final String DEFAULT_WORK_SCHEDULE = "9-5";
        public static final String DEFAULT_PERFORMANCE_RATING = "5";
        public static final String DEFAULT_CONTRACT_TYPE = "FULL_TIME";
        public static final String DEFAULT_HIRE_STATUS = "COMPLETED";
        public static final String DEFAULT_SUBSCRIPTION_PLAN = "Free";
        public static final String DEFAULT_FEEDBACK = "Excellent work";
        public static final String SKILLS_MATCH_REASON = "Skills match";
        public static final String LOCATION_MATCH_REASON = "Location match";
        public static final String EXPERIENCE_MATCH_REASON = "Experience level match";
        public static final String BASIC_FEATURE_1 = "Basic job posting";
        public static final String BASIC_FEATURE_2 = "View applications";
        public static final String ADVANCED_FEATURE_1 = "Advanced job posting";
        public static final String ADVANCED_FEATURE_2 = "Artist suggestions";
        public static final String ADVANCED_FEATURE_3 = "Boost jobs";
        public static final String ADVANCED_FEATURE_4 = "Analytics";
    }

    // ==================== FIELD TYPES ====================
    public static final class FieldTypes {
        public static final String TEXT = "TEXT";
        public static final String EMAIL = "EMAIL";
        public static final String PHONE = "PHONE";
        public static final String NUMBER = "NUMBER";
        public static final String DATE = "DATE";
        public static final String SELECT = "SELECT";
        public static final String MULTI_SELECT = "MULTI_SELECT";
        public static final String TEXTAREA = "TEXTAREA";
        public static final String CHECKBOX = "CHECKBOX";
        public static final String RADIO = "RADIO";
    }

    // ==================== BUSINESS LOGIC CONSTANTS ====================
    public static final class BusinessLogic {
        public static final int DEFAULT_MAX_RETRIES = 3;
        public static final int DEFAULT_RETRY_DELAY_MINUTES = 5;
        public static final int DEFAULT_SESSION_TIMEOUT_MINUTES = 30;
        public static final int DEFAULT_PASSWORD_MIN_LENGTH = 8;
        public static final int DEFAULT_OTP_LENGTH = 6;
        public static final int DEFAULT_OTP_EXPIRY_MINUTES = 10;
        public static final int DEFAULT_TOKEN_EXPIRY_HOURS = 24;
        public static final double DEFAULT_MATCH_SCORE_THRESHOLD = 0.3;
        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int DEFAULT_MAX_PAGE_SIZE = 100;
    }

    // ==================== API CONSTANTS ====================
    public static final class ApiConstants {
        public static final String API_BASE_PATH = "/api";
        public static final String VERSION_V1 = "/v1";
        public static final String CONTENT_TYPE_JSON = "application/json";
        public static final String AUTHORIZATION_HEADER = "Authorization";
        public static final String BEARER_PREFIX = "Bearer ";
    }

    // ==================== DATABASE CONSTANTS ====================
    public static final class DatabaseConstants {
        public static final String DEFAULT_SCHEMA = "icastar_dev";
        public static final String FLYWAY_TABLE = "flyway_schema_history";
        public static final String UNIQUE_CONSTRAINT_PREFIX = "unique_";
        public static final String FOREIGN_KEY_PREFIX = "fk_";
        public static final String INDEX_PREFIX = "idx_";
    }

    // ==================== VALIDATION CONSTANTS ====================
    public static final class ValidationConstants {
        public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        public static final String PHONE_REGEX = "^[+]?[0-9]{10,15}$";
        public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        public static final int MIN_NAME_LENGTH = 2;
        public static final int MAX_NAME_LENGTH = 100;
        public static final int MIN_DESCRIPTION_LENGTH = 10;
        public static final int MAX_DESCRIPTION_LENGTH = 5000;
    }
}
