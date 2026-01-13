package com.icastar.platform.dto.artist;

import com.icastar.platform.entity.ArtistProfile;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SimpleCreateArtistProfileDto {

    @NotNull(message = "Artist type ID is required")
    private Long artistTypeId;

    private LocalDate dateOfBirth;

    private ArtistProfile.Gender gender;

    private String location;

    private Integer experienceYears;

    private Boolean isOnboardingComplete;
}

