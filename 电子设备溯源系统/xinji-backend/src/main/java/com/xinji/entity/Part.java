package com.xinji.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("part")
public class Part {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String partId;

    private String partName;

    private String partType;

    private String batchNumber;

    private String manufacturer;

    private LocalDate productionDate;

    private String specification;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
