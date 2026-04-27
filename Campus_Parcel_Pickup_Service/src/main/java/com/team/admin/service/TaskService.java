package com.team.admin.service;

import com.team.admin.dto.PageResult;
import com.team.admin.dto.TaskDTO;
import com.team.admin.dto.TaskPublishRequest;

import java.util.List;

public interface TaskService {

    TaskDTO publishTask(TaskPublishRequest request, Long publisherId);

    List<TaskDTO> listMyTasks(Long publisherId);

    /**
     * 分页查询我发布的任务
     * @param publisherId 发布者ID
     * @param statusGroup "in_progress"(待接单/已接单/途中) 或 "completed"(已完成/已取消)，null 不过滤
     * @param page 页码（从1开始）
     * @param size 每页条数
     */
    PageResult<TaskDTO> getMyPublished(Long publisherId, String statusGroup, int page, int size);

    /**
     * 获取单个任务详情
     */
    TaskDTO getTaskDetail(Long taskId, Long publisherId);
}

