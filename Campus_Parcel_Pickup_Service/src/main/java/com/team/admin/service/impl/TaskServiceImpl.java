package com.team.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.admin.dto.PageResult;
import com.team.admin.dto.TaskDTO;
import com.team.admin.dto.TaskPublishRequest;
import com.team.admin.entity.Task;
import com.team.admin.mapper.TaskMapper;
import com.team.admin.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    private TaskDTO toDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setPickupCode(task.getPickupCode());
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
        return dto;
    }
}
