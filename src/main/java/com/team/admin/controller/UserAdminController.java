package com.team.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.team.admin.common.Result;
import com.team.admin.dto.PageRequest;
import com.team.admin.dto.UserListDTO;
import com.team.admin.entity.User;
import com.team.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器（客服端）
 */
@RestController
@RequestMapping("/api/admin/users")
@Validated
public class UserAdminController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表（支持学号/姓名模糊搜索）
     * 客服特权：返回明文手机号
     */
    @GetMapping
    public Result<IPage<UserListDTO>> getUserList(@Validated PageRequest pageRequest) {
        IPage<UserListDTO> result = userService.getUserList(pageRequest);
        return Result.success(result);
    }

    /**
     * 根据ID查询用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 根据学号查询用户
     */
    @GetMapping("/student/{studentId}")
    public Result<User> getUserByStudentId(@PathVariable String studentId) {
        User user = userService.getUserByStudentId(studentId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<Boolean> createUser(@Validated @RequestBody User user) {
        // 检查学号是否已存在
        User existingUser = userService.getUserByStudentId(user.getStudentId());
        if (existingUser != null) {
            return Result.error("学号已存在");
        }
        
        boolean result = userService.createUser(user);
        return result ? Result.success("创建成功", true) : Result.error("创建失败");
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateUser(@PathVariable Long id, @Validated @RequestBody User user) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        user.setId(id);
        boolean result = userService.updateUser(user);
        return result ? Result.success("更新成功", true) : Result.error("更新失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        boolean result = userService.deleteUser(id);
        return result ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/{id}/status")
    public Result<Boolean> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        if (status != 0 && status != 1) {
            return Result.error("状态值无效，只能是0（禁用）或1（启用）");
        }
        
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        boolean result = userService.updateUserStatus(id, status);
        String statusText = status == 1 ? "启用" : "禁用";
        return result ? Result.success(statusText + "成功", true) : Result.error(statusText + "失败");
    }
}
