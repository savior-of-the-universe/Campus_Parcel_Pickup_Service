package com.team.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.admin.common.R;
import com.team.admin.dto.PageRequest;
import com.team.admin.entity.User;
import com.team.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class UserAdminController {

    @Autowired
    private UserService userService;

    /**
     * 客服端 - 分页查询用户列表
     * GET /api/admin/users?page=1&size=20&keyword=张三
     */
    @GetMapping("/users")
    public R<Page<User>> listUsers(@Valid PageRequest pageRequest) {
        Page<User> page = userService.pageForAdmin(
                pageRequest.getPage(),
                pageRequest.getSize(),
                pageRequest.getKeyword()
        );
        return R.ok(page);
    }

    /**
     * 客服端 - 获取用户详情（手机号明文）
     * GET /api/admin/users/{userId}
     */
    @GetMapping("/users/{userId}")
    public R<User> getUserDetail(@PathVariable Long userId) {
        User user = userService.getDetailForAdmin(userId);
        if (user == null) {
            return R.error("用户不存在");
        }
        return R.ok(user);
    }
}