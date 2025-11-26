package com.icastar.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookmarked_jobs")
@Data
@EqualsAndHashCode(callSuper = true)
public class BookmarkedJob extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistProfile artist;

    @Column(name = "bookmarked_at", nullable = false)
    private LocalDateTime bookmarkedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // Personal notes about the job
}