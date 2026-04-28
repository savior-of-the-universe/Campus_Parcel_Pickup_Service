package com.team.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("task")
public class Task {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long publisherId;
    private String pickupCode;
    private String deliveryPoint;
    private BigDecimal weight;
    private Integer rewardPoints;
    private String remark;
    private String status;
    private LocalDateTime createTime;
    /** 跑腿员接单时间 */
    private LocalDateTime acceptTime;
    /** 跑腿员取件成功时间 */
    private LocalDateTime pickupTime;
    /** 任务完成时间 */
    private LocalDateTime completeTime;
    /** 任务取消时间 */
    private LocalDateTime cancelTime;
    /** 接单跑腿员ID，接单后填写 */
    private Long runnerId;
    /** 跑腿员昵称（冗余存储，避免关联查询） */
    private String runnerNickname;
    /** 跑腿员手机号（返回时脱敏） */
    private String runnerPhone;

    @TableLogic
    private Integer deleted;

    public Task() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

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

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public LocalDateTime getAcceptTime() { return acceptTime; }
    public void setAcceptTime(LocalDateTime acceptTime) { this.acceptTime = acceptTime; }

    public LocalDateTime getPickupTime() { return pickupTime; }
    public void setPickupTime(LocalDateTime pickupTime) { this.pickupTime = pickupTime; }

    public LocalDateTime getCompleteTime() { return completeTime; }
    public void setCompleteTime(LocalDateTime completeTime) { this.completeTime = completeTime; }

    public LocalDateTime getCancelTime() { return cancelTime; }
    public void setCancelTime(LocalDateTime cancelTime) { this.cancelTime = cancelTime; }

    public Long getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(Long runnerId) {
        this.runnerId = runnerId;
    }

    public String getRunnerNickname() {
        return runnerNickname;
    }

    public void setRunnerNickname(String runnerNickname) {
        this.runnerNickname = runnerNickname;
    }

    public String getRunnerPhone() {
        return runnerPhone;
    }

    public void setRunnerPhone(String runnerPhone) {
        this.runnerPhone = runnerPhone;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", publisherId=" + publisherId +
                ", pickupCode='" + pickupCode + '\'' +
                ", deliveryPoint='" + deliveryPoint + '\'' +
                ", weight=" + weight +
                ", rewardPoints=" + rewardPoints +
                ", remark='" + remark + '\'' +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
