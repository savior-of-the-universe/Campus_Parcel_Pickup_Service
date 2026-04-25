package com.team.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.admin.dto.ChatMessageDTO;
import com.team.admin.dto.ChatSessionDTO;
import com.team.admin.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天消息Mapper接口
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    
    /**
     * 分页查询会话历史消息
     * @param page 分页对象
     * @param sessionId 会话ID
     * @return 分页结果
     */
    IPage<ChatMessageDTO> selectMessagesBySessionId(Page<ChatMessageDTO> page, 
                                                    @Param("sessionId") String sessionId);
    
    /**
     * 获取客服所有活跃会话列表
     * @param adminUserId 客服用户ID
     * @return 会话列表
     */
    List<ChatSessionDTO> selectActiveSessions(@Param("adminUserId") Long adminUserId);
    
    /**
     * 全文搜索聊天消息
     * @param page 分页对象
     * @param sessionId 会话ID（可选）
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    IPage<ChatMessageDTO> searchMessagesByKeyword(Page<ChatMessageDTO> page,
                                                 @Param("sessionId") String sessionId,
                                                 @Param("keyword") String keyword);
    
    /**
     * 标记会话中的消息为已读
     * @param sessionId 会话ID
     * @param adminUserId 客服用户ID
     * @return 更新行数
     */
    int markMessagesAsRead(@Param("sessionId") String sessionId,
                          @Param("adminUserId") Long adminUserId);
    
    /**
     * 获取会话未读消息数量
     * @param sessionId 会话ID
     * @param adminUserId 客服用户ID
     * @return 未读数量
     */
    Long getUnreadCount(@Param("sessionId") String sessionId,
                       @Param("adminUserId") Long adminUserId);
}
