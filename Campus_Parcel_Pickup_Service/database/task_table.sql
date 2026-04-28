-- 任务表：存储发布的代取任务
CREATE TABLE IF NOT EXISTS `task` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `publisher_id` BIGINT NOT NULL COMMENT '发布者ID',
    `pickup_code` VARCHAR(50) NOT NULL COMMENT '取件码',
    `delivery_point` VARCHAR(100) NOT NULL COMMENT '快递点名称',
    `weight` DECIMAL(10,2) NULL COMMENT '重量kg',
    `reward_points` INT NOT NULL COMMENT '悬赏积分',
    `remark` VARCHAR(100) NULL COMMENT '备注',
    `status` VARCHAR(20) NOT NULL COMMENT '任务状态：PENDING/ACCEPTED/IN_TRANSIT/COMPLETED/CANCELLED',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `accept_time` DATETIME NULL COMMENT '跑腿员接单时间',
    `pickup_time` DATETIME NULL COMMENT '跑腿员取件成功时间',
    `complete_time` DATETIME NULL COMMENT '任务完成时间',
    `cancel_time` DATETIME NULL COMMENT '任务取消时间',
    `runner_id` BIGINT NULL COMMENT '接单跑腿员ID',
    `runner_nickname` VARCHAR(50) NULL COMMENT '跑腿员昵称（冗余）',
    `runner_phone` VARCHAR(20) NULL COMMENT '跑腿员手机号',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_publisher_pickup_status (`publisher_id`, `pickup_code`, `status`),
    INDEX idx_publisher_status (`publisher_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
ALTER TABLE `task` ADD COLUMN `accept_time` DATETIME NULL COMMENT '跑腿员接单时间';
ALTER TABLE `task` ADD COLUMN `pickup_time` DATETIME NULL COMMENT '跑腿员取件成功时间';
ALTER TABLE `task` ADD COLUMN `complete_time` DATETIME NULL COMMENT '任务完成时间';
ALTER TABLE `task` ADD COLUMN `cancel_time` DATETIME NULL COMMENT '任务取消时间';
