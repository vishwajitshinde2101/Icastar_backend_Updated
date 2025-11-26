package com.icastar.platform.repository;

import com.icastar.platform.entity.ArtistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistTypeRepository extends JpaRepository<ArtistType, Long> {

    Optional<ArtistType> findByName(String name);

    List<ArtistType> findByIsActiveTrueOrderBySortOrder();

    @Query("SELECT at FROM ArtistType at WHERE at.isActive = true ORDER BY at.sortOrder, at.displayName")
    List<ArtistType> findActiveArtistTypesOrdered();

    @Query("SELECT at FROM ArtistType at WHERE at.displayName LIKE %:searchTerm% AND at.isActive = true")
    List<ArtistType> findByDisplayNameContainingAndActive(@Param("searchTerm") String searchTerm);
}
