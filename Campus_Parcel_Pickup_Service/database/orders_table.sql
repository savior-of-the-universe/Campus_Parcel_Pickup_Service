-- 订单表（旧版，供管理端订单管理/客服功能使用）
-- 效果页面：
--   · 管理端 → 订单管理（/admin/orders）→ 展示全部订单，可按订单号/状态/学号筛选
--   · 管理端 → 客服聊天中心 → 客服可查询订单号关联状态
--   · 跑腿端 → 我的订单（旧版）→ 查看 runner_id=自己 的订单
CREATE TABLE IF NOT EXISTS `orders` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no`    VARCHAR(64)   NOT NULL               COMMENT '订单号（唯一）',
    `title`       VARCHAR(255)  NOT NULL DEFAULT ''    COMMENT '订单标题',
    `amount`      DECIMAL(10,2) NOT NULL DEFAULT 0     COMMENT '订单金额',
    `customer_id` BIGINT                               COMMENT '发布者ID，关联 user.id',
    `runner_id`   BIGINT                               COMMENT '跑腿员ID，关联 user.id',
    `status`      VARCHAR(32)   NOT NULL               COMMENT '状态：PENDING / ACCEPTED / IN_TRANSIT / COMPLETED / CANCELLED',
    `pickup_code` VARCHAR(32)                          COMMENT '取件码',
    `timeline`    TEXT                                 COMMENT '时间线JSON字符串',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP                    COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no`  (`order_no`),
    KEY `idx_customer_id`     (`customer_id`),
    KEY `idx_runner_id`       (`runner_id`),
    KEY `idx_status`          (`status`),
    KEY `idx_create_time`     (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表（旧版）';

-- ============================================================
-- 测试/演示数据（多状态覆盖）
-- 说明：customer_id / runner_id 对应 user_table.sql 插入顺序
--       张三=1, 李四=2, 王五=3, 赵六=4, 客服小美=5
-- ============================================================
INSERT INTO `orders` (`order_no`, `title`, `amount`, `customer_id`, `runner_id`, `status`, `pickup_code`, `timeline`, `create_time`, `update_time`) VALUES
-- 待接单（管理端订单列表"待接单"栏可见）
('ORD20240425001', '代取快递-菜鸟驿站',  12.50, 1, NULL, 'PENDING',
 'ABC123', '[]', DATE_SUB(NOW(), INTERVAL 2 HOUR),   DATE_SUB(NOW(), INTERVAL 2 HOUR)),
-- 已接单（管理端"处理中"栏，跑腿端可在我的订单看到）
('ORD20240425002', '代取快递-快递柜',    18.00, 2,    3, 'ACCEPTED',
 'DEF456', '[]', DATE_SUB(NOW(), INTERVAL 90 MINUTE), DATE_SUB(NOW(), INTERVAL 80 MINUTE)),
-- 进行中（管理端"处理中"栏）
('ORD20240425003', '代取快递-顺丰驿站', 22.50, 1,    3, 'IN_TRANSIT',
 'GHI789', '[]', DATE_SUB(NOW(), INTERVAL 3 HOUR),   DATE_SUB(NOW(), INTERVAL 1 HOUR)),
-- 已完成（管理端"已完成"栏）
('ORD20240425004', '代取快递-京东快递',  15.00, 2,    4, 'COMPLETED',
 'JKL012', '[]', DATE_SUB(NOW(), INTERVAL 5 HOUR),   DATE_SUB(NOW(), INTERVAL 4 HOUR)),
('ORD20240425005', '代取快递-圆通驿站',   9.90, 1,    3, 'COMPLETED',
 'MNO345', '[]', DATE_SUB(NOW(), INTERVAL 8 HOUR),   DATE_SUB(NOW(), INTERVAL 7 HOUR)),
-- 已取消（管理端"已取消"栏）
('ORD20240425006', '代取快递-韵达站',    8.00, 2, NULL, 'CANCELLED',
 'PQR678', '[]', DATE_SUB(NOW(), INTERVAL 10 HOUR),  DATE_SUB(NOW(), INTERVAL 9 HOUR));
