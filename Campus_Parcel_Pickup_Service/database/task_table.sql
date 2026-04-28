-- 任务表：存储发布的代取任务
-- 效果页面：
--   · 跑腿端 → 任务大厅（/runner/tasks）tab"待接单" → PENDING 状态卡片，取件码显示后四位
--   · 跑腿端 → 任务大厅 tab"进行中"  → ACCEPTED / IN_TRANSIT 状态（runner_id=当前登录跑腿员）
--   · 跑腿端 → 任务大厅 tab"已完成"  → COMPLETED 状态
--   · 跑腿端 → 点击卡片 → 详情页（/runner/tasks/:id）显示完整取件码 + 状态时间线 + 操作按钮
--   · 跑腿端 → 详情页"立即接单" → POST /api/runner/tasks/{id}/accept（乐观锁防并发，version 字段）
CREATE TABLE IF NOT EXISTS `task` (
    `id`               BIGINT         NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `publisher_id`     BIGINT         NOT NULL                COMMENT '发布者ID，关联 user.id',
    `pickup_code`      VARCHAR(50)    NOT NULL                COMMENT '取件码（完整，仅跑腿员在详情页可见）',
    `delivery_point`   VARCHAR(100)   NOT NULL                COMMENT '快递点/驿站名称',
    `weight`           DECIMAL(10,2)                          COMMENT '重量(kg)',
    `reward_points`    INT            NOT NULL                COMMENT '悬赏积分（跑腿员完成后获得，写入 user.points）',
    `remark`           VARCHAR(100)                           COMMENT '备注',
    `status`           VARCHAR(20)    NOT NULL                COMMENT '任务状态：PENDING=待接单 / ACCEPTED=已接单 / IN_TRANSIT=取件中 / COMPLETED=已完成 / CANCELLED=已取消',
    `create_time`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `accept_time`      DATETIME                               COMMENT '跑腿员接单时间（时间线节点2）',
    `pickup_time`      DATETIME                               COMMENT '跑腿员取件成功时间（时间线节点3）',
    `complete_time`    DATETIME                               COMMENT '任务完成时间（时间线节点4）',
    `cancel_time`      DATETIME                               COMMENT '任务取消时间',
    `runner_id`        BIGINT                                 COMMENT '接单跑腿员ID，关联 user.id',
    `runner_nickname`  VARCHAR(50)                            COMMENT '跑腿员昵称（冗余快照）',
    `runner_phone`     VARCHAR(20)                            COMMENT '跑腿员手机号（冗余快照）',
    `deleted`          TINYINT        NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=未删除 1=已删除（MyBatis-Plus @TableLogic）',
    `version`          INT            NOT NULL DEFAULT 0      COMMENT '乐观锁版本号（MyBatis-Plus @Version，防止并发接单）',
    INDEX `idx_publisher_pickup_status` (`publisher_id`, `pickup_code`, `status`),
    INDEX `idx_publisher_status`        (`publisher_id`, `status`),
    INDEX `idx_status_create`           (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代取任务表';

-- ============================================================
-- 升级脚本：若 task 表已存在且缺少以下字段，逐条执行
-- （每条单独执行，遇到 Duplicate column name 报错可跳过）
-- ============================================================

-- 1. 接单跑腿员ID
ALTER TABLE `task` ADD COLUMN `runner_id` BIGINT NULL COMMENT '接单跑腿员ID';

-- 2. 跑腿员昵称（冗余）
ALTER TABLE `task` ADD COLUMN `runner_nickname` VARCHAR(50) NULL COMMENT '跑腿员昵称（冗余）';

-- 3. 跑腿员手机号
ALTER TABLE `task` ADD COLUMN `runner_phone` VARCHAR(20) NULL COMMENT '跑腿员手机号';

-- 4. 逻辑删除标志（MyBatis-Plus 全局逻辑删除字段）
ALTER TABLE `task` ADD COLUMN `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除';

-- 5. 状态时间线时间戳
ALTER TABLE `task` ADD COLUMN `accept_time`   DATETIME NULL COMMENT '跑腿员接单时间';
ALTER TABLE `task` ADD COLUMN `pickup_time`   DATETIME NULL COMMENT '跑腿员取件成功时间';
ALTER TABLE `task` ADD COLUMN `complete_time` DATETIME NULL COMMENT '任务完成时间';
ALTER TABLE `task` ADD COLUMN `cancel_time`   DATETIME NULL COMMENT '任务取消时间';

-- 6. 乐观锁版本号（防止并发接单）
ALTER TABLE `task` ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号';

-- ============================================================
-- 测试/演示数据
-- 说明：publisher_id=1（张三）/2（李四），runner_id=3（王五）/4（赵六）
--       以实际 user.id AUTO_INCREMENT 值为准
-- ============================================================

-- ---- 待接单（任务大厅首页展示，跑腿端可点击接单）----
INSERT INTO `task` (`publisher_id`, `pickup_code`, `delivery_point`, `weight`, `reward_points`, `remark`, `status`, `create_time`, `deleted`, `version`) VALUES
(1, 'ST2024050001', '菜鸟驿站（东门）',   1.5,  4, '轻拿轻放，谢谢',  'PENDING', DATE_SUB(NOW(), INTERVAL 40 MINUTE), 0, 0),
(2, 'JD2024050012', '京东快递站（北门）', 3.0,  6, NULL,              'PENDING', DATE_SUB(NOW(), INTERVAL 25 MINUTE), 0, 0),
(1, 'YZ2024050098', '圆通驿站（西区）',   0.5, 10, '积分翻倍，急急急', 'PENDING', DATE_SUB(NOW(), INTERVAL 10 MINUTE), 0, 0),
(2, 'SF2024050033', '顺丰驿站（南门）',   2.0,  5, NULL,              'PENDING', DATE_SUB(NOW(), INTERVAL 5  MINUTE), 0, 0);

-- ---- 进行中（跑腿端"进行中"tab，接单人=王五 runner_id=3）----
INSERT INTO `task` (`publisher_id`, `pickup_code`, `delivery_point`, `weight`, `reward_points`, `remark`, `status`, `create_time`, `accept_time`, `runner_id`, `runner_nickname`, `runner_phone`, `deleted`, `version`) VALUES
(2, 'ZT2024040021',  '中通驿站（东区）',     1.0, 5, NULL,           'ACCEPTED',
 DATE_SUB(NOW(), INTERVAL 50 MINUTE), DATE_SUB(NOW(), INTERVAL 20 MINUTE), 3, '王五', '13800138003', 0, 1),
(1, 'EMS2024040066', '邮政EMS（学生中心）',  4.5, 8, '大件，小心轻放', 'IN_TRANSIT',
 DATE_SUB(NOW(), INTERVAL 2 HOUR),    DATE_SUB(NOW(), INTERVAL 90 MINUTE), 3, '王五', '13800138003', 0, 1);

-- ---- 已完成（跑腿端"已完成"tab，积分已结算）----
INSERT INTO `task` (`publisher_id`, `pickup_code`, `delivery_point`, `weight`, `reward_points`, `remark`, `status`, `create_time`, `accept_time`, `pickup_time`, `complete_time`, `runner_id`, `runner_nickname`, `runner_phone`, `deleted`, `version`) VALUES
(1, 'SF2024030011', '顺丰驿站（南门）',   2.5,  6, NULL,          'COMPLETED',
 DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR),
 DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR), 3, '王五', '13800138003', 0, 2),
(2, 'KD2024030088', '快递柜（图书馆旁）', 1.0, 10, '已完成测试',  'COMPLETED',
 DATE_SUB(NOW(), INTERVAL 8 HOUR), DATE_SUB(NOW(), INTERVAL 7 HOUR),
 DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR), 4, '赵六', '13800138004', 0, 2);

-- ---- 已取消（前端一般不展示，数据保留供审计）----
INSERT INTO `task` (`publisher_id`, `pickup_code`, `delivery_point`, `weight`, `reward_points`, `remark`, `status`, `create_time`, `cancel_time`, `deleted`, `version`) VALUES
(1, 'YD2024020055', '韵达站（西门）', 0.5, 3, '已取消测试', 'CANCELLED',
 DATE_SUB(NOW(), INTERVAL 24 HOUR), DATE_SUB(NOW(), INTERVAL 23 HOUR), 0, 0);
