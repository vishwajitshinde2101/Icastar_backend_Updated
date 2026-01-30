package com.icastar.platform.service;

import com.icastar.platform.dto.recruiter.CreateHireRequestDto;
import com.icastar.platform.dto.recruiter.HireRequestDto;
import com.icastar.platform.dto.recruiter.HireRequestStatsDto;
import com.icastar.platform.dto.recruiter.UpdateHireRequestStatusDto;
import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.HireRequest;
import com.icastar.platform.entity.Job;
import com.icastar.platform.entity.User;
import com.icastar.platform.repository.ArtistProfileRepository;
import com.icastar.platform.repository.HireRequestRepository;
import com.icastar.platform.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HireRequestService {

    private final HireRequestRepository hireRequestRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    /**
     * Create a new hire request
     */
    public HireRequest createHireRequest(User recruiter, CreateHireRequestDto dto) {
        log.info("Creating hire request from recruiter {} to artist {}", recruiter.getId(), dto.getArtistId());

        // Validate artist exists
        ArtistProfile artist = artistProfileRepository.findById(dto.getArtistId())
                .orElseThrow(() -> new RuntimeException("Artist not found with id: " + dto.getArtistId()));

        // Validate job if provided
        Job job = null;
        if (dto.getJobId() != null) {
            job = jobRepository.findById(dto.getJobId())
                    .orElseThrow(() -> new RuntimeException("Job not found with id: " + dto.getJobId()));

            // Verify recruiter owns the job
            if (!job.getRecruiter().getId().equals(recruiter.getId())) {
                throw new RuntimeException("You don't have permission to use this job for hire request");
            }
        }

        // Check for existing pending request
        if (hireRequestRepository.existsPendingRequestByRecruiterAndArtist(recruiter.getId(), dto.getArtistId())) {
            throw new RuntimeException("You already have a pending hire request for this artist");
        }

        // Create hire request
        HireRequest hireRequest = new HireRequest();
        hireRequest.setRecruiter(recruiter);
        hireRequest.setArtist(artist);
        hireRequest.setJob(job);
        hireRequest.setMessage(dto.getMessage());
        hireRequest.setOfferedSalary(dto.getOfferedSalary());
        hireRequest.setProjectDetails(dto.getProjectDetails());
        hireRequest.setNotes(dto.getNotes());
        hireRequest.setStatus(HireRequest.HireRequestStatus.PENDING);
        hireRequest.setSentAt(LocalDateTime.now());
        hireRequest.setEmailSent(false);
        hireRequest.setReminderSent(false);

        HireRequest savedRequest = hireRequestRepository.save(hireRequest);

        // Send email notification to artist
        try {
            String artistEmail = artist.getUser().getEmail();
            String artistName = artist.getFirstName() + " " + artist.getLastName();
            String recruiterName = recruiter.getFirstName() + " " + recruiter.getLastName();
            String companyName = recruiter.getRecruiterProfile() != null ?
                    recruiter.getRecruiterProfile().getCompanyName() : "Unknown Company";
            String jobTitle = job != null ? job.getTitle() : "Direct Hire Opportunity";

            emailService.sendHireRequestEmail(artistEmail, artistName, recruiterName, companyName,
                    jobTitle, dto.getMessage(), dto.getOfferedSalary());

            savedRequest.setEmailSent(true);
            savedRequest = hireRequestRepository.save(savedRequest);
            log.info("Hire request email sent to artist: {}", artistEmail);
        } catch (Exception e) {
            log.error("Failed to send hire request email", e);
        }

        return savedRequest;
    }

    /**
     * Get all hire requests for a recruiter with filters
     */
    @Transactional(readOnly = true)
    public Page<HireRequest> getHireRequestsByRecruiter(Long recruiterId, HireRequest.HireRequestStatus status,
                                                        Long jobId, Long artistId, String artistCategory, String searchTerm,
                                                        Pageable pageable) {
        return hireRequestRepository.findByRecruiterIdWithFilters(recruiterId, status, jobId, artistId, artistCategory, searchTerm, pageable);
    }

    /**
     * Get hire request by ID
     */
    @Transactional(readOnly = true)
    public Optional<HireRequest> findById(Long id) {
        return hireRequestRepository.findById(id);
    }

    /**
     * Update hire request status
     */
    public HireRequest updateStatus(Long hireRequestId, Long recruiterId, UpdateHireRequestStatusDto dto) {
        HireRequest hireRequest = hireRequestRepository.findById(hireRequestId)
                .orElseThrow(() -> new RuntimeException("Hire request not found with id: " + hireRequestId));

        // Verify ownership
        if (!hireRequest.getRecruiter().getId().equals(recruiterId)) {
            throw new RuntimeException("You don't have permission to update this hire request");
        }

        HireRequest.HireRequestStatus oldStatus = hireRequest.getStatus();
        hireRequest.setStatus(dto.getStatus());

        if (dto.getNotes() != null) {
            hireRequest.setNotes(dto.getNotes());
        }

        // Update timestamps based on new status
        switch (dto.getStatus()) {
            case VIEWED:
                if (hireRequest.getViewedAt() == null) {
                    hireRequest.setViewedAt(LocalDateTime.now());
                }
                break;
            case ACCEPTED:
            case DECLINED:
                hireRequest.setRespondedAt(LocalDateTime.now());
                if (dto.getArtistResponse() != null) {
                    hireRequest.setArtistResponse(dto.getArtistResponse());
                }
                break;
            case HIRED:
                hireRequest.setHiredAt(LocalDateTime.now());
                break;
            case WITHDRAWN:
                break;
            default:
                break;
        }

        HireRequest updatedRequest = hireRequestRepository.save(hireRequest);

        // Send notification email for status changes
        try {
            if (dto.getStatus() == HireRequest.HireRequestStatus.ACCEPTED ||
                dto.getStatus() == HireRequest.HireRequestStatus.DECLINED) {
                sendStatusUpdateEmail(updatedRequest, oldStatus);
            }
        } catch (Exception e) {
            log.error("Failed to send status update email", e);
        }

        return updatedRequest;
    }

    /**
     * Delete/Withdraw hire request
     */
    public void withdrawHireRequest(Long hireRequestId, Long recruiterId) {
        HireRequest hireRequest = hireRequestRepository.findById(hireRequestId)
                .orElseThrow(() -> new RuntimeException("Hire request not found with id: " + hireRequestId));

        // Verify ownership
        if (!hireRequest.getRecruiter().getId().equals(recruiterId)) {
            throw new RuntimeException("You don't have permission to withdraw this hire request");
        }

        // Only allow withdrawal of pending/viewed requests
        if (hireRequest.getStatus() != HireRequest.HireRequestStatus.PENDING &&
            hireRequest.getStatus() != HireRequest.HireRequestStatus.VIEWED) {
            throw new RuntimeException("Cannot withdraw hire request with status: " + hireRequest.getStatus());
        }

        hireRequest.setStatus(HireRequest.HireRequestStatus.WITHDRAWN);
        hireRequestRepository.save(hireRequest);
        log.info("Hire request {} withdrawn by recruiter {}", hireRequestId, recruiterId);
    }

    /**
     * Send reminder email to artist
     */
    public HireRequest sendReminder(Long hireRequestId, Long recruiterId) {
        HireRequest hireRequest = hireRequestRepository.findById(hireRequestId)
                .orElseThrow(() -> new RuntimeException("Hire request not found with id: " + hireRequestId));

        // Verify ownership
        if (!hireRequest.getRecruiter().getId().equals(recruiterId)) {
            throw new RuntimeException("You don't have permission to send reminder for this hire request");
        }

        // Only allow reminders for pending/viewed requests
        if (hireRequest.getStatus() != HireRequest.HireRequestStatus.PENDING &&
            hireRequest.getStatus() != HireRequest.HireRequestStatus.VIEWED) {
            throw new RuntimeException("Cannot send reminder for hire request with status: " + hireRequest.getStatus());
        }

        // Check if reminder was already sent recently (within 24 hours)
        if (hireRequest.getReminderSent() && hireRequest.getReminderSentAt() != null &&
            hireRequest.getReminderSentAt().plusHours(24).isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Reminder was already sent within the last 24 hours");
        }

        // Send reminder email
        try {
            ArtistProfile artist = hireRequest.getArtist();
            User recruiter = hireRequest.getRecruiter();
            String artistEmail = artist.getUser().getEmail();
            String artistName = artist.getFirstName() + " " + artist.getLastName();
            String recruiterName = recruiter.getFirstName() + " " + recruiter.getLastName();
            String companyName = recruiter.getRecruiterProfile() != null ?
                    recruiter.getRecruiterProfile().getCompanyName() : "Unknown Company";
            String jobTitle = hireRequest.getJob() != null ? hireRequest.getJob().getTitle() : "Direct Hire Opportunity";

            emailService.sendHireRequestReminderEmail(artistEmail, artistName, recruiterName, companyName, jobTitle);

            hireRequest.setReminderSent(true);
            hireRequest.setReminderSentAt(LocalDateTime.now());
            hireRequest = hireRequestRepository.save(hireRequest);
            log.info("Reminder email sent for hire request {} to artist: {}", hireRequestId, artistEmail);
        } catch (Exception e) {
            log.error("Failed to send reminder email", e);
            throw new RuntimeException("Failed to send reminder email: " + e.getMessage());
        }

        return hireRequest;
    }

    /**
     * Get hire request statistics for a recruiter
     */
    @Transactional(readOnly = true)
    public HireRequestStatsDto getStats(Long recruiterId) {
        Long total = hireRequestRepository.countByRecruiterId(recruiterId);
        Long pending = hireRequestRepository.countByRecruiterIdAndStatus(recruiterId, HireRequest.HireRequestStatus.PENDING);
        Long viewed = hireRequestRepository.countByRecruiterIdAndStatus(recruiterId, HireRequest.HireRequestStatus.VIEWED);
        Long accepted = hireRequestRepository.countByRecruiterIdAndStatus(recruiterId, HireRequest.HireRequestStatus.ACCEPTED);
        Long declined = hireRequestRepository.countByRecruiterIdAndStatus(recruiterId, HireRequest.HireRequestStatus.DECLINED);
        Long hired = hireRequestRepository.countByRecruiterIdAndStatus(recruiterId, HireRequest.HireRequestStatus.HIRED);
        Long withdrawn = hireRequestRepository.countByRecruiterIdAndStatus(recruiterId, HireRequest.HireRequestStatus.WITHDRAWN);
        Long expired = hireRequestRepository.countByRecruiterIdAndStatus(recruiterId, HireRequest.HireRequestStatus.EXPIRED);

        Long responded = hireRequestRepository.countRespondedByRecruiterId(recruiterId);

        Double acceptanceRate = total > 0 ? ((accepted + hired) * 100.0) / total : 0.0;
        Double responseRate = total > 0 ? (responded * 100.0) / total : 0.0;

        return HireRequestStatsDto.builder()
                .total(total)
                .pending(pending)
                .viewed(viewed)
                .accepted(accepted)
                .declined(declined)
                .hired(hired)
                .withdrawn(withdrawn)
                .expired(expired)
                .acceptanceRate(Math.round(acceptanceRate * 100.0) / 100.0)
                .responseRate(Math.round(responseRate * 100.0) / 100.0)
                .build();
    }

    /**
     * Artist: Get hire requests received
     */
    @Transactional(readOnly = true)
    public Page<HireRequest> getHireRequestsForArtist(Long artistId, HireRequest.HireRequestStatus status, Pageable pageable) {
        return hireRequestRepository.findByArtistIdWithFilters(artistId, status, pageable);
    }

    /**
     * Artist: Respond to hire request
     */
    public HireRequest artistRespond(Long hireRequestId, Long artistId, HireRequest.HireRequestStatus status, String response) {
        HireRequest hireRequest = hireRequestRepository.findById(hireRequestId)
                .orElseThrow(() -> new RuntimeException("Hire request not found with id: " + hireRequestId));

        // Verify artist ownership
        if (!hireRequest.getArtist().getId().equals(artistId)) {
            throw new RuntimeException("You don't have permission to respond to this hire request");
        }

        // Validate status transition
        if (status != HireRequest.HireRequestStatus.ACCEPTED && status != HireRequest.HireRequestStatus.DECLINED) {
            throw new RuntimeException("Artist can only accept or decline hire requests");
        }

        // Only allow response to pending/viewed requests
        if (hireRequest.getStatus() != HireRequest.HireRequestStatus.PENDING &&
            hireRequest.getStatus() != HireRequest.HireRequestStatus.VIEWED) {
            throw new RuntimeException("Cannot respond to hire request with status: " + hireRequest.getStatus());
        }

        hireRequest.setStatus(status);
        hireRequest.setRespondedAt(LocalDateTime.now());
        hireRequest.setArtistResponse(response);

        HireRequest updatedRequest = hireRequestRepository.save(hireRequest);

        // Send notification to recruiter
        try {
            sendStatusUpdateEmail(updatedRequest, hireRequest.getStatus());
        } catch (Exception e) {
            log.error("Failed to send status update email to recruiter", e);
        }

        return updatedRequest;
    }

    /**
     * Mark hire request as viewed by artist
     */
    public HireRequest markAsViewed(Long hireRequestId, Long artistId) {
        HireRequest hireRequest = hireRequestRepository.findById(hireRequestId)
                .orElseThrow(() -> new RuntimeException("Hire request not found with id: " + hireRequestId));

        // Verify artist ownership
        if (!hireRequest.getArtist().getId().equals(artistId)) {
            throw new RuntimeException("You don't have permission to view this hire request");
        }

        if (hireRequest.getStatus() == HireRequest.HireRequestStatus.PENDING) {
            hireRequest.setStatus(HireRequest.HireRequestStatus.VIEWED);
            hireRequest.setViewedAt(LocalDateTime.now());
            return hireRequestRepository.save(hireRequest);
        }

        return hireRequest;
    }

    private void sendStatusUpdateEmail(HireRequest hireRequest, HireRequest.HireRequestStatus oldStatus) {
        User recruiter = hireRequest.getRecruiter();
        ArtistProfile artist = hireRequest.getArtist();
        String recruiterEmail = recruiter.getEmail();
        String recruiterName = recruiter.getFirstName() + " " + recruiter.getLastName();
        String artistName = artist.getFirstName() + " " + artist.getLastName();
        String jobTitle = hireRequest.getJob() != null ? hireRequest.getJob().getTitle() : "Direct Hire Opportunity";

        emailService.sendHireRequestResponseEmail(recruiterEmail, recruiterName, artistName, jobTitle,
                hireRequest.getStatus(), hireRequest.getArtistResponse());
    }
}