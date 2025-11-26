package com.icastar.platform.dto.recruiter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecruiterProfileDto {
    
    @NotNull(message = "Recruiter category ID is required")
    private Long recruiterCategoryId;
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    @NotBlank(message = "Contact person name is required")
    private String contactPersonName;
    
    private String designation;
    
    private String companyDescription;
    
    private String companyWebsite;
    
    private String companyLogoUrl;
    
    private String industry;
    
    private String companySize;
    
    private String location;
    
    private Boolean isVerifiedCompany = false;
    
    private List<RecruiterProfileFieldDto> dynamicFields;
}
