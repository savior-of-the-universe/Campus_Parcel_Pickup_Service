package com.team.admin.service;

import com.team.admin.dto.PageResult;
import com.team.admin.dto.TaskDTO;
import com.team.admin.dto.TaskPublishRequest;
import com.team.admin.dto.TaskStatusUpdateRequest;

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
     * 获取单个任务详情（发布者视角）
     */
    TaskDTO getTaskDetail(Long taskId, Long publisherId);

    /**
     * 跑腿员分页查询可接/已接任务
     * @param runnerId 跑腿员ID
     * @param statusGroup "available"=待接单, "mine"=我接的进行中, "completed"=已完成/取消, null=全部
     */
    PageResult<TaskDTO> getRunnerTasks(Long runnerId, String statusGroup, int page, int size);

    /**
     * 获取待接单任务列表（支持快递点筛选、积分排序）
     * @param deliveryPoint 快递点关键字（可空）
     * @param sortByPoints  true=按积分倒序，false/null=按发布时间倒序
     */
    PageResult<TaskDTO> getAvailableTasks(String deliveryPoint, Boolean sortByPoints, int page, int size);

    /**
     * 获取任务详情（跑腿员视角，可查看待接单或自己接的任务）
     */
    TaskDTO getTaskDetailForRunner(Long taskId, Long runnerId);

    /**
     * 跑腿员接单（乐观锁防并发）
     */
    TaskDTO acceptTask(Long taskId, Long runnerId);

    /**
     * 跑腿员更新任务状态（状态机校验）
     * PENDING→ACCEPTED, ACCEPTED→IN_TRANSIT, IN_TRANSIT→COMPLETED
     * @param taskId     任务ID
     * @param runnerId   操作跑腿员ID
     * @param request    目标状态及凭证图
     * @return 更新后的 TaskDTO
     */
    TaskDTO updateTaskStatus(Long taskId, Long runnerId, TaskStatusUpdateRequest request);
}

