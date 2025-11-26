package com.icastar.platform.repository;

import com.icastar.platform.entity.ArtistProfileField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistProfileFieldRepository extends JpaRepository<ArtistProfileField, Long> {

    List<ArtistProfileField> findByArtistProfileId(Long artistProfileId);


    @Query("SELECT apf FROM ArtistProfileField apf WHERE apf.artistProfile.id = :artistProfileId AND apf.artistTypeField.isSearchable = true")
    List<ArtistProfileField> findSearchableFieldsByArtistProfile(@Param("artistProfileId") Long artistProfileId);

    @Query("SELECT apf FROM ArtistProfileField apf WHERE apf.artistTypeField.artistType.id = :artistTypeId AND apf.artistTypeField.fieldName = :fieldName AND apf.fieldValue LIKE %:value%")
    List<ArtistProfileField> findByArtistTypeAndFieldNameAndValue(@Param("artistTypeId") Long artistTypeId, 
                                                                  @Param("fieldName") String fieldName, 
                                                                  @Param("value") String value);

    @Query("SELECT apf FROM ArtistProfileField apf WHERE apf.artistTypeField.artistType.name = :artistTypeName AND apf.artistTypeField.fieldName = :fieldName AND apf.fieldValue = :value")
    List<ArtistProfileField> findByArtistTypeNameAndFieldNameAndValue(@Param("artistTypeName") String artistTypeName, 
                                                                      @Param("fieldName") String fieldName, 
                                                                      @Param("value") String value);
}
