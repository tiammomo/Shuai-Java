package com.shuai.rabbitmq.advanced;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 死信队列演示
 *
 * 【进入死信队列的条件】
 *   1. 消息被拒绝(basicReject/basicNack)，且 requeue=false
 *   2. 消息过期（TTL），无论是消息 TTL 还是队列 TTL
 *   3. 队列达到最大长度(x-max-length)
 *
 * 【运行方式】
 *   1. 确保 Docker RabbitMQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class RabbitMqDLXDemo {

    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";

    public static void main(String[] args) {
        RabbitMqDLXDemo demo = new RabbitMqDLXDemo();
        try {
            demo.configureDLQ();
            demo.rejectToDLQ();
            demo.messageTTLToDLQ();
            System.out.println("RabbitMQ 死信队列演示完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 死信队列配置
     */
    public void configureDLQ() throws Exception {
        System.out.println("\n=== 死信队列配置 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 1. 声明死信交换机
        channel.exchangeDeclare("dlx-exchange", BuiltinExchangeType.DIRECT, true);
        System.out.println("死信交换机创建成功: dlx-exchange");

        // 2. 声明死信队列
        channel.queueDeclare("dead-letter-queue", true, false, false, null);
        System.out.println("死信队列创建成功: dead-letter-queue");

        // 3. 绑定死信队列到死信交换机
        channel.queueBind("dead-letter-queue", "dlx-exchange", "dlq");
        System.out.println("死信队列绑定成功: dead-letter-queue -> dlx-exchange [dlq]");

        // 4. 工作队列配置死信
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "dlx-exchange");
        args.put("x-dead-letter-routing-key", "dlq");
        channel.queueDeclare("work-queue", true, false, false, args);
        System.out.println("工作队列创建成功（已配置死信）: work-queue");

        channel.close();
        connection.close();
    }

    /**
     * 消息拒绝进入死信队列
     *
     * 【区别】
     *   - requeue=false: 进入死信队列
     *   - requeue=true: 重新入队
     */
    public void rejectToDLQ() throws Exception {
        System.out.println("\n=== 消息拒绝进入死信队列 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 确保队列存在
        ensureQueuesExist(channel);

        // 启动死信队列消费者
        String dlqConsumerTag = channel.basicConsume("dead-letter-queue", true, (tag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[死信队列] 收到被拒绝的消息: " + message);
        }, tag -> {});

        // 发送测试消息
        channel.basicPublish("", "work-queue", null,
            "需要被拒绝的消息".getBytes(StandardCharsets.UTF_8));
        System.out.println("测试消息已发送");

        // 消费并拒绝消息（requeue=false 进入死信队列）
        String workConsumerTag = channel.basicConsume("work-queue", false, (tag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[工作队列] 收到消息: " + message);
            // 拒绝消息，不重入队 -> 进入死信队列
            channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
        }, tag -> {});

        Thread.sleep(2000);

        channel.basicCancel(dlqConsumerTag);
        channel.basicCancel(workConsumerTag);
        channel.close();
        connection.close();
    }

    /**
     * 消息过期进入死信队列
     *
     * 【消息 TTL】
     *   消息级别 TTL：每条消息独立过期时间
     *
     * 【队列 TTL】
     *   队列级别 TTL：队首消息过期时间（先进先出）
     */
    public void messageTTLToDLQ() throws Exception {
        System.out.println("\n=== 消息过期进入死信队列 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 确保队列存在
        ensureQueuesExist(channel);

        // 启动死信队列消费者
        String dlqConsumerTag = channel.basicConsume("dead-letter-queue", true, (tag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            String ttl = delivery.getProperties().getExpiration();
            System.out.println("[死信队列] 收到过期消息: " + message + ", TTL=" + ttl + "ms");
        }, tag -> {});

        // 发送消息级别 TTL 的消息
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .expiration("5000")  // 5秒过期
                .build();
        channel.basicPublish("", "work-queue", props,
            "5秒后过期的消息".getBytes(StandardCharsets.UTF_8));
        System.out.println("消息已发送，5秒后过期进入死信队列");

        // 等待消息过期
        Thread.sleep(8000);

        channel.basicCancel(dlqConsumerTag);
        channel.close();
        connection.close();
    }

    /**
     * 确保必要的队列存在
     */
    private void ensureQueuesExist(Channel channel) throws IOException {
        // 死信交换机
        channel.exchangeDeclare("dlx-exchange", BuiltinExchangeType.DIRECT, true);

        // 死信队列
        channel.queueDeclare("dead-letter-queue", true, false, false, null);
        channel.queueBind("dead-letter-queue", "dlx-exchange", "dlq");

        // 工作队列（配置死信）
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "dlx-exchange");
        args.put("x-dead-letter-routing-key", "dlq");
        channel.queueDeclare("work-queue", true, false, false, args);
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
