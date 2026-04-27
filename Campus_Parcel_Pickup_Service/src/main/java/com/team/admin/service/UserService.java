package com.team.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.team.admin.dto.PageRequest;
import com.team.admin.dto.UserListDTO;
import com.team.admin.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 分页查询用户列表（支持学号/姓名模糊搜索）
     * @param pageRequest 分页和搜索请求
     * @return 分页结果
     */
    IPage<UserListDTO> getUserList(PageRequest pageRequest);
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);
    
    /**
     * 根据学号查询用户
     * @param studentId 学号
     * @return 用户信息
     */
    User getUserByStudentId(String studentId);
    
    /**
     * 创建用户
     * @param user 用户信息
     * @return 创建结果
     */
    boolean createUser(User user);
    
    /**
     * 更新用户
     * @param user 用户信息
     * @return 更新结果
     */
    boolean updateUser(User user);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    boolean deleteUser(Long id);
    
    /**
     * 启用/禁用用户
     * @param id 用户ID
     * @param status 状态：0-禁用，1-启用
     * @return 更新结果
     */
    boolean updateUserStatus(Long id, Integer status);
    
    /**
     * 根据学号或手机号查找用户
     * @param username 学号或手机号
     * @return 用户信息
     */
    User findByStudentIdOrPhone(String username);
}
