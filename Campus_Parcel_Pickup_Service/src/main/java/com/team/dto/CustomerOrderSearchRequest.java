package com.team.dto;

import jakarta.validation.constraints.Min;

/**
 * 客户端订单查询请求
 */
public class CustomerOrderSearchRequest {
    @Min(1)
    private Integer page = 1;

    @Min(1)
    private Integer size = 10;

    /**
     * 订单状态：PENDING/ACCEPTED/IN_TRANSIT/COMPLETED/CANCELLED，可选
     */
    private String status;

    /**
     * 排序方向：ASC 或 DESC，默认 DESC（按发布时间倒序）
     */
    private String sort = "DESC";

    public CustomerOrderSearchRequest() {
    }

    public CustomerOrderSearchRequest(Integer page, Integer size, String status, String sort) {
        this.page = page;
        this.size = size;
        this.status = status;
        this.sort = sort;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "CustomerOrderSearchRequest{" +
                "page=" + page +
                ", size=" + size +
                ", status='" + status + '\'' +
                ", sort='" + sort + '\'' +
                '}';
    }
}
