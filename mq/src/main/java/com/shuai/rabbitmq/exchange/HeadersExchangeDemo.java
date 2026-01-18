package com.shuai.rabbitmq.exchange;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Headers Exchange 演示
 *
 * 【特性】
 *   - 基于消息头属性匹配
 *   - x-match=all (AND) 或 x-match=any (OR)
 *
 * 【使用场景】
 *   - 复杂条件匹配
 *   - 多属性路由
 *   - 内容类型路由
 *
 * 【运行方式】
 *   1. 确保 Docker RabbitMQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class HeadersExchangeDemo {

    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";

    public static void main(String[] args) {
        HeadersExchangeDemo demo = new HeadersExchangeDemo();
        try {
            demo.headersExchangeDemo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Headers Exchange 完整演示
     */
    public void headersExchangeDemo() throws Exception {
        System.out.println("\n=== Headers Exchange 演示 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 1. 声明 Headers Exchange
        String exchangeName = "headers-exchange";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.HEADERS, true);
        System.out.println("Headers Exchange 创建成功: " + exchangeName);

        // 2. 声明队列并绑定（使用 x-match=all AND 匹配）
        channel.queueDeclare("json-high-queue", true, false, false, null);
        Map<String, Object> allArgs = new HashMap<>();
        allArgs.put("x-match", "all");
        allArgs.put("content-type", "application/json");
        allArgs.put("priority", 5);
        channel.queueBind("json-high-queue", exchangeName, "", allArgs);
        System.out.println("队列绑定: json-high-queue (x-match=all, content-type=application/json, priority=5)");

        // 3. 声明队列并绑定（使用 x-match=any OR 匹配）
        channel.queueDeclare("urgent-queue", true, false, false, null);
        Map<String, Object> anyArgs = new HashMap<>();
        anyArgs.put("x-match", "any");
        anyArgs.put("type", "urgent");
        anyArgs.put("importance", "high");
        channel.queueBind("urgent-queue", exchangeName, "", anyArgs);
        System.out.println("队列绑定: urgent-queue (x-match=any, type=urgent OR importance=high)");

        // 4. 启动消费者
        startConsumer(channel, "json-high-queue", "JSON高优先级");
        startConsumer(channel, "urgent-queue", "紧急消息");

        // 5. 发送消息（设置消息头属性）
        System.out.println("\n发送消息（带消息头属性）...");

        // 发送 JSON 高优先级消息（匹配 json-high-queue）
        AMQP.BasicProperties jsonProps = new AMQP.BasicProperties.Builder()
                .headers(Map.of("content-type", "application/json", "priority", 5))
                .build();
        channel.basicPublish(exchangeName, "", jsonProps,
                "{\"type\": \"json\", \"data\": \"测试JSON消息\"}".getBytes(StandardCharsets.UTF_8));
        System.out.println("发送: JSON高优先级消息");

        // 发送紧急消息（匹配 urgent-queue）
        AMQP.BasicProperties urgentProps = new AMQP.BasicProperties.Builder()
                .headers(Map.of("type", "urgent"))
                .build();
        channel.basicPublish(exchangeName, "", urgentProps,
                "这是一个紧急消息".getBytes(StandardCharsets.UTF_8));
        System.out.println("发送: 紧急消息");

        // 发送不匹配任何队列的消息
        AMQP.BasicProperties normalProps = new AMQP.BasicProperties.Builder()
                .headers(Map.of("content-type", "text/plain"))
                .build();
        channel.basicPublish(exchangeName, "", normalProps,
                "普通文本消息（不匹配任何队列）".getBytes(StandardCharsets.UTF_8));
        System.out.println("发送: 普通文本消息（不会被任何队列消费）");

        // 等待消费者接收消息
        Thread.sleep(2000);

        channel.close();
        connection.close();
        System.out.println("Headers Exchange 演示完成");
    }

    /**
     * 启动消费者
     */
    private void startConsumer(Channel channel, String queueName, String queueType) throws Exception {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            Map<String, Object> headers = delivery.getProperties().getHeaders();
            System.out.println("[" + queueType + "] 收到消息: " + message);
            if (headers != null) {
                System.out.println("  消息头: " + headers);
            }
        };
        channel.basicConsume(queueName, true, deliverCallback, tag -> {});
    }

    private Connection createConnection() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        return factory.newConnection();
    }
}
