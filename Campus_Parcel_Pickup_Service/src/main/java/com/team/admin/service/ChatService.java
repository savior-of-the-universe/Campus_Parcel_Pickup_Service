package com.team.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.team.admin.dto.ChatMessageDTO;
import com.team.admin.dto.ChatSessionDTO;
import com.team.admin.entity.ChatMessage;

import java.util.List;

/**
 * 聊天服务接口
 */
public interface ChatService {
    
    /**
     * 获取客服所有活跃会话
     * @param adminUserId 客服用户ID
     * @return 会话列表
     */
    List<ChatSessionDTO> getActiveSessions(Long adminUserId);
    
    /**
     * 分页查询会话历史消息
     * @param sessionId 会话ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<ChatMessageDTO> getSessionMessages(String sessionId, Integer page, Integer size);
    
    /**
     * 发送消息
     * @param message 消息信息
     * @return 发送结果
     */
    boolean sendMessage(ChatMessage message);
    
    /**
     * 标记会话消息为已读
     * @param sessionId 会话ID
     * @param adminUserId 客服用户ID
     * @return 更新结果
     */
    boolean markMessagesAsRead(String sessionId, Long adminUserId);
    
    /**
     * 获取会话未读消息数量
     * @param sessionId 会话ID
     * @param adminUserId 客服用户ID
     * @return 未读数量
     */
    Long getUnreadCount(String sessionId, Long adminUserId);
    
    /**
     * 搜索聊天消息
     * @param sessionId 会话ID（可选）
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<ChatMessageDTO> searchMessages(String sessionId, String keyword, Integer page, Integer size);
}
