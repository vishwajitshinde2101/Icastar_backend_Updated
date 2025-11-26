package com.icastar.platform.service;

import com.icastar.platform.dto.job.BookmarkedJobDto;
import com.icastar.platform.dto.job.CreateBookmarkDto;
import com.icastar.platform.entity.BookmarkedJob;
import com.icastar.platform.entity.Job;
import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.repository.BookmarkedJobRepository;
import com.icastar.platform.repository.JobRepository;
import com.icastar.platform.repository.ArtistProfileRepository;
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
@Transactional
public class BookmarkedJobService {

    private final BookmarkedJobRepository bookmarkedJobRepository;
    private final JobRepository jobRepository;
    private final ArtistProfileRepository artistProfileRepository;

    @Transactional(readOnly = true)
    public List<BookmarkedJob> findByArtist(ArtistProfile artist) {
        return bookmarkedJobRepository.findByArtist(artist);
    }

    @Transactional(readOnly = true)
    public Page<BookmarkedJob> findByArtist(ArtistProfile artist, Pageable pageable) {
        return bookmarkedJobRepository.findByArtist(artist, pageable);
    }

    @Transactional(readOnly = true)
    public List<BookmarkedJob> findActiveBookmarksByArtist(ArtistProfile artist) {
        return bookmarkedJobRepository.findActiveBookmarksByArtist(artist);
    }

    @Transactional(readOnly = true)
    public Page<BookmarkedJob> findActiveBookmarksByArtist(ArtistProfile artist, Pageable pageable) {
        return bookmarkedJobRepository.findActiveBookmarksByArtist(artist, pageable);
    }

    @Transactional(readOnly = true)
    public List<BookmarkedJob> findBookmarksWithNotes(ArtistProfile artist) {
        return bookmarkedJobRepository.findBookmarksWithNotes(artist);
    }

    @Transactional(readOnly = true)
    public List<BookmarkedJob> findByArtistAndJobType(ArtistProfile artist, Job.JobType jobType) {
        return bookmarkedJobRepository.findByArtistAndJobType(artist, jobType);
    }

    @Transactional(readOnly = true)
    public List<BookmarkedJob> findByArtistAndLocation(ArtistProfile artist, String location) {
        return bookmarkedJobRepository.findByArtistAndLocation(artist, location);
    }

    @Transactional(readOnly = true)
    public boolean isJobBookmarked(Long artistId, Long jobId) {
        ArtistProfile artist = artistProfileRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist profile not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        return bookmarkedJobRepository.findByArtistAndJob(artist, job).isPresent();
    }

    public BookmarkedJob bookmarkJob(Long artistId, Long jobId, CreateBookmarkDto createDto) {
        ArtistProfile artist = artistProfileRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist profile not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Check if already bookmarked
        Optional<BookmarkedJob> existingBookmark = bookmarkedJobRepository.findByArtistAndJob(artist, job);
        if (existingBookmark.isPresent()) {
            throw new RuntimeException("Job is already bookmarked");
        }

        try {
            BookmarkedJob bookmark = new BookmarkedJob();
            bookmark.setArtist(artist);
            bookmark.setJob(job);
            bookmark.setBookmarkedAt(LocalDateTime.now());
            bookmark.setNotes(createDto.getNotes());

            BookmarkedJob savedBookmark = bookmarkedJobRepository.save(bookmark);
            log.info("Job bookmarked: {} bookmarked job {}", artist.getUser().getEmail(), job.getTitle());
            return savedBookmark;
        } catch (Exception e) {
            // Handle duplicate constraint error
            if (e.getMessage().contains("unique_bookmark") || e.getMessage().contains("Duplicate entry")) {
                throw new RuntimeException("Job is already bookmarked");
            }
            throw e;
        }
    }

    public BookmarkedJob updateBookmark(Long bookmarkId, CreateBookmarkDto updateDto) {
        BookmarkedJob bookmark = bookmarkedJobRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));

        bookmark.setNotes(updateDto.getNotes());
        return bookmarkedJobRepository.save(bookmark);
    }

    public void removeBookmark(Long bookmarkId) {
        BookmarkedJob bookmark = bookmarkedJobRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));

        bookmarkedJobRepository.delete(bookmark);
        log.info("Bookmark removed: {}", bookmarkId);
    }

    public void removeBookmarkByJob(Long artistId, Long jobId) {
        ArtistProfile artist = artistProfileRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist profile not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        Optional<BookmarkedJob> bookmark = bookmarkedJobRepository.findByArtistAndJob(artist, job);
        if (bookmark.isPresent()) {
            bookmarkedJobRepository.delete(bookmark.get());
            log.info("Bookmark removed: {} unbookmarked job {}", artist.getUser().getEmail(), job.getTitle());
        }
    }

    @Transactional(readOnly = true)
    public Long countBookmarksByArtist(ArtistProfile artist) {
        return bookmarkedJobRepository.countByArtist(artist);
    }

    @Transactional(readOnly = true)
    public Long countBookmarksByJob(Job job) {
        return bookmarkedJobRepository.countByJob(job);
    }
}
