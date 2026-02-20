package com.icastar.platform.controller;

import com.icastar.platform.dto.upload.PresignedUrlRequestDto;
import com.icastar.platform.dto.upload.PresignedUrlResponseDto;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.S3Service;
import com.icastar.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Slf4j
public class UploadController {

    private final S3Service s3Service;
    private final UserService userService;

    /**
     * Generate presigned URL for S3 upload
     * POST /api/upload/presigned-url
     *
     * @param request   PresignedUrlRequestDto containing fileName, fileType, uploadType
     * @param authentication Authentication object
     * @return Presigned URL and file details
     */
    @PostMapping("/presigned-url")
    public ResponseEntity<Map<String, Object>> getPresignedUrl(
            @Valid @RequestBody PresignedUrlRequestDto request,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Generating presigned URL for user: {}, uploadType: {}", email, request.getUploadType());

            PresignedUrlResponseDto presignedUrlResponse = s3Service.generatePresignedUrl(request, user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", presignedUrlResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to generate presigned URL: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
