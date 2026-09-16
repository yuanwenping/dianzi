package com.xinji.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
public class DashboardDTO {
    private Long totalParts;
    private Long activeParts;
    private Long totalTraceRecords;
    private Map<String, Long> eventTypeDistribution;
}
