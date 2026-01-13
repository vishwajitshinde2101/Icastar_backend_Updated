package com.icastar.platform.repository;

import com.icastar.platform.entity.Audition;
import com.icastar.platform.entity.ArtistProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditionRepository extends JpaRepository<Audition, Long> {

    // Find auditions by artist
    List<Audition> findByArtist(ArtistProfile artist);
    Page<Audition> findByArtist(ArtistProfile artist, Pageable pageable);

    // Find auditions by artist ID
    @Query("SELECT a FROM Audition a WHERE a.artist.id = :artistId")
    List<Audition> findByArtistId(@Param("artistId") Long artistId);

    @Query("SELECT a FROM Audition a WHERE a.artist.id = :artistId")
    Page<Audition> findByArtistId(@Param("artistId") Long artistId, Pageable pageable);

    // Find upcoming auditions for artist (scheduled and in future)
    @Query("SELECT a FROM Audition a WHERE a.artist.id = :artistId AND a.scheduledAt > :now AND a.status = com.icastar.platform.entity.Audition$AuditionStatus.SCHEDULED ORDER BY a.scheduledAt ASC")
    List<Audition> findUpcomingByArtistId(@Param("artistId") Long artistId, @Param("now") LocalDateTime now);

    @Query("SELECT a FROM Audition a WHERE a.artist.id = :artistId AND a.scheduledAt > :now AND a.status = com.icastar.platform.entity.Audition$AuditionStatus.SCHEDULED ORDER BY a.scheduledAt ASC")
    Page<Audition> findUpcomingByArtistId(@Param("artistId") Long artistId, @Param("now") LocalDateTime now, Pageable pageable);

    // Find past auditions for artist
    @Query("SELECT a FROM Audition a WHERE a.artist.id = :artistId AND (a.scheduledAt <= :now OR a.status IN (com.icastar.platform.entity.Audition$AuditionStatus.COMPLETED, com.icastar.platform.entity.Audition$AuditionStatus.CANCELLED, com.icastar.platform.entity.Audition$AuditionStatus.NO_SHOW)) ORDER BY a.scheduledAt DESC")
    List<Audition> findPastByArtistId(@Param("artistId") Long artistId, @Param("now") LocalDateTime now);

    @Query("SELECT a FROM Audition a WHERE a.artist.id = :artistId AND (a.scheduledAt <= :now OR a.status IN (com.icastar.platform.entity.Audition$AuditionStatus.COMPLETED, com.icastar.platform.entity.Audition$AuditionStatus.CANCELLED, com.icastar.platform.entity.Audition$AuditionStatus.NO_SHOW)) ORDER BY a.scheduledAt DESC")
    Page<Audition> findPastByArtistId(@Param("artistId") Long artistId, @Param("now") LocalDateTime now, Pageable pageable);

    // Find auditions by status
    @Query("SELECT a FROM Audition a WHERE a.artist.id = :artistId AND a.status = :status ORDER BY a.scheduledAt DESC")
    List<Audition> findByArtistIdAndStatus(@Param("artistId") Long artistId, @Param("status") Audition.AuditionStatus status);

    @Query("SELECT a FROM Audition a WHERE a.artist.id = :artistId AND a.status = :status ORDER BY a.scheduledAt DESC")
    Page<Audition> findByArtistIdAndStatus(@Param("artistId") Long artistId, @Param("status") Audition.AuditionStatus status, Pageable pageable);

    // Count upcoming auditions for artist
    @Query("SELECT COUNT(a) FROM Audition a WHERE a.artist.id = :artistId AND a.scheduledAt > :now AND a.status = com.icastar.platform.entity.Audition$AuditionStatus.SCHEDULED")
    Long countUpcomingByArtistId(@Param("artistId") Long artistId, @Param("now") LocalDateTime now);

    // Count completed auditions for artist
    @Query("SELECT COUNT(a) FROM Audition a WHERE a.artist.id = :artistId AND a.status = com.icastar.platform.entity.Audition$AuditionStatus.COMPLETED")
    Long countCompletedByArtistId(@Param("artistId") Long artistId);

    // Find auditions by recruiter ID
    @Query("SELECT a FROM Audition a WHERE a.recruiter.id = :recruiterId ORDER BY a.scheduledAt DESC")
    List<Audition> findByRecruiterId(@Param("recruiterId") Long recruiterId);

    @Query("SELECT a FROM Audition a WHERE a.recruiter.id = :recruiterId ORDER BY a.scheduledAt DESC")
    Page<Audition> findByRecruiterId(@Param("recruiterId") Long recruiterId, Pageable pageable);

    // Find upcoming auditions for recruiter
    @Query("SELECT a FROM Audition a WHERE a.recruiter.id = :recruiterId AND a.scheduledAt > :now AND a.status = com.icastar.platform.entity.Audition$AuditionStatus.SCHEDULED ORDER BY a.scheduledAt ASC")
    List<Audition> findUpcomingByRecruiterId(@Param("recruiterId") Long recruiterId, @Param("now") LocalDateTime now);

    // Find auditions by job application
    @Query("SELECT a FROM Audition a WHERE a.jobApplication.id = :jobApplicationId ORDER BY a.scheduledAt DESC")
    List<Audition> findByJobApplicationId(@Param("jobApplicationId") Long jobApplicationId);

    // Find auditions scheduled between dates
    @Query("SELECT a FROM Audition a WHERE a.artist.id = :artistId AND a.scheduledAt BETWEEN :startDate AND :endDate ORDER BY a.scheduledAt ASC")
    List<Audition> findByArtistIdAndScheduledAtBetween(@Param("artistId") Long artistId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // ========== OPEN AUDITIONS (Role-based) ==========

    // Find open auditions by target artist type (for role-based filtering)
    @Query("SELECT a FROM Audition a WHERE a.isOpenAudition = true AND a.targetArtistType.id = :artistTypeId AND a.status = com.icastar.platform.entity.Audition$AuditionStatus.SCHEDULED AND a.scheduledAt > :now ORDER BY a.scheduledAt ASC")
    List<Audition> findOpenAuditionsByArtistTypeId(@Param("artistTypeId") Long artistTypeId, @Param("now") LocalDateTime now);

    @Query("SELECT a FROM Audition a WHERE a.isOpenAudition = true AND a.targetArtistType.id = :artistTypeId AND a.status = com.icastar.platform.entity.Audition$AuditionStatus.SCHEDULED AND a.scheduledAt > :now ORDER BY a.scheduledAt ASC")
    Page<Audition> findOpenAuditionsByArtistTypeId(@Param("artistTypeId") Long artistTypeId, @Param("now") LocalDateTime now, Pageable pageable);

    // Find all open auditions (regardless of role)
    @Query("SELECT a FROM Audition a WHERE a.isOpenAudition = true AND a.status = com.icastar.platform.entity.Audition$AuditionStatus.SCHEDULED AND a.scheduledAt > :now ORDER BY a.scheduledAt ASC")
    List<Audition> findAllOpenAuditions(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Audition a WHERE a.isOpenAudition = true AND a.status = com.icastar.platform.entity.Audition$AuditionStatus.SCHEDULED AND a.scheduledAt > :now ORDER BY a.scheduledAt ASC")
    Page<Audition> findAllOpenAuditions(@Param("now") LocalDateTime now, Pageable pageable);

    // Count open auditions by artist type
    @Query("SELECT COUNT(a) FROM Audition a WHERE a.isOpenAudition = true AND a.targetArtistType.id = :artistTypeId AND a.status = com.icastar.platform.entity.Audition$AuditionStatus.SCHEDULED AND a.scheduledAt > :now")
    Long countOpenAuditionsByArtistTypeId(@Param("artistTypeId") Long artistTypeId, @Param("now") LocalDateTime now);

    // Find open auditions created by recruiter
    @Query("SELECT a FROM Audition a WHERE a.recruiter.id = :recruiterId AND a.isOpenAudition = true ORDER BY a.scheduledAt DESC")
    List<Audition> findOpenAuditionsByRecruiterId(@Param("recruiterId") Long recruiterId);

    @Query("SELECT a FROM Audition a WHERE a.recruiter.id = :recruiterId AND a.isOpenAudition = true ORDER BY a.scheduledAt DESC")
    Page<Audition> findOpenAuditionsByRecruiterId(@Param("recruiterId") Long recruiterId, Pageable pageable);
}
