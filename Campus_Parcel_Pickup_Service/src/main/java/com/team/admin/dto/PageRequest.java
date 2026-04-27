package com.team.admin.dto;

import jakarta.validation.constraints.Min;

/**
 * 分页请求基类
 */
public class PageRequest {
    @Min(1)
    private Integer page = 1;      // 当前页码，默认第1页
    @Min(1)
    private Integer size = 20;     // 每页大小，默认20条
    private String keyword;         // 搜索关键词

    // 无参构造器
    public PageRequest() {
    }

    // 全参构造器
    public PageRequest(Integer page, Integer size, String keyword) {
        this.page = page;
        this.size = size;
        this.keyword = keyword;
    }

    // Getter和Setter方法
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "PageRequest{" +
                "page=" + page +
                ", size=" + size +
                ", keyword='" + keyword + '\'' +
                '}';
    }
}
