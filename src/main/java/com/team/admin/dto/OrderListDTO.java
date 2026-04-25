package com.team.admin.dto;

import java.time.LocalDateTime;

/**
 * 订单列表DTO（包含用户姓名）
 */
public class OrderListDTO {
    private Long id;                // 订单ID
    private String orderNo;         // 订单号
    private String status;          // 订单状态
    private LocalDateTime createTime; // 创建时间
    private String customerName;    // 客户姓名
    private String runnerName;      // 跑腿员姓名

    // 无参构造器
    public OrderListDTO() {
    }

    // 全参构造器
    public OrderListDTO(Long id, String orderNo, String status, LocalDateTime createTime, String customerName, String runnerName) {
        this.id = id;
        this.orderNo = orderNo;
        this.status = status;
        this.createTime = createTime;
        this.customerName = customerName;
        this.runnerName = runnerName;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    @Override
    public String toString() {
        return "OrderListDTO{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", customerName='" + customerName + '\'' +
                ", runnerName='" + runnerName + '\'' +
                '}';
    }
}
