package com.team.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;                // 用户ID
    private String studentId;       // 学号
    private String name;            // 姓名
    private String phone;           // 手机号（客服特权：明文返回）
    private String role;            // 角色：USER, RUNNER, ADMIN
    private Integer status;         // 状态：0-禁用，1-启用
    private String password;        // 密码
    private String dormitoryArea;   // 宿舍区域
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间

    // 无参构造器
    public User() {
    }

    // 全参构造器
    public User(Long id, String studentId, String name, String phone, String role, Integer status, String password, String dormitoryArea, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.password = password;
        this.dormitoryArea = dormitoryArea;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDormitoryArea() {
        return dormitoryArea;
    }

    public void setDormitoryArea(String dormitoryArea) {
        this.dormitoryArea = dormitoryArea;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", status=" + status +
                ", dormitoryArea='" + dormitoryArea + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
