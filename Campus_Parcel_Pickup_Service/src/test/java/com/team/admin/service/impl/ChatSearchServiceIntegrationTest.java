package com.team.admin.service.impl;

import com.team.admin.entity.ChatMessage;
import com.team.admin.mapper.ChatMessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ChatSearchService 集成测试")
class ChatSearchServiceIntegrationTest {

    @Autowired
    private ChatSearchServiceImpl chatSearchService;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    private static final String TEST_SESSION_ID_1 = "test-session-1";
    private static final String TEST_SESSION_ID_2 = "test-session-2";

    @BeforeEach
    void setUp() {
        // 清理测试数据
        chatMessageMapper.delete(null);
        
        // 插入测试数据
        insertTestData();
    }

    private void insertTestData() {
        // 会话1的消息
        ChatMessage message1 = new ChatMessage()
                .setSessionId(TEST_SESSION_ID_1)
                .setSenderId(1L)
                .setReceiverId(2L)
                .setContent("我的包裹在哪里")
                .setContentType("TEXT")
                .setSendTime(LocalDateTime.now().minusHours(3))
                .setIsRead(false);

        ChatMessage message2 = new ChatMessage()
                .setSessionId(TEST_SESSION_ID_1)
                .setSenderId(2L)
                .setReceiverId(1L)
                .setContent("您的包裹已经到达快递站，请及时领取")
                .setContentType("TEXT")
                .setSendTime(LocalDateTime.now().minusHours(2))
                .setIsRead(true);

        ChatMessage message3 = new ChatMessage()
                .setSessionId(TEST_SESSION_ID_1)
                .setSenderId(1L)
                .setReceiverId(2L)
                .setContent("快递单号是多少")
                .setContentType("TEXT")
                .setSendTime(LocalDateTime.now().minusHours(1))
                .setIsRead(false);

        // 会话2的消息
        ChatMessage message4 = new ChatMessage()
                .setSessionId(TEST_SESSION_ID_2)
                .setSenderId(3L)
                .setReceiverId(2L)
                .setContent("我的快递丢失了怎么办")
                .setContentType("TEXT")
                .setSendTime(LocalDateTime.now().minusMinutes(30))
                .setIsRead(false);

        ChatMessage message5 = new ChatMessage()
                .setSessionId(TEST_SESSION_ID_2)
                .setSenderId(2L)
                .setReceiverId(3L)
                .setContent("我们会帮您查询快递状态")
                .setContentType("TEXT")
                .setSendTime(LocalDateTime.now().minusMinutes(15))
                .setIsRead(true);

        // 插入数据
        chatMessageMapper.insert(message1);
        chatMessageMapper.insert(message2);
        chatMessageMapper.insert(message3);
        chatMessageMapper.insert(message4);
        chatMessageMapper.insert(message5);
    }

    @Test
    @DisplayName("全文搜索 - 搜索'包裹'关键词")
    void searchMessages_ByKeyword_Package() {
        // When
        var result = chatSearchService.searchMessages(null, "包裹", 1, 20);

        // Then
        assertNotNull(result);
        assertTrue(result.getTotal() >= 2); // 至少2条包含"包裹"的消息
        List<ChatMessage> messages = result.getRecords();
        
        // 验证搜索结果包含关键词
        assertTrue(messages.stream().anyMatch(msg -> msg.getContent().contains("包裹")));
        
        // 验证按时间降序排列
        for (int i = 0; i < messages.size() - 1; i++) {
            assertTrue(messages.get(i).getSendTime().isAfter(messages.get(i + 1).getSendTime()) ||
                      messages.get(i).getSendTime().equals(messages.get(i + 1).getSendTime()));
        }
    }

    @Test
    @DisplayName("全文搜索 - 搜索'快递'关键词")
    void searchMessages_ByKeyword_Express() {
        // When
        var result = chatSearchService.searchMessages(null, "快递", 1, 20);

        // Then
        assertNotNull(result);
        assertTrue(result.getTotal() >= 3); // 至少3条包含"快递"的消息
        List<ChatMessage> messages = result.getRecords();
        
        // 验证搜索结果包含关键词
        assertTrue(messages.stream().allMatch(msg -> msg.getContent().contains("快递")));
    }

    @Test
    @DisplayName("全文搜索 - 按会话ID过滤")
    void searchMessages_WithSessionIdFilter() {
        // When
        var result = chatSearchService.searchMessages(TEST_SESSION_ID_1, "包裹", 1, 20);

        // Then
        assertNotNull(result);
        assertTrue(result.getTotal() >= 2); // 会话1中至少2条包含"包裹"的消息
        List<ChatMessage> messages = result.getRecords();
        
        // 验证所有消息都属于指定会话
        assertTrue(messages.stream().allMatch(msg -> TEST_SESSION_ID_1.equals(msg.getSessionId())));
        
        // 验证搜索结果包含关键词
        assertTrue(messages.stream().anyMatch(msg -> msg.getContent().contains("包裹")));
    }

    @Test
    @DisplayName("全文搜索 - 按会话ID过滤，搜索'快递'")
    void searchMessages_WithSessionIdFilter_Express() {
        // When
        var result = chatSearchService.searchMessages(TEST_SESSION_ID_2, "快递", 1, 20);

        // Then
        assertNotNull(result);
        assertTrue(result.getTotal() >= 2); // 会话2中至少2条包含"快递"的消息
        List<ChatMessage> messages = result.getRecords();
        
        // 验证所有消息都属于指定会话
        assertTrue(messages.stream().allMatch(msg -> TEST_SESSION_ID_2.equals(msg.getSessionId())));
        
        // 验证搜索结果包含关键词
        assertTrue(messages.stream().allMatch(msg -> msg.getContent().contains("快递")));
    }

