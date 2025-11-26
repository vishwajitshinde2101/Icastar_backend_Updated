package com.icastar.platform.controller;

import com.icastar.platform.dto.user.UpdateUserStatusDto;
import com.icastar.platform.dto.user.UserDetailDto;
import com.icastar.platform.dto.user.UserListDto;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserListDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) User.UserRole role,
            @RequestParam(required = false) User.UserStatus status,
            @RequestParam(required = false) String search) {

        log.info("Fetching users - page: {}, size: {}, sortBy: {}, sortDir: {}, role: {}, status: {}, search: {}", 
                page, size, sortBy, sortDir, role, status, search);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users;

        if (search != null && !search.trim().isEmpty()) {
            users = userService.searchUsers(search.trim(), pageable);
        } else if (role != null && status != null) {
            users = userService.findUsersByRoleAndStatus(role, status, pageable);
        } else if (role != null) {
            users = userService.findUsersByRole(role, pageable);
        } else if (status != null) {
            users = userService.findUsersByStatus(status, pageable);
        } else {
            users = userService.findAllUsers(pageable);
        }

        Page<UserListDto> userDtos = users.map(UserListDto::new);
        
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailDto> getUserById(@PathVariable Long userId) {
        log.info("Fetching user details for user ID: {}", userId);
        
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        UserDetailDto userDto = new UserDetailDto(user);
        
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<UserDetailDto> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusDto updateDto) {
        
        log.info("Updating user status for user ID: {}", userId);
        
        User updatedUser = userService.updateUserStatus(userId, updateDto);
        UserDetailDto userDto = new UserDetailDto(updatedUser);
        
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/{userId}/ban")
    public ResponseEntity<UserDetailDto> banUser(
            @PathVariable Long userId,
            @RequestParam(required = false) String reason) {
        
        log.info("Banning user ID: {} with reason: {}", userId, reason);
        
        User bannedUser = userService.banUser(userId, reason);
        UserDetailDto userDto = new UserDetailDto(bannedUser);
        
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/{userId}/unban")
    public ResponseEntity<UserDetailDto> unbanUser(@PathVariable Long userId) {
        log.info("Unbanning user ID: {}", userId);
        
        User unbannedUser = userService.unbanUser(userId);
        UserDetailDto userDto = new UserDetailDto(unbannedUser);
        
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/{userId}/verify")
    public ResponseEntity<UserDetailDto> verifyUser(@PathVariable Long userId) {
        log.info("Verifying user ID: {}", userId);
        
        User verifiedUser = userService.verifyUser(userId);
        UserDetailDto userDto = new UserDetailDto(verifiedUser);
        
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/{userId}/unverify")
    public ResponseEntity<UserDetailDto> unverifyUser(@PathVariable Long userId) {
        log.info("Unverifying user ID: {}", userId);
        
        User unverifiedUser = userService.unverifyUser(userId);
        UserDetailDto userDto = new UserDetailDto(unverifiedUser);
        
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        log.info("Deleting user ID: {}", userId);

        userService.deleteUser(userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        log.info("Fetching user statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsers", userService.getTotalUsersCount());
        stats.put("totalArtists", userService.getUsersCountByRole(User.UserRole.ARTIST));
        stats.put("totalRecruiters", userService.getUsersCountByRole(User.UserRole.RECRUITER));
        stats.put("totalAdmins", userService.getUsersCountByRole(User.UserRole.ADMIN));
        stats.put("activeUsers", userService.getUsersCountByStatus(User.UserStatus.ACTIVE));
        stats.put("bannedUsers", userService.getUsersCountByStatus(User.UserStatus.BANNED));
        stats.put("verifiedUsers", userService.getVerifiedUsersCount());
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<UserListDto>> getRecentlyRegisteredUsers(
            @RequestParam(defaultValue = "7") int days) {
        
        log.info("Fetching recently registered users for last {} days", days);
        
        List<User> recentUsers = userService.getRecentlyRegisteredUsers(days);
        List<UserListDto> userDtos = recentUsers.stream()
                .map(UserListDto::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserListDto>> getActiveUsersByLastLogin(
            @RequestParam(defaultValue = "7") int days) {
        
        log.info("Fetching active users by last login for last {} days", days);
        
        List<User> activeUsers = userService.getActiveUsersByLastLogin(days);
        List<UserListDto> userDtos = activeUsers.stream()
                .map(UserListDto::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(userDtos);
    }
}
