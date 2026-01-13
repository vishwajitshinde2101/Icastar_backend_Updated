package com.icastar.platform.controller;

import com.icastar.platform.entity.User;
import com.icastar.platform.service.S3Service;
import com.icastar.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Upload", description = "APIs for generating presigned URLs for direct S3 uploads")
public class UploadController {

    private final S3Service s3Service;
    private final UserService userService;

    @Operation(
            summary = "Generate Presigned URL for Upload",
            description = "Generate a presigned URL for direct upload to S3. " +
                    "The frontend should use this URL to upload files directly to S3."
    )
    @PostMapping("/presigned-url")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> generatePresignedUrl(
            @RequestBody PresignedUrlRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate upload type
            S3Service.UploadType uploadType;
            try {
                uploadType = S3Service.UploadType.valueOf(request.uploadType.toUpperCase());
            } catch (IllegalArgumentException e) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid upload type: " + request.uploadType);
                response.put("validTypes", getValidUploadTypes());
                return ResponseEntity.badRequest().body(response);
            }

            // Validate file type
            if (!s3Service.isValidFileType(request.fileType, uploadType)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid file type for " + uploadType + ": " + request.fileType);
                return ResponseEntity.badRequest().body(response);
            }

            // Generate presigned URL
            Map<String, Object> presignedData = s3Service.generatePresignedUrl(
                    user.getId(),
                    request.fileName,
                    request.fileType,
                    uploadType,
                    request.auditionId
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Presigned URL generated successfully");
            response.put("data", presignedData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error generating presigned URL", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to generate presigned URL");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(
            summary = "Get Upload Configuration",
            description = "Get upload configuration including valid upload types and file size limits"
    )
    @GetMapping("/config")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> getUploadConfig() {
        try {
            Map<String, Object> config = new HashMap<>();

            // Upload types with their configurations
            Map<String, Map<String, Object>> uploadTypes = new HashMap<>();
            for (S3Service.UploadType type : S3Service.UploadType.values()) {
                Map<String, Object> typeConfig = new HashMap<>();
                typeConfig.put("maxFileSize", s3Service.getMaxFileSize(type));
                typeConfig.put("maxFileSizeMB", s3Service.getMaxFileSize(type) / (1024 * 1024));
                typeConfig.put("folder", type.getFolder());
                uploadTypes.put(type.name(), typeConfig);
            }

            config.put("uploadTypes", uploadTypes);
            config.put("validImageTypes", new String[]{"image/jpeg", "image/png", "image/gif", "image/webp"});
            config.put("validVideoTypes", new String[]{"video/mp4", "video/quicktime", "video/webm"});
            config.put("validDocumentTypes", new String[]{"image/jpeg", "image/png", "application/pdf"});

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", config);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting upload config", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get upload configuration");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(
            summary = "Delete Uploaded File",
            description = "Delete a file from S3 using the file URL"
    )
    @DeleteMapping("/file")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> deleteFile(@RequestParam String fileUrl) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate that the file belongs to the user (URL should contain user ID)
            if (!fileUrl.contains("/" + user.getId() + "/")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Unauthorized to delete this file");
                return ResponseEntity.status(403).body(response);
            }

            s3Service.deleteFileFromS3(fileUrl);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File deleted successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error deleting file", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete file");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private String[] getValidUploadTypes() {
        S3Service.UploadType[] types = S3Service.UploadType.values();
        String[] typeNames = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            typeNames[i] = types[i].name();
        }
        return typeNames;
    }

    // Request DTO
    public static class PresignedUrlRequest {
        public String fileName;
        public String fileType;
        public String uploadType;
        public Long auditionId;
    }
}
