package com.shuai.rabbitmq.api;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ 生产者 API 演示
 *
 * 【运行方式】
 *   1. 确保 Docker RabbitMQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class RabbitMqProducerDemo {

    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";
    private static final String VIRTUAL_HOST = "/";

    public static void main(String[] args) {
        RabbitMqProducerDemo demo = new RabbitMqProducerDemo();

        try {
            // 创建连接
            demo.createConnection();

            // Channel 操作
            demo.channelOperation();

            // Exchange 声明
            demo.declareExchange();

            // Queue 声明
            demo.declareQueue();

            // Binding 创建
            demo.createBinding();

            // 发送消息
            demo.sendMessage();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建连接
     */
    public void createConnection() throws Exception {
        System.out.println("\n=== 创建连接 ===");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setVirtualHost(VIRTUAL_HOST);

        Connection connection = factory.newConnection();
        System.out.println("连接创建成功: " + connection.getClass().getSimpleName());

        connection.close();
        System.out.println("连接已关闭");
    }

    /**
     * Channel 操作
     */
    public void channelOperation() throws Exception {
        System.out.println("\n=== Channel 操作 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 开启发布确认
        channel.confirmSelect();
        System.out.println("发布确认已开启");

        channel.close();
        connection.close();
    }

    /**
     * Exchange 声明
     */
    public void declareExchange() throws Exception {
        System.out.println("\n=== Exchange 声明 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // DIRECT - 精确匹配
        channel.exchangeDeclare("demo-direct", BuiltinExchangeType.DIRECT, true);
        System.out.println("DIRECT Exchange 创建成功: demo-direct");

        // FANOUT - 广播
        channel.exchangeDeclare("demo-fanout", BuiltinExchangeType.FANOUT, true);
        System.out.println("FANOUT Exchange 创建成功: demo-fanout");

        // TOPIC - 通配符匹配
        channel.exchangeDeclare("demo-topic", BuiltinExchangeType.TOPIC, true);
        System.out.println("TOPIC Exchange 创建成功: demo-topic");

        // HEADERS - 基于消息头
        channel.exchangeDeclare("demo-headers", BuiltinExchangeType.HEADERS, true);
        System.out.println("HEADERS Exchange 创建成功: demo-headers");

        channel.close();
        connection.close();
    }

    /**
     * Queue 声明
     */
    public void declareQueue() throws Exception {
        System.out.println("\n=== Queue 声明 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 基础队列
        channel.queueDeclare("demo-queue", true, false, false, null);
        System.out.println("基础队列创建成功: demo-queue");

        // 临时队列
        channel.queueDeclare("temp-queue", false, true, true, null);
        System.out.println("临时队列创建成功: temp-queue");

        // 带 TTL 的队列
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 60000);  // 60秒
        args.put("x-max-length", 10000);
        channel.queueDeclare("ttl-queue", true, false, false, args);
        System.out.println("TTL 队列创建成功: ttl-queue");

        channel.close();
        connection.close();
    }

    /**
     * Binding 创建
     */
    public void createBinding() throws Exception {
        System.out.println("\n=== Binding 创建 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 队列绑定到 Exchange
        channel.queueBind("demo-queue", "demo-direct", "demo-key");
        System.out.println("Binding 创建成功: demo-queue -> demo-direct [demo-key]");

        channel.queueBind("demo-queue", "demo-topic", "order.*");
        System.out.println("Binding 创建成功: demo-queue -> demo-topic [order.*]");

        channel.queueBind("demo-queue", "demo-fanout", "");
        System.out.println("Binding 创建成功: demo-queue -> demo-fanout");

        channel.close();
        connection.close();
    }

    /**
     * 发送消息
     */
    public void sendMessage() throws Exception {
        System.out.println("\n=== 发送消息 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 确保 Exchange 和 Queue 存在
        channel.exchangeDeclare("demo-topic", BuiltinExchangeType.TOPIC, true);
        channel.queueDeclare("demo-queue", true, false, false, null);
        channel.queueBind("demo-queue", "demo-topic", "order.*");

        // 发送消息
        byte[] messageBody = "Hello RabbitMQ!".getBytes(StandardCharsets.UTF_8);

        // 使用默认 Exchange
        channel.basicPublish("", "demo-queue", null, messageBody);
        System.out.println("消息发送成功: demo-queue");

        // 使用 Topic Exchange
        channel.basicPublish("demo-topic", "order.created", null,
                "订单创建消息".getBytes(StandardCharsets.UTF_8));
        System.out.println("消息发送成功: demo-topic [order.created]");

        channel.basicPublish("demo-topic", "order.paid", null,
                "订单支付消息".getBytes(StandardCharsets.UTF_8));
        System.out.println("消息发送成功: demo-topic [order.paid]");

        channel.close();
        connection.close();
    }

    private Connection createConnection() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setVirtualHost(VIRTUAL_HOST);
        return factory.newConnection();
    }
}
