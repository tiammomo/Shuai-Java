package com.shuai.rabbitmq.advanced;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 延迟队列演示
 *
 * 【运行方式】
 *   1. 确保 Docker RabbitMQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class RabbitMqDelayDemo {

    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";

    public static void main(String[] args) {
        RabbitMqDelayDemo demo = new RabbitMqDelayDemo();
        try {
            demo.delayWithTTL();
            demo.orderTimeoutCancel();
            System.out.println("RabbitMQ 延迟队列演示完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TTL + 死信队列实现延迟队列
     *
     * 【实现原理】
     *   1. 消息发送到延迟队列
     *   2. 消息等待 TTL 后过期
     *   3. 过期消息进入死信交换机
     *   4. 死信交换机路由到延迟死信队列
     *   5. 消费者从延迟死信队列消费
     */
    public void delayWithTTL() throws Exception {
        System.out.println("\n=== TTL + 死信队列实现延迟 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 1. 声明死信交换机和死信队列
        channel.exchangeDeclare("delay-dlx", BuiltinExchangeType.DIRECT, true);
        channel.queueDeclare("delay-dlq", true, false, false, null);
        channel.queueBind("delay-dlq", "delay-dlx", "delay-dlq");
        System.out.println("死信队列创建成功: delay-dlx -> delay-dlq");

        // 2. 声明延迟队列（配置 TTL 和死信）
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "delay-dlx");
        args.put("x-dead-letter-routing-key", "delay-dlq");
        args.put("x-message-ttl", 10000);  // 10秒延迟
        channel.queueDeclare("delay-queue", true, false, false, args);
        System.out.println("延迟队列创建成功: TTL=10秒");

        // 3. 启动消费者
        String consumerTag = channel.basicConsume("delay-dlq", true, (tag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[消费者] 收到延迟消息: " + message);
        }, tag -> {});
        System.out.println("消费者启动，等待延迟消息...");

        // 4. 发送延迟消息
        channel.basicPublish("", "delay-queue", null,
            "10秒后到达的消息".getBytes(StandardCharsets.UTF_8));
        System.out.println("延迟消息已发送，10秒后将被消费");

        // 等待消息消费
        Thread.sleep(15000);

        channel.basicCancel(consumerTag);
        channel.close();
        connection.close();
    }

    /**
     * 订单超时取消
     *
     * 【业务场景】
     *   1. 用户下单，发送延迟30分钟消息
     *   2. 30分钟后消息进入死信队列
     *   3. 消费者检查订单状态
     *   4. 未支付则取消订单
     */
    public void orderTimeoutCancel() throws Exception {
        System.out.println("\n=== 订单超时取消演示 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 1. 声明死信交换机和死信队列
        channel.exchangeDeclare("order-dlx", BuiltinExchangeType.DIRECT, true);
        channel.queueDeclare("order-timeout-dlq", true, false, false, null);
        channel.queueBind("order-timeout-dlq", "order-dlx", "order-dlq");

        // 2. 声明订单超时队列（30分钟延迟）
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "order-dlx");
        args.put("x-dead-letter-routing-key", "order-dlq");
        args.put("x-message-ttl", 1800000);  // 30分钟 = 1800000ms
        channel.queueDeclare("order-timeout-queue", true, false, false, args);

        // 3. 启动消费者
        String orderId = "ORD-20240119-001";
        System.out.println("订单消费者启动，等待30分钟后检查订单: " + orderId);

        String consumerTag = channel.basicConsume("order-timeout-dlq", true, (tag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[超时检查] 收到消息: " + message);
            // 检查订单状态，如果未支付则取消
            System.out.println("检查订单状态: " + orderId + " -> 已取消（超时未支付）");
        }, tag -> {});

        // 4. 下单时发送延迟消息
        channel.basicPublish("", "order-timeout-queue", null,
            ("订单超时检查: " + orderId).getBytes(StandardCharsets.UTF_8));
        System.out.println("订单创建成功，超时消息已发送，30分钟后将检查订单状态");

        // 等待测试（实际使用时消费者会持续运行）
        Thread.sleep(5000);

        channel.basicCancel(consumerTag);
        channel.close();
        connection.close();
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
