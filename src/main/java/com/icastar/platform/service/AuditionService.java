package com.icastar.platform.service;

import com.icastar.platform.entity.Audition;
import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.JobApplication;
import com.icastar.platform.entity.RecruiterProfile;
import com.icastar.platform.repository.AuditionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditionService {

    private final AuditionRepository auditionRepository;

    /**
     * Get all auditions for an artist with pagination
     */
    public Page<Audition> getAuditionsByArtist(ArtistProfile artist, Pageable pageable) {
        return auditionRepository.findByArtistOrderByScheduledAtDesc(artist, pageable);
    }

    /**
     * Get upcoming auditions for an artist
     */
    public List<Audition> getUpcomingAuditions(ArtistProfile artist) {
        return auditionRepository.findUpcomingAuditionsByArtist(artist, LocalDateTime.now());
    }

    /**
     * Get past auditions for an artist
     */
    public Page<Audition> getPastAuditions(ArtistProfile artist, Pageable pageable) {
        return auditionRepository.findPastAuditionsByArtist(artist, LocalDateTime.now(), pageable);
    }

    /**
     * Get auditions by status for an artist
     */
    public Page<Audition> getAuditionsByStatus(ArtistProfile artist, Audition.AuditionStatus status, Pageable pageable) {
        return auditionRepository.findByArtistAndStatusOrderByScheduledAtDesc(artist, status, pageable);
    }

    /**
     * Get auditions by type for an artist
     */
    public Page<Audition> getAuditionsByType(ArtistProfile artist, Audition.AuditionType type, Pageable pageable) {
        return auditionRepository.findByArtistAndAuditionTypeOrderByScheduledAtDesc(artist, type, pageable);
    }

    /**
     * Get an audition by ID
     */
    public Optional<Audition> getAuditionById(Long id) {
        return auditionRepository.findById(id);
    }

    /**
     * Count upcoming auditions for an artist
     */
    public Long countUpcomingAuditions(ArtistProfile artist) {
        return auditionRepository.countUpcomingAuditionsByArtist(artist, LocalDateTime.now());
    }

    /**
     * Check if artist has any upcoming auditions
     */
    public boolean hasUpcomingAuditions(ArtistProfile artist) {
        return auditionRepository.hasUpcomingAuditions(artist, LocalDateTime.now());
    }

    /**
     * Create a new audition (for recruiters)
     */
    @Transactional
    public Audition createAudition(JobApplication jobApplication, RecruiterProfile recruiter, ArtistProfile artist,
                                   Audition.AuditionType type, LocalDateTime scheduledAt, Integer durationMinutes,
                                   String meetingLink, String instructions) {
        Audition audition = new Audition();
        audition.setJobApplication(jobApplication);
        audition.setRecruiter(recruiter);
        audition.setArtist(artist);
        audition.setAuditionType(type);
        audition.setScheduledAt(scheduledAt);
        audition.setDurationMinutes(durationMinutes);
        audition.setMeetingLink(meetingLink);
        audition.setInstructions(instructions);
        audition.setStatus(Audition.AuditionStatus.SCHEDULED);

        audition = auditionRepository.save(audition);
        log.info("Created audition {} for artist {} by recruiter {}", audition.getId(), artist.getId(), recruiter.getId());
        return audition;
    }

    /**
     * Update audition status
     */
    @Transactional
    public Audition updateAuditionStatus(Long auditionId, Audition.AuditionStatus status, ArtistProfile artist) {
        Optional<Audition> auditionOpt = auditionRepository.findById(auditionId);
        if (auditionOpt.isPresent()) {
            Audition audition = auditionOpt.get();
            // Verify the audition belongs to the artist
            if (audition.getArtist().getId().equals(artist.getId())) {
                audition.setStatus(status);
                if (status == Audition.AuditionStatus.COMPLETED) {
                    audition.setCompletedAt(LocalDateTime.now());
                }
                audition = auditionRepository.save(audition);
                log.info("Updated audition {} status to {} for artist {}", auditionId, status, artist.getId());
                return audition;
            } else {
                throw new RuntimeException("Unauthorized access to audition");
            }
        } else {
            throw new RuntimeException("Audition not found");
        }
    }

    /**
     * Cancel an audition
     */
    @Transactional
    public Audition cancelAudition(Long auditionId, ArtistProfile artist) {
        return updateAuditionStatus(auditionId, Audition.AuditionStatus.CANCELLED, artist);
    }

    /**
     * Add feedback and rating to an audition (for recruiters)
     */
    @Transactional
    public Audition addFeedback(Long auditionId, RecruiterProfile recruiter, String feedback, Integer rating) {
        Optional<Audition> auditionOpt = auditionRepository.findById(auditionId);
        if (auditionOpt.isPresent()) {
            Audition audition = auditionOpt.get();
            // Verify the audition belongs to the recruiter
            if (audition.getRecruiter().getId().equals(recruiter.getId())) {
                audition.setFeedback(feedback);
                audition.setRating(rating);
                audition = auditionRepository.save(audition);
                log.info("Added feedback to audition {} by recruiter {}", auditionId, recruiter.getId());
                return audition;
            } else {
                throw new RuntimeException("Unauthorized access to audition");
            }
        } else {
            throw new RuntimeException("Audition not found");
        }
    }
}
