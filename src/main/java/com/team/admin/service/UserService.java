package com.team.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.admin.entity.User;

public interface UserService {
    /**
     * 客服端分页查询用户列表（支持学号/姓名模糊搜索）
     */
    Page<User> pageForAdmin(Integer page, Integer size, String keyword);

    /**
     * 客服端获取用户详情（手机号明文）
     */
    User getDetailForAdmin(Long userId);
}