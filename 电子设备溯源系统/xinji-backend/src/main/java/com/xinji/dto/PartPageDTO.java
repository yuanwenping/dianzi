package com.xinji.dto;

import lombok.Data;

@Data
public class PartPageDTO {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String keyword;
}
