package com.icastar.platform.dto.recruiter;

import com.icastar.platform.entity.HireRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HireRequestDto {

    private Long id;
    private Long recruiterId;
    private String recruiterName;
    private String recruiterCompany;
    private Long artistId;
    private String artistName;
    private String artistEmail;
    private String artistProfileUrl;
    private String artistCategory;
    private Long jobId;
    private String jobTitle;
    private String message;
    private HireRequest.HireRequestStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime viewedAt;
    private LocalDateTime respondedAt;
    private LocalDateTime hiredAt;
    private String notes;
    private String artistResponse;
    private Boolean emailSent;
    private Boolean reminderSent;
    private LocalDateTime reminderSentAt;
    private Double offeredSalary;
    private String projectDetails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public HireRequestDto(HireRequest hireRequest) {
        this.id = hireRequest.getId();
        this.recruiterId = hireRequest.getRecruiter() != null ? hireRequest.getRecruiter().getId() : null;
        this.recruiterName = hireRequest.getRecruiter() != null ?
            hireRequest.getRecruiter().getFirstName() + " " + hireRequest.getRecruiter().getLastName() : null;
        this.recruiterCompany = hireRequest.getRecruiter() != null && hireRequest.getRecruiter().getRecruiterProfile() != null ?
            hireRequest.getRecruiter().getRecruiterProfile().getCompanyName() : null;

        if (hireRequest.getArtist() != null) {
            this.artistId = hireRequest.getArtist().getId();
            this.artistName = hireRequest.getArtist().getFirstName() + " " + hireRequest.getArtist().getLastName();
            this.artistEmail = hireRequest.getArtist().getUser() != null ?
                hireRequest.getArtist().getUser().getEmail() : null;
            this.artistProfileUrl = hireRequest.getArtist().getProfileUrl();
            this.artistCategory = hireRequest.getArtist().getArtistType() != null ?
                hireRequest.getArtist().getArtistType().getName() : null;
        }

        if (hireRequest.getJob() != null) {
            this.jobId = hireRequest.getJob().getId();
            this.jobTitle = hireRequest.getJob().getTitle();
        }

        this.message = hireRequest.getMessage();
        this.status = hireRequest.getStatus();
        this.sentAt = hireRequest.getSentAt();
        this.viewedAt = hireRequest.getViewedAt();
        this.respondedAt = hireRequest.getRespondedAt();
        this.hiredAt = hireRequest.getHiredAt();
        this.notes = hireRequest.getNotes();
        this.artistResponse = hireRequest.getArtistResponse();
        this.emailSent = hireRequest.getEmailSent();
        this.reminderSent = hireRequest.getReminderSent();
        this.reminderSentAt = hireRequest.getReminderSentAt();
        this.offeredSalary = hireRequest.getOfferedSalary();
        this.projectDetails = hireRequest.getProjectDetails();
        this.createdAt = hireRequest.getCreatedAt();
        this.updatedAt = hireRequest.getUpdatedAt();
    }
}