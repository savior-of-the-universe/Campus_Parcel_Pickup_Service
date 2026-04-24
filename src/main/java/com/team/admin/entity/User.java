package com.team.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String studentId;      // 学号
    private String name;           // 姓名
    private String phone;          // 手机号（数据库存储密文，客服接口返回明文）
    private String role;           // 角色: CUSTOMER, RUNNER, ADMIN
    private Integer status;        // 0-正常 1-禁用
    private String password;       // 加密密码
    private String dormitoryArea;  // 宿舍区
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}