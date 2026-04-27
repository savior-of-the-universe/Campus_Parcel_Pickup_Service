package com.team.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.admin.dto.OrderDetailDTO;
import com.team.admin.dto.OrderListDTO;
import com.team.admin.dto.OrderSearchRequest;
import com.team.admin.entity.Order;
import com.team.admin.mapper.OrderMapper;
import com.team.admin.service.OrderService;
import com.team.dto.CustomerOrderListDTO;
import com.team.dto.CustomerOrderSearchRequest;
import com.team.security.JwtAuthenticationFilter;
import com.team.utils.DataMaskingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;



/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public IPage<OrderListDTO> getOrderList(OrderSearchRequest searchRequest) {
        Page<OrderListDTO> page = new Page<>(searchRequest.getPage(), searchRequest.getSize());
        String sort = "DESC";
        if (StringUtils.hasText(searchRequest.getSort()) && "ASC".equalsIgnoreCase(searchRequest.getSort())) {
            sort = "ASC";
        }
        IPage<OrderListDTO> result = orderMapper.selectOrderListWithNames(page,
                searchRequest.getStatus(),
                searchRequest.getRunnerName(),
                searchRequest.getCustomerName(),
                searchRequest.getOrderNo(),
                searchRequest.getStudentId(),
                sort);
        // CS/ADMIN 入口，统一脱敏
        maskOrderList(result.getRecords());
        return result;
    }

    @Override
    public IPage<CustomerOrderListDTO> getCustomerOrders(Long customerId, CustomerOrderSearchRequest searchRequest) {
        Page<CustomerOrderListDTO> page = new Page<>(searchRequest.getPage(), searchRequest.getSize());
        String sort = "DESC";
        if (StringUtils.hasText(searchRequest.getSort()) && "ASC".equalsIgnoreCase(searchRequest.getSort())) {
            sort = "ASC";
        }
        return orderMapper.selectCustomerOrders(page, customerId, searchRequest.getStatus(), sort);
    }

    @Override
    public IPage<CustomerOrderListDTO> getRunnerOrders(Long runnerId, CustomerOrderSearchRequest searchRequest) {
        Page<CustomerOrderListDTO> page = new Page<>(searchRequest.getPage(), searchRequest.getSize());
        String sort = "DESC";
        if (StringUtils.hasText(searchRequest.getSort()) && "ASC".equalsIgnoreCase(searchRequest.getSort())) {
            sort = "ASC";
        }
        return orderMapper.selectRunnerOrders(page, runnerId, searchRequest.getStatus(), sort);
    }
    

    @Override
    public OrderDetailDTO getOrderDetail(Long id) {
        OrderDetailDTO detail = orderMapper.selectOrderDetailById(id);
        if (detail == null) {
            return null;
        }
        Order order = orderMapper.selectById(id);
        if (order != null) {
            List<OrderDetailDTO.TimelineEvent> timeline = parseTimeline(order.getTimeline());
            ensureTimeline(detail, order, timeline);
        }
        applyMaskForRole(detail, true);
        return detail;
    }

    @Override
    public OrderDetailDTO getCustomerOrderDetail(Long id, Long customerId) {
        OrderDetailDTO detail = orderMapper.selectOrderDetailByIdAndCustomer(id, customerId);
        if (detail == null) {
            return null;
        }
        Order order = orderMapper.selectById(id);
        if (order != null) {
            List<OrderDetailDTO.TimelineEvent> timeline = parseTimeline(order.getTimeline());
            ensureTimeline(detail, order, timeline);
        }
        applyMaskForRole(detail, true);
        return detail;
    }

    @Override
    public OrderDetailDTO getRunnerOrderDetail(Long id, Long runnerId) {
        OrderDetailDTO detail = orderMapper.selectOrderDetailByIdAndRunner(id, runnerId);
        if (detail == null) {
            return null;
        }
        Order order = orderMapper.selectById(id);
        if (order != null) {
            List<OrderDetailDTO.TimelineEvent> timeline = parseTimeline(order.getTimeline());
            ensureTimeline(detail, order, timeline);
        }
        applyMaskForRole(detail, true);
        return detail;
    }
    

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        return orderMapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean createOrder(Order order) {
        if (!StringUtils.hasText(order.getTitle())) {
            order.setTitle(order.getOrderNo());
        }
        if (order.getAmount() == null) {
            order.setAmount(BigDecimal.ZERO);
        }
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        // 初始化时间线
        order.setTimeline(buildInitialTimeline());
        return orderMapper.insert(order) > 0;
    }
    
    @Override
    public boolean updateOrder(Order order) {
        if (!StringUtils.hasText(order.getTitle())) {
            order.setTitle(order.getOrderNo());
        }
        if (order.getAmount() == null) {
            order.setAmount(BigDecimal.ZERO);
        }
        order.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateById(order) > 0;
    }

    
    @Override
    public boolean updateOrderStatus(Long id, String status) {
        Order current = orderMapper.selectById(id);
        if (current == null) {
            return false;
        }
        String timeline = appendTimelineEvent(current.getTimeline(), statusToEvent(status), statusDescription(status));
        Order order = new Order();
        order.setId(id);
        order.setStatus(status);
        order.setTimeline(timeline);
        order.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    public boolean assignRunner(Long orderId, Long runnerId) {
        Order current = orderMapper.selectById(orderId);
        if (current == null) {
            return false;
        }
        String timeline = appendTimelineEvent(current.getTimeline(), "跑腿员接单", "分配跑腿员" + runnerId);
        Order order = new Order();
        order.setId(orderId);
        order.setRunnerId(runnerId);
        order.setStatus("ACCEPTED");
        order.setTimeline(timeline);
        order.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateById(order) > 0;
    }

    private void maskOrderList(List<OrderListDTO> records) {
        if (records == null) {
            return;
        }
        for (OrderListDTO dto : records) {
            if (dto == null) {
                continue;
            }
            dto.setCustomerStudentId(DataMaskingUtils.maskStudentId(dto.getCustomerStudentId()));
            dto.setRunnerStudentId(DataMaskingUtils.maskStudentId(dto.getRunnerStudentId()));
            dto.setCustomerPhone(DataMaskingUtils.maskPhone(dto.getCustomerPhone()));
            dto.setRunnerPhone(DataMaskingUtils.maskPhone(dto.getRunnerPhone()));
        }
    }

    private void applyMaskForRole(OrderDetailDTO detail, boolean includePhoneAndStudent) {
        if (detail == null || !includePhoneAndStudent) {
            return;
        }
        String role = getCurrentRole();
        if (isCsOrAdmin(role)) {
            // 客服/管理员：学号、手机号全部脱敏
            detail.setCustomerStudentId(DataMaskingUtils.maskStudentId(detail.getCustomerStudentId()));
            detail.setRunnerStudentId(DataMaskingUtils.maskStudentId(detail.getRunnerStudentId()));
            detail.setCustomerPhone(DataMaskingUtils.maskPhone(detail.getCustomerPhone()));
            detail.setRunnerPhone(DataMaskingUtils.maskPhone(detail.getRunnerPhone()));
        } else {
            // 非客服（用户/跑腿）：隐藏学号，仅保留对端手机号明文
            detail.setCustomerStudentId(null);
            detail.setRunnerStudentId(null);
        }
    }

    private void ensureTimeline(OrderDetailDTO detail, Order order, List<OrderDetailDTO.TimelineEvent> timeline) {
        if (timeline == null) {
            timeline = new ArrayList<>();
        }
        // 确保创建事件存在
        if (timeline.stream().noneMatch(e -> "订单创建".equalsIgnoreCase(e.getEvent()))) {
            LocalDateTime created = order.getCreateTime() != null ? order.getCreateTime() : LocalDateTime.now();
            timeline.add(new OrderDetailDTO.TimelineEvent("订单创建", "用户发布订单", "CUSTOMER", created));
        }
        // 按当前状态补齐缺失的状态节点，保证完整链路
        fillMissingStatusEvents(order, timeline);
        timeline.sort((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));
        detail.setTimeline(timeline);
    }


    private String buildInitialTimeline() {
        List<OrderDetailDTO.TimelineEvent> list = new ArrayList<>();
        list.add(new OrderDetailDTO.TimelineEvent("订单创建", "用户发布订单", "CUSTOMER", LocalDateTime.now()));
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private String appendTimelineEvent(String timelineJson, String event, String description) {
        List<OrderDetailDTO.TimelineEvent> list = parseTimeline(timelineJson);
        OrderDetailDTO.TimelineEvent timelineEvent = new OrderDetailDTO.TimelineEvent(event, description, normalizeRole(getCurrentRole()), LocalDateTime.now());
        list.add(timelineEvent);
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return timelineJson;
        }
    }

    private void fillMissingStatusEvents(Order order, List<OrderDetailDTO.TimelineEvent> timeline) {
        String status = order.getStatus();
        List<String> requiredStatuses = requiredStatusesByCurrent(status);
        if (requiredStatuses.isEmpty()) {
            return;
        }
        LocalDateTime start = order.getCreateTime() != null ? order.getCreateTime() : LocalDateTime.now();
        LocalDateTime end = order.getUpdateTime() != null ? order.getUpdateTime() : start;
        if (end.isBefore(start)) {
            end = start.plusMinutes(1);
        }
        Duration span = Duration.between(start, end);
        long seconds = Math.max(span.getSeconds(), requiredStatuses.size() - 1);

        for (int i = 0; i < requiredStatuses.size(); i++) {
            String stepStatus = requiredStatuses.get(i);
            boolean exists = timeline.stream().anyMatch(e -> stepStatus.equalsIgnoreCase(e.getEvent()));
            if (exists) {
                continue;
            }
            long offset = requiredStatuses.size() == 1 ? seconds : (seconds * i) / Math.max(requiredStatuses.size() - 1, 1);
            LocalDateTime ts = start.plusSeconds(offset);
            timeline.add(new OrderDetailDTO.TimelineEvent(
                    statusToEvent(stepStatus),
                    statusDescription(stepStatus),
                    syntheticRoleForStatus(order, stepStatus),
                    ts
            ));
        }
    }

    private List<String> requiredStatusesByCurrent(String currentStatus) {
        List<String> steps = new ArrayList<>();
        if (!StringUtils.hasText(currentStatus)) {
            return steps;
        }
        String status = currentStatus.toUpperCase();
        // 按业务链路补齐历史节点
        steps.add("PENDING");
        if ("ACCEPTED".equals(status)) {
            steps.add("ACCEPTED");
        } else if ("IN_TRANSIT".equals(status)) {
            steps.add("ACCEPTED");
            steps.add("IN_TRANSIT");
        } else if ("COMPLETED".equals(status)) {
            steps.add("ACCEPTED");
            steps.add("IN_TRANSIT");
            steps.add("COMPLETED");
        } else if ("CANCELLED".equals(status)) {
            steps.add("CANCELLED");
        }
        return steps;
    }

    private String syntheticRoleForStatus(Order order, String status) {
        if ("PENDING".equalsIgnoreCase(status)) {
            return "SYSTEM";
        }
        if ("CANCELLED".equalsIgnoreCase(status)) {
            return "SYSTEM";
        }
        return order.getRunnerId() != null ? "RUNNER" : "SYSTEM";
    }


    private String normalizeRole(String role) {
        if (!StringUtils.hasText(role)) {
            return "SYSTEM";
        }
        return role.toUpperCase();
    }

    private String statusToEvent(String status) {
        if (status == null) {
            return "状态更新";
        }
        switch (status) {
            case "PENDING":
                return "订单发布";
            case "ACCEPTED":
                return "跑腿员接单";
            case "IN_TRANSIT":
                return "取件/配送中";
            case "COMPLETED":
                return "订单完成";
            case "CANCELLED":
                return "订单取消";
            default:
                return status;
        }
    }


    private String statusDescription(String status) {
        switch (status) {
            case "PENDING":
                return "用户发布订单，等待接单";
            case "ACCEPTED":
                return "跑腿员已接单";
            case "IN_TRANSIT":
                return "跑腿员已取件，配送中";
            case "COMPLETED":
                return "订单已送达并完成";
            case "CANCELLED":
                return "订单已取消";
            default:
                return "状态更新";
        }
    }


    private String getCurrentRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object details = authentication.getDetails();
        if (details instanceof JwtAuthenticationFilter.JwtUserDetails) {
            return ((JwtAuthenticationFilter.JwtUserDetails) details).getRole();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtAuthenticationFilter.JwtUserDetails) {
            return ((JwtAuthenticationFilter.JwtUserDetails) principal).getRole();
        }
        return null;
    }

    private boolean isCsOrAdmin(String role) {
        if (!StringUtils.hasText(role)) {
            return false;
        }
        return "CS".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    private List<OrderDetailDTO.TimelineEvent> parseTimeline(String timelineJson) {
        if (!StringUtils.hasText(timelineJson)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(timelineJson, new TypeReference<List<OrderDetailDTO.TimelineEvent>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}

