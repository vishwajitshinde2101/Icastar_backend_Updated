package com.icastar.platform.dto;

import com.icastar.platform.entity.ArtistTypeField;
import com.icastar.platform.entity.FieldType;
import lombok.Data;
import java.util.Map;

@Data
public class ArtistTypeFieldDto {
    private Long id;
    private String fieldName;
    private String displayName;
    private FieldType fieldType;
    private Boolean isRequired;
    private Boolean isSearchable;
    private Integer sortOrder;
    private Map<String, Object> validationRules;
    private Map<String, Object> options;
    private String placeholder;
    private String helpText;
}
