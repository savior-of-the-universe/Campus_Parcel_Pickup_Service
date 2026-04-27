package com.team.admin;

import com.team.admin.controller.ChatSearchControllerTest;
import com.team.admin.service.impl.ChatSearchServiceImplTest;
import com.team.admin.service.impl.ChatSearchServiceIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * 聊天搜索功能测试套件
 * 
 * 运行所有与聊天搜索相关的测试：
 * - ChatSearchControllerTest: 控制器层单元测试
 * - ChatSearchServiceImplTest: 服务层单元测试  
 * - ChatSearchServiceIntegrationTest: 服务层集成测试
 * 
 * 使用方法：
 * mvn test -Dtest=ChatSearchTestSuite
 * 或在IDE中直接运行此类
 */
@Suite
@SelectClasses({
    ChatSearchControllerTest.class,
    ChatSearchServiceImplTest.class,
    ChatSearchServiceIntegrationTest.class
})
public class ChatSearchTestSuite {
}
