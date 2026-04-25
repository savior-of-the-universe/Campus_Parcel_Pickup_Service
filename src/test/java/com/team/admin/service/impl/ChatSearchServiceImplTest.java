package com.team.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.admin.entity.ChatMessage;
import com.team.admin.mapper.ChatMessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatSearchServiceImpl 单元测试")
class ChatSearchServiceImplTest {

    @Mock
    private ChatMessageMapper chatMessageMapper;

    @InjectMocks
    private ChatSearchServiceImpl chatSearchService;

    private Page<ChatMessage> mockPageResult;
    private List<ChatMessage> mockMessages;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        ChatMessage message1 = new ChatMessage()
                .setId(1L)
                .setSessionId("session-123")
                .setSenderId(1L)
                .setReceiverId(2L)
                .setContent("我的包裹在哪里")
                .setContentType("TEXT")
                .setSendTime(LocalDateTime.now().minusHours(2))
                .setIsRead(false);

        ChatMessage message2 = new ChatMessage()
                .setId(2L)
                .setSessionId("session-123")
                .setSenderId(2L)
                .setReceiverId(1L)
                .setContent("您的包裹已经到达快递站")
                .setContentType("TEXT")
                .setSendTime(LocalDateTime.now().minusHours(1))
                .setIsRead(true);

        ChatMessage message3 = new ChatMessage()
                .setId(3L)
                .setSessionId("session-456")
                .setSenderId(3L)
                .setReceiverId(1L)
                .setContent("快递单号是什么")
                .setContentType("TEXT")
                .setSendTime(LocalDateTime.now().minusMinutes(30))
                .setIsRead(false);

        mockMessages = Arrays.asList(message1, message2, message3);
        
        // 创建模拟分页结果
        mockPageResult = new Page<>(1, 20);
        mockPageResult.setRecords(mockMessages);
        mockPageResult.setTotal(3L);
        mockPageResult.setPages(1L);
    }

    @Test
    @DisplayName("搜索消息 - 带会话ID和关键词")
    void searchMessages_WithSessionIdAndKeyword_ShouldReturnFilteredResults() {
        // Given
        String sessionId = "session-123";
        String keyword = "包裹";
        int page = 1;
        int size = 20;

        when(chatMessageMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(mockPageResult);

        // When
        Page<ChatMessage> result = chatSearchService.searchMessages(sessionId, keyword, page, size);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getTotal());
        assertEquals(3, result.getRecords().size());
        
        // 验证调用参数
        verify(chatMessageMapper, times(1)).selectPage(any(Page.class), argThat(wrapper -> {
            // 验证 QueryWrapper 包含正确的条件
            String sqlSegment = wrapper.getCustomSqlSegment();
            return sqlSegment != null && 
                   sqlSegment.contains("MATCH(content) AGAINST") &&
                   sqlSegment.contains("session_id");
        }));
    }

    @Test
    @DisplayName("搜索消息 - 仅关键词（不限制会话）")
    void searchMessages_WithKeywordOnly_ShouldSearchAllSessions() {
        // Given
        String sessionId = null;
        String keyword = "快递";
        int page = 1;
        int size = 20;

        when(chatMessageMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(mockPageResult);

        // When
        Page<ChatMessage> result = chatSearchService.searchMessages(sessionId, keyword, page, size);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getTotal());
        assertEquals(3, result.getRecords().size());
        
        // 验证调用参数 - 不应该包含 session_id 条件
        verify(chatMessageMapper, times(1)).selectPage(any(Page.class), argThat(wrapper -> {
            String sqlSegment = wrapper.getCustomSqlSegment();
            return sqlSegment != null && 
                   sqlSegment.contains("MATCH(content) AGAINST") &&
                   !sqlSegment.contains("session_id");
        }));
    }

    @Test
    @DisplayName("搜索消息 - 空会话ID字符串")
    void searchMessages_WithEmptySessionId_ShouldSearchAllSessions() {
        // Given
        String sessionId = "";
        String keyword = "包裹";
        int page = 1;
        int size = 20;

        when(chatMessageMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(mockPageResult);

        // When
        Page<ChatMessage> result = chatSearchService.searchMessages(sessionId, keyword, page, size);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getTotal());
        
        // 验证空字符串被当作 null 处理
        verify(chatMessageMapper, times(1)).selectPage(any(Page.class), argThat(wrapper -> {
            String sqlSegment = wrapper.getCustomSqlSegment();
            return sqlSegment != null && 
                   sqlSegment.contains("MATCH(content) AGAINST") &&
                   !sqlSegment.contains("session_id");
        }));
    }

    @Test
    @DisplayName("搜索消息 - 分页参数")
    void searchMessages_WithPagination_ShouldUseCorrectPageParams() {
        // Given
        String sessionId = "session-123";
        String keyword = "测试";
        int page = 2;
        int size = 10;

        Page<ChatMessage> expectedPage = new Page<>(2, 10);
        expectedPage.setRecords(Arrays.asList());
        expectedPage.setTotal(0L);

        when(chatMessageMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(expectedPage);

        // When
        Page<ChatMessage> result = chatSearchService.searchMessages(sessionId, keyword, page, size);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getCurrent());
        assertEquals(10L, result.getSize());
        
        // 验证分页参数正确传递
        verify(chatMessageMapper, times(1)).selectPage(argThat(pageParam -> 
                pageParam.getCurrent() == 2 && pageParam.getSize() == 10), any(QueryWrapper.class));
    }

    @Test
    @DisplayName("搜索消息 - 数据库异常")
    void searchMessages_DatabaseException_ShouldPropagateException() {
        // Given
        String sessionId = "session-123";
        String keyword = "测试";
        int page = 1;
        int size = 20;

        when(chatMessageMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            chatSearchService.searchMessages(sessionId, keyword, page, size);
        });
        
        verify(chatMessageMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    @DisplayName("搜索消息 - 空结果")
    void searchMessages_NoResults_ShouldReturnEmptyPage() {
        // Given
        String sessionId = "non-existent-session";
        String keyword = "不存在的关键词";
        int page = 1;
        int size = 20;

        Page<ChatMessage> emptyResult = new Page<>(1, 20);
        emptyResult.setRecords(Arrays.asList());
        emptyResult.setTotal(0L);

        when(chatMessageMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(emptyResult);

        // When
        Page<ChatMessage> result = chatSearchService.searchMessages(sessionId, keyword, page, size);

        // Then
        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
        assertEquals(1L, result.getCurrent());
        assertEquals(20L, result.getSize());
    }

    @Test
    @DisplayName("搜索消息 - 验证排序")
    void searchMessages_ShouldOrderBySendTimeDesc() {
        // Given
        String sessionId = "session-123";
        String keyword = "测试";

        when(chatMessageMapper.selectPage(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(mockPageResult);

        // When
        chatSearchService.searchMessages(sessionId, keyword, 1, 20);

        // Then
        verify(chatMessageMapper, times(1)).selectPage(any(Page.class), argThat(wrapper -> {
            // 验证排序条件
            String orderBySql = wrapper.getCustomSqlSegment();
            return wrapper.toString().contains("ORDER BY") && 
                   wrapper.toString().contains("send_time") &&
                   wrapper.toString().contains("DESC");
        }));
    }
}
