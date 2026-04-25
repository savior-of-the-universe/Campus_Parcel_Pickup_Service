package com.team.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情DTO（包含完整信息和时间线）
 */
public class OrderDetailDTO {
    private Long id;                        // 订单ID
    private String orderNo;                 // 订单号
    private String status;                  // 订单状态
    private String pickupCode;              // 取件码
    private LocalDateTime createTime;       // 创建时间
    private LocalDateTime updateTime;       // 更新时间
    private String customerName;            // 客户姓名
    private String runnerName;              // 跑腿员姓名
    private List<TimelineEvent> timeline;   // 时间线事件列表

    // 无参构造器
    public OrderDetailDTO() {
    }

    // 全参构造器
    public OrderDetailDTO(Long id, String orderNo, String status, String pickupCode, LocalDateTime createTime, LocalDateTime updateTime, String customerName, String runnerName, List<TimelineEvent> timeline) {
        this.id = id;
        this.orderNo = orderNo;
        this.status = status;
        this.pickupCode = pickupCode;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.customerName = customerName;
        this.runnerName = runnerName;
        this.timeline = timeline;
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

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
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

    public List<TimelineEvent> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<TimelineEvent> timeline) {
        this.timeline = timeline;
    }

    /**
     * 时间线事件内部类
     */
    public static class TimelineEvent {
        private String event;           // 事件名称
        private String description;     // 事件描述
        private LocalDateTime timestamp; // 事件时间

        // 无参构造器
        public TimelineEvent() {
        }

        // 全参构造器
        public TimelineEvent(String event, String description, LocalDateTime timestamp) {
            this.event = event;
            this.description = description;
            this.timestamp = timestamp;
        }

        // Getter和Setter方法
        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "TimelineEvent{" +
                    "event='" + event + '\'' +
                    ", description='" + description + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "OrderDetailDTO{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", status='" + status + '\'' +
                ", pickupCode='" + pickupCode + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", customerName='" + customerName + '\'' +
                ", runnerName='" + runnerName + '\'' +
                ", timeline=" + timeline +
                '}';
    }
}
