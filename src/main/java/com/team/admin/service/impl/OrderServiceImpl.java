package com.team.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.admin.dto.OrderDetailDTO;
import com.team.admin.dto.OrderListDTO;
import com.team.admin.dto.OrderSearchRequest;
import com.team.admin.entity.Order;
import com.team.admin.mapper.OrderMapper;
import com.team.admin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Override
    public IPage<OrderListDTO> getOrderList(OrderSearchRequest searchRequest) {
        Page<OrderListDTO> page = new Page<>(searchRequest.getPage(), searchRequest.getSize());
        return orderMapper.selectOrderListWithNames(page, 
                searchRequest.getStatus(), 
                searchRequest.getRunnerName(), 
                searchRequest.getCustomerName());
    }
    
    @Override
    public OrderDetailDTO getOrderDetail(Long id) {
        return orderMapper.selectOrderDetailById(id);
    }
    
    @Override
    public Order getOrderByOrderNo(String orderNo) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        return orderMapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean createOrder(Order order) {
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return orderMapper.insert(order) > 0;
    }
    
    @Override
    public boolean updateOrder(Order order) {
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
}
