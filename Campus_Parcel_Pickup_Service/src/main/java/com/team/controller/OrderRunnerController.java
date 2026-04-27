package com.team.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.team.admin.common.Result;
import com.team.admin.dto.OrderDetailDTO;
import com.team.admin.service.OrderService;
import com.team.dto.CustomerOrderListDTO;
import com.team.dto.CustomerOrderSearchRequest;
import com.team.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 跑腿端「我的接单」接口
 */
@RestController
@RequestMapping("/api/runner/orders")
@Validated
@PreAuthorize("hasRole('RUNNER')")
public class OrderRunnerController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result<IPage<CustomerOrderListDTO>> list(@Validated CustomerOrderSearchRequest searchRequest) {
        Long runnerId = getCurrentUserId();
        if (runnerId == null) {
            return Result.error(401, "未登录或用户信息缺失");
        }
        IPage<CustomerOrderListDTO> page = orderService.getRunnerOrders(runnerId, searchRequest);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<OrderDetailDTO> detail(@PathVariable Long id) {
        Long runnerId = getCurrentUserId();
        if (runnerId == null) {
            return Result.error(401, "未登录或用户信息缺失");
        }
        OrderDetailDTO detail = orderService.getRunnerOrderDetail(id, runnerId);
        if (detail == null) {
            return Result.error(404, "订单不存在或无权查看");
        }
        return Result.success(detail);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
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
