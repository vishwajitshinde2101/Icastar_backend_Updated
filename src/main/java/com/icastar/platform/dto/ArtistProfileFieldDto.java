package com.icastar.platform.dto;

import lombok.Data;

@Data
public class ArtistProfileFieldDto {
    private Long id;
    private Long artistTypeFieldId;
    private String fieldName;
    private String displayName;
    private String fieldValue;
    // File handling is now managed by Document entity, not stored here
}
