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
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `runner_id` BIGINT NULL COMMENT '接单跑腿员ID',
    `runner_nickname` VARCHAR(50) NULL COMMENT '跑腿员昵称（冗余）',
    `runner_phone` VARCHAR(20) NULL COMMENT '跑腿员手机号',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_publisher_pickup_status (`publisher_id`, `pickup_code`, `status`),
    INDEX idx_publisher_status (`publisher_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 若表已存在，补充新字段（升级用）
ALTER TABLE `task`
    ADD COLUMN IF NOT EXISTS `runner_id` BIGINT NULL COMMENT '接单跑腿员ID',
    ADD COLUMN IF NOT EXISTS `runner_nickname` VARCHAR(50) NULL COMMENT '跑腿员昵称',
    ADD COLUMN IF NOT EXISTS `runner_phone` VARCHAR(20) NULL COMMENT '跑腿员手机号',
    ADD COLUMN IF NOT EXISTS `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除';
