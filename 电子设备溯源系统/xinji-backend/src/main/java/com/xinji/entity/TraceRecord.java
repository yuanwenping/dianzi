package com.xinji.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("trace_record")
public class TraceRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long partId;

    private String eventType;

    private String operator;

    private LocalDateTime eventTime;

    private String location;

    private String description;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
