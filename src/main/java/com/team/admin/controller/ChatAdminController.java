package com.team.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.team.admin.common.Result;
import com.team.admin.dto.ChatMessageDTO;
import com.team.admin.dto.ChatSessionDTO;
import com.team.admin.entity.ChatMessage;
import com.team.admin.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天管理控制器（客服端）
 */
@RestController
@RequestMapping("/api/admin/chats")
@Validated
public class ChatAdminController {

    @Autowired
    private ChatService chatService;

    /**
     * 获取客服所有活跃会话
     */
    @GetMapping("/sessions")
    public Result<List<ChatSessionDTO>> getActiveSessions(@RequestParam Long adminUserId) {
        List<ChatSessionDTO> sessions = chatService.getActiveSessions(adminUserId);
        return Result.success(sessions);
    }

    /**
     * 分页查询会话历史消息
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public Result<IPage<ChatMessageDTO>> getSessionMessages(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<ChatMessageDTO> messages = chatService.getSessionMessages(sessionId, page, size);
        return Result.success(messages);
    }

    /**
     * 发送消息
     */
    @PostMapping("/messages")
    public Result<Boolean> sendMessage(@Validated @RequestBody ChatMessage message) {
        boolean result = chatService.sendMessage(message);
        return result ? Result.success("发送成功", true) : Result.error("发送失败");
    }

    /**
     * 标记会话消息为已读
     */
    @PutMapping("/sessions/{sessionId}/read")
    public Result<Boolean> markMessagesAsRead(
            @PathVariable String sessionId,
            @RequestParam Long adminUserId) {
        boolean result = chatService.markMessagesAsRead(sessionId, adminUserId);
        return result ? Result.success("标记成功", true) : Result.error("标记失败");
    }

    /**
     * 获取会话未读消息数量
     */
    @GetMapping("/sessions/{sessionId}/unread-count")
    public Result<Long> getUnreadCount(
            @PathVariable String sessionId,
            @RequestParam Long adminUserId) {
        Long unreadCount = chatService.getUnreadCount(sessionId, adminUserId);
        return Result.success(unreadCount);
    }

    /**
     * 搜索聊天消息（全文搜索）
     */
    @GetMapping("/search")
    public Result<IPage<ChatMessageDTO>> searchMessages(
            @RequestParam(required = false) String sessionId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error("搜索关键词不能为空");
        }
        
        IPage<ChatMessageDTO> messages = chatService.searchMessages(sessionId, keyword, page, size);
        return Result.success(messages);
    }
}
