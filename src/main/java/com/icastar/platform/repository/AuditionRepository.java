package com.icastar.platform.repository;

import com.icastar.platform.entity.Audition;
import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.RecruiterProfile;
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

    /**
     * Find all auditions for an artist
     */
    Page<Audition> findByArtistOrderByScheduledAtDesc(ArtistProfile artist, Pageable pageable);

    /**
     * Find upcoming auditions for an artist (scheduled in the future and not completed)
     */
    @Query("SELECT a FROM Audition a WHERE a.artist = :artist AND a.scheduledAt > :now AND a.status != 'COMPLETED' AND a.status != 'CANCELLED' ORDER BY a.scheduledAt ASC")
    List<Audition> findUpcomingAuditionsByArtist(@Param("artist") ArtistProfile artist, @Param("now") LocalDateTime now);

    /**
     * Find past auditions for an artist
     */
    @Query("SELECT a FROM Audition a WHERE a.artist = :artist AND (a.scheduledAt < :now OR a.status = 'COMPLETED' OR a.status = 'CANCELLED') ORDER BY a.scheduledAt DESC")
    Page<Audition> findPastAuditionsByArtist(@Param("artist") ArtistProfile artist, @Param("now") LocalDateTime now, Pageable pageable);

    /**
     * Find all auditions for a recruiter
     */
    Page<Audition> findByRecruiterOrderByScheduledAtDesc(RecruiterProfile recruiter, Pageable pageable);

    /**
     * Find auditions by status
     */
    Page<Audition> findByArtistAndStatusOrderByScheduledAtDesc(ArtistProfile artist, Audition.AuditionStatus status, Pageable pageable);

    /**
     * Count upcoming auditions for an artist
     */
    @Query("SELECT COUNT(a) FROM Audition a WHERE a.artist = :artist AND a.scheduledAt > :now AND a.status != 'COMPLETED' AND a.status != 'CANCELLED'")
    Long countUpcomingAuditionsByArtist(@Param("artist") ArtistProfile artist, @Param("now") LocalDateTime now);

    /**
     * Find auditions by type for an artist
     */
    Page<Audition> findByArtistAndAuditionTypeOrderByScheduledAtDesc(ArtistProfile artist, Audition.AuditionType type, Pageable pageable);

    /**
     * Check if artist has any upcoming auditions
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Audition a WHERE a.artist = :artist AND a.scheduledAt > :now AND a.status != 'COMPLETED' AND a.status != 'CANCELLED'")
    boolean hasUpcomingAuditions(@Param("artist") ArtistProfile artist, @Param("now") LocalDateTime now);
}
