package com.icastar.platform.dto.recruiter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HireRequestStatsDto {

    private Long total;
    private Long pending;
    private Long viewed;
    private Long accepted;
    private Long declined;
    private Long hired;
    private Long withdrawn;
    private Long expired;
    private Double acceptanceRate;
    private Double responseRate;
}