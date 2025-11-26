package com.icastar.platform.dto.recruiter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterProfileFieldDto {
    
    private Long id;
    private Long recruiterCategoryFieldId;
    private String fieldName;
    private String displayName;
    private String fieldValue;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String mimeType;
}
