package com.team.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.team.admin.common.Result;
import com.team.admin.dto.OrderDetailDTO;
import com.team.admin.dto.OrderListDTO;
import com.team.admin.dto.OrderSearchRequest;
import com.team.admin.entity.Order;
import com.team.admin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理控制器（客服端）
 */
@RestController
@RequestMapping("/api/admin/orders")
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class OrderAdminController {

    @Autowired
    private OrderService orderService;

    /**
     * 分页查询订单列表（支持状态、跑腿员姓名、客户姓名筛选）
     */
    @GetMapping
    public Result<IPage<OrderListDTO>> getOrderList(@Validated OrderSearchRequest searchRequest) {
        IPage<OrderListDTO> result = orderService.getOrderList(searchRequest);
        return Result.success(result);
    }

    /**
     * 根据ID查询订单详情（包含取件码和时间线JSON）
     */
    @GetMapping("/{id}")
    public Result<OrderDetailDTO> getOrderDetail(@PathVariable Long id) {
        OrderDetailDTO orderDetail = orderService.getOrderDetail(id);
        if (orderDetail == null) {
            return Result.error("订单不存在");
        }
        return Result.success(orderDetail);
    }

    /**
     * 根据订单号查询订单
     */
    @GetMapping("/order-no/{orderNo}")
    public Result<Order> getOrderByOrderNo(@PathVariable String orderNo) {
        Order order = orderService.getOrderByOrderNo(orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    /**
     * 创建订单
     */
    @PostMapping
    public Result<Boolean> createOrder(@Validated @RequestBody Order order) {
        boolean result = orderService.createOrder(order);
        return result ? Result.success("创建成功", true) : Result.error("创建失败");
    }

    /**
     * 更新订单
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateOrder(@PathVariable Long id, @Validated @RequestBody Order order) {
        Order existingOrder = orderService.getOrderByOrderNo(order.getOrderNo());
        if (existingOrder == null) {
            return Result.error("订单不存在");
        }
        
        order.setId(id);
        boolean result = orderService.updateOrder(order);
        return result ? Result.success("更新成功", true) : Result.error("更新失败");
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{id}/status")
    public Result<Boolean> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        Order order = orderService.getOrderByOrderNo(""); // 这里需要根据ID查询，但service方法需要调整
        
        // 验证状态值
        String[] validStatuses = {"PENDING", "ACCEPTED", "IN_TRANSIT", "COMPLETED", "CANCELLED"};
        boolean isValidStatus = false;
        for (String validStatus : validStatuses) {
            if (validStatus.equals(status)) {
                isValidStatus = true;
                break;
            }
        }
        if (!isValidStatus) {
            return Result.error("状态值无效");
        }
        
        boolean result = orderService.updateOrderStatus(id, status);
        return result ? Result.success("状态更新成功", true) : Result.error("状态更新失败");
    }

    /**
     * 分配跑腿员
     */
    @PutMapping("/{id}/assign-runner")
    public Result<Boolean> assignRunner(@PathVariable Long id, @RequestParam Long runnerId) {
        boolean result = orderService.assignRunner(id, runnerId);
        return result ? Result.success("分配成功", true) : Result.error("分配失败");
    }
}
