package com.shuai.rabbitmq.api;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RabbitMQ 消费者 API 演示
 *
 * 【运行方式】
 *   1. 确保 Docker RabbitMQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class RabbitMqConsumerDemo {

    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";
    private static final String VIRTUAL_HOST = "/";

    public static void main(String[] args) {
        RabbitMqConsumerDemo demo = new RabbitMqConsumerDemo();

        try {
            // 创建消费者
            demo.createConsumer();

            // 推模式消费
            demo.pushConsume();

            // 拉模式消费
            demo.pullConsume();

            // QoS 设置
            demo.qosSettings();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建消费者
     */
    public void createConsumer() throws Exception {
        System.out.println("\n=== 创建消费者 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 确保队列存在
        channel.queueDeclare("demo-queue", true, false, false, null);

        // 创建消费者
        DeliverCallback deliverCallback = (tag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("收到消息: " + message);
        };

        String tag = channel.basicConsume("demo-queue", true, deliverCallback,
                cancelTag -> {
                    System.out.println("消费者取消: " + cancelTag);
                });

        System.out.println("消费者创建成功: " + consumerTag);

        // 消费一条消息
        Thread.sleep(1000);

        channel.basicCancel(consumerTag);
        channel.close();
        connection.close();
    }

    /**
     * 推模式消费
     */
    public void pushConsume() throws Exception {
        System.out.println("\n=== 推模式消费 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 确保队列存在
        channel.queueDeclare("push-queue", true, false, false, null);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("推模式收到: " + message);
            counter.incrementAndGet();

            if (counter.get() >= 3) {
                latch.countDown();
            }
        };

        // 手动确认
        String pushTag = channel.basicConsume("push-queue", false, deliverCallback,
                cancelTag -> System.out.println("消费者取消: " + cancelTag));

        System.out.println("推模式消费者启动，等待消息...");

        // 等待最多 5 秒
        latch.await(5, TimeUnit.SECONDS);

        channel.basicCancel(pushTag);
        channel.close();
        connection.close();

        System.out.println("推模式消费完成，共收到 " + counter.get() + " 条消息");
    }

    /**
     * 拉模式消费
     */
    public void pullConsume() throws Exception {
        System.out.println("\n=== 拉模式消费 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 确保队列存在
        channel.queueDeclare("pull-queue", true, false, false, null);

        int count = 0;
        // 拉取最多 5 条消息
        while (count < 5) {
            com.rabbitmq.client.GetResponse response = channel.basicGet("pull-queue", false);
            if (response == null) {
                break;
            }

            String message = new String(response.getBody(), StandardCharsets.UTF_8);
            System.out.println("拉模式收到: " + message);
            count++;

            // 确认消息
            channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
        }

        System.out.println("拉模式消费完成，共拉取 " + count + " 条消息");

        channel.close();
        connection.close();
    }

    /**
     * QoS 设置
     */
    public void qosSettings() throws Exception {
        System.out.println("\n=== QoS 设置 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 设置预取数量
        channel.basicQos(10);  // 每次最多预取 10 条消息
        System.out.println("QoS 设置成功: prefetchCount=10");

        // 确保队列存在
        channel.queueDeclare("qos-queue", true, false, false, null);

        CountDownLatch latch = new CountDownLatch(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("QoS 消费者收到: " + message);

            // 手动确认
            try {
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        String qosTag = channel.basicConsume("qos-queue", false, deliverCallback,
                cancelTag -> latch.countDown());

        System.out.println("QoS 消费者启动，等待消息...");

        // 等待最多 3 秒
        latch.await(3, TimeUnit.SECONDS);

        channel.basicCancel(qosTag);
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
