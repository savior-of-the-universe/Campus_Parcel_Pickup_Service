package com.team.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.team.admin.common.Result;
import com.team.admin.dto.ChatMessageDTO;
import com.team.admin.service.ChatSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 聊天搜索控制器
 */
@RestController
@RequestMapping("/api/admin/chats/search")
@Validated
public class ChatSearchController {

    @Autowired
    private ChatSearchService chatSearchService;

    /**
     * 全文搜索聊天消息
     */
    @GetMapping("/messages")
    public Result<IPage<ChatMessageDTO>> searchMessages(
            @RequestParam(required = false) String sessionId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error("搜索关键词不能为空");
        }
        
        IPage<ChatMessageDTO> result = chatSearchService.searchMessages(sessionId, keyword, page, size);
        return Result.success(result);
    }

    /**
     * 搜索用户相关的聊天消息
     */
    @GetMapping("/user/{userId}")
    public Result<IPage<ChatMessageDTO>> searchMessagesByUser(
            @PathVariable Long userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error("搜索关键词不能为空");
        }
        
        IPage<ChatMessageDTO> result = chatSearchService.searchMessagesByUser(userId, keyword, page, size);
        return Result.success(result);
    }

    /**
     * 搜索指定时间范围内的消息
     */
    @GetMapping("/time-range")
    public Result<IPage<ChatMessageDTO>> searchMessagesByTimeRange(
            @RequestParam(required = false) String sessionId,
            @RequestParam String keyword,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error("搜索关键词不能为空");
        }
        
        if (startTime == null || endTime == null) {
            return Result.error("时间范围不能为空");
        }
        
        if (startTime.isAfter(endTime)) {
            return Result.error("开始时间不能晚于结束时间");
        }
        
        IPage<ChatMessageDTO> result = chatSearchService.searchMessagesByTimeRange(
                sessionId, keyword, startTime, endTime, page, size);
        return Result.success(result);
    }
}
