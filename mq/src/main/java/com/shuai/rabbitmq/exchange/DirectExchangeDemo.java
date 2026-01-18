package com.shuai.rabbitmq.exchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Direct Exchange 演示
 *
 * 【特性】
 *   - 精确匹配 routingKey 和 bindingKey
 *   - routingKey 完全相等才路由到队列
 *
 * 【使用场景】
 *   - 日志分级处理（error/warning/info）
 *   - 任务类型路由
 *   - 精确消息分发
 *
 * 【运行方式】
 *   1. 确保 Docker RabbitMQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class DirectExchangeDemo {

    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";

    public static void main(String[] args) {
        DirectExchangeDemo demo = new DirectExchangeDemo();
        try {
            demo.directExchangeDemo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Direct Exchange 完整演示
     */
    public void directExchangeDemo() throws Exception {
        System.out.println("\n=== Direct Exchange 演示 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 1. 声明 Direct Exchange
        String exchangeName = "direct-logs";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT, true);
        System.out.println("Direct Exchange 创建成功: " + exchangeName);

        // 2. 声明队列并绑定到 Exchange
        channel.queueDeclare("error-queue", true, false, false, null);
        channel.queueDeclare("warning-queue", true, false, false, null);
        channel.queueDeclare("info-queue", true, false, false, null);

        channel.queueBind("error-queue", exchangeName, "error");
        channel.queueBind("warning-queue", exchangeName, "warning");
        channel.queueBind("info-queue", exchangeName, "info");
        System.out.println("队列绑定成功: error/warning/info -> direct-logs");

        // 3. 启动消费者接收消息
        startConsumer(channel, "error-queue", "error");
        startConsumer(channel, "warning-queue", "warning");
        startConsumer(channel, "info-queue", "info");

        // 4. 发送不同级别的日志消息
        System.out.println("\n发送日志消息...");
        channel.basicPublish(exchangeName, "error", null,
                "[ERROR] 系统发生严重错误".getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(exchangeName, "warning", null,
                "[WARNING] 内存使用率过高".getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(exchangeName, "info", null,
                "[INFO] 用户登录成功".getBytes(StandardCharsets.UTF_8));
        System.out.println("消息发送完成");

        // 等待消费者接收消息
        Thread.sleep(2000);

        channel.close();
        connection.close();
        System.out.println("Direct Exchange 演示完成");
    }

    /**
     * 启动消费者
     */
    private void startConsumer(Channel channel, String queueName, String routingKey) throws Exception {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[" + routingKey + "] 队列收到消息: " + message);
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
