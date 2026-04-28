package com.team.controller;

import com.team.admin.common.Result;
import com.team.admin.dto.PageResult;
import com.team.admin.dto.TaskDTO;
import com.team.admin.dto.TaskStatusUpdateRequest;
import com.team.admin.service.TaskService;
import com.team.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 跑腿员任务相关接口
 */
@RestController
@RequestMapping("/api/runner/tasks")
@PreAuthorize("hasRole('RUNNER')")
public class RunnerTaskController {

    @Autowired
    private TaskService taskService;

    /**
     * 跑腿员分页查询任务列表
     * @param statusGroup available=待接单, mine=我接的进行中, completed=已完成/取消, 不传=全部
     */
    @GetMapping
    public Result<PageResult<TaskDTO>> list(
            @RequestParam(value = "statusGroup", required = false) String statusGroup,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Long runnerId = getCurrentUserId();
        if (runnerId == null) {
            return Result.error(401, "未登录或用户信息缺失");
        }
        PageResult<TaskDTO> result = taskService.getRunnerTasks(runnerId, statusGroup, page, size);
        return Result.success(result);
    }

    /**
     * 跑腿员查看任务详情
     */
    @GetMapping("/{taskId}")
    public Result<TaskDTO> detail(@PathVariable Long taskId) {
        Long runnerId = getCurrentUserId();
        if (runnerId == null) {
            return Result.error(401, "未登录或用户信息缺失");
        }
        TaskDTO dto = taskService.getTaskDetailForRunner(taskId, runnerId);
        if (dto == null) {
            return Result.error(404, "任务不存在或无权查看");
        }
        return Result.success(dto);
    }

    /**
     * 跑腿员更新任务状态
     * PUT /api/runner/tasks/{taskId}/status
     * 请求体：{ "toStatus": "ACCEPTED" | "IN_TRANSIT" | "COMPLETED", "proofImageUrl": "..." }
     */
    @PutMapping("/{taskId}/status")
    public Result<TaskDTO> updateStatus(
            @PathVariable Long taskId,
            @RequestBody TaskStatusUpdateRequest request) {
        Long runnerId = getCurrentUserId();
        if (runnerId == null) {
            return Result.error(401, "未登录或用户信息缺失");
        }
        try {
            TaskDTO dto = taskService.updateTaskStatus(taskId, runnerId, request);
            return Result.success("状态更新成功", dto);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 待接单任务大厅（无需登录身份，仅需RUNNER角色）
     * GET /api/runner/tasks/available
     * @param deliveryPoint 快递点关键字筛选（可选）
     * @param sortByPoints  true=按积分倒序（可选，默认按时间）
     */
    @GetMapping("/available")
    public Result<PageResult<TaskDTO>> available(
            @RequestParam(value = "deliveryPoint", required = false) String deliveryPoint,
            @RequestParam(value = "sortByPoints", required = false) Boolean sortByPoints,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageResult<TaskDTO> result = taskService.getAvailableTasks(deliveryPoint, sortByPoints, page, size);
        return Result.success(result);
    }

    /**
     * 接单（乐观锁防并发）
     * POST /api/runner/tasks/{taskId}/accept
     */
    @PostMapping("/{taskId}/accept")
    public Result<TaskDTO> accept(@PathVariable Long taskId) {
        Long runnerId = getCurrentUserId();
        if (runnerId == null) {
            return Result.error(401, "未登录或用户信息缺失");
        }
        try {
            TaskDTO dto = taskService.acceptTask(taskId, runnerId);
            return Result.success("接单成功", dto);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return Result.error(ex.getMessage());
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;
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
