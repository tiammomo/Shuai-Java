package com.shuai.common;

import com.shuai.model.Message;
import com.shuai.model.MessageResult;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 消息模型测试
 *
 * @author Shuai
 */
public class MessageModelTest {

    @Test
    void testMessageBuilder() {
        Message message = Message.builder()
                .topic("test-topic")
                .tag("test-tag")
                .key("test-key")
                .body("test-body")
                .messageId("msg-001")
                .property("prop1", "value1")
                .property("prop2", "value2")
                .build();

        assertEquals("test-topic", message.getTopic());
        assertEquals("test-tag", message.getTag());
        assertEquals("test-key", message.getKey());
        assertEquals("test-body", message.getBody());
        assertEquals("msg-001", message.getMessageId());
        assertEquals("value1", message.getProperty("prop1"));
        assertEquals("value2", message.getProperty("prop2"));
        assertTrue(message.getTimestamp() > 0);
    }

    @Test
    void testMessageBuilderWithMap() {
        Map<String, String> props = new HashMap<>();
        props.put("key1", "value1");
        props.put("key2", "value2");

        Message message = Message.builder()
                .topic("topic")
                .body("body")
                .properties(props)
                .build();

        assertEquals("value1", message.getProperty("key1"));
        assertEquals("value2", message.getProperty("key2"));
    }

    @Test
    void testMessageSetters() {
        Message message = new Message();
        message.setTopic("topic");
        message.setTag("tag");
        message.setKey("key");
        message.setBody("body");
        message.setMessageId("msg-002");
        message.setProperties(new HashMap<>());

        assertEquals("topic", message.getTopic());
        assertEquals("tag", message.getTag());
        assertEquals("key", message.getKey());
        assertEquals("body", message.getBody());
        assertEquals("msg-002", message.getMessageId());
    }

    @Test
    void testMessageToString() {
        Message message = Message.builder()
                .topic("topic")
                .tag("tag")
                .key("key")
                .body("body")
                .messageId("msg-003")
                .build();

        String str = message.toString();
        assertTrue(str.contains("topic"));
        assertTrue(str.contains("tag"));
        assertTrue(str.contains("key"));
        assertTrue(str.contains("body"));
        assertTrue(str.contains("msg-003"));
    }

    @Test
    void testMessageResultSuccess() {
        MessageResult result = MessageResult.success("msg-001", "topic", 0, 100L);

        assertTrue(result.isSuccess());
        assertEquals("msg-001", result.getMessageId());
        assertEquals("topic", result.getTopic());
        assertEquals(0, result.getPartition());
        assertEquals(100L, result.getOffset());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testMessageResultFail() {
        MessageResult result = MessageResult.fail("msg-002", "error message");

        assertFalse(result.isSuccess());
        assertEquals("msg-002", result.getMessageId());
        assertEquals("error message", result.getErrorMessage());
        assertNull(result.getTopic());
    }

    @Test
    void testMessageResultSetters() {
        MessageResult result = new MessageResult();
        result.setMessageId("msg-003");
        result.setTopic("topic");
        result.setPartition(1);
        result.setOffset(200L);
        result.setSuccess(true);
        result.setErrorMessage(null);

        assertEquals("msg-003", result.getMessageId());
        assertEquals("topic", result.getTopic());
        assertEquals(1, result.getPartition());
        assertEquals(200L, result.getOffset());
        assertTrue(result.isSuccess());
    }

    @Test
    void testMqException() {
        MqException ex1 = new MqException("simple error");
        assertEquals("MQ_ERROR", ex1.getErrorCode());
        assertEquals("simple error", ex1.getMessage());

        MqException ex2 = new MqException("CODE001", "coded error");
        assertEquals("CODE001", ex2.getErrorCode());

        MqException ex3 = new MqException("error with cause", new RuntimeException("cause"));
        assertEquals("MQ_ERROR", ex3.getErrorCode());
        assertNotNull(ex3.getCause());
    }
}
