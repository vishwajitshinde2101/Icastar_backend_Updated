package com.icastar.platform.controller;

import com.icastar.platform.dto.auth.*;
import com.icastar.platform.entity.Otp;
import com.icastar.platform.entity.RecruiterProfile;
import com.icastar.platform.entity.User;
import com.icastar.platform.repository.RecruiterProfileRepository;
import com.icastar.platform.repository.UserRepository;
import com.icastar.platform.security.JwtTokenProvider;
import com.icastar.platform.service.EmailService;
import com.icastar.platform.service.OtpService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {

    private final OtpService otpService;
    private final UserService userService;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;


    @Operation(
            summary = "Send OTP",
            description = "Send OTP to user's mobile number for authentication. If user exists, sends login OTP; otherwise sends registration OTP.",
            operationId = "sendOtp"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OTP sent successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"success\": true, \"message\": \"OTP sent successfully\", \"mobile\": \"+919876543210\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or mobile number",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Error Response",
                                    value = "{\"success\": false, \"message\": \"Invalid mobile number format\"}"
                            )
                    )
            )
    })
    @PostMapping("/otp/send")
    public ResponseEntity<Map<String, Object>> sendOtp(
            @Parameter(description = "OTP request details", required = true)
            @Valid @RequestBody OtpRequestDto request) {
        try {
            // Check if user exists for login OTP
            User existingUser = userRepository.findByEmailOrMobile(request.getMobile(), request.getMobile()).orElse(null);
            
            if (existingUser != null) {
                // User exists - send login OTP
                otpService.sendOtp(request.getMobile(), existingUser.getEmail(), Otp.OtpType.LOGIN);
            } else {
                // User doesn't exist - send registration OTP
                otpService.sendOtp(request.getMobile(), null, Otp.OtpType.REGISTRATION);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "OTP sent successfully");
            response.put("data", Map.of("mobile", request.getMobile()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending OTP", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send OTP");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/vishwa")
    public String register() {
        return "Registered!";
    }


    @PostMapping("/otp/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody OtpVerificationDto request) {
        try {
            // Try login OTP first
            boolean isLoginOtp = otpService.verifyOtp(request.getMobile(), null, request.getOtp(), Otp.OtpType.LOGIN);
            
            if (isLoginOtp) {
                // User login
                User user = userRepository.findByEmailOrMobile(request.getMobile(), request.getMobile())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                
                // Update last login
                user.setLastLogin(LocalDateTime.now());
                user.setFailedLoginAttempts(0);
                user.setAccountLockedUntil(null);
                userRepository.save(user);

                String token = jwtTokenProvider.generateTokenFromUsername(user.getEmail());
                AuthResponseDto authResponse = new AuthResponseDto(token, user);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("data", authResponse);

                return ResponseEntity.ok(response);
            }

            // Try registration OTP
            boolean isRegistrationOtp = otpService.verifyOtp(request.getMobile(), null, request.getOtp(), Otp.OtpType.REGISTRATION);
            
            if (isRegistrationOtp) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "OTP verified successfully. Please complete registration.");
                response.put("data", Map.of("mobile", request.getMobile(), "verified", true));

                return ResponseEntity.ok(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid OTP");
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Error verifying OTP", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to verify OTP");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@Valid @RequestBody OtpRequestDto request) {
        try {
            User user = userRepository.findByEmailOrMobile(request.getMobile(), request.getMobile())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            otpService.sendOtp(request.getMobile(), user.getEmail(), Otp.OtpType.FORGOT_PASSWORD);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "OTP sent for password reset");
            response.put("data", Map.of("mobile", request.getMobile()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending forgot password OTP", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send reset OTP");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@Valid @RequestBody OtpVerificationDto request, 
                                                           @RequestParam String newPassword) {
        try {
            // Verify OTP
            boolean isOtpVerified = otpService.verifyOtp(request.getMobile(), null, request.getOtp(), Otp.OtpType.FORGOT_PASSWORD);
            
            if (!isOtpVerified) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid OTP");
                return ResponseEntity.badRequest().body(response);
            }

            // Update password
            User user = userRepository.findByEmailOrMobile(request.getMobile(), request.getMobile())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password reset successful");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error resetting password", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to reset password");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Email-based authentication endpoints (without OTP verification)

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> emailLogin(@Valid @RequestBody EmailLoginRequestDto request) {
        try {
            // Find user by email
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));

            // Check if user is active
            if (user.getStatus() != User.UserStatus.ACTIVE) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Account is not active. Please contact support.");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if account is locked
            if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Account is temporarily locked. Please try again later.");
                return ResponseEntity.badRequest().body(response);
            }

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // Increment failed login attempts
                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                
                // Lock account after 5 failed attempts for 30 minutes
                if (user.getFailedLoginAttempts() >= 5) {
                    user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
                }
                userRepository.save(user);

                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.badRequest().body(response);
            }

            // Reset failed login attempts and update last login
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // Generate JWT token
            String token = jwtTokenProvider.generateTokenFromUsername(user.getEmail());
            AuthResponseDto authResponse = new AuthResponseDto(token, user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("data", authResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error during email login", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Login failed");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> emailRegister(@Valid @RequestBody EmailRegisterRequestDto request) {
        try {
            // Check if user already exists
            if (userRepository.findByEmailOrMobile(request.getEmail(), request.getMobile()).isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "User already exists with this email or mobile");
                return ResponseEntity.badRequest().body(response);
            }

            // Create user
            User user = new User();
            user.setEmail(request.getEmail());
            user.setMobile(request.getMobile());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getUserRole() );
            user.setStatus(User.UserStatus.ACTIVE);
            user.setIsVerified(true); // Auto-verify for email registration
            user.setFailedLoginAttempts(0);

            User savedUser = userRepository.save(user);

           if(User.UserRole.RECRUITER == request.getUserRole()){
               RecruiterProfile recruiterProfile= new RecruiterProfile();
               recruiterProfile.setUser(user);
               recruiterProfile.setIsVerifiedCompany(Boolean.FALSE);
               recruiterProfile.setCompanyName("");
               recruiterProfile.setContactPersonName(request.getFirstName() + " " + request.getLastName());
               recruiterProfileRepository.save(recruiterProfile);
           }

            // Send welcome email (optional - can be implemented later)
            try {
                emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getEmail());
            } catch (Exception e) {
                log.warn("Failed to send welcome email: {}", e.getMessage());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("data", Map.of(
                "id", savedUser.getId(),
                "email", savedUser.getEmail(),
                "mobile", savedUser.getMobile(),
                "role", savedUser.getRole(),
                "status", savedUser.getStatus(),
                "isVerified", savedUser.getIsVerified()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error during email registration", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Registration failed");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
