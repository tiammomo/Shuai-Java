package com.shuai.rocketmq;

import com.shuai.model.Message;
import com.shuai.model.MessageResult;
import com.shuai.rocketmq.producer.RocketMqProducerImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * RocketMQ 消费者测试
 *
 * @author Shuai
 */
public class RocketMqConsumerTest {

    private boolean isRocketMqAvailable() {
        try {
            RocketMqProducerImpl producer = new RocketMqProducerImpl();
            producer.setProducerGroup("test-group");
            producer.setNamesrvAddr("localhost:9876");
            producer.start();
            producer.shutdown();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    void testSendSync() {
        assumeTrue(isRocketMqAvailable(), "RocketMQ not available, skipping test");

        // 创建生产者
        RocketMqProducerImpl producer = new RocketMqProducerImpl();
        producer.setProducerGroup("test-group");
        producer.setNamesrvAddr("localhost:9876");
        producer.setRetryTimesWhenSendFailed(2);
        producer.start();

        // 构建消息
        Message message = Message.builder()
                .topic("test-topic")
                .tag("tag-a")
                .body("Hello RocketMQ")
                .messageId("rmq-001")
                .build();

        // 发送消息
        MessageResult result = producer.send(message);

        // 验证结果
        assertNotNull(result);
        assertEquals("rmq-001", result.getMessageId());

        producer.shutdown();
    }

    @Test
    void testSendAsync() {
        assumeTrue(isRocketMqAvailable(), "RocketMQ not available, skipping test");

        RocketMqProducerImpl producer = new RocketMqProducerImpl();
        producer.setProducerGroup("test-group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        Message message = Message.builder()
                .topic("test-topic")
                .body("Async message")
                .messageId("rmq-002")
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
    void testDelayedMessage() {
        assumeTrue(isRocketMqAvailable(), "RocketMQ not available, skipping test");

        RocketMqProducerImpl producer = new RocketMqProducerImpl();
        producer.setProducerGroup("test-group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        Message message = Message.builder()
                .topic("delay-topic")
                .body("Delayed message")
                .messageId("rmq-delay-001")
                .build();

        // 延迟消息发送
        MessageResult result = producer.sendDelayed(message, 2);
        assertNotNull(result);

        producer.shutdown();
    }

    @Test
    void testTransactionMessage() {
        assumeTrue(isRocketMqAvailable(), "RocketMQ not available, skipping test");

        RocketMqProducerImpl producer = new RocketMqProducerImpl();
        producer.setProducerGroup("test-group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        Message message = Message.builder()
                .topic("tx-topic")
                .body("Transaction message")
                .messageId("rmq-tx-001")
                .build();

        // 事务消息发送
        MessageResult result = producer.send(message);
        assertNotNull(result);

        producer.shutdown();
    }
}
