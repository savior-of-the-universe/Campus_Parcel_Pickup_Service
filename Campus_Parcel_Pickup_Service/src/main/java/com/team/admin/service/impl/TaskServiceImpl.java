package com.team.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.admin.dto.PageResult;
import com.team.admin.dto.TaskDTO;
import com.team.admin.dto.TaskPublishRequest;
import com.team.admin.dto.TaskStatusUpdateRequest;
import com.team.admin.entity.Task;
import com.team.admin.entity.TaskLog;
import com.team.admin.entity.User;
import com.team.admin.mapper.TaskLogMapper;
import com.team.admin.mapper.TaskMapper;
import com.team.admin.mapper.UserMapper;
import com.team.admin.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private static final String STATUS_PENDING = "PENDING";
    private static final List<String> IN_PROGRESS_STATUSES = Arrays.asList("PENDING", "ACCEPTED", "IN_TRANSIT");
    private static final List<String> COMPLETED_STATUSES = Arrays.asList("COMPLETED", "CANCELLED");

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskLogMapper taskLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public TaskDTO publishTask(TaskPublishRequest request, Long publisherId) {
        if (publisherId == null) {
            throw new IllegalArgumentException("发布者信息缺失");
        }
        QueryWrapper<Task> duplicateWrapper = new QueryWrapper<>();
        duplicateWrapper.eq("publisher_id", publisherId)
                .eq("pickup_code", request.getPickupCode())
                .eq("status", STATUS_PENDING);
        Long count = taskMapper.selectCount(duplicateWrapper);
        if (count != null && count > 0) {
            throw new IllegalStateException("已存在相同取件码的待接单任务，禁止重复发布");
        }

        Task task = new Task();
        task.setPublisherId(publisherId);
        task.setPickupCode(request.getPickupCode());
        task.setDeliveryPoint(request.getDeliveryPoint());
        task.setRewardPoints(request.getRewardPoints());
        task.setRemark(StringUtils.hasText(request.getRemark()) ? request.getRemark().trim() : null);
        task.setStatus(STATUS_PENDING);
        task.setCreateTime(LocalDateTime.now());

        if (StringUtils.hasText(request.getWeight())) {
            try {
                BigDecimal weight = new BigDecimal(request.getWeight());
                task.setWeight(weight);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("重量格式不正确");
            }
        }

        taskMapper.insert(task);
        return toDTO(task);
    }

    @Override
    public List<TaskDTO> listMyTasks(Long publisherId) {
        if (publisherId == null) {
            return new ArrayList<>();
        }
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("publisher_id", publisherId)
                .orderByDesc("create_time");
        List<Task> tasks = taskMapper.selectList(wrapper);
        return tasks.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public PageResult<TaskDTO> getMyPublished(Long publisherId, String statusGroup, int page, int size) {
        if (publisherId == null) {
            return new PageResult<>(new ArrayList<>(), 0, page, size);
        }
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        if (size > 50) size = 50;

        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("publisher_id", publisherId);

        if ("in_progress".equals(statusGroup)) {
            wrapper.in("status", IN_PROGRESS_STATUSES);
        } else if ("completed".equals(statusGroup)) {
            wrapper.in("status", COMPLETED_STATUSES);
        }
        wrapper.orderByDesc("create_time");

        Page<Task> pageParam = new Page<>(page, size);
        Page<Task> result = taskMapper.selectPage(pageParam, wrapper);

        List<TaskDTO> dtos = result.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(dtos, result.getTotal(), page, size);
    }

    @Override
    public TaskDTO getTaskDetail(Long taskId, Long publisherId) {
        if (taskId == null || publisherId == null) {
            return null;
        }
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("id", taskId).eq("publisher_id", publisherId);
        Task task = taskMapper.selectOne(wrapper);
        return task == null ? null : toDTO(task);
    }

    @Override
    public PageResult<TaskDTO> getAvailableTasks(String deliveryPoint, Boolean sortByPoints, int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        if (size > 50) size = 50;

        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "PENDING");
        if (StringUtils.hasText(deliveryPoint)) {
            wrapper.like("delivery_point", deliveryPoint.trim());
        }
        if (Boolean.TRUE.equals(sortByPoints)) {
            wrapper.orderByDesc("reward_points");
        } else {
            wrapper.orderByDesc("create_time");
        }

        Page<Task> pageParam = new Page<>(page, size);
        Page<Task> result = taskMapper.selectPage(pageParam, wrapper);
        List<TaskDTO> dtos = result.getRecords().stream().map(t -> toDTO(t, true)).collect(Collectors.toList());
        return new PageResult<>(dtos, result.getTotal(), page, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskDTO acceptTask(Long taskId, Long runnerId) {
        if (taskId == null || runnerId == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 查询任务（带 version 做乐观锁）
        Task task = taskMapper.selectById(taskId);
        if (task == null || task.getDeleted() == 1) {
            throw new IllegalStateException("任务不存在");
        }
        if (!"PENDING".equals(task.getStatus())) {
            throw new IllegalStateException("任务已被接单，请刷新后重试");
        }

        User runner = userMapper.selectById(runnerId);
        if (runner == null) {
            throw new IllegalStateException("跑腿员信息不存在");
        }

        LocalDateTime now = LocalDateTime.now();

        // 使用乐观锁：条件 status=PENDING AND version=当前版本
        // MyBatis-Plus @Version 会自动拦截 update，但这里用 UpdateWrapper 更新实体对象时需先 set version
        // 直接 update entity，MP 会自动带 version 条件
        task.setStatus("ACCEPTED");
        task.setRunnerId(runnerId);
        task.setRunnerNickname(runner.getName());
        task.setRunnerPhone(runner.getPhone());
        task.setAcceptTime(now);
        // version 字段由 MP @Version 自动 +1

        int updated = taskMapper.updateById(task);
        if (updated == 0) {
            // 乐观锁失败：版本号不匹配，说明被其他人抢先接单
            throw new IllegalStateException("手速太慢，任务已被其他人接单！");
        }

        // 写 task_log
        TaskLog log = new TaskLog();
        log.setTaskId(taskId);
        log.setOperatorId(runnerId);
        log.setOperatorRole("RUNNER");
        log.setFromStatus("PENDING");
        log.setToStatus("ACCEPTED");
        log.setCreateTime(now);
        taskLogMapper.insert(log);

        Task updated2 = taskMapper.selectById(taskId);
        return toDTO(updated2, false);
    }

    @Override
    public PageResult<TaskDTO> getRunnerTasks(Long runnerId, String statusGroup, int page, int size) {
        if (runnerId == null) {
            return new PageResult<>(new ArrayList<>(), 0, page, size);
        }
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        if (size > 50) size = 50;

        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        if ("available".equals(statusGroup)) {
            // 待接单：状态为 PENDING
            wrapper.eq("status", "PENDING");
        } else if ("mine".equals(statusGroup)) {
            // 我接的进行中：ACCEPTED 或 IN_TRANSIT
            wrapper.eq("runner_id", runnerId)
                    .in("status", Arrays.asList("ACCEPTED", "IN_TRANSIT"));
        } else if ("completed".equals(statusGroup)) {
            // 我的已完成/取消
            wrapper.eq("runner_id", runnerId)
                    .in("status", COMPLETED_STATUSES);
        } else {
            // 全部：待接单 + 我接的
            wrapper.and(w -> w.eq("status", "PENDING")
                    .or().eq("runner_id", runnerId));
        }
        wrapper.orderByDesc("create_time");

        Page<Task> pageParam = new Page<>(page, size);
        Page<Task> result = taskMapper.selectPage(pageParam, wrapper);
        List<TaskDTO> dtos = result.getRecords().stream().map(t -> toDTO(t, true)).collect(Collectors.toList());
        return new PageResult<>(dtos, result.getTotal(), page, size);
    }

    @Override
    public TaskDTO getTaskDetailForRunner(Long taskId, Long runnerId) {
        if (taskId == null || runnerId == null) return null;
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("id", taskId);
        Task task = taskMapper.selectOne(wrapper);
        if (task == null) return null;
        // 跑腿员只能查看：待接单 或 自己接的
        if (!"PENDING".equals(task.getStatus()) && !runnerId.equals(task.getRunnerId())) {
            return null;
        }
        return toDTO(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskDTO updateTaskStatus(Long taskId, Long runnerId, TaskStatusUpdateRequest request) {
        if (taskId == null || runnerId == null || request == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        String toStatus = request.getToStatus();
        if (!StringUtils.hasText(toStatus)) {
            throw new IllegalArgumentException("目标状态不能为空");
        }

        Task task = taskMapper.selectById(taskId);
        if (task == null || task.getDeleted() == 1) {
            throw new IllegalStateException("任务不存在");
        }
        String fromStatus = task.getStatus();

        // 状态机校验：合法流转路径
        validateTransition(fromStatus, toStatus, runnerId, task);

        LocalDateTime now = LocalDateTime.now();

        // 更新 task 字段
        UpdateWrapper<Task> update = new UpdateWrapper<>();
        update.eq("id", taskId).set("status", toStatus);

        if ("ACCEPTED".equals(toStatus)) {
            // 接单：绑定跑腿员信息
            User runner = userMapper.selectById(runnerId);
            if (runner == null) throw new IllegalStateException("跑腿员不存在");
            update.set("runner_id", runnerId)
                    .set("runner_nickname", runner.getName())
                    .set("runner_phone", runner.getPhone())
                    .set("accept_time", now);
        } else if ("IN_TRANSIT".equals(toStatus)) {
            update.set("pickup_time", now);
        } else if ("COMPLETED".equals(toStatus)) {
            update.set("complete_time", now);
        } else if ("CANCELLED".equals(toStatus)) {
            update.set("cancel_time", now);
        }

        taskMapper.update(null, update);

        // 写 task_log
        TaskLog log = new TaskLog();
        log.setTaskId(taskId);
        log.setOperatorId(runnerId);
        log.setOperatorRole("RUNNER");
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setRemark(request.getProofImageUrl());
        log.setCreateTime(now);
        taskLogMapper.insert(log);

        // 任务完成：积分从发布者转给跑腿员
        if ("COMPLETED".equals(toStatus)) {
            transferPoints(task.getPublisherId(), runnerId, task.getRewardPoints());
        }

        // 重新查询返回最新数据
        Task updated = taskMapper.selectById(taskId);
        return toDTO(updated);
    }

    /**
     * 状态机校验
     * PENDING → ACCEPTED（任何跑腿员）
     * ACCEPTED → IN_TRANSIT（必须是接单跑腿员）
     * IN_TRANSIT → COMPLETED（必须是接单跑腿员）
     */
    private void validateTransition(String from, String to, Long runnerId, Task task) {
        if ("PENDING".equals(from) && "ACCEPTED".equals(to)) {
            // 正常接单，任何跑腿员均可
            return;
        }
        if ("ACCEPTED".equals(from) && "IN_TRANSIT".equals(to)) {
            if (!runnerId.equals(task.getRunnerId())) {
                throw new IllegalStateException("只有接单跑腿员才能更新状态");
            }
            return;
        }
        if ("IN_TRANSIT".equals(from) && "COMPLETED".equals(to)) {
            if (!runnerId.equals(task.getRunnerId())) {
                throw new IllegalStateException("只有接单跑腿员才能更新状态");
            }
            return;
        }
        throw new IllegalStateException(
                String.format("不允许的状态流转：%s → %s", from, to));
    }

    /**
     * 积分转账：发布者扣积分，跑腿员加积分
     */
    private void transferPoints(Long publisherId, Long runnerId, Integer points) {
        if (points == null || points <= 0) return;

        // 扣发布者积分（不低于0）
        UpdateWrapper<User> deduct = new UpdateWrapper<>();
        deduct.eq("id", publisherId)
                .setSql("points = GREATEST(points - " + points + ", 0)");
        userMapper.update(null, deduct);

        // 加跑腿员积分
        UpdateWrapper<User> add = new UpdateWrapper<>();
        add.eq("id", runnerId)
                .setSql("points = points + " + points);
        userMapper.update(null, add);
    }

    private TaskDTO toDTO(Task task) {
        return toDTO(task, false);
    }

    /**
     * @param maskPickupCode true=列表模式(取件码后四位)，false=详情模式(完整取件码)
     */
    private TaskDTO toDTO(Task task, boolean maskPickupCode) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setPickupCode(maskPickupCode ? null : task.getPickupCode());
        // 后四位脱敏
        if (StringUtils.hasText(task.getPickupCode())) {
            String code = task.getPickupCode();
            dto.setPickupCodeMasked(code.length() >= 4
                    ? "****" + code.substring(code.length() - 4)
                    : "****");
        }
        dto.setDeliveryPoint(task.getDeliveryPoint());
        dto.setWeight(task.getWeight());
        dto.setRewardPoints(task.getRewardPoints());
        dto.setRemark(task.getRemark());
        dto.setStatus(task.getStatus());
        dto.setCreateTime(task.getCreateTime());
        dto.setRunnerNickname(task.getRunnerNickname());
        // 手机号脱敏：保留后四位
        if (StringUtils.hasText(task.getRunnerPhone())) {
            String phone = task.getRunnerPhone();
            if (phone.length() >= 4) {
                dto.setRunnerPhoneMasked("****" + phone.substring(phone.length() - 4));
            } else {
                dto.setRunnerPhoneMasked("****");
            }
        }
        // 组装状态时间线
        String status = task.getStatus();
        boolean cancelled = "CANCELLED".equals(status);
        List<TaskDTO.TimelineItem> timeline = new ArrayList<>();
        // 发布
        timeline.add(new TaskDTO.TimelineItem("PENDING", "发布任务", task.getCreateTime(), true));
        // 接单
        boolean accepted = task.getAcceptTime() != null
                || "ACCEPTED".equals(status) || "IN_TRANSIT".equals(status) || "COMPLETED".equals(status);
        timeline.add(new TaskDTO.TimelineItem("ACCEPTED", "跑腿员接单", task.getAcceptTime(), accepted && !cancelled));
        // 取件
        boolean pickedUp = task.getPickupTime() != null
                || "IN_TRANSIT".equals(status) || "COMPLETED".equals(status);
        timeline.add(new TaskDTO.TimelineItem("IN_TRANSIT", "已取到快递", task.getPickupTime(), pickedUp && !cancelled));
        // 完成
        boolean completed = task.getCompleteTime() != null || "COMPLETED".equals(status);
        timeline.add(new TaskDTO.TimelineItem("COMPLETED", "完成送达", task.getCompleteTime(), completed));
        // 取消（仅取消时追加）
        if (cancelled) {
            timeline.add(new TaskDTO.TimelineItem("CANCELLED", "任务取消", task.getCancelTime(), true));
        }
        dto.setTimeline(timeline);
        return dto;
    }
}
