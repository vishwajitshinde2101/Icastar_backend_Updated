package com.icastar.platform.controller;

import com.icastar.platform.dto.artist.ArtistProfileCompleteDto;
import com.icastar.platform.service.ArtistProfileService;
import com.icastar.platform.service.DocumentService;
import com.icastar.platform.entity.Document;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Artist Profile Management", description = "APIs for managing artist profiles with complete CRUD operations")
public class ArtistProfileController {

    private final ArtistProfileService artistProfileService;
    private final DocumentService documentService;
    private final UserService userService;

    @Operation(
            summary = "Get Complete Artist Profile",
            description = "Get complete artist profile including user details, artist profile, and documents for the authenticated user.",
            operationId = "getCompleteArtistProfile"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Artist profile retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": true, \"data\": {...}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Artist profile not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @GetMapping("/profile/complete")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> getCompleteProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfileCompleteDto profile = artistProfileService.getCompleteProfileByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", profile);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting complete artist profile", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get artist profile");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(
            summary = "Get Artist Profile by ID",
            description = "Get complete artist profile by artist profile ID for public viewing.",
            operationId = "getArtistProfileById"
    )
    @GetMapping("/profile/{id}")
    public ResponseEntity<Map<String, Object>> getArtistProfileById(@PathVariable Long id) {
        try {
            ArtistProfileCompleteDto profile = artistProfileService.getCompleteProfileById(id)
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", profile);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting artist profile by ID: {}", id, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get artist profile");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(
            summary = "Get All Artist Profiles",
            description = "Get all artist profiles with pagination support.",
            operationId = "getAllArtistProfiles"
    )
    @GetMapping("/profiles")
    public ResponseEntity<Map<String, Object>> getAllArtistProfiles(Pageable pageable) {
        try {
            Page<ArtistProfileCompleteDto> profiles = artistProfileService.getAllCompleteProfiles(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", profiles.getContent());
            response.put("totalElements", profiles.getTotalElements());
            response.put("totalPages", profiles.getTotalPages());
            response.put("currentPage", profiles.getNumber());
            response.put("size", profiles.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting all artist profiles", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get artist profiles");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(
            summary = "Get Artist Profiles by Type",
            description = "Get artist profiles filtered by artist type (e.g., ACTOR, DANCER, SINGER).",
            operationId = "getArtistProfilesByType"
    )
    @GetMapping("/profiles/type/{artistType}")
    public ResponseEntity<Map<String, Object>> getArtistProfilesByType(@PathVariable String artistType) {
        try {
            List<ArtistProfileCompleteDto> profiles = artistProfileService.getProfilesByArtistType(artistType);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", profiles);
            response.put("count", profiles.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting artist profiles by type: {}", artistType, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get artist profiles by type");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(
            summary = "Search Artist Profiles",
            description = "Search artist profiles by name, stage name, or skills.",
            operationId = "searchArtistProfiles"
    )
    @GetMapping("/profiles/search")
    public ResponseEntity<Map<String, Object>> searchArtistProfiles(@RequestParam String q) {
        try {
            List<ArtistProfileCompleteDto> profiles = artistProfileService.searchProfiles(q);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", profiles);
            response.put("count", profiles.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching artist profiles with query: {}", q, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to search artist profiles");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(
            summary = "Update Artist Profile",
            description = "Update artist profile information including user details and artist-specific fields.",
            operationId = "updateArtistProfile"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Artist profile updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": true, \"message\": \"Profile updated successfully\", \"data\": {...}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class)
                    )
            )
    })
    @PutMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> updateArtistProfile(
            @Parameter(description = "Complete artist profile update details", required = true)
            @RequestBody ArtistProfileCompleteDto updateDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfileCompleteDto updatedProfile = artistProfileService.updateProfile(user.getId(), updateDto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Artist profile updated successfully");
            response.put("data", updatedProfile);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating artist profile", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update artist profile");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(
            summary = "Upload Document",
            description = "Upload a document for the artist profile (profile image, documents, videos, etc.).",
            operationId = "uploadDocument"
    )
    @PostMapping("/profile/documents")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Document.DocumentType type = Document.DocumentType.valueOf(documentType.toUpperCase());
            Document document = documentService.uploadDocument(user, file, type);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Document uploaded successfully");
            response.put("data", document);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error uploading document", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to upload document");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(
            summary = "Delete Artist Profile",
            description = "Delete the artist profile and all associated documents.",
            operationId = "deleteArtistProfile"
    )
    @DeleteMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> deleteArtistProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            artistProfileService.deleteProfile(user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Artist profile deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting artist profile", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete artist profile");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
