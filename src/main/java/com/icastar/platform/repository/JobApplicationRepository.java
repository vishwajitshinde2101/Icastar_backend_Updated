package com.icastar.platform.repository;

import com.icastar.platform.entity.JobApplication;
import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // Find applications by artist
    List<JobApplication> findByArtist(ArtistProfile artist);
    Page<JobApplication> findByArtist(ArtistProfile artist, Pageable pageable);

    // Find applications by job
    List<JobApplication> findByJob(Job job);
    Page<JobApplication> findByJob(Job job, Pageable pageable);

    // Find applications by status
    List<JobApplication> findByStatus(JobApplication.ApplicationStatus status);
    Page<JobApplication> findByStatus(JobApplication.ApplicationStatus status, Pageable pageable);

    // Find applications by artist and status
    List<JobApplication> findByArtistAndStatus(ArtistProfile artist, JobApplication.ApplicationStatus status);
    Page<JobApplication> findByArtistAndStatus(ArtistProfile artist, JobApplication.ApplicationStatus status, Pageable pageable);

    // Find applications by job and status
    List<JobApplication> findByJobAndStatus(Job job, JobApplication.ApplicationStatus status);
    Page<JobApplication> findByJobAndStatus(Job job, JobApplication.ApplicationStatus status, Pageable pageable);

    // Find applications by recruiter (through job)
    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.recruiter.id = :recruiterId")
    List<JobApplication> findByRecruiterId(@Param("recruiterId") Long recruiterId);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.recruiter.id = :recruiterId")
    Page<JobApplication> findByRecruiterId(@Param("recruiterId") Long recruiterId, Pageable pageable);

    // Find applications by recruiter and status
    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.recruiter.id = :recruiterId AND ja.status = :status")
    List<JobApplication> findByRecruiterIdAndStatus(@Param("recruiterId") Long recruiterId, @Param("status") JobApplication.ApplicationStatus status);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.recruiter.id = :recruiterId AND ja.status = :status")
    Page<JobApplication> findByRecruiterIdAndStatus(@Param("recruiterId") Long recruiterId, @Param("status") JobApplication.ApplicationStatus status, Pageable pageable);

    // Find recent applications
    @Query("SELECT ja FROM JobApplication ja WHERE ja.appliedAt >= :since ORDER BY ja.appliedAt DESC")
    List<JobApplication> findRecentApplications(@Param("since") LocalDateTime since);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.appliedAt >= :since ORDER BY ja.appliedAt DESC")
    Page<JobApplication> findRecentApplications(@Param("since") LocalDateTime since, Pageable pageable);

    // Find applications by date range
    @Query("SELECT ja FROM JobApplication ja WHERE ja.appliedAt BETWEEN :startDate AND :endDate ORDER BY ja.appliedAt DESC")
    List<JobApplication> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.appliedAt BETWEEN :startDate AND :endDate ORDER BY ja.appliedAt DESC")
    Page<JobApplication> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // Find shortlisted applications
    List<JobApplication> findByIsShortlistedTrue();
    Page<JobApplication> findByIsShortlistedTrue(Pageable pageable);

    // Find hired applications
    List<JobApplication> findByIsHiredTrue();
    Page<JobApplication> findByIsHiredTrue(Pageable pageable);

    // Find applications with interviews scheduled
    @Query("SELECT ja FROM JobApplication ja WHERE ja.interviewScheduledAt IS NOT NULL AND ja.interviewScheduledAt >= :currentTime ORDER BY ja.interviewScheduledAt ASC")
    List<JobApplication> findUpcomingInterviews(@Param("currentTime") LocalDateTime currentTime);

    // Check if artist already applied for job
    Optional<JobApplication> findByArtistAndJob(ArtistProfile artist, Job job);

    // Count applications by artist
    Long countByArtist(ArtistProfile artist);

    // Count applications by job
    Long countByJob(Job job);

    // Count applications by status
    Long countByStatus(JobApplication.ApplicationStatus status);

    // Count applications by recruiter
    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.job.recruiter.id = :recruiterId")
    Long countByRecruiterId(@Param("recruiterId") Long recruiterId);

    // Count applications by recruiter and status
    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.job.recruiter.id = :recruiterId AND ja.status = :status")
    Long countByRecruiterIdAndStatus(@Param("recruiterId") Long recruiterId, @Param("status") JobApplication.ApplicationStatus status);

    // Find applications needing review
    @Query("SELECT ja FROM JobApplication ja WHERE ja.status = 'APPLIED' AND ja.job.recruiter.id = :recruiterId ORDER BY ja.appliedAt ASC")
    List<JobApplication> findApplicationsNeedingReview(@Param("recruiterId") Long recruiterId);

    // Find top applicants by rating
    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.id = :jobId AND ja.rating IS NOT NULL ORDER BY ja.rating DESC")
    List<JobApplication> findTopApplicantsByRating(@Param("jobId") Long jobId, Pageable pageable);

    // Find applications by job ID
    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.id = :jobId")
    List<JobApplication> findByJobId(@Param("jobId") Long jobId);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.id = :jobId")
    Page<JobApplication> findByJobId(@Param("jobId") Long jobId, Pageable pageable);
}