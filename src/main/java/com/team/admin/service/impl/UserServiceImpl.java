package com.team.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.team.admin.entity.User;
import com.team.admin.mapper.UserMapper;
import com.team.admin.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.team.admin.util.PhoneDesensitizationUtil;
import com.team.admin.util.SecurityUtil;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Page<User> pageForAdmin(Integer page, Integer size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getStudentId, keyword)
                    .or()
                    .like(User::getName, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> resultPage = this.page(pageParam, wrapper);
        
        // 客服角色返回明文手机号
        String currentUserRole = SecurityUtil.getCurrentUserRole();
        if ("ADMIN".equals(currentUserRole)) {
            // 客服可以看到明文手机号，无需脱敏
            return resultPage;
        } else {
            // 其他角色对手机号进行脱敏
            resultPage.getRecords().forEach(user -> {
                user.setPhone(PhoneDesensitizationUtil.desensitizePhone(user.getPhone()));
            });
            return resultPage;
        }
    }

    @Override
    public User getDetailForAdmin(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            return null;
        }
        // 注意：如果数据库中手机号是加密存储的，这里需要解密后再返回
        // 假设数据库存的是明文，直接返回即可；若是密文，请调用解密方法
        // user.setPhone(decrypt(user.getPhone()));
        // 根据当前用户角色决定是否对手机号进行脱敏
        String currentUserRole = SecurityUtil.getCurrentUserRole();
        if (!"ADMIN".equals(currentUserRole)) {
            user.setPhone(PhoneDesensitizationUtil.desensitizePhone(user.getPhone()));
        }
        
        return user;
    }
}