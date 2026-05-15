-- ============================================================
-- 校园代取快递服务系统 — 数据库完整初始化脚本
-- 文件：init_all.sql
-- 说明：全部按顺序执行，幂等设计（已存在可跳过 ALTER 报错）
-- 执行顺序：
--   1. 建表（user / orders / chat_message / task / task_log）
--   2. 字段升级 ALTER（已有字段报 Duplicate column name 可跳过）
--   3. 测试/演示数据插入（各功能模块效果说明见注释）
-- ============================================================

-- ----------------------------------------------------------------
-- 【Step 1】建表
-- ----------------------------------------------------------------

-- ① user 表
CREATE TABLE IF NOT EXISTS `user` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `student_id`     VARCHAR(50)  NOT NULL                COMMENT '学号（登录账号）',
    `name`           VARCHAR(100) NOT NULL                COMMENT '姓名',
    `phone`          VARCHAR(20)  NOT NULL                COMMENT '手机号',
    `role`           VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：USER=普通用户 / RUNNER=跑腿员 / ADMIN=管理员/客服',
    `status`         TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0=禁用 1=启用',
    `password`       VARCHAR(255) NOT NULL                COMMENT '密码（BCrypt 加密）',
    `dormitory_area` VARCHAR(100)                         COMMENT '宿舍区域',
    `points`         INT          NOT NULL DEFAULT 0      COMMENT '积分余额（任务完成后跑腿员获得悬赏积分）',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                    COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_id` (`student_id`),
    KEY `idx_name`   (`name`),
    KEY `idx_phone`  (`phone`),
    KEY `idx_role`   (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ② orders 表（旧版订单，供客服/跑腿订单管理功能使用）
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
    UNIQUE KEY `uk_order_no`    (`order_no`),
    KEY `idx_customer_id`       (`customer_id`),
    KEY `idx_runner_id`         (`runner_id`),
    KEY `idx_status`            (`status`),
    KEY `idx_create_time`       (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表（旧版）';

-- ③ chat_message 表（客服聊天功能使用）
CREATE TABLE IF NOT EXISTS `chat_message` (
    `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `session_id`   VARCHAR(100) NOT NULL               COMMENT '会话ID（格式：user_{用户id}_admin_{客服id}）',
    `sender_id`    BIGINT      NOT NULL                COMMENT '发送者ID，关联 user.id',
    `receiver_id`  BIGINT      NOT NULL                COMMENT '接收者ID，关联 user.id',
    `content`      TEXT        NOT NULL                COMMENT '消息内容',
    `content_type` VARCHAR(20) NOT NULL DEFAULT 'TEXT' COMMENT '内容类型：TEXT / IMAGE',
    `send_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    `is_read`      TINYINT(1)  NOT NULL DEFAULT 0      COMMENT '是否已读：0=未读 1=已读',
    PRIMARY KEY (`id`),
    KEY `idx_session_id`  (`session_id`),
    KEY `idx_sender_id`   (`sender_id`),
    KEY `idx_receiver_id` (`receiver_id`),
    KEY `idx_send_time`   (`send_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- ④ task 表（任务大厅/代取任务）
