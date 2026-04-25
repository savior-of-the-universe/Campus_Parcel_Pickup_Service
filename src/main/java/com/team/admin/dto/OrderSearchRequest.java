package com.team.admin.dto;

import jakarta.validation.constraints.Min;

/**
 * 订单搜索请求DTO
 */
public class OrderSearchRequest {
    @Min(1)
    private Integer page = 1;      // 当前页码，默认第1页
    @Min(1)
    private Integer size = 20;     // 每页大小，默认20条
    private String status;          // 订单状态筛选
    private String runnerName;      // 跑腿员姓名筛选
    private String customerName;    // 客户姓名筛选

    // 无参构造器
    public OrderSearchRequest() {
    }

    // 全参构造器
    public OrderSearchRequest(Integer page, Integer size, String status, String runnerName, String customerName) {
        this.page = page;
        this.size = size;
        this.status = status;
        this.runnerName = runnerName;
        this.customerName = customerName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String toString() {
        return "OrderSearchRequest{" +
                "page=" + page +
                ", size=" + size +
                ", status='" + status + '\'' +
                ", runnerName='" + runnerName + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}
