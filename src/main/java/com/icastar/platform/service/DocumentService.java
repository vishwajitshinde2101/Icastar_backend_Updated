package com.icastar.platform.service;

import com.icastar.platform.entity.Document;
import com.icastar.platform.entity.User;
import com.icastar.platform.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${icastar.file.upload-dir:uploads/}")
    private String uploadDir;

    /**
     * Upload a document for a user (stores locally)
     */
    public Document uploadDocument(User user, MultipartFile file, Document.DocumentType documentType) {
        try {
            // Generate unique filename
            String fileName = generateFileName(file.getOriginalFilename());
            String folder = "users/" + user.getId() + "/documents/" + documentType.name().toLowerCase();

            // Save file locally
            String fileUrl = saveFileLocally(file, folder, fileName);

            // Create document entity
            Document document = new Document();
            document.setUser(user);
            document.setDocumentType(documentType);
            document.setFileName(fileName);
            document.setFileUrl(fileUrl);
            document.setFileSize(file.getSize());
            document.setMimeType(file.getContentType());
            document.setUploadedAt(LocalDateTime.now());
            document.setIsVerified(false);

            return documentRepository.save(document);
        } catch (Exception e) {
            log.error("Error uploading document", e);
            throw new RuntimeException("Failed to upload document: " + e.getMessage());
        }
    }

    /**
     * Save file to local storage
     */
    private String saveFileLocally(MultipartFile file, String folder, String fileName) {
        try {
            Path uploadPath = Paths.get(uploadDir, folder);
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            String fileUrl = "/" + uploadDir + folder + "/" + fileName;
            log.info("File saved locally: {}", fileUrl);
            return fileUrl;
        } catch (IOException e) {
            log.error("Error saving file locally", e);
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }

    /**
     * Upload multiple documents of the same type
     */
    public List<Document> uploadMultipleDocuments(User user, List<MultipartFile> files, Document.DocumentType documentType) {
        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> uploadDocument(user, file, documentType))
                .toList();
    }

    /**
     * Upload profile pictures (left, right, front)
     */
    public List<Document> uploadProfilePictures(User user, List<MultipartFile> files, List<Document.DocumentType> types) {
        if (files.size() != types.size()) {
            throw new IllegalArgumentException("Number of files must match number of types");
        }

        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (!file.isEmpty()) {
                Document.DocumentType type = types.get(i);
                documents.add(uploadDocument(user, file, type));
            }
        }
        return documents;
    }

    /**
     * Get all documents for a user
     */
    public List<Document> getDocumentsByUserId(Long userId) {
        return documentRepository.findByUserId(userId);
    }

    /**
     * Get documents by type for a user
     */
    public List<Document> getDocumentsByUserIdAndType(Long userId, Document.DocumentType documentType) {
        return documentRepository.findByUserIdAndDocumentType(userId, documentType);
    }

    /**
     * Get profile pictures for a user
     */
    public List<Document> getProfilePictures(Long userId) {
        return documentRepository.findProfilePicturesByUserId(userId);
    }

    /**
     * Get acting videos for a user
     */
    public List<Document> getActingVideos(Long userId) {
        return documentRepository.findActingVideosByUserId(userId);
    }

    /**
     * Get verified documents for a user
     */
    public List<Document> getVerifiedDocuments(Long userId) {
        return documentRepository.findByUserIdAndIsVerifiedTrue(userId);
    }

    /**
     * Delete a document
     */
    public void deleteDocument(Long documentId) {
        Optional<Document> document = documentRepository.findById(documentId);
        if (document.isPresent()) {
            // Delete local file
            try {
                String fileUrl = document.get().getFileUrl();
                if (fileUrl != null && fileUrl.startsWith("/")) {
                    Path filePath = Paths.get(fileUrl.substring(1));
                    Files.deleteIfExists(filePath);
                    log.info("Deleted local file: {}", fileUrl);
                }
            } catch (IOException e) {
                log.error("Error deleting local file", e);
            }
            documentRepository.deleteById(documentId);
        }
    }

    /**
     * Verify a document
     */
    public Document verifyDocument(Long documentId, Long verifiedBy, String notes) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        document.setIsVerified(true);
        document.setVerifiedAt(LocalDateTime.now());
        document.setVerifiedBy(verifiedBy);
        document.setVerificationNotes(notes);

        return documentRepository.save(document);
    }

    /**
     * Generate unique filename
     */
    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}
