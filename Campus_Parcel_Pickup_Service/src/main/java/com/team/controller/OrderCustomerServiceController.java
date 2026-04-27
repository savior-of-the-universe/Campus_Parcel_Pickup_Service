package com.team.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.team.admin.common.Result;
import com.team.admin.dto.OrderDetailDTO;
import com.team.admin.dto.OrderListDTO;
import com.team.admin.dto.OrderSearchRequest;
import com.team.admin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    /**
     * 分页查询全量订单，支持状态、订单号、客户学号、姓名等筛选
     */
    @GetMapping
    public Result<IPage<OrderListDTO>> list(@Validated OrderSearchRequest searchRequest) {
        IPage<OrderListDTO> result = orderService.getOrderList(searchRequest);
        return Result.success(result);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{id}")
    public Result<OrderDetailDTO> detail(@PathVariable Long id) {
        OrderDetailDTO detail = orderService.getOrderDetail(id);
        if (detail == null) {
            return Result.error(404, "订单不存在");
        }
        return Result.success(detail);
    }
}
