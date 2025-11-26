package com.icastar.platform.dto;

import lombok.Data;
import java.util.List;

@Data
public class ArtistTypeDto {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private String iconUrl;
    private Boolean isActive;
    private Integer sortOrder;
    private List<ArtistTypeFieldDto> fields;
}
