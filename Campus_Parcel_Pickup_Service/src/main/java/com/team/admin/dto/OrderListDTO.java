package com.team.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表DTO（包含用户姓名）
 */
public class OrderListDTO {
    private Long id;                // 订单ID
    private String orderNo;         // 订单号
    private String title;           // 订单标题
    private BigDecimal amount;      // 订单金额
    private String status;          // 订单状态
    private LocalDateTime createTime; // 创建时间
    private String customerName;      // 客户姓名
    private String runnerName;        // 跑腿员姓名
    private String customerStudentId; // 客户学号（脱敏后）
    private String runnerStudentId;   // 跑腿员学号（脱敏后）
    private String customerPhone;     // 客户手机号（按角色脱敏）
    private String runnerPhone;       // 跑腿员手机号（按角色脱敏）

    // 无参构造器
    public OrderListDTO() {
    }

    // 全参构造器
    public OrderListDTO(Long id, String orderNo, String title, BigDecimal amount, String status, LocalDateTime createTime, String customerName, String runnerName, String customerStudentId, String runnerStudentId, String customerPhone, String runnerPhone) {
        this.id = id;
        this.orderNo = orderNo;
        this.title = title;
        this.amount = amount;
        this.status = status;
        this.createTime = createTime;
        this.customerName = customerName;
        this.runnerName = runnerName;
        this.customerStudentId = customerStudentId;
        this.runnerStudentId = runnerStudentId;
        this.customerPhone = customerPhone;
        this.runnerPhone = runnerPhone;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getCustomerStudentId() {
        return customerStudentId;
    }

    public void setCustomerStudentId(String customerStudentId) {
        this.customerStudentId = customerStudentId;
    }

    public String getRunnerStudentId() {
        return runnerStudentId;
    }

    public void setRunnerStudentId(String runnerStudentId) {
        this.runnerStudentId = runnerStudentId;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getRunnerPhone() {
        return runnerPhone;
    }

    public void setRunnerPhone(String runnerPhone) {
        this.runnerPhone = runnerPhone;
    }

    @Override
    public String toString() {
        return "OrderListDTO{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", title='" + title + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", customerName='" + customerName + '\'' +
                ", runnerName='" + runnerName + '\'' +
                ", customerStudentId='" + customerStudentId + '\'' +
                ", runnerStudentId='" + runnerStudentId + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", runnerPhone='" + runnerPhone + '\'' +
                '}';
    }
}


