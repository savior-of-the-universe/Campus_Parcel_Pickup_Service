package com.team.admin.controller;

import com.team.admin.entity.ChatMessage;
import com.team.admin.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * WebSocket聊天控制器
 */
@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 处理发送消息的请求
     * @param chatMessage 聊天消息
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // 设置发送时间
        chatMessage.setSendTime(LocalDateTime.now());
        
        // 保存消息到数据库
        chatService.sendMessage(chatMessage);
        
        // 广播消息到订阅者
        return chatMessage;
    }

    /**
     * 处理添加用户的请求
     * @param chatMessage 聊天消息（包含用户信息）
     * @return 用户加入消息
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage) {
        // 设置用户加入消息
        chatMessage.setContent("用户 " + chatMessage.getSenderId() + " 加入了聊天室");
        chatMessage.setSendTime(LocalDateTime.now());
        chatMessage.setContentType("SYSTEM");
        
        return chatMessage;
    }

    /**
     * 发送私人消息（点对点）
     * @param chatMessage 聊天消息
     */
    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        // 设置发送时间
        chatMessage.setSendTime(LocalDateTime.now());
        
        // 保存消息到数据库
        chatService.sendMessage(chatMessage);
        
        // 发送私人消息到特定用户
        messagingTemplate.convertAndSendToUser(
            String.valueOf(chatMessage.getReceiverId()),
            "/queue/private",
            chatMessage
        );
    }
}
