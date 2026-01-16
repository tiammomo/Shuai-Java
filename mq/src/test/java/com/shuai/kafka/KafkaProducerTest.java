package com.shuai.kafka;

import com.shuai.kafka.producer.KafkaProducerImpl;
import com.shuai.model.Message;
import com.shuai.model.MessageResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kafka 生产者测试
 *
 * @author Shuai
 */
public class KafkaProducerTest {

    @Test
    void testSendSync() {
        // 创建生产者
        KafkaProducerImpl producer = new KafkaProducerImpl();
        producer.setBootstrapServers("localhost:9092");
        producer.setAcks("all");
        producer.setRetries(3);
        producer.start();

        // 构建消息
        Message message = Message.builder()
                .topic("test-topic")
                .key("key-001")
                .body("Hello Kafka")
                .messageId("msg-001")
                .build();

        // 发送消息
        MessageResult result = producer.send(message);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("msg-001", result.getMessageId());
        assertEquals("test-topic", result.getTopic());

        producer.shutdown();
    }

    @Test
    void testSendAsync() {
        KafkaProducerImpl producer = new KafkaProducerImpl();
        producer.setBootstrapServers("localhost:9092");
        producer.start();

        Message message = Message.builder()
                .topic("test-topic")
                .body("Async message")
                .messageId("msg-002")
                .build();

        // 异步发送
        producer.sendAsync(message, new com.shuai.common.interfaces.MqProducer.SendCallback() {
            @Override
            public void onSuccess(MessageResult result) {
                assertNotNull(result);
                assertTrue(result.isSuccess());
            }

            @Override
            public void onFailure(MessageResult result) {
                fail("Should not fail");
            }
        });

        producer.shutdown();
    }

    @Test
    void testSendOneWay() {
        KafkaProducerImpl producer = new KafkaProducerImpl();
        producer.setBootstrapServers("localhost:9092");
        producer.start();

        Message message = Message.builder()
                .topic("test-topic")
                .body("One way message")
                .messageId("msg-003")
                .build();

        // 单向发送不应抛出异常
        assertDoesNotThrow(() -> producer.sendOneWay(message));

        producer.shutdown();
    }

    @Test
    void testMessageBuilder() {
        Message message = Message.builder()
                .topic("topic")
                .tag("tag")
                .key("key")
                .body("body")
                .property("key1", "value1")
                .build();

        assertEquals("topic", message.getTopic());
        assertEquals("tag", message.getTag());
        assertEquals("key", message.getKey());
        assertEquals("body", message.getBody());
        assertEquals("value1", message.getProperty("key1"));
    }
}
