package com.team.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.admin.dto.UserListDTO;
import com.team.admin.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 分页查询用户列表（支持学号/姓名模糊搜索）
     * @param page 分页对象
     * @param keyword 搜索关键词（学号或姓名）
     * @return 分页结果
     */
    IPage<UserListDTO> selectUserListWithSearch(Page<UserListDTO> page, 
                                               @Param("keyword") String keyword);
}
