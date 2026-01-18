package com.shuai.rabbitmq.advanced;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 优先级队列演示
 *
 * 【运行方式】
 *   1. 确保 Docker RabbitMQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class RabbitMqPriorityDemo {

    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";

    public static void main(String[] args) {
        RabbitMqPriorityDemo demo = new RabbitMqPriorityDemo();
        try {
            demo.priorityQueueDemo();
            System.out.println("RabbitMQ 优先级队列演示完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 优先级队列完整演示
     *
     * 【优先级范围】
     *   - 0-10 (默认 0)
     *   - 数字越大优先级越高
     *   - 建议设置 1-5，避免过多优先级
     */
    public void priorityQueueDemo() throws Exception {
        System.out.println("\n=== 优先级队列演示 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 1. 创建优先级队列（最大优先级 10）
        Map<String, Object> args = new HashMap<>();
        args.put("x-max-priority", 10);
        channel.queueDeclare("priority-queue", true, false, false, args);
        System.out.println("优先级队列创建成功: x-max-priority=10");

        // 2. 启动消费者
        String consumerTag = channel.basicConsume("priority-queue", true, (tag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            Integer priority = delivery.getProperties().getPriority();
            System.out.println("收到消息: " + message + " [优先级: " + priority + "]");
        }, tag -> {});

        // 3. 逆序发送不同优先级消息（验证高优先级优先消费）
        System.out.println("\n发送消息（低优先级到高优先级）...");

        // 先发送低优先级
        sendWithPriority(channel, "priority-queue", "普通消息", 1);
        sendWithPriority(channel, "priority-queue", "重要消息", 5);

        // 后发送高优先级
        sendWithPriority(channel, "priority-queue", "紧急消息", 10);

        // 等待消费
        Thread.sleep(2000);

        // 再次测试：同优先级按入队顺序
        System.out.println("\n发送同优先级消息...");
        for (int i = 1; i <= 3; i++) {
            sendWithPriority(channel, "priority-queue", "同优先级消息-" + i, 3);
        }

        Thread.sleep(2000);

        channel.basicCancel(consumerTag);
        channel.close();
        connection.close();
    }

    /**
     * 发送优先级消息
     */
    private void sendWithPriority(Channel channel, String queue, String message, int priority) throws IOException {
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .priority(priority)
                .build();
        channel.basicPublish("", queue, props, message.getBytes(StandardCharsets.UTF_8));
        System.out.println("发送: " + message + " [优先级: " + priority + "]");
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
