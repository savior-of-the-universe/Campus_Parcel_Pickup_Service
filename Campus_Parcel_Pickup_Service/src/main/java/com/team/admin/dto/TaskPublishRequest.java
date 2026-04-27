package com.team.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TaskPublishRequest {
    @NotBlank(message = "取件码不能为空")
    private String pickupCode;

    @NotBlank(message = "快递点不能为空")
    private String deliveryPoint;

    @Min(value = 1, message = "悬赏积分必须大于等于1")
    @NotNull(message = "悬赏积分不能为空")
    private Integer rewardPoints;

    private String weight; // 前端传字符串再解析为数字，方便空值处理

    @Size(max = 100, message = "备注最多100字")
    private String remark;

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    public String getDeliveryPoint() {
        return deliveryPoint;
    }

    public void setDeliveryPoint(String deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
