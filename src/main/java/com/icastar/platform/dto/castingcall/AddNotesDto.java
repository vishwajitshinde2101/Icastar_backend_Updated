package com.icastar.platform.dto.castingcall;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddNotesDto {

    @NotBlank(message = "Notes are required")
    private String notes;

    private Integer rating; // Optional 1-5
}
