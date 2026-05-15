package com.team.admin.dto;

/**
 * 跑腿员更新任务状态请求体
 */
public class TaskStatusUpdateRequest {

    /** 目标状态：ACCEPTED / IN_TRANSIT / COMPLETED / CANCELLED */
    private String toStatus;

    /** 凭证图片URL（可选，取件时上传） */
    private String proofImageUrl;

    public TaskStatusUpdateRequest() {
    }

    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }

    public String getProofImageUrl() { return proofImageUrl; }
    public void setProofImageUrl(String proofImageUrl) { this.proofImageUrl = proofImageUrl; }
}
