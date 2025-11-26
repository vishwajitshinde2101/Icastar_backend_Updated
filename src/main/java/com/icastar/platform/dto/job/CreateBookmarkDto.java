package com.icastar.platform.dto.job;

import lombok.Data;

import jakarta.validation.constraints.Size;

@Data
public class CreateBookmarkDto {

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
