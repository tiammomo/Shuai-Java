package com.shuai.elasticsearch.llm;

import com.shuai.elasticsearch.llm.OpenAIClient.Message;
import com.shuai.elasticsearch.llm.OpenAIClient.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * OpenAI 客户端测试类
 *
 * 测试 OpenAI API 客户端的消息构建、角色管理等功能。
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 */
class OpenAIClientTest {

    @Test
    @DisplayName("测试消息创建 - System 角色")
    void testSystemMessageCreation() {
        Message message = Message.system("你是一个技术专家");

        assertNotNull(message);
        assertEquals(Role.SYSTEM, message.getRole());
        assertEquals("你是一个技术专家", message.getContent());
    }

    @Test
    @DisplayName("测试消息创建 - User 角色")
    void testUserMessageCreation() {
        Message message = Message.user("如何安装 Elasticsearch?");

        assertNotNull(message);
        assertEquals(Role.USER, message.getRole());
        assertEquals("如何安装 Elasticsearch?", message.getContent());
    }

    @Test
    @DisplayName("测试消息创建 - Assistant 角色")
    void testAssistantMessageCreation() {
        Message message = Message.assistant("Elasticsearch 的安装步骤如下...");

        assertNotNull(message);
        assertEquals(Role.ASSISTANT, message.getRole());
        assertEquals("Elasticsearch 的安装步骤如下...", message.getContent());
    }

    @Test
    @DisplayName("测试消息角色枚举")
    void testRoleEnum() {
        assertEquals("system", Role.SYSTEM.getValue());
        assertEquals("user", Role.USER.getValue());
        assertEquals("assistant", Role.ASSISTANT.getValue());
        assertEquals("function", Role.FUNCTION.getValue());
    }

    @Test
    @DisplayName("测试消息列表构建")
    void testMessageListBuilding() {
        List<Message> messages = List.of(
            Message.system("你是一个技术专家"),
            Message.user("请解释什么是向量数据库")
        );

        assertNotNull(messages);
        assertEquals(2, messages.size());
        assertEquals(Role.SYSTEM, messages.get(0).getRole());
        assertEquals(Role.USER, messages.get(1).getRole());
    }

    @Test
    @DisplayName("测试消息 Setters 和 Getters")
    void testMessageSettersAndGetters() {
        Message message = new Message();

        message.setRole(Role.USER);
        message.setContent("测试问题");
        message.setName("test-user");

        assertEquals(Role.USER, message.getRole());
        assertEquals("测试问题", message.getContent());
        assertEquals("test-user", message.getName());
    }

    @Test
    @DisplayName("测试聊天响应构建")
    void testChatResponseBuilder() {
        OpenAIClient.ChatResponse response = new OpenAIClient.ChatResponse();
        OpenAIClient.Choice choice = new OpenAIClient.Choice();
        Message assistantMessage = new Message(Role.ASSISTANT, "这是一个回答");

        choice.setIndex(0);
        choice.setMessage(assistantMessage);
        choice.setFinishReason("stop");

        response.setId("chatcmpl-123");
        response.setObject("chat.completion");
        response.setCreated(System.currentTimeMillis());
        response.setModel("gpt-3.5-turbo");
        response.setChoices(List.of(choice));

        OpenAIClient.Usage usage = new OpenAIClient.Usage();
        usage.setPromptTokens(10);
        usage.setCompletionTokens(20);
        usage.setTotalTokens(30);
        response.setUsage(usage);

        assertEquals("chatcmpl-123", response.getId());
        assertEquals("chat.completion", response.getObject());
        assertEquals("gpt-3.5-turbo", response.getModel());
        assertEquals(1, response.getChoices().size());
        assertEquals("stop", response.getChoices().get(0).getFinishReason());
        assertEquals(30, response.getUsage().getTotalTokens());
    }

    @Test
    @DisplayName("测试获取第一条消息内容")
    void testGetFirstContent() {
        OpenAIClient.ChatResponse response = new OpenAIClient.ChatResponse();

        // 空响应
        assertNull(response.getFirstContent());

        // 有内容的响应
        OpenAIClient.Choice choice = new OpenAIClient.Choice();
        Message message = new Message(Role.ASSISTANT, "Hello World");
        choice.setMessage(message);

        response.setChoices(List.of(choice));

        assertEquals("Hello World", response.getFirstContent());
    }

    @Test
    @DisplayName("测试 Usage 统计")
    void testUsageStatistics() {
        OpenAIClient.Usage usage = new OpenAIClient.Usage();

        usage.setPromptTokens(100);
        usage.setCompletionTokens(200);

        assertEquals(100, usage.getPromptTokens());
        assertEquals(200, usage.getCompletionTokens());
        assertEquals(300, usage.getTotalTokens());
    }

    @Test
    @DisplayName("测试 Choice 索引")
    void testChoiceIndex() {
        OpenAIClient.Choice choice1 = new OpenAIClient.Choice();
        choice1.setIndex(0);

        OpenAIClient.Choice choice2 = new OpenAIClient.Choice();
        choice2.setIndex(1);

        assertEquals(0, choice1.getIndex());
        assertEquals(1, choice2.getIndex());
    }

    @Test
    @DisplayName("测试流式回调接口")
    void testStreamCallback() {
        // 测试 StreamCallback 是一个有效的函数式接口
        assertTrue(OpenAIClient.StreamCallback.class.isInterface(),
            "StreamCallback 应该是接口");

        // 测试可以使用 lambda
        StringBuilder output = new StringBuilder();
        OpenAIClient.StreamCallback callback = (content, progress) -> {
            output.append(content).append(":").append(progress);
        };

        // 调用回调
        callback.onChunk("Hello", 0.5f);
        callback.onChunk(" World", 1.0f);

        assertEquals("Hello:0.5 World:1.0", output.toString());
    }

    @Test
    @DisplayName("测试多轮对话消息构建")
    void testMultiTurnConversation() {
        List<Message> conversation = List.of(
            Message.system("你是一个乐于助人的助手"),
            Message.user("什么是 Elasticsearch?"),
            Message.assistant("Elasticsearch 是一个分布式搜索和分析引擎。"),
            Message.user("它有什么特点?")
        );

        assertEquals(4, conversation.size());
        assertEquals(Role.SYSTEM, conversation.get(0).getRole());
        assertEquals(Role.USER, conversation.get(1).getRole());
        assertEquals(Role.ASSISTANT, conversation.get(2).getRole());
        assertEquals(Role.USER, conversation.get(3).getRole());
    }

    @Test
    @DisplayName("测试客户端工厂方法")
    void testClientFactoryMethods() {
        // 测试静态工厂方法可以正常调用（不真正发起请求）
        OpenAIClient client1 = OpenAIClient.create("sk-test-key");
        assertNotNull(client1);

        OpenAIClient client2 = OpenAIClient.create("sk-test-key", "gpt-4");
        assertNotNull(client2);
        assertEquals("gpt-4", client2.getModel());
    }
}
