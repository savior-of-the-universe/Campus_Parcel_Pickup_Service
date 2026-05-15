package com.team.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.team.admin.common.Result;
import com.team.admin.dto.OrderDetailDTO;
import com.team.admin.dto.OrderListDTO;
import com.team.admin.dto.OrderSearchRequest;
import com.team.admin.dto.PageResult;
import com.team.admin.dto.TaskDTO;
import com.team.admin.service.OrderService;
import com.team.admin.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客服端订单管理接口：查看全量订单，支持订单号/学号检索
 */
@RestController
@RequestMapping("/api/cs/orders")
@Validated
@PreAuthorize("hasAnyRole('CS','ADMIN')")
public class OrderCustomerServiceController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TaskService taskService;

    /**
     * 分页查询全量订单（旧 orders 表），支持状态、订单号、客户学号、姓名等筛选
     */
    @GetMapping
    public Result<IPage<OrderListDTO>> list(@Validated OrderSearchRequest searchRequest) {
        IPage<OrderListDTO> result = orderService.getOrderList(searchRequest);
        return Result.success(result);
    }

    /**
     * 查询订单详情（旧 orders 表）
     */
    @GetMapping("/{id}")
    public Result<OrderDetailDTO> detail(@PathVariable Long id) {
        OrderDetailDTO detail = orderService.getOrderDetail(id);
        if (detail == null) {
            return Result.error(404, "订单不存在");
        }
        return Result.success(detail);
    }

    /**
     * 客服查看全量任务列表（task 表）
     */
    @GetMapping("/tasks")
    public Result<PageResult<TaskDTO>> listTasks(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        PageResult<TaskDTO> result = taskService.getAllTasks(status, keyword, page, size);
        return Result.success(result);
    }

    /**
     * 客服查看单个任务详情（task 表）
     */
    @GetMapping("/tasks/{id}")
    public Result<TaskDTO> taskDetail(@PathVariable Long id) {
        // 客服可查看任意任务，传 publisherId=null 走宽松查询
        TaskDTO dto = taskService.getTaskDetail(id, null);
        if (dto == null) {
            // 尝试用管理员视角（传 0L 触发无限制查询）
            return Result.error(404, "任务不存在");
        }
        return Result.success(dto);
    }
}
