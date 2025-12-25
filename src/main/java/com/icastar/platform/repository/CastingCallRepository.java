package com.icastar.platform.repository;

import com.icastar.platform.entity.CastingCall;
import com.icastar.platform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CastingCallRepository extends JpaRepository<CastingCall, Long> {

    // Basic finders
    List<CastingCall> findByRecruiter(User recruiter);
    Page<CastingCall> findByRecruiter(User recruiter, Pageable pageable);

    @Query("SELECT c FROM CastingCall c WHERE c.recruiter.id = :recruiterId")
    List<CastingCall> findByRecruiterId(@Param("recruiterId") Long recruiterId);

    @Query("SELECT c FROM CastingCall c LEFT JOIN FETCH c.recruiter WHERE c.id = :id")
    Optional<CastingCall> findByIdWithRecruiter(@Param("id") Long id);

    // Status queries
    List<CastingCall> findByStatus(CastingCall.CastingCallStatus status);
    Page<CastingCall> findByStatus(CastingCall.CastingCallStatus status, Pageable pageable);

    @Query("SELECT c FROM CastingCall c WHERE c.status = 'OPEN' AND c.auditionDeadline >= :currentDate")
    List<CastingCall> findOpenCastingCalls(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT c FROM CastingCall c WHERE c.status = 'OPEN' AND c.auditionDeadline >= :currentDate")
    Page<CastingCall> findOpenCastingCalls(@Param("currentDate") LocalDate currentDate, Pageable pageable);

    // Filtering with comprehensive query
    @Query("SELECT c FROM CastingCall c WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.characterName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:roleType IS NULL OR LOWER(c.roleType) = LOWER(:roleType)) AND " +
           "(:projectType IS NULL OR LOWER(c.projectType) = LOWER(:projectType)) AND " +
           "(:location IS NULL OR LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:isUrgent IS NULL OR c.isUrgent = :isUrgent) AND " +
           "(:isFeatured IS NULL OR c.isFeatured = :isFeatured) AND " +
           "(:acceptsRemoteAuditions IS NULL OR c.acceptsRemoteAuditions = :acceptsRemoteAuditions) AND " +
           "(:genderPreference IS NULL OR c.genderPreference = :genderPreference) AND " +
           "c.deletedAt IS NULL")
    Page<CastingCall> findCastingCallsWithFilters(
            @Param("searchTerm") String searchTerm,
            @Param("status") CastingCall.CastingCallStatus status,
            @Param("roleType") String roleType,
            @Param("projectType") String projectType,
            @Param("location") String location,
            @Param("isUrgent") Boolean isUrgent,
            @Param("isFeatured") Boolean isFeatured,
            @Param("acceptsRemoteAuditions") Boolean acceptsRemoteAuditions,
            @Param("genderPreference") CastingCall.GenderPreference genderPreference,
            Pageable pageable);

    // Statistics queries
    Long countByRecruiter(User recruiter);
    Long countByStatus(CastingCall.CastingCallStatus status);

    @Query("SELECT COUNT(c) FROM CastingCall c WHERE c.recruiter.id = :recruiterId AND c.status = :status")
    Long countByRecruiterIdAndStatus(@Param("recruiterId") Long recruiterId,
                                     @Param("status") CastingCall.CastingCallStatus status);

    @Query("SELECT COUNT(c) FROM CastingCall c WHERE c.recruiter.id = :recruiterId AND c.deletedAt IS NULL")
    Long countActiveByRecruiterId(@Param("recruiterId") Long recruiterId);

    // Date range queries
    @Query("SELECT c FROM CastingCall c WHERE c.auditionDeadline BETWEEN :startDate AND :endDate " +
           "AND c.status = 'OPEN' ORDER BY c.auditionDeadline ASC")
    List<CastingCall> findExpiringCastingCalls(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    // Recent queries
    @Query("SELECT c FROM CastingCall c WHERE c.status = 'OPEN' ORDER BY c.publishedAt DESC")
    List<CastingCall> findRecentlyPublished(Pageable pageable);

    @Query("SELECT c FROM CastingCall c WHERE c.recruiter.id = :recruiterId AND c.deletedAt IS NULL " +
           "ORDER BY c.createdAt DESC")
    Page<CastingCall> findByRecruiterIdNotDeleted(@Param("recruiterId") Long recruiterId, Pageable pageable);
}
