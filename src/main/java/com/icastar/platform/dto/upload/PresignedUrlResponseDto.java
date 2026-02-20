package com.icastar.platform.dto.upload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PresignedUrlResponseDto {

    private String presignedUrl;
    private String fileUrl;
    private String uploadId;
    private Integer expiresIn;
    private String s3Key;
    private String bucketName;
}
