package com.team.controller;

import com.team.admin.common.Result;
import com.team.admin.entity.User;
import com.team.admin.service.UserService;
import com.team.dto.LoginRequest;
import com.team.utils.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 根据学号或手机号查找用户
            User user = userService.findByStudentIdOrPhone(loginRequest.getUsername());
            
            if (user == null) {
                return Result.error(401, "用户不存在");
            }
            
            // 检查用户状态
            if (user.getStatus() == 0) {
                return Result.error(401, "账户已被禁用");
            }
            
            // 验证密码
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return Result.error(401, "密码错误");
            }
            
            // 生成JWT token
            String token = jwtUtils.generateToken(
                user.getStudentId(), 
                user.getId(), 
                user.getStudentId(), 
                user.getName(), 
                user.getRole()
            );
            
            // 构建返回的用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("studentId", user.getStudentId());
            userInfo.put("name", user.getName());
            userInfo.put("role", user.getRole());
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", userInfo);
            
            return Result.success("登录成功", result);
            
        } catch (Exception e) {
            return Result.error(500, "登录失败：" + e.getMessage());
        }
    }

    /**
     * 用户退出登录
     * 实际的token清除由前端完成，这里仅用于规范
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("退出成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> getCurrentUser() {
        try {
            // 从SecurityContext获取当前用户信息
            String username = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
            
            if (username == null || "anonymousUser".equals(username)) {
                return Result.error(401, "用户未登录");
            }
            
            // 根据用户名查找用户信息
            User user = userService.findByStudentIdOrPhone(username);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }
            
            // 构建返回的用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("studentId", user.getStudentId());
            userInfo.put("name", user.getName());
            userInfo.put("phone", user.getPhone());
            userInfo.put("role", user.getRole());
            userInfo.put("dormitoryArea", user.getDormitoryArea());
            userInfo.put("status", user.getStatus());
            
            return Result.success(userInfo);
            
        } catch (Exception e) {
            return Result.error(500, "获取用户信息失败：" + e.getMessage());
        }
    }
}
