package com.team.controller;

import com.team.admin.common.Result;
import com.team.admin.dto.TaskDTO;
import com.team.admin.dto.TaskPublishRequest;
import com.team.admin.service.TaskService;
import com.team.security.JwtAuthenticationFilter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@Validated
@PreAuthorize("hasRole('USER')")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/publish")
    public Result<TaskDTO> publish(@Valid @RequestBody TaskPublishRequest request) {
        Long publisherId = getCurrentUserId();
        if (publisherId == null) {
            return Result.error(401, "未登录或用户信息缺失");
        }
        try {
            TaskDTO dto = taskService.publishTask(request, publisherId);
            return Result.success("发布成功", dto);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return Result.error(ex.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<List<TaskDTO>> myTasks() {
        Long publisherId = getCurrentUserId();
        if (publisherId == null) {
            return Result.error(401, "未登录或用户信息缺失");
        }
        List<TaskDTO> list = taskService.listMyTasks(publisherId);
        return Result.success(list);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object details = authentication.getDetails();
        if (details instanceof JwtAuthenticationFilter.JwtUserDetails) {
            return ((JwtAuthenticationFilter.JwtUserDetails) details).getUserId();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtAuthenticationFilter.JwtUserDetails) {
            return ((JwtAuthenticationFilter.JwtUserDetails) principal).getUserId();
        }
        return null;
    }
}
