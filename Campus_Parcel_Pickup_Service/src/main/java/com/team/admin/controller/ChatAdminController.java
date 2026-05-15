package com.team.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.team.admin.common.Result;
import com.team.admin.dto.ChatMessageDTO;
import com.team.admin.dto.ChatSessionDTO;
import com.team.admin.entity.ChatMessage;
import com.team.admin.entity.User;
import com.team.admin.service.ChatService;
import com.team.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * 聊天管理控制器（客服端）
 */
@RestController
@RequestMapping("/api/admin/chats")
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class ChatAdminController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    /**
     * 获取客服所有活跃会话（前端未传 adminUserId 时自动取当前登录用户）
     */
    @GetMapping("/sessions")
    public Result<List<ChatSessionDTO>> getActiveSessions(@RequestParam(required = false) Long adminUserId,
                                                          Principal principal) {
        try {
            Long uid = resolveAdminUserId(adminUserId, principal);
            // 兜底：仍取不到时默认管理员ID=1，避免 500
            if (uid == null) {
                uid = 1L;
            }
            List<ChatSessionDTO> sessions = chatService.getActiveSessions(uid);
            return Result.success(sessions);
        } catch (Exception e) {
            return Result.error("获取会话列表失败: " + e.getMessage());
        }
    }


    /**
     * 分页查询会话历史消息（路径参数版）
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public Result<List<ChatMessageDTO>> getSessionMessages(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<ChatMessageDTO> messages = chatService.getSessionMessages(sessionId, page, size);
        return Result.success(messages.getRecords());
    }

    /**
     * 分页查询会话历史消息（兼容前端 query 参数版）
     */
    @GetMapping("/messages")
    public Result<List<ChatMessageDTO>> getSessionMessagesByQuery(
            @RequestParam String sessionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<ChatMessageDTO> messages = chatService.getSessionMessages(sessionId, page, size);
        return Result.success(messages.getRecords());
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
     * 标记会话消息为已读（兼容前端 POST 调用）
     */
    @PostMapping("/sessions/{sessionId}/read")
    public Result<Boolean> markMessagesAsReadPost(
            @PathVariable String sessionId,
            @RequestParam(required = false) Long adminUserId,
            Principal principal) {
        return markMessagesAsReadInternal(sessionId, adminUserId, principal);
    }

    /**
     * 标记会话消息为已读（原 PUT 接口保留）
     */
    @PutMapping("/sessions/{sessionId}/read")
    public Result<Boolean> markMessagesAsReadPut(
            @PathVariable String sessionId,
            @RequestParam(required = false) Long adminUserId,
            Principal principal) {
        return markMessagesAsReadInternal(sessionId, adminUserId, principal);
    }

    private Result<Boolean> markMessagesAsReadInternal(String sessionId, Long adminUserId, Principal principal) {
        Long uid = resolveAdminUserId(adminUserId, principal);
        if (uid == null) {
            return Result.error("无法获取管理员ID，请重新登录");
        }
        chatService.markMessagesAsRead(sessionId, uid);
        return Result.success("标记成功", true);
    }


    /**
     * 获取会话未读消息数量
     */
    @GetMapping("/sessions/{sessionId}/unread-count")
    public Result<Long> getUnreadCount(
            @PathVariable String sessionId,
            @RequestParam(required = false) Long adminUserId,
            Principal principal) {
        Long uid = resolveAdminUserId(adminUserId, principal);
        if (uid == null) {
            return Result.error("无法获取管理员ID，请重新登录");
        }
        Long unreadCount = chatService.getUnreadCount(sessionId, uid);
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

    /**
     * 从参数或当前登录用户解析管理员ID
     */
    private Long resolveAdminUserId(Long adminUserId, Principal principal) {
        if (adminUserId != null) {
            return adminUserId;
        }
        String username = principal != null ? principal.getName() : null;
        if (username == null) {
            Object principalObj = SecurityContextHolder.getContext().getAuthentication() != null
                    ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                    : null;
            if (principalObj instanceof String) {
                username = (String) principalObj;
            }
        }
        if (username == null) {
            return 1L; // 再兜底默认管理员账号ID=1，避免空指针
        }
        User user = userService.findByStudentIdOrPhone(username);
        return user != null ? user.getId() : 1L;
    }

}

