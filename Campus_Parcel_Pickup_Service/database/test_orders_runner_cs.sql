-- 测试数据脚本：客服/跑腿订单场景覆盖（订单号、学号搜索，跑腿自查）
-- 说明：
-- 1) 用户表需存在下列账号（如不存在请先插入）：
--    - admin / 角色 ADMIN（可复用现有管理员）
--    - user001 / 角色 USER / id=7（示例客户）
--    - runner001 / 角色 RUNNER / id=9（示例跑腿员）
-- 2) 如你的实际 user.id 不同，请将下面的 customer_id / runner_id 替换成对应的 id。
-- 3) order_no 为唯一键，若已被占用，请调整为未用过的编号。

-- 插入示例订单（customer_id=7 对应 user001，runner_id=9 对应 runner001）
INSERT INTO orders (
  order_no, title, amount, customer_id, runner_id, status, pickup_code, timeline, create_time, update_time
) VALUES
  ('ORD20240427030', '测试-待接单', 12.00, 7, 9, 'PENDING',   '111111', '[]', NOW(), NOW()),
  ('ORD20240427031', '测试-接单中', 15.00, 7, 9, 'ACCEPTED',  '222222', '[]', NOW(), NOW()),
  ('ORD20240427032', '测试-进行中', 20.50, 7, 9, 'IN_TRANSIT','333333', '[]', NOW(), NOW()),
  ('ORD20240427033', '测试-已完成',  8.80, 7, 9, 'COMPLETED', '444444', '[]', NOW(), NOW()),
  ('ORD20240427034', '测试-已取消',  5.20, 7, 9, 'CANCELLED', '555555', '[]', NOW(), NOW());

-- 如需补充跑腿员账号（若不存在 runner001）：
-- INSERT INTO user (student_id, name, phone, role, status, password, dormitory_area, create_time, update_time)
-- VALUES ('runner001', '王五', '13800138003', 'RUNNER', 1,
--         '$2a$10$rOzJdQe8mPqHgLzVjHnIvO9PQrHmL8QO5J5X5K8H5X5K8H5X5K8H5', -- runner123 的 BCrypt
--         '南区宿舍', NOW(), NOW());
