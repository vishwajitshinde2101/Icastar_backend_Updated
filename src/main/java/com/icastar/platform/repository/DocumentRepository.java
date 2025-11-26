package com.icastar.platform.repository;

import com.icastar.platform.entity.Document;
import com.icastar.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUserId(Long userId);

    List<Document> findByUserIdAndDocumentType(Long userId, Document.DocumentType documentType);

    List<Document> findByUserIdAndIsVerifiedTrue(Long userId);

    @Query("SELECT d FROM Document d WHERE d.user.id = :userId AND d.documentType IN :documentTypes")
    List<Document> findByUserIdAndDocumentTypeIn(@Param("userId") Long userId, 
                                                  @Param("documentTypes") List<Document.DocumentType> documentTypes);

    @Query("SELECT d FROM Document d WHERE d.user.id = :userId AND d.documentType = :documentType ORDER BY d.uploadedAt DESC")
    Optional<Document> findLatestByUserIdAndDocumentType(@Param("userId") Long userId, 
                                                         @Param("documentType") Document.DocumentType documentType);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.user.id = :userId AND d.documentType = :documentType")
    Long countByUserIdAndDocumentType(@Param("userId") Long userId, 
                                      @Param("documentType") Document.DocumentType documentType);

    @Query("SELECT d FROM Document d WHERE d.isVerified = false ORDER BY d.uploadedAt ASC")
    List<Document> findPendingVerification();

    @Query("SELECT d FROM Document d WHERE d.user.id = :userId AND d.documentType IN ('PROFILE_LEFT', 'PROFILE_RIGHT', 'PROFILE_FRONT') ORDER BY d.uploadedAt DESC")
    List<Document> findProfilePicturesByUserId(@Param("userId") Long userId);

    @Query("SELECT d FROM Document d WHERE d.user.id = :userId AND d.documentType = 'ACTING_VIDEO' ORDER BY d.uploadedAt DESC")
    List<Document> findActingVideosByUserId(@Param("userId") Long userId);
}
