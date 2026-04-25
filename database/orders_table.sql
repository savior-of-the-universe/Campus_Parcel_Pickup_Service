-- 订单表
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
    `customer_id` BIGINT COMMENT '发布者ID',
    `runner_id` BIGINT COMMENT '跑腿员ID',
    `status` VARCHAR(32) NOT NULL COMMENT '订单状态：PENDING, ACCEPTED, IN_TRANSIT, COMPLETED, CANCELLED',
    `pickup_code` VARCHAR(32) COMMENT '取件码',
    `timeline` TEXT COMMENT '时间线JSON字符串',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_customer_id` (`customer_id`),
    KEY `idx_runner_id` (`runner_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 插入示例数据
INSERT INTO `orders` (`order_no`, `customer_id`, `runner_id`, `status`, `pickup_code`, `timeline`) VALUES
('ORD20240425001', 1, NULL, 'PENDING', 'ABC123', '[{"event":"订单创建","description":"用户发布取件订单","timestamp":"2024-04-25T10:00:00"}]'),
('ORD20240425002', 2, 3, 'ACCEPTED', 'DEF456', '[{"event":"订单创建","description":"用户发布取件订单","timestamp":"2024-04-25T09:30:00"},{"event":"跑腿员接单","description":"跑腿员王五接受了订单","timestamp":"2024-04-25T09:35:00"}]'),
('ORD20240425003', 4, 5, 'IN_TRANSIT', 'GHI789', '[{"event":"订单创建","description":"用户发布取件订单","timestamp":"2024-04-25T08:00:00"},{"event":"跑腿员接单","description":"跑腿员赵六接受了订单","timestamp":"2024-04-25T08:05:00"},{"event":"取件中","description":"跑腿员已取件，正在配送","timestamp":"2024-04-25T08:30:00"}]'),
('ORD20240425004', 6, 7, 'COMPLETED', 'JKL012', '[{"event":"订单创建","description":"用户发布取件订单","timestamp":"2024-04-24T15:00:00"},{"event":"跑腿员接单","description":"跑腿员张三接受了订单","timestamp":"2024-04-24T15:10:00"},{"event":"取件中","description":"跑腿员已取件，正在配送","timestamp":"2024-04-24T15:30:00"},{"event":"配送完成","description":"订单已送达","timestamp":"2024-04-24T16:00:00"}]');
