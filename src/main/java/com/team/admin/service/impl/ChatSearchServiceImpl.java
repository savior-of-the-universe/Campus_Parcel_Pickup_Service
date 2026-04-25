package com.team.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.admin.dto.ChatMessageDTO;
import com.team.admin.mapper.ChatMessageMapper;
import com.team.admin.service.ChatSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 聊天搜索服务实现类
 */
@Service
public class ChatSearchServiceImpl implements ChatSearchService {
    
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    
    @Override
    public IPage<ChatMessageDTO> searchMessages(String sessionId, String keyword, Integer page, Integer size) {
        if (!StringUtils.hasText(keyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        Page<ChatMessageDTO> pageObj = new Page<>(page, size);
        return chatMessageMapper.searchMessagesByKeyword(pageObj, sessionId, keyword.trim());
    }
    
    @Override
    public IPage<ChatMessageDTO> searchMessagesByUser(Long userId, String keyword, Integer page, Integer size) {
        if (!StringUtils.hasText(keyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        // 通过搜索发送者或接收者ID来查找用户相关的消息
        Page<ChatMessageDTO> pageObj = new Page<>(page, size);
        // 这里需要在Mapper中添加相应的方法，暂时使用通用搜索
        return chatMessageMapper.searchMessagesByKeyword(pageObj, null, keyword.trim());
    }
    
    @Override
    public IPage<ChatMessageDTO> searchMessagesByTimeRange(String sessionId, String keyword, 
                                                         java.time.LocalDateTime startTime, 
                                                         java.time.LocalDateTime endTime,
                                                         Integer page, Integer size) {
        if (!StringUtils.hasText(keyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("时间范围不能为空");
        }
        
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        
        Page<ChatMessageDTO> pageObj = new Page<>(page, size);
        // 这里需要在Mapper中添加时间范围搜索的方法
        return chatMessageMapper.searchMessagesByKeyword(pageObj, sessionId, keyword.trim());
    }
}
