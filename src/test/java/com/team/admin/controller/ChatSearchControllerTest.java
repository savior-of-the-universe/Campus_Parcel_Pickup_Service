package com.team.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.team.admin.common.Result;
import com.team.admin.entity.ChatMessage;
import com.team.admin.service.ChatSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatSearchController.class)
@DisplayName("ChatSearchController 单元测试")
class ChatSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatSearchService chatSearchService;

    @Autowired
    private ObjectMapper objectMapper;

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

        mockMessages = Arrays.asList(message1, message2);
        
        // 创建模拟分页结果
        mockPageResult = new Page<>(1, 20);
        mockPageResult.setRecords(mockMessages);
        mockPageResult.setTotal(2L);
        mockPageResult.setPages(1L);
        mockPageResult.setCurrent(1L);
        mockPageResult.setSize(20L);
    }

    @Test
    @DisplayName("搜索消息 - 成功响应（带会话ID）")
    void searchMessages_WithSessionId_ShouldReturnSuccess() throws Exception {
        // Given
        String sessionId = "session-123";
        String keyword = "包裹";

        when(chatSearchService.searchMessages(eq(sessionId), eq(keyword), eq(1), eq(20)))
                .thenReturn(mockPageResult);

        // When & Then
        mockMvc.perform(get("/api/admin/chats/search")
                .param("sessionId", sessionId)
                .param("keyword", keyword)
                .param("page", "1")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records[0].id").value(1))
                .andExpect(jsonPath("$.data.records[0].sessionId").value("session-123"))
                .andExpect(jsonPath("$.data.records[0].content").value("我的包裹在哪里"));

        verify(chatSearchService, times(1)).searchMessages(sessionId, keyword, 1, 20);
    }

    @Test
    @DisplayName("搜索消息 - 成功响应（不带会话ID）")
    void searchMessages_WithoutSessionId_ShouldReturnSuccess() throws Exception {
        // Given
        String keyword = "快递";

        when(chatSearchService.searchMessages(isNull(), eq(keyword), eq(1), eq(20)))
                .thenReturn(mockPageResult);

        // When & Then
        mockMvc.perform(get("/api/admin/chats/search")
                .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(2));

        verify(chatSearchService, times(1)).searchMessages(null, keyword, 1, 20);
    }

    @Test
    @DisplayName("搜索消息 - 使用默认分页参数")
    void searchMessages_WithDefaultPagination_ShouldUseDefaults() throws Exception {
        // Given
        String keyword = "测试";

        when(chatSearchService.searchMessages(isNull(), eq(keyword), eq(1), eq(20)))
                .thenReturn(mockPageResult);

        // When & Then
        mockMvc.perform(get("/api/admin/chats/search")
                .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(chatSearchService, times(1)).searchMessages(null, keyword, 1, 20);
    }

    @Test
    @DisplayName("搜索消息 - 自定义分页参数")
    void searchMessages_WithCustomPagination_ShouldUseCustomParams() throws Exception {
        // Given
        String keyword = "测试";
        int customPage = 2;
        int customSize = 10;

        Page<ChatMessage> customPageResult = new Page<>(customPage, customSize);
        customPageResult.setRecords(Arrays.asList());
        customPageResult.setTotal(0L);

        when(chatSearchService.searchMessages(isNull(), eq(keyword), eq(customPage), eq(customSize)))
                .thenReturn(customPageResult);

        // When & Then
        mockMvc.perform(get("/api/admin/chats/search")
                .param("keyword", keyword)
                .param("page", String.valueOf(customPage))
                .param("size", String.valueOf(customSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.current").value(customPage))
                .andExpect(jsonPath("$.data.size").value(customSize));

        verify(chatSearchService, times(1)).searchMessages(null, keyword, customPage, customSize);
    }

    @Test
    @DisplayName("搜索消息 - 缺少必需的keyword参数")
    void searchMessages_MissingKeyword_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/chats/search")
                .param("sessionId", "session-123"))
                .andExpect(status().isBadRequest());

        verify(chatSearchService, never()).searchMessages(anyString(), anyString(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("搜索消息 - 空keyword参数")
    void searchMessages_EmptyKeyword_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/chats/search")
                .param("keyword", ""))
                .andExpect(status().isBadRequest());

        verify(chatSearchService, never()).searchMessages(anyString(), anyString(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("搜索消息 - 服务层异常")
    void searchMessages_ServiceException_ShouldReturnInternalServerError() throws Exception {
        // Given
        String keyword = "测试";

        when(chatSearchService.searchMessages(anyString(), anyString(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/admin/chats/search")
                .param("keyword", keyword))
                .andExpect(status().isInternalServerError());

        verify(chatSearchService, times(1)).searchMessages(isNull(), eq(keyword), eq(1), eq(20));
    }

    @Test
    @DisplayName("搜索消息 - 无效的分页参数")
    void searchMessages_InvalidPagination_ShouldHandleGracefully() throws Exception {
        // Given
        String keyword = "测试";

        when(chatSearchService.searchMessages(isNull(), eq(keyword), eq(0), eq(0)))
                .thenReturn(new Page<>(0, 0));

        // When & Then
        mockMvc.perform(get("/api/admin/chats/search")
                .param("keyword", keyword)
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isOk());

        verify(chatSearchService, times(1)).searchMessages(null, keyword, 0, 0);
    }

    @Test
    @DisplayName("搜索消息 - 特殊字符关键词")
    void searchMessages_SpecialCharacters_ShouldHandleCorrectly() throws Exception {
        // Given
        String keyword = "包裹!@#$%^&*()";

        when(chatSearchService.searchMessages(isNull(), eq(keyword), eq(1), eq(20)))
                .thenReturn(mockPageResult);

        // When & Then
        mockMvc.perform(get("/api/admin/chats/search")
                .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(chatSearchService, times(1)).searchMessages(null, keyword, 1, 20);
    }

    @Test
    @DisplayName("搜索消息 - 中文关键词")
    void searchMessages_ChineseKeyword_ShouldHandleCorrectly() throws Exception {
        // Given
        String keyword = "快递包裹";

        when(chatSearchService.searchMessages(isNull(), eq(keyword), eq(1), eq(20)))
                .thenReturn(mockPageResult);

        // When & Then
        mockMvc.perform(get("/api/admin/chats/search")
                .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(chatSearchService, times(1)).searchMessages(null, keyword, 1, 20);
    }

    @Test
    @DisplayName("搜索消息 - 长关键词")
    void searchMessages_LongKeyword_ShouldHandleCorrectly() throws Exception {
        // Given
        String keyword = "这是一个非常长的搜索关键词用来测试系统对长文本搜索的处理能力";

        when(chatSearchService.searchMessages(isNull(), eq(keyword), eq(1), eq(20)))
                .thenReturn(mockPageResult);

        // When & Then
        mockMvc.perform(get("/api/admin/chats/search")
                .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(chatSearchService, times(1)).searchMessages(null, keyword, 1, 20);
    }

    @Test
    @DisplayName("搜索消息 - 空结果集")
    void searchMessages_EmptyResults_ShouldReturnEmptyPage() throws Exception {
        // Given
        String keyword = "不存在的关键词";

        Page<ChatMessage> emptyResult = new Page<>(1, 20);
        emptyResult.setRecords(Arrays.asList());
        emptyResult.setTotal(0L);

        when(chatSearchService.searchMessages(isNull(), eq(keyword), eq(1), eq(20)))
                .thenReturn(emptyResult);

        // When & Then
        mockMvc.perform(get("/api/admin/chats/search")
                .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.records").isEmpty());

        verify(chatSearchService, times(1)).searchMessages(null, keyword, 1, 20);
    }
}
