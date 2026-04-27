package com.team.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.admin.dto.PageRequest;
import com.team.admin.dto.UserListDTO;
import com.team.admin.entity.User;
import com.team.admin.mapper.UserMapper;
import com.team.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public IPage<UserListDTO> getUserList(PageRequest pageRequest) {
        Page<UserListDTO> page = new Page<>(pageRequest.getPage(), pageRequest.getSize());
        return userMapper.selectUserListWithSearch(page, pageRequest.getKeyword());
    }
    
    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
    
    @Override
    public User getUserByStudentId(String studentId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean createUser(User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.insert(user) > 0;
    }
    
    @Override
    public boolean updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean updateUserStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    public User findByStudentIdOrPhone(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", username)
                   .or()
                   .eq("phone", username);
        
        return userMapper.selectOne(queryWrapper);
    }
}
