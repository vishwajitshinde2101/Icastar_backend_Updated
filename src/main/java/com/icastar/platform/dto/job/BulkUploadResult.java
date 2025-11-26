package com.icastar.platform.dto.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BulkUploadResult {
    private int successCount;
    private int failedCount;
    private List<String> errorMessages;
}
