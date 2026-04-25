-- 创建聊天消息表
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id` varchar(255) NOT NULL COMMENT '会话ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text NOT NULL COMMENT '消息内容',
  `content_type` varchar(10) NOT NULL DEFAULT 'TEXT' COMMENT '内容类型：TEXT/IMAGE',
  `send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_send_time` (`send_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 插入示例数据
INSERT INTO `chat_message` (`session_id`, `sender_id`, `receiver_id`, `content`, `content_type`) VALUES
('session_001', 1, 5, '你好，我想咨询一下取件服务', 'TEXT'),
('session_001', 5, 1, '您好！很高兴为您服务，请问有什么可以帮助您的？', 'TEXT'),
('session_001', 1, 5, '我的订单什么时候能送到？', 'TEXT'),
('session_002', 2, 5, '跑腿员已经取件了吗？', 'TEXT'),
('session_002', 5, 2, '我帮您查询一下，您的订单正在配送中', 'TEXT');
