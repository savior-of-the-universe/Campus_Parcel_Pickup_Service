-- 聊天消息表
-- 效果页面：
--   · 管理端 → 客服聊天中心（/admin/chat）→ 左侧会话列表展示所有用户会话
--   · 点击某个会话 → 右侧展示该 session_id 下的历史消息（用户提问 + 客服回复）
--   · 消息搜索（/admin/chat-search）→ 可按关键词搜索所有消息内容
--   · 未读消息角标 → 基于 is_read=0 统计
-- 相关代码：ChatMessageMapper / ChatServiceImpl / ChatAdminController
CREATE TABLE IF NOT EXISTS `chat_message` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `session_id`   VARCHAR(100) NOT NULL               COMMENT '会话ID，格式：user_{用户id}_admin_{客服id}',
    `sender_id`    BIGINT       NOT NULL               COMMENT '发送者ID，关联 user.id',
    `receiver_id`  BIGINT       NOT NULL               COMMENT '接收者ID，关联 user.id',
    `content`      TEXT         NOT NULL               COMMENT '消息内容（TEXT类型为文字，IMAGE类型为图片URL）',
    `content_type` VARCHAR(20)  NOT NULL DEFAULT 'TEXT' COMMENT '内容类型：TEXT=文字 / IMAGE=图片',
    `send_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    `is_read`      TINYINT(1)   NOT NULL DEFAULT 0     COMMENT '是否已读：0=未读 1=已读',
    PRIMARY KEY (`id`),
    KEY `idx_session_id`  (`session_id`),
    KEY `idx_sender_id`   (`sender_id`),
    KEY `idx_receiver_id` (`receiver_id`),
    KEY `idx_send_time`   (`send_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- ============================================================
-- 测试/演示数据
-- 说明：
--   sender_id / receiver_id 对应 user.id（以 user_table.sql 插入顺序为准）
--   假设：张三=1, 李四=2, 王五=3, 赵六=4, 客服小美=5
--   session_id 格式：user_{用户id}_admin_{客服id}
-- ============================================================

-- 会话1：张三（id=1）↔ 客服小美（id=5）
-- 效果：客服聊天中心左侧出现"张三"会话，消息已全部已读
INSERT INTO `chat_message` (`session_id`, `sender_id`, `receiver_id`, `content`, `content_type`, `send_time`, `is_read`) VALUES
('user_1_admin_5', 1, 5, '你好，我的快递取件码是 ST2024050001，帮我看看任务状态？', 'TEXT', DATE_SUB(NOW(), INTERVAL 120 MINUTE), 1),
('user_1_admin_5', 5, 1, '您好！查到了，您发布的任务当前状态：待接单，稍候会有跑腿员接单。', 'TEXT', DATE_SUB(NOW(), INTERVAL 118 MINUTE), 1),
('user_1_admin_5', 1, 5, '好的，谢谢！大概多久能有人接单？', 'TEXT', DATE_SUB(NOW(), INTERVAL 117 MINUTE), 1),
('user_1_admin_5', 5, 1, '一般 30 分钟内会有人接单，请稍等~', 'TEXT', DATE_SUB(NOW(), INTERVAL 116 MINUTE), 1);

-- 会话2：李四（id=2）↔ 客服小美（id=5）
-- 效果：客服聊天中心左侧出现"李四"会话，3条未读消息（is_read=0，角标显示3）
INSERT INTO `chat_message` (`session_id`, `sender_id`, `receiver_id`, `content`, `content_type`, `send_time`, `is_read`) VALUES
('user_2_admin_5', 2, 5, '我下单了但找不到在哪里看快递进度？', 'TEXT', DATE_SUB(NOW(), INTERVAL 30 MINUTE), 0),
('user_2_admin_5', 5, 2, '在首页 → 我的任务里可以查看实时进度哦~',  'TEXT', DATE_SUB(NOW(), INTERVAL 28 MINUTE), 0),
('user_2_admin_5', 2, 5, '找到了，谢谢客服！',                     'TEXT', DATE_SUB(NOW(), INTERVAL 25 MINUTE), 0);
