package com.icastar.platform.controller;

import com.icastar.platform.dto.ArtistProfileFieldDto;
import com.icastar.platform.dto.artist.CreateArtistProfileDto;
import com.icastar.platform.dto.artist.SimpleCreateArtistProfileDto;
import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.ArtistService;
import com.icastar.platform.service.ArtistTypeService;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Artists", description = "Artist profile management and operations")
@SecurityRequirement(name = "bearerAuth")
public class ArtistController {

    private final ArtistService artistService;
    private final ArtistTypeService artistTypeService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @PostMapping("/profile")
    @Operation(summary = "Create Artist Profile", description = "Create a new artist profile with simplified fields")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Artist profile created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or artist profile already exists")
    })
    public ResponseEntity<Map<String, Object>> createArtistProfile(@Valid @RequestBody SimpleCreateArtistProfileDto createDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if artist profile already exists
            if (artistService.findByUserId(user.getId()).isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist profile already exists");
                return ResponseEntity.badRequest().body(response);
            }

            // Create artist profile with simplified data
            // Use user's name if available, otherwise use default values
            String firstName = (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) 
                ? user.getFirstName().trim() : "User";
            String lastName = (user.getLastName() != null && !user.getLastName().trim().isEmpty()) 
                ? user.getLastName().trim() : "Artist";
            
            ArtistProfile artistProfile = artistService.createArtistProfile(
                    user.getId(), 
                    createDto.getArtistTypeId(),
                    firstName,
                    lastName
            );

            // Set the additional fields from the simplified DTO
            if (createDto.getDateOfBirth() != null) {
                artistProfile.setDateOfBirth(createDto.getDateOfBirth());
            }
            if (createDto.getGender() != null) {
                artistProfile.setGender(createDto.getGender());
            }
            if (createDto.getLocation() != null) {
                artistProfile.setLocation(createDto.getLocation());
            }
            if (createDto.getExperienceYears() != null) {
                artistProfile.setExperienceYears(createDto.getExperienceYears());
            }

            artistService.save(artistProfile);

            // Get dynamic fields for the response
            List<ArtistProfileFieldDto> dynamicFields = artistService.getDynamicFields(artistProfile.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Artist profile created successfully");
            response.put("data", artistProfile);
            response.put("dynamicFields", dynamicFields);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating artist profile", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create artist profile");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getCurrentArtistProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            // Initialize lazy-loaded fields to avoid serialization issues
            if (artistProfile.getArtistType() != null) {
                artistProfile.getArtistType().getName(); // Trigger lazy loading
            }

            // Get dynamic fields for the response
            List<ArtistProfileFieldDto> dynamicFields = artistService.getDynamicFields(artistProfile.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", artistProfile);
            response.put("dynamicFields", dynamicFields);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting artist profile", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get artist profile");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<Map<String, Object>> getArtistProfile(@PathVariable Long id) {
        try {
            ArtistProfile artistProfile = artistService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            // Initialize lazy-loaded fields to avoid serialization issues
            if (artistProfile.getArtistType() != null) {
                artistProfile.getArtistType().getName(); // Trigger lazy loading
            }

            // Get dynamic fields for the response
            List<ArtistProfileFieldDto> dynamicFields = artistService.getDynamicFields(artistProfile.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", artistProfile);
            response.put("dynamicFields", dynamicFields);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting artist profile", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get artist profile");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateArtistProfile(@Valid @RequestBody CreateArtistProfileDto updateDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile existingProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            // Create updated profile object
            // Update user fields
            User profileUser = existingProfile.getUser();
            if (updateDto.getFirstName() != null) {
                profileUser.setFirstName(updateDto.getFirstName());
            }
            if (updateDto.getLastName() != null) {
                profileUser.setLastName(updateDto.getLastName());
            }
            userService.save(profileUser);

            ArtistProfile updatedProfile = new ArtistProfile();
            updatedProfile.setStageName(updateDto.getStageName());
            updatedProfile.setBio(updateDto.getBio());
            updatedProfile.setDateOfBirth(updateDto.getDateOfBirth());
            updatedProfile.setGender(updateDto.getGender());
            updatedProfile.setLocation(updateDto.getLocation());
            // Note: Profile images and portfolio URLs are now handled by Document entity
            if (updateDto.getSkills() != null) {
                try {
                    updatedProfile.setSkills(objectMapper.writeValueAsString(updateDto.getSkills()));
                } catch (JsonProcessingException e) {
                    log.error("Error converting skills to JSON", e);
                    throw new RuntimeException("Error processing skills data");
                }
            }
            updatedProfile.setExperienceYears(updateDto.getExperienceYears());
            updatedProfile.setHourlyRate(updateDto.getHourlyRate());
            
            // Handle other JSON fields
            if (updateDto.getLanguagesSpoken() != null) {
                try {
                    updatedProfile.setLanguagesSpoken(objectMapper.writeValueAsString(updateDto.getLanguagesSpoken()));
                } catch (JsonProcessingException e) {
                    log.error("Error converting languagesSpoken to JSON", e);
                    throw new RuntimeException("Error processing languagesSpoken data");
                }
            }
            
            if (updateDto.getComfortableAreas() != null) {
                try {
                    updatedProfile.setComfortableAreas(objectMapper.writeValueAsString(updateDto.getComfortableAreas()));
                } catch (JsonProcessingException e) {
                    log.error("Error converting comfortableAreas to JSON", e);
                    throw new RuntimeException("Error processing comfortableAreas data");
                }
            }
            
            if (updateDto.getProjectsWorked() != null) {
                try {
                    updatedProfile.setProjectsWorked(objectMapper.writeValueAsString(updateDto.getProjectsWorked()));
                } catch (JsonProcessingException e) {
                    log.error("Error converting projectsWorked to JSON", e);
                    throw new RuntimeException("Error processing projectsWorked data");
                }
            }
            
            if (updateDto.getTravelCities() != null) {
                try {
                    updatedProfile.setTravelCities(objectMapper.writeValueAsString(updateDto.getTravelCities()));
                } catch (JsonProcessingException e) {
                    log.error("Error converting travelCities to JSON", e);
                    throw new RuntimeException("Error processing travelCities data");
                }
            }

            ArtistProfile savedProfile = artistService.updateArtistProfile(existingProfile.getId(), updatedProfile);

            // Handle dynamic fields if provided
            if (updateDto.getDynamicFields() != null && !updateDto.getDynamicFields().isEmpty()) {
                artistService.saveDynamicFields(existingProfile.getId(), updateDto.getDynamicFields());
            }

            List<ArtistProfileFieldDto> dynamicFields = artistService.getDynamicFields(existingProfile.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Artist profile updated successfully");
            response.put("data", savedProfile);
            response.put("dynamicFields", dynamicFields);

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

    @PostMapping("/profile/verify")
    public ResponseEntity<Map<String, Object>> requestVerificationBadge() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            artistService.requestVerificationBadge(artistProfile.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Verification request submitted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error requesting verification badge", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to request verification badge");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchArtists(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) Long artistTypeId,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Double maxRate,
            Pageable pageable) {
        try {
            List<ArtistProfile> artists = artistService.searchArtists(
                    searchTerm, location, skills, artistTypeId, minExperience, maxRate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", artists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching artists", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to search artists");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/type/{artistTypeId}")
    public ResponseEntity<Map<String, Object>> getArtistsByType(@PathVariable Long artistTypeId) {
        try {
            List<ArtistProfile> artists = artistService.findActiveArtistsByType(artistTypeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", artists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting artists by type", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get artists by type");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/verified")
    public ResponseEntity<Map<String, Object>> getVerifiedArtists() {
        try {
            List<ArtistProfile> artists = artistService.findActiveVerifiedArtists();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", artists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting verified artists", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get verified artists");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
