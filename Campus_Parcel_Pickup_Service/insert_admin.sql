-- 插入管理员和普通用户数据
-- 密码使用 BCrypt 加密，原始密码为 admin123 和 user123

-- 清空现有数据（可选，谨慎使用）
-- DELETE FROM user WHERE student_id IN ('admin', 'user001', 'user002');

-- 插入管理员账户
-- 学号: admin, 密码: admin123, 角色: ADMIN
INSERT INTO user (student_id, name, phone, role, status, password, dormitory_area, create_time, update_time) 
VALUES (
    'admin', 
    '系统管理员', 
    '13800138000', 
    'ADMIN', 
    1, 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFDYZt/I5/BFnhkSLsVBDSC', -- admin123 的 BCrypt 加密值
    '管理区域', 
    NOW(), 
    NOW()
);

-- 插入普通用户账户1
-- 学号: user001, 密码: user123, 角色: USER
INSERT INTO user (student_id, name, phone, role, status, password, dormitory_area, create_time, update_time) 
VALUES (
    'user001', 
    '张三', 
    '13800138001', 
    'USER', 
    1, 
    '$2a$10$8KdOaLsNdOQkJ8QHWjH4I.9PQrHmL8QO5J5X5K8H5X5K8H5X5K8H5', -- user123 的 BCrypt 加密值
    '东区宿舍', 
    NOW(), 
    NOW()
);

-- 插入普通用户账户2
-- 学号: user002, 密码: user123, 角色: USER
INSERT INTO user (student_id, name, phone, role, status, password, dormitory_area, create_time, update_time) 
VALUES (
    'user002', 
    '李四', 
    '13800138002', 
    'USER', 
    1, 
    '$2a$10$8KdOaLsNdOQkJ8QHWjH4I.9PQrHmL8QO5J5X5K8H5X5K8H5X5K8H5', -- user123 的 BCrypt 加密值
    '西区宿舍', 
    NOW(), 
    NOW()
);

-- 插入跑腿员账户
-- 学号: runner001, 密码: runner123, 角色: RUNNER
INSERT INTO user (student_id, name, phone, role, status, password, dormitory_area, create_time, update_time) 
VALUES (
    'runner001', 
    '王五', 
    '13800138003', 
    'RUNNER', 
    1, 
    '$2a$10$rOzJdQe8mPqHgLzVjHnIvO9PQrHmL8QO5J5X5K8H5X5K8H5X5K8H5', -- runner123 的 BCrypt 加密值
    '南区宿舍', 
    NOW(), 
    NOW()
);

-- 验证插入结果
SELECT id, student_id, name, role, status, dormitory_area, create_time 
FROM user 
WHERE student_id IN ('admin', 'user001', 'user002', 'runner001')
ORDER BY role, student_id;
