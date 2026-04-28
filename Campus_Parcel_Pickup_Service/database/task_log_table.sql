-- 任务状态流转日志表
-- 效果页面：
--   · 后端数据库直接查看，记录每次接单/取件/完成操作
--   · 用于审计和追溯任务流转历史
--   · TaskServiceImpl.acceptTask / updateTaskStatus 每次状态变更都会写入此表
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

-- ============================================================
-- 测试/演示数据
-- 说明：task_id 对应 task_table.sql 插入数据的顺序（AUTO_INCREMENT 从1开始）
--       PENDING  任务：task.id = 1~4（待接单，无日志）
--       ACCEPTED 任务：task.id = 5（王五接单）
--       IN_TRANSIT 任务：task.id = 6（王五接单 → 取件）
--       COMPLETED 任务：task.id = 7（王五完整流程）/ task.id = 8（赵六完整流程）
--       CANCELLED 任务：task.id = 9（无操作日志）
-- ============================================================
INSERT INTO `task_log` (`task_id`, `operator_id`, `operator_role`, `from_status`, `to_status`, `remark`, `create_time`) VALUES
-- task_id=5（ACCEPTED）：王五接单
(5, 3, 'RUNNER', 'PENDING', 'ACCEPTED', NULL, DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
-- task_id=6（IN_TRANSIT）：王五接单 → 取件
(6, 3, 'RUNNER', 'PENDING',  'ACCEPTED',  NULL, DATE_SUB(NOW(), INTERVAL 90 MINUTE)),
(6, 3, 'RUNNER', 'ACCEPTED', 'IN_TRANSIT', NULL, DATE_SUB(NOW(), INTERVAL 60 MINUTE)),
-- task_id=7（COMPLETED）：王五完整流程
(7, 3, 'RUNNER', 'PENDING',    'ACCEPTED',   NULL, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(7, 3, 'RUNNER', 'ACCEPTED',   'IN_TRANSIT',  NULL, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(7, 3, 'RUNNER', 'IN_TRANSIT', 'COMPLETED',   NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
-- task_id=8（COMPLETED）：赵六完整流程
(8, 4, 'RUNNER', 'PENDING',    'ACCEPTED',   NULL, DATE_SUB(NOW(), INTERVAL 7 HOUR)),
(8, 4, 'RUNNER', 'ACCEPTED',   'IN_TRANSIT',  NULL, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(8, 4, 'RUNNER', 'IN_TRANSIT', 'COMPLETED',   NULL, DATE_SUB(NOW(), INTERVAL 5 HOUR));
