package com.icastar.platform.repository;

import com.icastar.platform.entity.HireRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HireRequestRepository extends JpaRepository<HireRequest, Long> {

    // Find by recruiter
    Page<HireRequest> findByRecruiterId(Long recruiterId, Pageable pageable);

    List<HireRequest> findByRecruiterId(Long recruiterId);

    // Find by artist
    Page<HireRequest> findByArtistId(Long artistId, Pageable pageable);

    List<HireRequest> findByArtistId(Long artistId);

    // Find by status
    Page<HireRequest> findByRecruiterIdAndStatus(Long recruiterId, HireRequest.HireRequestStatus status, Pageable pageable);

    List<HireRequest> findByRecruiterIdAndStatus(Long recruiterId, HireRequest.HireRequestStatus status);

    // Find by job
    Page<HireRequest> findByRecruiterIdAndJobId(Long recruiterId, Long jobId, Pageable pageable);

    List<HireRequest> findByJobId(Long jobId);

    // Find by recruiter and artist (to check for duplicates)
    Optional<HireRequest> findByRecruiterIdAndArtistIdAndJobId(Long recruiterId, Long artistId, Long jobId);

    Optional<HireRequest> findByRecruiterIdAndArtistIdAndStatusIn(Long recruiterId, Long artistId, List<HireRequest.HireRequestStatus> statuses);

    // Count queries for statistics
    Long countByRecruiterId(Long recruiterId);

    Long countByRecruiterIdAndStatus(Long recruiterId, HireRequest.HireRequestStatus status);

    // Search with filters
    @Query("SELECT hr FROM HireRequest hr " +
           "WHERE hr.recruiter.id = :recruiterId " +
           "AND (:status IS NULL OR hr.status = :status) " +
           "AND (:jobId IS NULL OR hr.job.id = :jobId) " +
           "AND (:artistId IS NULL OR hr.artist.id = :artistId) " +
           "AND (:artistCategory IS NULL OR hr.artist.artistType.name = :artistCategory) " +
           "AND (:searchTerm IS NULL OR " +
           "     LOWER(hr.artist.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(hr.artist.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(hr.job.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<HireRequest> findByRecruiterIdWithFilters(
            @Param("recruiterId") Long recruiterId,
            @Param("status") HireRequest.HireRequestStatus status,
            @Param("jobId") Long jobId,
            @Param("artistId") Long artistId,
            @Param("artistCategory") String artistCategory,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    // Artist side queries
    @Query("SELECT hr FROM HireRequest hr " +
           "WHERE hr.artist.id = :artistId " +
           "AND (:status IS NULL OR hr.status = :status)")
    Page<HireRequest> findByArtistIdWithFilters(
            @Param("artistId") Long artistId,
            @Param("status") HireRequest.HireRequestStatus status,
            Pageable pageable);

    // Check if artist has pending request from recruiter
    @Query("SELECT CASE WHEN COUNT(hr) > 0 THEN true ELSE false END FROM HireRequest hr " +
           "WHERE hr.recruiter.id = :recruiterId " +
           "AND hr.artist.id = :artistId " +
           "AND hr.status IN ('PENDING', 'VIEWED')")
    boolean existsPendingRequestByRecruiterAndArtist(
            @Param("recruiterId") Long recruiterId,
            @Param("artistId") Long artistId);

    // Statistics queries
    @Query("SELECT COUNT(hr) FROM HireRequest hr " +
           "WHERE hr.recruiter.id = :recruiterId " +
           "AND hr.status IN ('ACCEPTED', 'HIRED')")
    Long countAcceptedByRecruiterId(@Param("recruiterId") Long recruiterId);

    @Query("SELECT COUNT(hr) FROM HireRequest hr " +
           "WHERE hr.recruiter.id = :recruiterId " +
           "AND hr.respondedAt IS NOT NULL")
    Long countRespondedByRecruiterId(@Param("recruiterId") Long recruiterId);
}