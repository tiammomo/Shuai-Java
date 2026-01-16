package com.shuai.rabbitmq.exchange;

import com.shuai.rabbitmq.exchange.DirectExchangeDemo;
import com.shuai.rabbitmq.exchange.FanoutExchangeDemo;
import com.shuai.rabbitmq.exchange.TopicExchangeDemo;
import com.shuai.rabbitmq.exchange.HeadersExchangeDemo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RabbitMQ Exchange 测试
 *
 * @author Shuai
 */
public class ExchangeDemoTest {

    @Test
    void testDirectExchange() {
        DirectExchangeDemo demo = new DirectExchangeDemo();
        assertDoesNotThrow(() -> demo.demo());
    }

    @Test
    void testFanoutExchange() {
        FanoutExchangeDemo demo = new FanoutExchangeDemo();
        assertDoesNotThrow(() -> demo.demo());
    }

    @Test
    void testTopicExchange() {
        TopicExchangeDemo demo = new TopicExchangeDemo();
        assertDoesNotThrow(() -> demo.demo());
    }

    @Test
    void testHeadersExchange() {
        HeadersExchangeDemo demo = new HeadersExchangeDemo();
        assertDoesNotThrow(() -> demo.demo());
    }

    @Test
    void testDirectExchangeDemoInternal() {
        // 测试 DirectExchangeDemo.MockChannel
        DirectExchangeDemo.MockChannel channel = new DirectExchangeDemo.MockChannel();

        assertDoesNotThrow(() -> channel.exchangeDeclare("test", "DIRECT", true));
        assertDoesNotThrow(() -> channel.queueBind("queue", "exchange", "key"));
        assertDoesNotThrow(() -> channel.basicPublish("exchange", "key", "message"));
        assertDoesNotThrow(() -> channel.close());
    }

    @Test
    void testFanoutExchangeDemoInternal() {
        FanoutExchangeDemo.MockChannel channel = new FanoutExchangeDemo.MockChannel();

        assertDoesNotThrow(() -> channel.exchangeDeclare("test", "FANOUT", true));
        assertDoesNotThrow(() -> channel.queueBind("queue", "exchange", ""));
        assertDoesNotThrow(() -> channel.basicPublish("exchange", "", "message"));
        assertDoesNotThrow(() -> channel.close());
    }

    @Test
    void testTopicExchangeDemoInternal() {
        TopicExchangeDemo.MockChannel channel = new TopicExchangeDemo.MockChannel();

        assertDoesNotThrow(() -> channel.exchangeDeclare("test", "TOPIC", true));
        assertDoesNotThrow(() -> channel.queueBind("queue", "exchange", "user.login"));
        assertDoesNotThrow(() -> channel.basicPublish("exchange", "user.login", "message"));
        assertDoesNotThrow(() -> channel.close());
    }

    @Test
    void testHeadersExchangeDemoInternal() {
        HeadersExchangeDemo.MockChannel channel = new HeadersExchangeDemo.MockChannel();

        assertDoesNotThrow(() -> channel.exchangeDeclare("test", "HEADERS", true));
        assertDoesNotThrow(() -> channel.basicPublishWithHeaders("application/json", "{}"));
        assertDoesNotThrow(() -> channel.close());
    }
}
