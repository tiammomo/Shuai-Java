package com.shuai.rabbitmq.exchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Fanout Exchange 演示
 *
 * 【特性】
 *   - 忽略 routingKey，广播到所有绑定的队列
 *   - 最简单的发布/订阅模式
 *
 * 【使用场景】
 *   - 广播通知
 *   - 实时消息推送
 *   - 多终端同步
 *
 * 【运行方式】
 *   1. 确保 Docker RabbitMQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class FanoutExchangeDemo {

    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";

    public static void main(String[] args) {
        FanoutExchangeDemo demo = new FanoutExchangeDemo();
        try {
            demo.fanoutExchangeDemo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fanout Exchange 完整演示
     */
    public void fanoutExchangeDemo() throws Exception {
        System.out.println("\n=== Fanout Exchange 演示 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 1. 声明 Fanout Exchange
        String exchangeName = "broadcast";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, true);
        System.out.println("Fanout Exchange 创建成功: " + exchangeName);

        // 2. 声明多个队列并绑定到 Exchange
        channel.queueDeclare("subscriber-1", true, false, false, null);
        channel.queueDeclare("subscriber-2", true, false, false, null);
        channel.queueDeclare("subscriber-3", true, false, false, null);

        channel.queueBind("subscriber-1", exchangeName, "");
        channel.queueBind("subscriber-2", exchangeName, "");
        channel.queueBind("subscriber-3", exchangeName, "");
        System.out.println("3 个队列绑定到广播 Exchange");

        // 3. 启动消费者
        startConsumer(channel, "subscriber-1", "订阅者1");
        startConsumer(channel, "subscriber-2", "订阅者2");
        startConsumer(channel, "subscriber-3", "订阅者3");

        // 4. 发送广播消息
        System.out.println("\n发送广播消息...");
        String broadcastMsg = "这是一个广播消息，所有订阅者都会收到！";
        channel.basicPublish(exchangeName, "", null, broadcastMsg.getBytes(StandardCharsets.UTF_8));
        System.out.println("广播消息已发送");

        // 等待消费者接收消息
        Thread.sleep(2000);

        channel.close();
        connection.close();
        System.out.println("Fanout Exchange 演示完成");
    }

    /**
     * 启动消费者
     */
    private void startConsumer(Channel channel, String queueName, String subscriberName) throws Exception {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[" + subscriberName + "] 收到广播: " + message);
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
