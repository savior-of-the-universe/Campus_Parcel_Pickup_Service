package com.team.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private static final String STATUS_PENDING = "PENDING";

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public TaskDTO publishTask(TaskPublishRequest request, Long publisherId) {
        if (publisherId == null) {
            throw new IllegalArgumentException("发布者信息缺失");
        }
        // 重复发布校验：同一发布者、取件码且状态待接单
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
        return dto;
    }
}
