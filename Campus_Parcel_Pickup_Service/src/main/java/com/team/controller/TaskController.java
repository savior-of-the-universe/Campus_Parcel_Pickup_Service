package com.team.controller;

import com.team.admin.common.Result;
import com.team.admin.dto.PageResult;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@Validated
@PreAuthorize("hasRole('USER')")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /** 发布任务 */
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

    /** 获取我发布的任务（无分页，兼容旧接口） */
    @GetMapping("/my")
    public Result<List<TaskDTO>> myTasks() {
        Long publisherId = getCurrentUserId();
        if (publisherId == null) {
            return Result.error(401, "未登录或用户信息缺失");
        }
        List<TaskDTO> list = taskService.listMyTasks(publisherId);
        return Result.success(list);
    }

    /**
     * 分页查询我发布的任务
     * @param status "in_progress"=进行中(PENDING/ACCEPTED/IN_TRANSIT)，"completed"=已完成(COMPLETED/CANCELLED)，不传则全部
     */
    @GetMapping("/my-published")
    public Result<PageResult<TaskDTO>> myPublished(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Long publisherId = getCurrentUserId();
        if (publisherId == null) {
            return Result.error(401, "未登录或用户信息缺失");
        }
        PageResult<TaskDTO> result = taskService.getMyPublished(publisherId, status, page, size);
        return Result.success(result);
    }

    /** 任务详情 */
    @GetMapping("/{id}")
    public Result<TaskDTO> detail(@PathVariable Long id) {
        Long publisherId = getCurrentUserId();
        if (publisherId == null) {
            return Result.error(401, "未登录或用户信息缺失");
        }
        TaskDTO dto = taskService.getTaskDetail(id, publisherId);
        if (dto == null) {
            return Result.error(404, "任务不存在或无权查看");
        }
        return Result.success(dto);
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
