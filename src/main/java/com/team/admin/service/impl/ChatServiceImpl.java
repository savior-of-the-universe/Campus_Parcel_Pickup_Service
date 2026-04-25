package com.team.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.admin.dto.ChatMessageDTO;
import com.team.admin.dto.ChatSessionDTO;
import com.team.admin.entity.ChatMessage;
import com.team.admin.mapper.ChatMessageMapper;
import com.team.admin.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 聊天服务实现类
 */
@Service
public class ChatServiceImpl implements ChatService {
    
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    
    @Override
    public List<ChatSessionDTO> getActiveSessions(Long adminUserId) {
        return chatMessageMapper.selectActiveSessions(adminUserId);
    }
    
    @Override
    public IPage<ChatMessageDTO> getSessionMessages(String sessionId, Integer page, Integer size) {
        Page<ChatMessageDTO> pageObj = new Page<>(page, size);
        return chatMessageMapper.selectMessagesBySessionId(pageObj, sessionId);
    }
    
    @Override
    public boolean sendMessage(ChatMessage message) {
        if (message.getSessionId() == null || message.getSessionId().isEmpty()) {
            // 生成新的会话ID
            message.setSessionId(generateSessionId(message.getSenderId(), message.getReceiverId()));
        }
        message.setSendTime(LocalDateTime.now());
        if (message.getIsRead() == null) {
            message.setIsRead(false);
        }
        if (message.getContentType() == null || message.getContentType().isEmpty()) {
            message.setContentType("TEXT");
        }
        return chatMessageMapper.insert(message) > 0;
    }
    
    @Override
    public boolean markMessagesAsRead(String sessionId, Long adminUserId) {
        return chatMessageMapper.markMessagesAsRead(sessionId, adminUserId) > 0;
    }
    
    @Override
    public Long getUnreadCount(String sessionId, Long adminUserId) {
        return chatMessageMapper.getUnreadCount(sessionId, adminUserId);
    }
    
    @Override
    public IPage<ChatMessageDTO> searchMessages(String sessionId, String keyword, Integer page, Integer size) {
        Page<ChatMessageDTO> pageObj = new Page<>(page, size);
        return chatMessageMapper.searchMessagesByKeyword(pageObj, sessionId, keyword);
    }
    
    /**
     * 生成会话ID（基于两个用户ID的组合）
     * @param userId1 用户ID1
     * @param userId2 用户ID2
     * @return 会话ID
     */
    private String generateSessionId(Long userId1, Long userId2) {
        // 确保较小的ID在前，保证会话ID的一致性
        Long minId = Math.min(userId1, userId2);
        Long maxId = Math.max(userId1, userId2);
        return "session_" + minId + "_" + maxId;
    }
}
