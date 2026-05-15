package com.team.admin.dto;

import java.time.LocalDateTime;

/**
 * 聊天消息DTO（包含发送者和接收者姓名）
 */
public class ChatMessageDTO {
    private Long id;                // 消息ID
    private String sessionId;       // 会话ID
    private Long senderId;          // 发送者ID
    private String senderName;      // 发送者姓名
    private Long receiverId;        // 接收者ID
    private String receiverName;    // 接收者姓名
    private String content;         // 消息内容
    private String contentType;     // 内容类型：TEXT/IMAGE
    private LocalDateTime sendTime; // 发送时间
    private Boolean isRead;         // 是否已读

    // 无参构造器
    public ChatMessageDTO() {
    }

    // 全参构造器
    public ChatMessageDTO(Long id, String sessionId, Long senderId, String senderName, Long receiverId, String receiverName, String content, String contentType, LocalDateTime sendTime, Boolean isRead) {
        this.id = id;
        this.sessionId = sessionId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.content = content;
        this.contentType = contentType;
        this.sendTime = sendTime;
        this.isRead = isRead;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    @Override
    public String toString() {
        return "ChatMessageDTO{" +
                "id=" + id +
                ", sessionId='" + sessionId + '\'' +
                ", senderId=" + senderId +
                ", senderName='" + senderName + '\'' +
                ", receiverId=" + receiverId +
                ", receiverName='" + receiverName + '\'' +
                ", content='" + content + '\'' +
                ", contentType='" + contentType + '\'' +
                ", sendTime=" + sendTime +
                ", isRead=" + isRead +
                '}';
    }
}
