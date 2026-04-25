package com.team.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.team.admin.dto.OrderDetailDTO;
import com.team.admin.dto.OrderListDTO;
import com.team.admin.dto.OrderSearchRequest;
import com.team.admin.entity.Order;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 分页查询订单列表（支持筛选）
     * @param searchRequest 搜索请求
     * @return 分页结果
     */
    IPage<OrderListDTO> getOrderList(OrderSearchRequest searchRequest);
    
    /**
     * 根据ID查询订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    OrderDetailDTO getOrderDetail(Long id);
    
    /**
     * 根据订单号查询订单
     * @param orderNo 订单号
     * @return 订单信息
     */
    Order getOrderByOrderNo(String orderNo);
    
    /**
     * 创建订单
     * @param order 订单信息
     * @return 创建结果
     */
    boolean createOrder(Order order);
    
    /**
     * 更新订单
     * @param order 订单信息
     * @return 更新结果
     */
    boolean updateOrder(Order order);
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 订单状态
     * @return 更新结果
     */
    boolean updateOrderStatus(Long id, String status);
    
    /**
     * 分配跑腿员
     * @param orderId 订单ID
     * @param runnerId 跑腿员ID
     * @return 更新结果
     */
    boolean assignRunner(Long orderId, Long runnerId);
}
