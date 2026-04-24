package com.team.admin.dto;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class PageRequest {
    @Min(1)
    private Integer page = 1;
    @Min(1)
    private Integer size = 20;
    private String keyword;   // 学号或姓名搜索关键词
}