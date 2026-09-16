package com.xinji.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PartDTO {
    private Long id;
    private String partId;
    private String partName;
    private String partType;
    private String batchNumber;
    private String manufacturer;
    private LocalDate productionDate;
    private String specification;
    private Integer status;
    private List<TraceRecordDTO> traceRecords;
}
