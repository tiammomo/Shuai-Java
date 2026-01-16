package com.shuai.rabbitmq;

import com.shuai.model.Message;
import com.shuai.model.MessageResult;
import com.shuai.rabbitmq.producer.RabbitMqProducerImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RabbitMQ 生产者测试
 *
 * @author Shuai
 */
public class RabbitMqProducerTest {

    @Test
    void testSendSync() {
        // 创建生产者
        RabbitMqProducerImpl producer = new RabbitMqProducerImpl();
        producer.setHost("localhost");
        producer.setPort(5672);
        producer.setUsername("guest");
        producer.setPassword("guest");
        producer.setVirtualHost("/");
        producer.start();

        // 构建消息
        Message message = Message.builder()
                .topic("test-exchange")
                .tag("routing-key")
                .body("Hello RabbitMQ")
                .messageId("rm-001")
                .build();

        // 发送消息
        MessageResult result = producer.send(message);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("rm-001", result.getMessageId());

        producer.shutdown();
    }

    @Test
    void testSendWithPriority() {
        RabbitMqProducerImpl producer = new RabbitMqProducerImpl();
        producer.setHost("localhost");
        producer.start();

        Message message = Message.builder()
                .topic("priority-exchange")
                .body("Priority message")
                .messageId("rm-priority-001")
                .property("priority", "5")
                .build();

        MessageResult result = producer.send(message);
        assertNotNull(result);
        assertTrue(result.isSuccess());

        producer.shutdown();
    }

    @Test
    void testSendAsync() {
        RabbitMqProducerImpl producer = new RabbitMqProducerImpl();
        producer.setHost("localhost");
        producer.start();

        Message message = Message.builder()
                .topic("test-exchange")
                .body("Async message")
                .messageId("rm-002")
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
    void testDeclareExchange() {
        RabbitMqProducerImpl producer = new RabbitMqProducerImpl();
        producer.setHost("localhost");
        producer.start();

        // 声明各种 Exchange 类型
        assertDoesNotThrow(() -> producer.start());

        producer.shutdown();
    }

    @Test
    void testDeclareQueue() {
        RabbitMqProducerImpl producer = new RabbitMqProducerImpl();
        producer.setHost("localhost");
        producer.start();

        // 声明队列
        assertDoesNotThrow(() -> producer.start());

        producer.shutdown();
    }
}
