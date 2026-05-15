package com.team.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 */
@TableName("chat_message")
public class ChatMessage {
    @TableId(type = IdType.AUTO)
    private Long id;                // 消息ID
    private String sessionId;       // 会话ID
    private Long senderId;          // 发送者ID
    private Long receiverId;        // 接收者ID
    private String content;         // 消息内容
    private String contentType;     // 内容类型：TEXT/IMAGE
    private LocalDateTime sendTime; // 发送时间
    private Boolean isRead;         // 是否已读：0-未读，1-已读

    // 无参构造器
    public ChatMessage() {
    }

    // 全参构造器
    public ChatMessage(Long id, String sessionId, Long senderId, Long receiverId, String content, String contentType, LocalDateTime sendTime, Boolean isRead) {
        this.id = id;
        this.sessionId = sessionId;
        this.senderId = senderId;
        this.receiverId = receiverId;
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

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
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
        return "ChatMessage{" +
                "id=" + id +
                ", sessionId='" + sessionId + '\'' +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", content='" + content + '\'' +
                ", contentType='" + contentType + '\'' +
                ", sendTime=" + sendTime +
                ", isRead=" + isRead +
                '}';
    }
}
