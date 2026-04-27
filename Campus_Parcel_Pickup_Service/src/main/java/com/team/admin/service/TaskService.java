package com.team.admin.service;

import com.team.admin.dto.TaskDTO;
import com.team.admin.dto.TaskPublishRequest;

import java.util.List;

public interface TaskService {

    TaskDTO publishTask(TaskPublishRequest request, Long publisherId);

    List<TaskDTO> listMyTasks(Long publisherId);
}
