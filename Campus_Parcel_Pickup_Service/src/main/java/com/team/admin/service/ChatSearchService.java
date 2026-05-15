package com.team.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.team.admin.dto.ChatMessageDTO;

/**
 * 聊天搜索服务接口
 */
public interface ChatSearchService {
    
    /**
     * 全文搜索聊天消息
     * @param sessionId 会话ID（可选）
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<ChatMessageDTO> searchMessages(String sessionId, String keyword, Integer page, Integer size);
    
    /**
     * 搜索用户相关的聊天消息
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<ChatMessageDTO> searchMessagesByUser(Long userId, String keyword, Integer page, Integer size);
    
    /**
     * 搜索指定时间范围内的消息
     * @param sessionId 会话ID（可选）
     * @param keyword 搜索关键词
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<ChatMessageDTO> searchMessagesByTimeRange(String sessionId, String keyword, 
                                                   java.time.LocalDateTime startTime, 
                                                   java.time.LocalDateTime endTime,
                                                   Integer page, Integer size);
}
