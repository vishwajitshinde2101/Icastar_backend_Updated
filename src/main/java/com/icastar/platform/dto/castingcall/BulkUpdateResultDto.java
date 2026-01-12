package com.icastar.platform.dto.castingcall;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUpdateResultDto {
    private Integer totalRequested;
    private Integer successful;
    private Integer failed;
    private List<String> errorMessages;
    private List<Long> successfulIds;
    private List<Long> failedIds;
}
