package com.team.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.admin.dto.OrderDetailDTO;
import com.team.admin.dto.OrderListDTO;
import com.team.admin.entity.Order;
import com.team.dto.CustomerOrderListDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 分页查询订单列表（包含用户姓名）
     * @param page 分页对象
     * @param status 订单状态筛选
     * @param runnerName 跑腿员姓名筛选
     * @param customerName 客户姓名筛选
     * @return 分页结果
     */
    IPage<OrderListDTO> selectOrderListWithNames(Page<OrderListDTO> page, 
                                                @Param("status") String status,
                                                @Param("runnerName") String runnerName,
                                                @Param("customerName") String customerName);
    
    /**
     * 用户端：分页查询当前客户的订单列表
     */
    IPage<CustomerOrderListDTO> selectCustomerOrders(Page<CustomerOrderListDTO> page,
                                                     @Param("customerId") Long customerId,
                                                     @Param("status") String status,
                                                     @Param("sort") String sort);
    
    /**
     * 根据订单ID查询订单详情（包含用户姓名和时间线）
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDetailDTO selectOrderDetailById(@Param("orderId") Long orderId);

    /**
     * 用户端：根据订单ID与客户ID查询订单详情
     */
    OrderDetailDTO selectOrderDetailByIdAndCustomer(@Param("orderId") Long orderId,
                                                    @Param("customerId") Long customerId);
}

