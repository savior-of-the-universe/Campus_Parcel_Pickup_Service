package com.team.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;                // 订单ID
    private String orderNo;         // 订单号
    private Long customerId;        // 客户ID
    private Long runnerId;          // 跑腿员ID
    private String status;          // 订单状态：PENDING, ACCEPTED, IN_TRANSIT, COMPLETED, CANCELLED
    private String pickupCode;      // 取件码
    private String timeline;        // 时间线JSON字符串
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间

    // 无参构造器
    public Order() {
    }

    // 全参构造器
    public Order(Long id, String orderNo, Long customerId, Long runnerId, String status, String pickupCode, String timeline, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.orderNo = orderNo;
        this.customerId = customerId;
        this.runnerId = runnerId;
        this.status = status;
        this.pickupCode = pickupCode;
        this.timeline = timeline;
        this.createTime = createTime;
        this.updateTime = updateTime;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(Long runnerId) {
        this.runnerId = runnerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", customerId=" + customerId +
                ", runnerId=" + runnerId +
                ", status='" + status + '\'' +
                ", pickupCode='" + pickupCode + '\'' +
                ", timeline='" + timeline + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
