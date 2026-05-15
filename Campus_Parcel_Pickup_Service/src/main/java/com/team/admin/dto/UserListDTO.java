package com.team.admin.dto;

import java.time.LocalDateTime;

/**
 * 用户列表DTO（用于客服接口，包含明文手机号）
 */
public class UserListDTO {
    private Long id;                // 用户ID
    private String studentId;       // 学号
    private String name;            // 姓名
    private String phone;           // 手机号（客服特权：明文返回）
    private String role;            // 角色
    private Integer status;         // 状态
    private String dormitoryArea;   // 宿舍区域
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间

    // 无参构造器
    public UserListDTO() {
    }

    // 全参构造器
    public UserListDTO(Long id, String studentId, String name, String phone, String role, Integer status, String dormitoryArea, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.status = status;
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
        return "UserListDTO{" +
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
