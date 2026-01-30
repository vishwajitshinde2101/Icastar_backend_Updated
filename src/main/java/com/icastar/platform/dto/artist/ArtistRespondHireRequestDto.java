package com.icastar.platform.dto.artist;

import com.icastar.platform.entity.HireRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistRespondHireRequestDto {

    @NotNull(message = "Status is required (ACCEPTED or DECLINED)")
    private HireRequest.HireRequestStatus status;

    private String response;
}