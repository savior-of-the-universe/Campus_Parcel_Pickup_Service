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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
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
        return orderMapper.selectOrderListWithNames(page,
                searchRequest.getStatus(),
                searchRequest.getRunnerName(),
                searchRequest.getCustomerName());
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
    public OrderDetailDTO getOrderDetail(Long id) {
        return orderMapper.selectOrderDetailById(id);
    }

    @Override
    public OrderDetailDTO getCustomerOrderDetail(Long id, Long customerId) {
        OrderDetailDTO detail = orderMapper.selectOrderDetailByIdAndCustomer(id, customerId);
        if (detail == null) {
            return null;
        }
        Order order = orderMapper.selectById(id);
        if (order != null) {
            detail.setTimeline(parseTimeline(order.getTimeline()));
        }
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
        Order order = new Order();
        order.setId(id);
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    public boolean assignRunner(Long orderId, Long runnerId) {
        Order order = new Order();
        order.setId(orderId);
        order.setRunnerId(runnerId);
        order.setStatus("ACCEPTED");
        order.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateById(order) > 0;
    }

    private List<OrderDetailDTO.TimelineEvent> parseTimeline(String timelineJson) {
        if (!StringUtils.hasText(timelineJson)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(timelineJson, new TypeReference<List<OrderDetailDTO.TimelineEvent>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}