    @Test
    @DisplayName("全文搜索 - 不存在的关键词")
    void searchMessages_NonExistentKeyword() {
        // When
        var result = chatSearchService.searchMessages(null, "不存在的关键词", 1, 20);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }

    @Test
    @DisplayName("全文搜索 - 不存在的会话ID")
    void searchMessages_NonExistentSessionId() {
        // When
        var result = chatSearchService.searchMessages("non-existent-session", "包裹", 1, 20);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }

    @Test
    @DisplayName("全文搜索 - 分页测试")
    void searchMessages_Pagination() {
        // When - 第一页
        var page1 = chatSearchService.searchMessages(null, "快递", 1, 2);
        
        // Then - 第一页
        assertNotNull(page1);
        assertTrue(page1.getTotal() >= 3);
        assertEquals(2, page1.getRecords().size());
        assertEquals(1, page1.getCurrent());
        assertEquals(2, page1.getSize());

        // When - 第二页
        var page2 = chatSearchService.searchMessages(null, "快递", 2, 2);
        
        // Then - 第二页
        assertNotNull(page2);
        assertEquals(page1.getTotal(), page2.getTotal()); // 总数相同
        assertEquals(2, page2.getCurrent());
        assertEquals(2, page2.getSize());
        
        // 验证两页数据不重复
        List<Long> page1Ids = page1.getRecords().stream().map(ChatMessage::getId).toList();
        List<Long> page2Ids = page2.getRecords().stream().map(ChatMessage::getId).toList();
        assertTrue(page1Ids.stream().noneMatch(page2Ids::contains));
    }

    @Test
    @DisplayName("全文搜索 - 复合关键词")
    void searchMessages_ComplexKeyword() {
        // When
        var result = chatSearchService.searchMessages(null, "快递 包裹", 1, 20);

        // Then
        assertNotNull(result);
        assertTrue(result.getTotal() >= 1); // 至少1条消息同时包含"快递"和"包裹"
        List<ChatMessage> messages = result.getRecords();
        
        // 验证搜索结果包含关键词
        assertTrue(messages.stream().anyMatch(msg -> 
            msg.getContent().contains("快递") && msg.getContent().contains("包裹")));
    }

    @Test
    @DisplayName("全文搜索 - 部分匹配")
    void searchMessages_PartialMatch() {
        // When
        var result = chatSearchService.searchMessages(null, "包", 1, 20);

        // Then
        assertNotNull(result);
        assertTrue(result.getTotal() >= 2); // 应该匹配"包裹"
        List<ChatMessage> messages = result.getRecords();
        
        // 验证搜索结果包含部分关键词
        assertTrue(messages.stream().anyMatch(msg -> msg.getContent().contains("包")));
    }

    @Test
    @DisplayName("全文搜索 - 空字符串会话ID")
    void searchMessages_EmptySessionId() {
        // When
        var result = chatSearchService.searchMessages("", "快递", 1, 20);

        // Then
        assertNotNull(result);
        assertTrue(result.getTotal() >= 3); // 应该搜索所有会话
        
        // 验证空字符串被当作null处理
        var resultWithNull = chatSearchService.searchMessages(null, "快递", 1, 20);
        assertEquals(result.getTotal(), resultWithNull.getTotal());
    }

    @Test
    @DisplayName("全文搜索 - 验证时间排序")
    void searchMessages_TimeOrdering() {
        // When
        var result = chatSearchService.searchMessages(null, "快递", 1, 20);

        // Then
        assertNotNull(result);
        List<ChatMessage> messages = result.getRecords();
        
        // 验证按发送时间降序排列
        for (int i = 0; i < messages.size() - 1; i++) {
            ChatMessage current = messages.get(i);
            ChatMessage next = messages.get(i + 1);
            assertTrue(current.getSendTime().isAfter(next.getSendTime()) ||
                      current.getSendTime().equals(next.getSendTime()),
                    "消息未按时间降序排列: " + current.getSendTime() + " 应该在 " + next.getSendTime() + " 之前");
        }
    }

    @Test
    @DisplayName("全文搜索 - 特殊字符处理")
    void searchMessages_SpecialCharacters() {
        // Given - 插入包含特殊字符的消息
        ChatMessage specialMessage = new ChatMessage()
                .setSessionId(TEST_SESSION_ID_1)
                .setSenderId(1L)
                .setReceiverId(2L)
                .setContent("包裹!@#$%^&*()测试")
                .setContentType("TEXT")
                .setSendTime(LocalDateTime.now())
                .setIsRead(false);
        chatMessageMapper.insert(specialMessage);

        // When
        var result = chatSearchService.searchMessages(TEST_SESSION_ID_1, "包裹", 1, 20);

        // Then
        assertNotNull(result);
        assertTrue(result.getTotal() >= 3); // 原有的2条 + 新插入的1条
        assertTrue(result.getRecords().stream()
                .anyMatch(msg -> msg.getContent().contains("包裹!@#$%^&*()测试")));
    }
}
