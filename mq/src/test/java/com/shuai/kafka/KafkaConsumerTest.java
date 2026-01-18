package com.shuai.kafka;

import com.shuai.kafka.consumer.KafkaConsumerImpl;
import com.shuai.kafka.producer.KafkaProducerImpl;
import com.shuai.model.Message;
import com.shuai.model.MessageResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kafka 消费者测试
 *
 * @author Shuai
 */
public class KafkaConsumerTest {

    @Test
    void testConsumerStartAndStop() {
        KafkaConsumerImpl consumer = new KafkaConsumerImpl();
        consumer.setBootstrapServers("localhost:9092");
        consumer.setGroupId("test-group");
        consumer.subscribe("test-topic");

        assertFalse(consumer.isStarted());

        consumer.start();
        assertTrue(consumer.isStarted());

        assertEquals("test-group", consumer.getGroupId());

        consumer.shutdown();
        assertFalse(consumer.isStarted());
    }

    @Test
    void testConsumerPollEmpty() {
        KafkaConsumerImpl consumer = new KafkaConsumerImpl();
        consumer.setBootstrapServers("localhost:9092");
        consumer.setGroupId("test-group");
        consumer.subscribe("test-topic");
        consumer.start();

        // 轮询空消息应该返回 null
        Message result = consumer.poll(100);
        assertNull(result);

        consumer.shutdown();
    }

    @Test
    void testConsumerPollBatch() {
        KafkaConsumerImpl consumer = new KafkaConsumerImpl();
        consumer.setBootstrapServers("localhost:9092");
        consumer.setGroupId("test-group");
        consumer.subscribe("test-topic");
        consumer.start();

        // 批量轮询应该返回空数组
        Message[] results = consumer.pollBatch(100, 10);
        assertNotNull(results);
        assertEquals(0, results.length);

        consumer.shutdown();
    }

    @Test
    void testConsumerCommitSync() {
        KafkaConsumerImpl consumer = new KafkaConsumerImpl();
        consumer.setBootstrapServers("localhost:9092");
        consumer.setGroupId("test-group");
        consumer.setEnableAutoCommit(false);
        consumer.subscribe("test-topic");
        consumer.start();

        // 手动提交不应抛出异常
        assertDoesNotThrow(() -> consumer.commitSync());

        consumer.shutdown();
    }

    @Test
    void testConsumerWithoutStarting() {
        KafkaConsumerImpl consumer = new KafkaConsumerImpl();
        consumer.setBootstrapServers("localhost:9092");
        consumer.setGroupId("test-group");
        consumer.subscribe("test-topic");

        // 未启动时 poll 返回 null
        Message result = consumer.poll(100);
        assertNull(result);
    }

    @Test
    void testProducerConsumerIntegration() throws InterruptedException {
        // 1. 创建生产者并发送消息
        KafkaProducerImpl producer = new KafkaProducerImpl();
        producer.setBootstrapServers("localhost:9092");
        producer.setAcks("all");
        producer.start();

        String testMessageId = "integration-test-" + System.currentTimeMillis();
        Message sendMessage = Message.builder()
                .topic("integration-test-topic")
                .key("integration-key")
                .body("Integration test message")
                .messageId(testMessageId)
                .build();

        MessageResult sendResult = producer.send(sendMessage);
        assertTrue(sendResult.isSuccess(), "发送失败: " + sendResult.getErrorMessage());

        producer.shutdown();

        // 2. 等待消息可消费
        Thread.sleep(2000);

        // 3. 创建消费者并消费消息
        KafkaConsumerImpl consumer = new KafkaConsumerImpl();
        consumer.setBootstrapServers("localhost:9092");
        consumer.setGroupId("integration-test-group");
        consumer.setAutoOffsetReset("earliest");
        consumer.subscribe("integration-test-topic");
        consumer.start();

        // 4. 验证消费者状态
        assertTrue(consumer.isStarted());
        assertEquals("integration-test-group", consumer.getGroupId());

        consumer.shutdown();
    }
}
