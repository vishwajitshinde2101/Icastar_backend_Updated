package com.icastar.platform.repository;

import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.BookmarkedJob;
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
public interface BookmarkedJobRepository extends JpaRepository<BookmarkedJob, Long> {

    // Find bookmarks by artist
    List<BookmarkedJob> findByArtist(ArtistProfile artist);
    Page<BookmarkedJob> findByArtist(ArtistProfile artist, Pageable pageable);

    // Find bookmarks by job
    List<BookmarkedJob> findByJob(Job job);

    // Find recent bookmarks
    @Query("SELECT bj FROM BookmarkedJob bj WHERE bj.artist = :artist ORDER BY bj.bookmarkedAt DESC")
    List<BookmarkedJob> findRecentBookmarksByArtist(@Param("artist") ArtistProfile artist, Pageable pageable);

    // Find bookmarks by date range
    @Query("SELECT bj FROM BookmarkedJob bj WHERE bj.artist = :artist AND bj.bookmarkedAt BETWEEN :startDate AND :endDate ORDER BY bj.bookmarkedAt DESC")
    List<BookmarkedJob> findByArtistAndDateRange(@Param("artist") ArtistProfile artist, 
                                                @Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);

    // Check if job is bookmarked by artist
    Optional<BookmarkedJob> findByArtistAndJob(ArtistProfile artist, Job job);

    // Count bookmarks by artist
    Long countByArtist(ArtistProfile artist);

    // Count bookmarks by job
    Long countByJob(Job job);

    // Find bookmarks with notes
    @Query("SELECT bj FROM BookmarkedJob bj WHERE bj.artist = :artist AND bj.notes IS NOT NULL AND bj.notes != '' ORDER BY bj.bookmarkedAt DESC")
    List<BookmarkedJob> findBookmarksWithNotes(@Param("artist") ArtistProfile artist);

    // Find bookmarks by job type
    @Query("SELECT bj FROM BookmarkedJob bj WHERE bj.artist = :artist AND bj.job.jobType = :jobType ORDER BY bj.bookmarkedAt DESC")
    List<BookmarkedJob> findByArtistAndJobType(@Param("artist") ArtistProfile artist, @Param("jobType") Job.JobType jobType);

    // Find bookmarks by location
    @Query("SELECT bj FROM BookmarkedJob bj WHERE bj.artist = :artist AND LOWER(bj.job.location) LIKE LOWER(CONCAT('%', :location, '%')) ORDER BY bj.bookmarkedAt DESC")
    List<BookmarkedJob> findByArtistAndLocation(@Param("artist") ArtistProfile artist, @Param("location") String location);

    // Find bookmarks by budget range
    @Query("SELECT bj FROM BookmarkedJob bj WHERE bj.artist = :artist AND bj.job.budgetMin >= :minBudget AND bj.job.budgetMax <= :maxBudget ORDER BY bj.bookmarkedAt DESC")
    List<BookmarkedJob> findByArtistAndBudgetRange(@Param("artist") ArtistProfile artist, 
                                                  @Param("minBudget") java.math.BigDecimal minBudget, 
                                                  @Param("maxBudget") java.math.BigDecimal maxBudget);

    // Find bookmarks for active jobs only
    @Query("SELECT bj FROM BookmarkedJob bj WHERE bj.artist = :artist AND bj.job.status = 'ACTIVE' ORDER BY bj.bookmarkedAt DESC")
    List<BookmarkedJob> findActiveBookmarksByArtist(@Param("artist") ArtistProfile artist);

    @Query("SELECT bj FROM BookmarkedJob bj WHERE bj.artist = :artist AND bj.job.status = 'ACTIVE' ORDER BY bj.bookmarkedAt DESC")
    Page<BookmarkedJob> findActiveBookmarksByArtist(@Param("artist") ArtistProfile artist, Pageable pageable);
}
