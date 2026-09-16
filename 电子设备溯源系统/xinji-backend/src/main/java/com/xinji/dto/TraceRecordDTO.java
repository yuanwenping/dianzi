package com.xinji.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TraceRecordDTO {
    private Long id;
    private Long partId;
    private String eventType;
    private String eventTypeName;
    private String operator;
    private LocalDateTime eventTime;
    private String location;
    private String description;
    private String remark;
    private LocalDateTime createdAt;
}
