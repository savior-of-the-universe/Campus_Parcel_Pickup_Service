-- 用户表
-- 效果页面：
--   · 管理端 → 用户管理 → 展示用户列表、角色、状态、积分
--   · 登录页 → 用学号 + 密码（明文 123456）登录
--   · 跑腿端 → 个人信息页 → 查看积分余额
--   · 客服聊天 → USER 发起会话 / ADMIN 接待回复
CREATE TABLE IF NOT EXISTS `user` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `student_id`     VARCHAR(50)  NOT NULL                COMMENT '学号（登录账号）',
    `name`           VARCHAR(100) NOT NULL                COMMENT '姓名',
    `phone`          VARCHAR(20)  NOT NULL                COMMENT '手机号',
    `role`           VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：USER=普通用户 / RUNNER=跑腿员 / ADMIN=管理员/客服',
    `status`         TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0=禁用 1=启用',
    `password`       VARCHAR(255) NOT NULL                COMMENT '密码（BCrypt 加密）',
    `dormitory_area` VARCHAR(100)                         COMMENT '宿舍区域',
    `points`         INT          NOT NULL DEFAULT 0      COMMENT '积分余额（跑腿员完成任务后获得）',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                    COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_id` (`student_id`),
    KEY `idx_name`   (`name`),
    KEY `idx_phone`  (`phone`),
    KEY `idx_role`   (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 升级脚本：添加积分字段（已有则跳过，报 Duplicate column name 可忽略）
-- ============================================================
ALTER TABLE `user` ADD COLUMN `points` INT NOT NULL DEFAULT 0 COMMENT '积分余额';

-- ============================================================
-- 测试/演示数据（密码明文统一：123456）
-- ============================================================
INSERT INTO `user` (`student_id`, `name`, `phone`, `role`, `status`, `password`, `dormitory_area`, `points`) VALUES
-- 普通用户：可发布任务、联系客服
-- 效果：管理端用户列表可见；登录后可在前台发布代取任务
('2021001', '张三',     '13800138001', 'USER',   1, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK', '东区', 0),
('2021002', '李四',     '13800138002', 'USER',   1, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK', '西区', 0),
-- 跑腿员：可在任务大厅（/runner/tasks）看到接单按钮、查看个人积分
('2021003', '王五',     '13800138003', 'RUNNER', 1, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK', '南区', 30),
('2021004', '赵六',     '13800138004', 'RUNNER', 1, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK', '北区', 15),
-- 管理员/客服：可登录管理端，查看用户管理/订单管理/客服聊天中心
('2021005', '客服小美', '13800138005', 'ADMIN',  1, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK', '东区', 0);
