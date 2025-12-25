package com.icastar.platform.repository;

import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.CastingCall;
import com.icastar.platform.entity.CastingCallApplication;
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
public interface CastingCallApplicationRepository extends JpaRepository<CastingCallApplication, Long> {

    // Basic finders
    List<CastingCallApplication> findByCastingCall(CastingCall castingCall);
    Page<CastingCallApplication> findByCastingCall(CastingCall castingCall, Pageable pageable);

    List<CastingCallApplication> findByArtist(ArtistProfile artist);
    Page<CastingCallApplication> findByArtist(ArtistProfile artist, Pageable pageable);

    @Query("SELECT a FROM CastingCallApplication a WHERE a.castingCall.id = :castingCallId")
    Page<CastingCallApplication> findByCastingCallId(@Param("castingCallId") Long castingCallId, Pageable pageable);

    @Query("SELECT a FROM CastingCallApplication a WHERE a.castingCall.id = :castingCallId AND a.id = :appId")
    Optional<CastingCallApplication> findByCastingCallIdAndId(@Param("castingCallId") Long castingCallId,
                                                               @Param("appId") Long appId);

    // Status queries
    List<CastingCallApplication> findByStatus(CastingCallApplication.ApplicationStatus status);
    Page<CastingCallApplication> findByStatus(CastingCallApplication.ApplicationStatus status, Pageable pageable);

    @Query("SELECT a FROM CastingCallApplication a WHERE a.castingCall.id = :castingCallId AND a.status = :status")
    List<CastingCallApplication> findByCastingCallIdAndStatus(@Param("castingCallId") Long castingCallId,
                                                               @Param("status") CastingCallApplication.ApplicationStatus status);

    // Recruiter queries (through casting call)
    @Query("SELECT a FROM CastingCallApplication a WHERE a.castingCall.recruiter.id = :recruiterId")
    List<CastingCallApplication> findByRecruiterId(@Param("recruiterId") Long recruiterId);

    @Query("SELECT a FROM CastingCallApplication a WHERE a.castingCall.recruiter.id = :recruiterId")
    Page<CastingCallApplication> findByRecruiterId(@Param("recruiterId") Long recruiterId, Pageable pageable);

    @Query("SELECT a FROM CastingCallApplication a WHERE a.castingCall.recruiter.id = :recruiterId " +
           "AND a.status = :status")
    List<CastingCallApplication> findByRecruiterIdAndStatus(@Param("recruiterId") Long recruiterId,
                                                            @Param("status") CastingCallApplication.ApplicationStatus status);

    // Shortlisted and selected
    List<CastingCallApplication> findByIsShortlistedTrue();
    Page<CastingCallApplication> findByIsShortlistedTrue(Pageable pageable);

    @Query("SELECT a FROM CastingCallApplication a WHERE a.castingCall.id = :castingCallId " +
           "AND a.isShortlisted = true")
    List<CastingCallApplication> findShortlistedByCastingCallId(@Param("castingCallId") Long castingCallId);

    List<CastingCallApplication> findByIsSelectedTrue();
    Page<CastingCallApplication> findByIsSelectedTrue(Pageable pageable);

    // Duplicate check
    Optional<CastingCallApplication> findByCastingCallAndArtist(CastingCall castingCall, ArtistProfile artist);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM CastingCallApplication a WHERE a.artist.id = :artistId AND a.castingCall.id = :castingCallId")
    boolean hasArtistApplied(@Param("artistId") Long artistId, @Param("castingCallId") Long castingCallId);

    // Count queries
    Long countByCastingCall(CastingCall castingCall);
    Long countByArtist(ArtistProfile artist);
    Long countByStatus(CastingCallApplication.ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM CastingCallApplication a WHERE a.castingCall.id = :castingCallId")
    Long countByCastingCallId(@Param("castingCallId") Long castingCallId);

    @Query("SELECT COUNT(a) FROM CastingCallApplication a WHERE a.castingCall.id = :castingCallId " +
           "AND a.status = :status")
    Long countByCastingCallIdAndStatus(@Param("castingCallId") Long castingCallId,
                                       @Param("status") CastingCallApplication.ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM CastingCallApplication a WHERE a.castingCall.recruiter.id = :recruiterId")
    Long countByRecruiterId(@Param("recruiterId") Long recruiterId);

    @Query("SELECT COUNT(a) FROM CastingCallApplication a WHERE a.castingCall.recruiter.id = :recruiterId " +
           "AND a.status = :status")
    Long countByRecruiterIdAndStatus(@Param("recruiterId") Long recruiterId,
                                     @Param("status") CastingCallApplication.ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM CastingCallApplication a WHERE a.castingCall.id IN :castingCallIds")
    Long countByCastingCallIdIn(@Param("castingCallIds") List<Long> castingCallIds);

    @Query("SELECT COUNT(a) FROM CastingCallApplication a WHERE a.castingCall.id IN :castingCallIds " +
           "AND a.status = :status")
    Long countByCastingCallIdInAndStatus(@Param("castingCallIds") List<Long> castingCallIds,
                                         @Param("status") CastingCallApplication.ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM CastingCallApplication a WHERE a.castingCall.id IN :castingCallIds " +
           "AND a.isShortlisted = true")
    Long countShortlistedByCastingCallIdIn(@Param("castingCallIds") List<Long> castingCallIds);

    @Query("SELECT COUNT(a) FROM CastingCallApplication a WHERE a.castingCall.id IN :castingCallIds " +
           "AND a.isSelected = true")
    Long countSelectedByCastingCallIdIn(@Param("castingCallIds") List<Long> castingCallIds);

    // Date range analytics
    @Query("SELECT COUNT(a) FROM CastingCallApplication a WHERE a.castingCall.id IN :castingCallIds " +
           "AND a.appliedAt BETWEEN :startDate AND :endDate")
    Long countByCastingCallIdInAndAppliedAtBetween(@Param("castingCallIds") List<Long> castingCallIds,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    // Filtering with comprehensive query
    @Query("SELECT a FROM CastingCallApplication a WHERE a.castingCall.id = :castingCallId " +
           "AND (:status IS NULL OR a.status = :status) " +
           "AND (:isShortlisted IS NULL OR a.isShortlisted = :isShortlisted) " +
           "AND (:minRating IS NULL OR a.rating >= :minRating)")
    Page<CastingCallApplication> findWithFilters(@Param("castingCallId") Long castingCallId,
                                                  @Param("status") CastingCallApplication.ApplicationStatus status,
                                                  @Param("isShortlisted") Boolean isShortlisted,
                                                  @Param("minRating") Integer minRating,
                                                  Pageable pageable);

    // Bulk operations - find by ID list
    @Query("SELECT a FROM CastingCallApplication a WHERE a.id IN :applicationIds")
    List<CastingCallApplication> findByIdIn(@Param("applicationIds") List<Long> applicationIds);
}
