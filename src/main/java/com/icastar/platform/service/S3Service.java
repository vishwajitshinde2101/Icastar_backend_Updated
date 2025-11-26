package com.icastar.platform.service;

import com.icastar.platform.dto.artist.CreateArtistProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    @Value("${icastar.aws.s3.bucket-name:icastar-uploads}")
    private String bucketName;
    
    @Value("${icastar.aws.s3.region:us-east-1}")
    private String region;
    
    // TODO: Add AWS S3 configuration
    // private final AmazonS3 s3Client;

    /**
     * Upload a single file to S3
     * @param file MultipartFile to upload
     * @param folder S3 folder path (e.g., "actors/profile-images", "actors/documents")
     * @return S3 URL of uploaded file
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // TODO: Implement actual S3 upload
            // String fileName = generateFileName(file.getOriginalFilename());
            // String key = folder + "/" + fileName;
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setContentLength(file.getSize());
            // metadata.setContentType(file.getContentType());
            // s3Client.putObject(bucketName, key, file.getInputStream(), metadata);
            // return s3Client.getUrl(bucketName, key).toString();
            
            // For now, return realistic dummy S3 URL
            String fileName = generateFileName(file.getOriginalFilename());
            String dummyUrl = String.format("https://%s.s3.%s.amazonaws.com/%s/%s", 
                bucketName, region, folder, fileName);
            log.info("File upload simulated: {} -> {}", file.getOriginalFilename(), dummyUrl);
            log.info("File size: {} bytes, Content type: {}", file.getSize(), file.getContentType());
            return dummyUrl;
            
        } catch (Exception e) {
            log.error("Error uploading file to S3", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * Upload multiple files to S3
     * @param files List of MultipartFiles to upload
     * @param folder S3 folder path
     * @return List of S3 URLs
     */
    public List<String> uploadFiles(List<MultipartFile> files, String folder) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String url = uploadFile(file, folder);
                urls.add(url);
            }
        }
        return urls;
    }

    /**
     * Upload profile pictures with specific types
     * @param files List of MultipartFiles
     * @param types List of picture types (FRONT_PROFILE, LEFT_PROFILE, RIGHT_PROFILE)
     * @param folder S3 folder path
     * @return List of ProfilePictureDto with S3 URLs
     */
    public List<CreateArtistProfileDto.ProfilePictureDto> uploadProfilePictures(
            List<MultipartFile> files, 
            List<CreateArtistProfileDto.ProfilePictureDto.ProfilePictureType> types,
            String folder) {
        
        List<CreateArtistProfileDto.ProfilePictureDto> profilePictures = new ArrayList<>();
        
        for (int i = 0; i < files.size() && i < types.size(); i++) {
            MultipartFile file = files.get(i);
            CreateArtistProfileDto.ProfilePictureDto.ProfilePictureType type = types.get(i);
            
            if (!file.isEmpty()) {
                String url = uploadFile(file, folder);
                CreateArtistProfileDto.ProfilePictureDto pictureDto = new CreateArtistProfileDto.ProfilePictureDto();
                pictureDto.setUrl(url);
                pictureDto.setType(type);
                profilePictures.add(pictureDto);
            }
        }
        
        return profilePictures;
    }

    /**
     * Upload acting videos
     * @param files List of video files
     * @param folder S3 folder path
     * @return List of S3 URLs
     */
    public List<String> uploadActingVideos(List<MultipartFile> files, String folder) {
        return uploadFiles(files, folder);
    }

    /**
     * Upload documents (passport, aadhar, PAN, etc.)
     * @param file Document file
     * @param documentType Type of document (passport, aadhar, pan, id_size_pic)
     * @param actorId Actor's ID for folder organization
     * @return S3 URL
     */
    public String uploadDocument(MultipartFile file, String documentType, Long actorId) {
        String folder = "actors/" + actorId + "/documents/" + documentType;
        return uploadFile(file, folder);
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

    /**
     * Delete file from S3
     * @param s3Url S3 URL to delete
     */
    public void deleteFile(String s3Url) {
        try {
            // TODO: Implement actual S3 delete
            // String key = extractKeyFromUrl(s3Url);
            // s3Client.deleteObject(bucketName, key);
            log.info("File deletion simulated: {}", s3Url);
        } catch (Exception e) {
            log.error("Error deleting file from S3", e);
        }
    }

    /**
     * Extract S3 key from URL
     */
    private String extractKeyFromUrl(String s3Url) {
        // Extract key from S3 URL
        // e.g., https://bucket.s3.amazonaws.com/folder/file.jpg -> folder/file.jpg
        return s3Url.substring(s3Url.indexOf(".s3.amazonaws.com/") + 19);
    }
}
