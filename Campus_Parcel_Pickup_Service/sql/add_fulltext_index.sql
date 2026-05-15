-- 为聊天消息表的 content 字段添加 FULLTEXT 索引
ALTER TABLE `chat_message` ADD FULLTEXT INDEX `ft_content` (`content`);

-- 验证索引是否创建成功
-- SHOW INDEX FROM chat_message WHERE Key_name = 'ft_content';
