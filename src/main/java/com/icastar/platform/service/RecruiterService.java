package com.icastar.platform.service;

import com.icastar.platform.dto.user.UpdateUserProfileDto;
import com.icastar.platform.entity.RecruiterProfile;
import com.icastar.platform.entity.User;
import com.icastar.platform.repository.RecruiterProfileRepository;
import com.icastar.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecruiterService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<RecruiterProfile> findById(Long id) {
        return recruiterProfileRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<RecruiterProfile> findByUserId(Long userId) {
        return recruiterProfileRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<RecruiterProfile> findActiveRecruiters() {
        return recruiterProfileRepository.findActiveRecruiters();
    }

    @Transactional(readOnly = true)
    public List<RecruiterProfile> findActiveVerifiedRecruiters() {
        return recruiterProfileRepository.findActiveVerifiedRecruiters();
    }

    @Transactional(readOnly = true)
    public Page<RecruiterProfile> findTopRecruitersByHires(Pageable pageable) {
        return recruiterProfileRepository.findTopRecruitersByHires(pageable);
    }

    @Transactional(readOnly = true)
    public Page<RecruiterProfile> findTopRecruitersByJobsPosted(Pageable pageable) {
        return recruiterProfileRepository.findTopRecruitersByJobsPosted(pageable);
    }

    @Transactional(readOnly = true)
    public List<RecruiterProfile> findRecruitersWithChatCredits() {
        return recruiterProfileRepository.findRecruitersWithChatCredits();
    }

    public RecruiterProfile createRecruiterProfile(Long userId, String companyName, String contactPersonName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RecruiterProfile recruiterProfile = new RecruiterProfile();
        recruiterProfile.setUser(user);
        recruiterProfile.setCompanyName(companyName);
        recruiterProfile.setContactPersonName(contactPersonName);
        recruiterProfile.setIsActive(true);

        return recruiterProfileRepository.save(recruiterProfile);
    }

    public RecruiterProfile updateRecruiterProfile(Long recruiterProfileId, RecruiterProfile updatedProfile) {
        RecruiterProfile existingProfile = recruiterProfileRepository.findById(recruiterProfileId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        // Update fields
        if (updatedProfile.getCompanyName() != null) {
            existingProfile.setCompanyName(updatedProfile.getCompanyName());
        }
        if (updatedProfile.getContactPersonName() != null) {
            existingProfile.setContactPersonName(updatedProfile.getContactPersonName());
        }
        if (updatedProfile.getDesignation() != null) {
            existingProfile.setDesignation(updatedProfile.getDesignation());
        }
        if (updatedProfile.getCompanyDescription() != null) {
            existingProfile.setCompanyDescription(updatedProfile.getCompanyDescription());
        }
        if (updatedProfile.getCompanyWebsite() != null) {
            existingProfile.setCompanyWebsite(updatedProfile.getCompanyWebsite());
        }
        if (updatedProfile.getCompanyLogoUrl() != null) {
            existingProfile.setCompanyLogoUrl(updatedProfile.getCompanyLogoUrl());
        }
        if (updatedProfile.getIndustry() != null) {
            existingProfile.setIndustry(updatedProfile.getIndustry());
        }
        if (updatedProfile.getCompanySize() != null) {
            existingProfile.setCompanySize(updatedProfile.getCompanySize());
        }
        if (updatedProfile.getLocation() != null) {
            existingProfile.setLocation(updatedProfile.getLocation());
        }

        return recruiterProfileRepository.save(existingProfile);
    }

    public void updateBasicProfile(Long userId, UpdateUserProfileDto updateDto) {
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        if (updateDto.getFirstName() != null) {
            recruiterProfile.setContactPersonName(updateDto.getFirstName());
        }
        if (updateDto.getBio() != null) {
            recruiterProfile.setCompanyDescription(updateDto.getBio());
        }
        if (updateDto.getLocation() != null) {
            recruiterProfile.setLocation(updateDto.getLocation());
        }
        if (updateDto.getProfileImageUrl() != null) {
            recruiterProfile.setCompanyLogoUrl(updateDto.getProfileImageUrl());
        }

        recruiterProfileRepository.save(recruiterProfile);
    }

    public RecruiterProfile verifyCompany(Long recruiterProfileId) {
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findById(recruiterProfileId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        recruiterProfile.setIsVerifiedCompany(true);
        return recruiterProfileRepository.save(recruiterProfile);
    }

    public RecruiterProfile unverifyCompany(Long recruiterProfileId) {
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findById(recruiterProfileId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        recruiterProfile.setIsVerifiedCompany(false);
        return recruiterProfileRepository.save(recruiterProfile);
    }

    public void incrementJobsPosted(Long recruiterProfileId) {
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findById(recruiterProfileId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        recruiterProfile.setTotalJobsPosted(recruiterProfile.getTotalJobsPosted() + 1);
        recruiterProfileRepository.save(recruiterProfile);
    }

    public void incrementSuccessfulHires(Long recruiterProfileId) {
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findById(recruiterProfileId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        recruiterProfile.setSuccessfulHires(recruiterProfile.getSuccessfulHires() + 1);
        recruiterProfileRepository.save(recruiterProfile);
    }

    public void addChatCredits(Long recruiterProfileId, Integer credits) {
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findById(recruiterProfileId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        recruiterProfile.setChatCredits(recruiterProfile.getChatCredits() + credits);
        recruiterProfileRepository.save(recruiterProfile);
    }

    public boolean useChatCredits(Long recruiterProfileId, Integer credits) {
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findById(recruiterProfileId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        if (recruiterProfile.getChatCredits() >= credits) {
            recruiterProfile.setChatCredits(recruiterProfile.getChatCredits() - credits);
            recruiterProfileRepository.save(recruiterProfile);
            return true;
        }
        return false;
    }

    public void deleteRecruiterProfile(Long recruiterProfileId) {
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findById(recruiterProfileId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        recruiterProfile.setIsActive(false);
        recruiterProfileRepository.save(recruiterProfile);
    }

    @Transactional(readOnly = true)
    public List<RecruiterProfile> searchRecruiters(String searchTerm, String location, String industry) {
        // This would be implemented with more complex search logic
        // For now, return basic search results
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return recruiterProfileRepository.findByCompanyNameOrContactPersonContaining(searchTerm);
        }
        return findActiveRecruiters();
    }
}
