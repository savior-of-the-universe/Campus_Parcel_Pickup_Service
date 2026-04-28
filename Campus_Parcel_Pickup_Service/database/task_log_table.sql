-- 任务日志表：记录任务每次状态流转
CREATE TABLE IF NOT EXISTS `task_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `task_id` BIGINT NOT NULL COMMENT '关联任务ID',
    `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
    `operator_role` VARCHAR(20) NOT NULL COMMENT '操作人角色：USER/RUNNER/ADMIN',
    `from_status` VARCHAR(20) NOT NULL COMMENT '流转前状态',
    `to_status` VARCHAR(20) NOT NULL COMMENT '流转后状态',
    `remark` VARCHAR(200) NULL COMMENT '备注（如凭证图URL）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_task_id (`task_id`),
    INDEX idx_operator_id (`operator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务状态流转日志';
