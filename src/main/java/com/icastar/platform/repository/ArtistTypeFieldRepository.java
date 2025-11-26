package com.icastar.platform.repository;

import com.icastar.platform.entity.ArtistTypeField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistTypeFieldRepository extends JpaRepository<ArtistTypeField, Long> {

    List<ArtistTypeField> findByArtistTypeIdOrderBySortOrder(Long artistTypeId);

    List<ArtistTypeField> findByArtistTypeIdAndIsRequiredTrueOrderBySortOrder(Long artistTypeId);

    List<ArtistTypeField> findByArtistTypeIdAndIsSearchableTrueOrderBySortOrder(Long artistTypeId);

    @Query("SELECT atf FROM ArtistTypeField atf WHERE atf.artistType.id = :artistTypeId AND atf.isActive = true ORDER BY atf.sortOrder")
    List<ArtistTypeField> findActiveFieldsByArtistType(@Param("artistTypeId") Long artistTypeId);

    @Query("SELECT atf FROM ArtistTypeField atf WHERE atf.artistType.name = :artistTypeName AND atf.isActive = true ORDER BY atf.sortOrder")
    List<ArtistTypeField> findActiveFieldsByArtistTypeName(@Param("artistTypeName") String artistTypeName);
}
