package com.team.admin.dto;

import java.time.LocalDateTime;

/**
 * 聊天会话DTO
 */
public class ChatSessionDTO {
    private String sessionId;           // 会话ID
    private Long userId;                // 用户ID
    private String userName;            // 用户姓名
    private Long unreadCount;           // 未读消息数量
    private String lastMessage;         // 最后一条消息
    private LocalDateTime lastMessageTime; // 最后消息时间

    // 无参构造器
    public ChatSessionDTO() {
    }

    // 全参构造器
    public ChatSessionDTO(String sessionId, Long userId, String userName, Long unreadCount, String lastMessage, LocalDateTime lastMessageTime) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.userName = userName;
        this.unreadCount = unreadCount;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    // Getter和Setter方法
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    @Override
    public String toString() {
        return "ChatSessionDTO{" +
                "sessionId='" + sessionId + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", unreadCount=" + unreadCount +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastMessageTime=" + lastMessageTime +
                '}';
    }
}
