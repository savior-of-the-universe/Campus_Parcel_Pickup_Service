package com.team.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户端订单列表 DTO（仅当前用户）
 */
public class CustomerOrderListDTO {
    private Long id;
    private String orderNo;
    private String title;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createTime;

    public CustomerOrderListDTO() {
    }

    public CustomerOrderListDTO(Long id, String orderNo, String title, BigDecimal amount, String status, LocalDateTime createTime) {
        this.id = id;
        this.orderNo = orderNo;
        this.title = title;
        this.amount = amount;
        this.status = status;
        this.createTime = createTime;
    }

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

    @Override
    public String toString() {
        return "CustomerOrderListDTO{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", title='" + title + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
