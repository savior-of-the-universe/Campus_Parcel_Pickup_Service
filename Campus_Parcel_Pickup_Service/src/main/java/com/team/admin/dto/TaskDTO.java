package com.team.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TaskDTO {
    private Long id;
    private String pickupCode;
    private String deliveryPoint;
    private BigDecimal weight;
    private Integer rewardPoints;
    private String remark;
    private String status;
    private LocalDateTime createTime;
    /** 跑腿员昵称（接单后有值） */
    private String runnerNickname;
    /** 跑腿员手机后四位（脱敏），格式 ****XXXX */
    private String runnerPhoneMasked;

    public TaskDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPickupCode() { return pickupCode; }
    public void setPickupCode(String pickupCode) { this.pickupCode = pickupCode; }

    public String getDeliveryPoint() { return deliveryPoint; }
    public void setDeliveryPoint(String deliveryPoint) { this.deliveryPoint = deliveryPoint; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public Integer getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public String getRunnerNickname() { return runnerNickname; }
    public void setRunnerNickname(String runnerNickname) { this.runnerNickname = runnerNickname; }

    public String getRunnerPhoneMasked() { return runnerPhoneMasked; }
    public void setRunnerPhoneMasked(String runnerPhoneMasked) { this.runnerPhoneMasked = runnerPhoneMasked; }
}

