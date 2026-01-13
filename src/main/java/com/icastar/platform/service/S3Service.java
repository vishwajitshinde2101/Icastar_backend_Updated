package com.icastar.platform.service;

import com.icastar.platform.dto.artist.CreateArtistProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name:icastar-uploads}")
    private String bucketName;

    @Value("${aws.region:ap-south-1}")
    private String awsRegion;

    @Value("${aws.s3.presigned-url-expiration:3600}")
    private int presignedUrlExpiration;

    /**
     * Upload types supported by the system
     */
    public enum UploadType {
        PROFILE_PHOTO("profiles"),
        COVER_PHOTO("profiles"),
        ID_PROOF("documents"),
        AUDITION_VIDEO("auditions"),
        AUDITION_THUMBNAIL("auditions"),
        PORTFOLIO_PHOTO("portfolio/photos"),
        PORTFOLIO_VIDEO("portfolio/videos");

        private final String folder;

        UploadType(String folder) {
            this.folder = folder;
        }

        public String getFolder() {
            return folder;
        }
    }

    // ==================== PRESIGNED URL METHODS ====================

    /**
     * Generate a pre-signed URL for direct upload to S3
     *
     * @param userId     User ID (artist or recruiter)
     * @param fileName   Original file name
     * @param fileType   MIME type (e.g., image/jpeg)
     * @param uploadType Type of upload (PROFILE_PHOTO, COVER_PHOTO, etc.)
     * @param auditionId Optional audition ID for audition uploads
     * @return Map containing presignedUrl, fileUrl, uploadId, expiresIn
     */
    public Map<String, Object> generatePresignedUrl(Long userId, String fileName, String fileType,
                                                     UploadType uploadType, Long auditionId) {
        try {
            // Generate unique file name
            String timestamp = String.valueOf(System.currentTimeMillis());
            String uploadId = UUID.randomUUID().toString();
            String extension = getFileExtension(fileName);

            // Build S3 key based on upload type
            String s3Key = buildS3Key(userId, uploadType, auditionId, timestamp, extension);

            // Create presigned URL request
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(fileType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            // Build file URL (for storing in database after upload)
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, awsRegion, s3Key);

            Map<String, Object> result = new HashMap<>();
            result.put("presignedUrl", presignedRequest.url().toString());
            result.put("fileUrl", fileUrl);
            result.put("s3Key", s3Key);
            result.put("uploadId", uploadId);
            result.put("expiresIn", presignedUrlExpiration);

            log.info("Generated presigned URL for user {} - type: {}, key: {}", userId, uploadType, s3Key);
            return result;

        } catch (Exception e) {
            log.error("Error generating presigned URL for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate upload URL: " + e.getMessage());
        }
    }

    /**
     * Build S3 key based on upload type
     */
    private String buildS3Key(Long userId, UploadType uploadType, Long auditionId,
                               String timestamp, String extension) {
        String folder = uploadType.getFolder();

        switch (uploadType) {
            case PROFILE_PHOTO:
                return String.format("%s/%d/photo_%s.%s", folder, userId, timestamp, extension);
            case COVER_PHOTO:
                return String.format("%s/%d/cover_%s.%s", folder, userId, timestamp, extension);
            case ID_PROOF:
                return String.format("%s/%d/id-proof_%s.%s", folder, userId, timestamp, extension);
            case AUDITION_VIDEO:
                return String.format("%s/%d/%d_video_%s.%s", folder, userId, auditionId != null ? auditionId : 0, timestamp, extension);
            case AUDITION_THUMBNAIL:
                return String.format("%s/%d/%d_thumbnail_%s.%s", folder, userId, auditionId != null ? auditionId : 0, timestamp, extension);
            case PORTFOLIO_PHOTO:
                return String.format("artists/%d/%s/%s_%s.%s", userId, folder, "photo", timestamp, extension);
            case PORTFOLIO_VIDEO:
                return String.format("artists/%d/%s/%s_%s.%s", userId, folder, "video", timestamp, extension);
            default:
                return String.format("uploads/%d/%s_%s.%s", userId, uploadType.name().toLowerCase(), timestamp, extension);
        }
    }

    /**
     * Delete a file from S3
     */
    public void deleteFileFromS3(String fileUrl) {
        try {
            String s3Key = extractS3KeyFromUrl(fileUrl);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("Deleted file from S3: {}", s3Key);

        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage(), e);
            // Don't throw - file deletion failures shouldn't block other operations
        }
    }

    /**
     * Extract S3 key from full URL
     */
    private String extractS3KeyFromUrl(String fileUrl) {
        // URL format: https://bucket.s3.region.amazonaws.com/key
        String prefix = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, awsRegion);
        if (fileUrl.startsWith(prefix)) {
            return fileUrl.substring(prefix.length());
        }
        // Alternative URL format: https://s3.region.amazonaws.com/bucket/key
        String altPrefix = String.format("https://s3.%s.amazonaws.com/%s/", awsRegion, bucketName);
        if (fileUrl.startsWith(altPrefix)) {
            return fileUrl.substring(altPrefix.length());
        }
        throw new RuntimeException("Invalid S3 URL format: " + fileUrl);
    }

    /**
     * Get file extension from file name
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "bin";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Validate file type based on upload type
     */
    public boolean isValidFileType(String fileType, UploadType uploadType) {
        if (fileType == null) return false;

        switch (uploadType) {
            case PROFILE_PHOTO:
            case COVER_PHOTO:
            case AUDITION_THUMBNAIL:
            case PORTFOLIO_PHOTO:
                return fileType.startsWith("image/");
            case ID_PROOF:
                return fileType.startsWith("image/") || fileType.equals("application/pdf");
            case AUDITION_VIDEO:
            case PORTFOLIO_VIDEO:
                return fileType.startsWith("video/");
            default:
                return true;
        }
    }

    /**
     * Get maximum file size in bytes based on upload type
     */
    public long getMaxFileSize(UploadType uploadType) {
        switch (uploadType) {
            case PROFILE_PHOTO:
            case COVER_PHOTO:
            case AUDITION_THUMBNAIL:
                return 5 * 1024 * 1024; // 5 MB
            case ID_PROOF:
            case PORTFOLIO_PHOTO:
                return 10 * 1024 * 1024; // 10 MB
            case AUDITION_VIDEO:
            case PORTFOLIO_VIDEO:
                return 100 * 1024 * 1024; // 100 MB
            default:
                return 10 * 1024 * 1024; // 10 MB default
        }
    }

    // ==================== LEGACY MULTIPART UPLOAD METHODS ====================

    /**
     * Upload a single file to S3 (legacy - uses simulated upload)
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            String fileName = generateFileName(file.getOriginalFilename());
            String url = String.format("https://%s.s3.%s.amazonaws.com/%s/%s",
                bucketName, awsRegion, folder, fileName);
            log.info("File upload simulated: {} -> {}", file.getOriginalFilename(), url);
            return url;
        } catch (Exception e) {
            log.error("Error uploading file to S3", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * Upload multiple files to S3
     */
    public List<String> uploadFiles(List<MultipartFile> files, String folder) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String url = uploadFile(file, folder);
                urls.add(url);
            }
        }
        return urls;
    }

    /**
     * Upload profile pictures with specific types
     */
    public List<CreateArtistProfileDto.ProfilePictureDto> uploadProfilePictures(
            List<MultipartFile> files,
            List<CreateArtistProfileDto.ProfilePictureDto.ProfilePictureType> types,
            String folder) {

        List<CreateArtistProfileDto.ProfilePictureDto> profilePictures = new ArrayList<>();

        for (int i = 0; i < files.size() && i < types.size(); i++) {
            MultipartFile file = files.get(i);
            CreateArtistProfileDto.ProfilePictureDto.ProfilePictureType type = types.get(i);

            if (!file.isEmpty()) {
                String url = uploadFile(file, folder);
                CreateArtistProfileDto.ProfilePictureDto pictureDto = new CreateArtistProfileDto.ProfilePictureDto();
                pictureDto.setUrl(url);
                pictureDto.setType(type);
                profilePictures.add(pictureDto);
            }
        }

        return profilePictures;
    }

    /**
     * Upload artist portfolio photo
     */
    public String uploadArtistPortfolioPhoto(MultipartFile file, Long artistId) {
        validateImageFile(file);
        String folder = String.format("artists/%d/portfolio/photos", artistId);
        log.info("Uploading portfolio photo for artist {}: {}", artistId, file.getOriginalFilename());
        return uploadFile(file, folder);
    }

    /**
     * Upload artist portfolio video
     */
    public String uploadArtistPortfolioVideo(MultipartFile file, Long artistId) {
        validateVideoFile(file);
        String folder = String.format("artists/%d/portfolio/videos", artistId);
        log.info("Uploading portfolio video for artist {}: {}", artistId, file.getOriginalFilename());
        return uploadFile(file, folder);
    }

    /**
     * Upload artist profile image
     */
    public String uploadArtistProfileImage(MultipartFile file, Long artistId) {
        validateImageFile(file);
        String folder = String.format("artists/%d/profile", artistId);
        log.info("Uploading profile image for artist {}: {}", artistId, file.getOriginalFilename());
        return uploadFile(file, folder);
    }

    /**
     * Upload document
     */
    public String uploadDocument(MultipartFile file, String documentType, Long actorId) {
        String folder = "actors/" + actorId + "/documents/" + documentType;
        return uploadFile(file, folder);
    }

    /**
     * Delete file (legacy method)
     */
    public void deleteFile(String s3Url) {
        try {
            deleteFileFromS3(s3Url);
        } catch (Exception e) {
            log.error("Error deleting file from S3", e);
        }
    }

    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Image file size must not exceed 10MB");
        }
    }

    private void validateVideoFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new IllegalArgumentException("File must be a video");
        }
        if (file.getSize() > 100 * 1024 * 1024) {
            throw new IllegalArgumentException("Video file size must not exceed 100MB");
        }
    }
}
