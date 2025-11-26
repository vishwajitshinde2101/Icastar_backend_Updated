package com.icastar.platform.dto.recruiter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobFieldDto {
    
    private String fieldName;
    private String fieldType;
    private String fieldValue;
    private String displayName;
    private Boolean isRequired;
    private String helpText;
    private String placeholder;
    private String validationRules;
    private String options;
}