CREATE TABLE IF NOT EXISTS `task` (
    `id`               BIGINT         NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `publisher_id`     BIGINT         NOT NULL                COMMENT '发布者ID，关联 user.id',
    `pickup_code`      VARCHAR(50)    NOT NULL                COMMENT '取件码（完整，仅跑腿员详情页可见）',
    `delivery_point`   VARCHAR(100)   NOT NULL                COMMENT '快递点/驿站名称',
    `weight`           DECIMAL(10,2)                          COMMENT '重量(kg)，可为空',
    `reward_points`    INT            NOT NULL                COMMENT '悬赏积分（跑腿员完成后获得）',
    `remark`           VARCHAR(100)                           COMMENT '备注',
    `status`           VARCHAR(20)    NOT NULL                COMMENT '任务状态：PENDING=待接单 / ACCEPTED=已接单 / IN_TRANSIT=取件中 / COMPLETED=已完成 / CANCELLED=已取消',
    `create_time`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `accept_time`      DATETIME                               COMMENT '跑腿员接单时间',
    `pickup_time`      DATETIME                               COMMENT '跑腿员取件成功时间',
    `complete_time`    DATETIME                               COMMENT '任务完成时间',
    `cancel_time`      DATETIME                               COMMENT '任务取消时间',
    `runner_id`        BIGINT                                 COMMENT '接单跑腿员ID，关联 user.id',
    `runner_nickname`  VARCHAR(50)                            COMMENT '跑腿员昵称（冗余快照）',
    `runner_phone`     VARCHAR(20)                            COMMENT '跑腿员手机号（冗余快照）',
    `deleted`          TINYINT        NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=未删除 1=已删除',
    `version`          INT            NOT NULL DEFAULT 0      COMMENT '乐观锁版本号（防止并发接单）',
    INDEX `idx_publisher_pickup_status` (`publisher_id`, `pickup_code`, `status`),
    INDEX `idx_publisher_status`        (`publisher_id`, `status`),
    INDEX `idx_status_create`           (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代取任务表';

-- ⑤ task_log 表（任务状态流转日志）
CREATE TABLE IF NOT EXISTS `task_log` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT,
    `task_id`       BIGINT      NOT NULL COMMENT '关联任务ID，关联 task.id',
    `operator_id`   BIGINT      NOT NULL COMMENT '操作人ID，关联 user.id',
    `operator_role` VARCHAR(20) NOT NULL COMMENT '操作人角色：USER / RUNNER / ADMIN',
    `from_status`   VARCHAR(20) NOT NULL COMMENT '流转前状态',
    `to_status`     VARCHAR(20) NOT NULL COMMENT '流转后状态',
    `remark`        VARCHAR(200)         COMMENT '备注（如凭证图URL）',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    INDEX `idx_task_id`     (`task_id`),
    INDEX `idx_operator_id` (`operator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务状态流转日志表';


-- ----------------------------------------------------------------
-- 【Step 2】字段升级 ALTER（表已存在但缺少以下字段时执行；
--           遇到 "Duplicate column name" 报错可跳过该条）
-- ----------------------------------------------------------------

-- user 表：积分字段
ALTER TABLE `user` ADD COLUMN `points` INT NOT NULL DEFAULT 0 COMMENT '积分余额';

-- task 表：各新增字段（按迭代顺序，建表时已含可全部跳过）
ALTER TABLE `task` ADD COLUMN `runner_id`       BIGINT      NULL    COMMENT '接单跑腿员ID';
ALTER TABLE `task` ADD COLUMN `runner_nickname`  VARCHAR(50) NULL    COMMENT '跑腿员昵称（冗余）';
ALTER TABLE `task` ADD COLUMN `runner_phone`     VARCHAR(20) NULL    COMMENT '跑腿员手机号';
ALTER TABLE `task` ADD COLUMN `deleted`          TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除';
ALTER TABLE `task` ADD COLUMN `accept_time`      DATETIME NULL COMMENT '跑腿员接单时间';
ALTER TABLE `task` ADD COLUMN `pickup_time`      DATETIME NULL COMMENT '跑腿员取件成功时间';
ALTER TABLE `task` ADD COLUMN `complete_time`    DATETIME NULL COMMENT '任务完成时间';
ALTER TABLE `task` ADD COLUMN `cancel_time`      DATETIME NULL COMMENT '任务取消时间';
ALTER TABLE `task` ADD COLUMN `version`          INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号';


-- ----------------------------------------------------------------
-- 【Step 3】测试/演示数据
-- ----------------------------------------------------------------

-- ==============================================================
-- ★ 3-1  user 表：基础账号
-- 效果页面：
--   · 管理端 → 用户管理 → 可看到全部用户列表、角色、状态
--   · 登录页 → 用学号 + 密码（明文 123456）登录
--   · 客服聊天 → 需要 USER 向 ADMIN 发起会话
--   · 任务大厅 → RUNNER 账号才能看到接单按钮
-- 密码统一：123456  （BCrypt: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK）
-- ==============================================================
INSERT INTO `user` (`student_id`, `name`, `phone`, `role`, `status`, `password`, `dormitory_area`, `points`) VALUES
-- 普通用户（发布任务的一方）
('2021001', '张三',   '13800138001', 'USER',   1, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK', '东区', 0),
('2021002', '李四',   '13800138002', 'USER',   1, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK', '西区', 0),
-- 跑腿员（可在任务大厅接单）
('2021003', '王五',   '13800138003', 'RUNNER', 1, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK', '南区', 30),
('2021004', '赵六',   '13800138004', 'RUNNER', 1, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK', '北区', 15),
-- 管理员/客服
('2021005', '客服小美', '13800138005', 'ADMIN', 1, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK', '东区', 0);


-- ==============================================================
-- ★ 3-2  orders 表：旧版订单（多状态覆盖）
-- 效果页面：
--   · 管理端 → 订单管理 → 可看到全部订单，可按状态/订单号/学号筛选
--   · 跑腿端 → 我的订单 → 查看自己接到的订单
--   · 客服端 → 聊天时关联订单号可快速查单
-- 说明：customer_id / runner_id 对应上方 user 插入后的实际 id
--       （若 AUTO_INCREMENT 从 1 开始，则 张三=1/李四=2/王五=3/赵六=4/客服=5）
-- ==============================================================
INSERT INTO `orders` (`order_no`, `title`, `amount`, `customer_id`, `runner_id`, `status`, `pickup_code`, `timeline`, `create_time`, `update_time`) VALUES
-- 待接单（在管理端订单列表"待接单"栏、跑腿端可见）
('ORD20240425001', '代取快递-菜鸟驿站',  12.50, 1, NULL, 'PENDING',    'ABC123', '[]', DATE_SUB(NOW(), INTERVAL 2 HOUR),  DATE_SUB(NOW(), INTERVAL 2 HOUR)),
-- 已接单/进行中（管理端"处理中"栏，跑腿端我的订单可见）
('ORD20240425002', '代取快递-快递柜',    18.00, 2,    3, 'ACCEPTED',   'DEF456', '[]', DATE_SUB(NOW(), INTERVAL 90 MINUTE), DATE_SUB(NOW(), INTERVAL 80 MINUTE)),
('ORD20240425003', '代取快递-顺丰驿站', 22.50, 1,    3, 'IN_TRANSIT',  'GHI789', '[]', DATE_SUB(NOW(), INTERVAL 3 HOUR),  DATE_SUB(NOW(), INTERVAL 1 HOUR)),
-- 已完成（管理端"已完成"栏）
('ORD20240425004', '代取快递-京东快递',  15.00, 2,    4, 'COMPLETED',  'JKL012', '[]', DATE_SUB(NOW(), INTERVAL 5 HOUR),  DATE_SUB(NOW(), INTERVAL 4 HOUR)),
('ORD20240425005', '代取快递-圆通驿站',  9.90, 1,    3, 'COMPLETED',  'MNO345', '[]', DATE_SUB(NOW(), INTERVAL 8 HOUR),  DATE_SUB(NOW(), INTERVAL 7 HOUR)),
-- 已取消（管理端"已取消"栏）
('ORD20240425006', '代取快递-韵达站',    8.00, 2, NULL, 'CANCELLED',  'PQR678', '[]', DATE_SUB(NOW(), INTERVAL 10 HOUR), DATE_SUB(NOW(), INTERVAL 9 HOUR));


-- ==============================================================
-- ★ 3-3  chat_message 表：客服聊天演示数据
-- 效果页面：
--   · 管理端 → 客服聊天中心 → 左侧会话列表可看到"张三"和"李四"两个会话
--   · 点击会话 → 右侧显示聊天记录（用户提问 / 客服回复）
--   · 消息搜索 → 输入"快递"可搜出下方消息
-- 说明：
--   session_id 格式：user_{用户id}_admin_{客服id}
--   sender_id / receiver_id 对应 user.id
--   假设：张三=1, 李四=2, 客服小美=5（以实际 AUTO_INCREMENT 为准）
-- ==============================================================
INSERT INTO `chat_message` (`session_id`, `sender_id`, `receiver_id`, `content`, `content_type`, `send_time`, `is_read`) VALUES
-- 会话1：张三 ↔ 客服小美
('user_1_admin_5', 1, 5, '你好，我的快递取件码是 ABC123，帮我看看订单状态？',   'TEXT', DATE_SUB(NOW(), INTERVAL 120 MINUTE), 1),
('user_1_admin_5', 5, 1, '您好！查到了，您的订单 ORD20240425001 当前状态：待接单，稍候会有跑腿员接单。', 'TEXT', DATE_SUB(NOW(), INTERVAL 118 MINUTE), 1),
('user_1_admin_5', 1, 5, '好的，谢谢！大概多久能送到？',                        'TEXT', DATE_SUB(NOW(), INTERVAL 117 MINUTE), 1),
('user_1_admin_5', 5, 1, '一般 30 分钟内会有人接单，请稍等~',                   'TEXT', DATE_SUB(NOW(), INTERVAL 116 MINUTE), 1),
-- 会话2：李四 ↔ 客服小美（有未读消息）
('user_2_admin_5', 2, 5, '我下单了但找不到在哪里看快递进度？',                   'TEXT', DATE_SUB(NOW(), INTERVAL 30 MINUTE),  0),
('user_2_admin_5', 5, 2, '在首页 → 我的订单里可以查看实时进度哦~',              'TEXT', DATE_SUB(NOW(), INTERVAL 28 MINUTE),  0),
('user_2_admin_5', 2, 5, '找到了，谢谢客服！',                                  'TEXT', DATE_SUB(NOW(), INTERVAL 25 MINUTE),  0);


-- ==============================================================
-- ★ 3-4  task 表：任务大厅数据（多状态）
-- 效果页面：
--   · 跑腿端 → 任务大厅（/runner/tasks）：
--       tab"待接单" → 显示 PENDING 状态任务卡片（取件码显示后四位 ****）
--       tab"进行中" → 显示 runner_id=王五 的 ACCEPTED/IN_TRANSIT 任务
--       tab"已完成" → 显示 COMPLETED 任务
--   · 跑腿端 → 点击任务卡片 → 详情页（/runner/tasks/:id）显示完整取件码和时间线
--   · 跑腿端 → 详情页点"立即接单" → 调用 POST /api/runner/tasks/{id}/accept（乐观锁）
-- 说明：publisher_id 对应张三/李四，runner_id 对应王五/赵六
-- ==============================================================

-- ---- PENDING 待接单（任务大厅主列表，跑腿员可接单）----
INSERT INTO `task` (`publisher_id`, `pickup_code`, `delivery_point`, `weight`, `reward_points`, `remark`, `status`, `create_time`, `deleted`, `version`) VALUES
(1, 'ST2024050001', '菜鸟驿站（东门）',   1.5,  4, '轻拿轻放，谢谢',   'PENDING', DATE_SUB(NOW(), INTERVAL 40 MINUTE), 0, 0),
(2, 'JD2024050012', '京东快递站（北门）', 3.0,  6, NULL,               'PENDING', DATE_SUB(NOW(), INTERVAL 25 MINUTE), 0, 0),
(1, 'YZ2024050098', '圆通驿站（西区）',   0.5, 10, '积分翻倍，急急急',  'PENDING', DATE_SUB(NOW(), INTERVAL 10 MINUTE), 0, 0),
(2, 'SF2024050033', '顺丰驿站（南门）',   2.0,  5, NULL,               'PENDING', DATE_SUB(NOW(), INTERVAL 5  MINUTE), 0, 0);

-- ---- ACCEPTED 已接单/进行中（跑腿端"进行中"tab 展示，接单人=王五）----
-- 假设 王五 id=3，实际请对照 user 表
INSERT INTO `task` (`publisher_id`, `pickup_code`, `delivery_point`, `weight`, `reward_points`, `remark`, `status`, `create_time`, `accept_time`, `runner_id`, `runner_nickname`, `runner_phone`, `deleted`, `version`) VALUES
(2, 'ZT2024040021', '中通驿站（东区）',  1.0, 5, NULL, 'ACCEPTED',
 DATE_SUB(NOW(), INTERVAL 50 MINUTE), DATE_SUB(NOW(), INTERVAL 20 MINUTE),
 3, '王五', '13800138003', 0, 1),
(1, 'EMS2024040066', '邮政EMS（学生中心）', 4.5, 8, '大件，小心轻放', 'IN_TRANSIT',
 DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 90 MINUTE),
 3, '王五', '13800138003', 0, 1);

-- ---- COMPLETED 已完成（跑腿端"已完成"tab 展示）----
INSERT INTO `task` (`publisher_id`, `pickup_code`, `delivery_point`, `weight`, `reward_points`, `remark`, `status`, `create_time`, `accept_time`, `pickup_time`, `complete_time`, `runner_id`, `runner_nickname`, `runner_phone`, `deleted`, `version`) VALUES
(1, 'SF2024030011', '顺丰驿站（南门）',  2.5,  6, NULL, 'COMPLETED',
 DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR),
 DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR),
 3, '王五', '13800138003', 0, 2),
(2, 'KD2024030088', '快递柜（图书馆旁）', 1.0, 10, '已完成测试', 'COMPLETED',
 DATE_SUB(NOW(), INTERVAL 8 HOUR), DATE_SUB(NOW(), INTERVAL 7 HOUR),
 DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR),
 4, '赵六', '13800138004', 0, 2);

-- ---- CANCELLED 已取消（逻辑删除不显示，但 cancelled 状态数据保留）----
INSERT INTO `task` (`publisher_id`, `pickup_code`, `delivery_point`, `weight`, `reward_points`, `remark`, `status`, `create_time`, `cancel_time`, `deleted`, `version`) VALUES
(1, 'YD2024020055', '韵达站（西门）', 0.5, 3, '已取消测试', 'CANCELLED',
 DATE_SUB(NOW(), INTERVAL 24 HOUR), DATE_SUB(NOW(), INTERVAL 23 HOUR), 0, 0);


-- ==============================================================
-- ★ 3-5  task_log 表：任务状态流转日志
-- 效果页面：
--   · 后端 task_log 表：记录每次接单/取件/完成操作，供审计
--   · （前端暂无独立展示页，可通过管理端日志功能查看）
-- 说明：task_id 对应上方 task 数据；operator_id 对应 user.id
--   由于 task AUTO_INCREMENT，实际 id 需对照插入顺序（从 1 开始）
--   以下假设：ACCEPTED 任务为 task.id=5（第5条），IN_TRANSIT 为 task.id=6
--             COMPLETED 任务为 task.id=7 / 8
-- ==============================================================
INSERT INTO `task_log` (`task_id`, `operator_id`, `operator_role`, `from_status`, `to_status`, `remark`, `create_time`) VALUES
-- task_id=5（ACCEPTED 任务）：王五接单
(5, 3, 'RUNNER', 'PENDING', 'ACCEPTED', NULL, DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
-- task_id=6（IN_TRANSIT 任务）：王五接单 → 取件
(6, 3, 'RUNNER', 'PENDING',   'ACCEPTED',   NULL, DATE_SUB(NOW(), INTERVAL 90 MINUTE)),
(6, 3, 'RUNNER', 'ACCEPTED',  'IN_TRANSIT',  NULL, DATE_SUB(NOW(), INTERVAL 60 MINUTE)),
-- task_id=7（COMPLETED 任务）：王五完整流程
(7, 3, 'RUNNER', 'PENDING',   'ACCEPTED',   NULL, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(7, 3, 'RUNNER', 'ACCEPTED',  'IN_TRANSIT',  NULL, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(7, 3, 'RUNNER', 'IN_TRANSIT', 'COMPLETED', NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
-- task_id=8（COMPLETED 任务）：赵六完整流程
(8, 4, 'RUNNER', 'PENDING',   'ACCEPTED',   NULL, DATE_SUB(NOW(), INTERVAL 7 HOUR)),
(8, 4, 'RUNNER', 'ACCEPTED',  'IN_TRANSIT',  NULL, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(8, 4, 'RUNNER', 'IN_TRANSIT', 'COMPLETED', NULL, DATE_SUB(NOW(), INTERVAL 5 HOUR));


-- ----------------------------------------------------------------
-- 【附录】各账号登录信息速查
-- ----------------------------------------------------------------
-- 学号         姓名      角色     密码(明文)   可见功能
-- 2021001    张三      USER     123456      发布任务、查看自己的任务状态、联系客服
-- 2021002    李四      USER     123456      发布任务、查看自己的任务状态、联系客服
-- 2021003    王五      RUNNER   123456      任务大厅接单、查看进行中/已完成任务、接单积分=30
-- 2021004    赵六      RUNNER   123456      任务大厅接单、查看进行中/已完成任务、接单积分=15
-- 2021005    客服小美  ADMIN    123456      客服聊天中心、用户管理、订单管理
-- ----------------------------------------------------------------
