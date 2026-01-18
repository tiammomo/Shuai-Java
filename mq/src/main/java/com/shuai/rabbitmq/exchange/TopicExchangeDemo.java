package com.shuai.rabbitmq.exchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import java.nio.charset.StandardCharsets;

/**
 * Topic Exchange 演示
 *
 * 【特性】
 *   - 通配符匹配 routingKey
 *   - * 匹配一个单词
 *   - # 匹配零个或多个单词
 *
 * 【路由规则】
 *   - user.* 匹配: user.login, user.logout
 *   - order.# 匹配: order.created, order.paid, order.paid.shipped
 *   - # 匹配所有
 *
 * 【使用场景】
 *   - 日志分类收集
 *   - 灵活路由
 *   - 复杂消息分发
 *
 * 【运行方式】
 *   1. 确保 Docker RabbitMQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class TopicExchangeDemo {

    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";

    public static void main(String[] args) {
        TopicExchangeDemo demo = new TopicExchangeDemo();
        try {
            demo.topicExchangeDemo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Topic Exchange 完整演示
     */
    public void topicExchangeDemo() throws Exception {
        System.out.println("\n=== Topic Exchange 演示 ===");

        Connection connection = createConnection();
        Channel channel = connection.createChannel();

        // 1. 声明 Topic Exchange
        String exchangeName = "topic-logs";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true);
        System.out.println("Topic Exchange 创建成功: " + exchangeName);

        // 2. 声明队列并使用通配符绑定
        channel.queueDeclare("all-logs", true, false, false, null);
        channel.queueDeclare("user-logs", true, false, false, null);
        channel.queueDeclare("order-logs", true, false, false, null);
        channel.queueDeclare("error-logs", true, false, false, null);

        channel.queueBind("all-logs", exchangeName, "#");              // 所有日志
        channel.queueBind("user-logs", exchangeName, "user.*");        // 用户相关
        channel.queueBind("order-logs", exchangeName, "order.#");       // 订单相关
        channel.queueBind("error-logs", exchangeName, "*.error");       // 错误日志
        System.out.println("队列绑定完成:");
        System.out.println("  #        -> all-logs (所有日志)");
        System.out.println("  user.*   -> user-logs (用户相关)");
        System.out.println("  order.#  -> order-logs (订单相关)");
        System.out.println("  *.error  -> error-logs (错误日志)");

        // 3. 启动消费者
        startConsumer(channel, "all-logs", "全部日志");
        startConsumer(channel, "user-logs", "用户日志");
        startConsumer(channel, "order-logs", "订单日志");
        startConsumer(channel, "error-logs", "错误日志");

        // 4. 发送不同类型的消息
        System.out.println("\n发送消息（测试路由规则）...");
        channel.basicPublish(exchangeName, "user.login",
                null, "用户登录".getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(exchangeName, "user.logout",
                null, "用户登出".getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(exchangeName, "order.created",
                null, "订单创建".getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(exchangeName, "order.paid",
                null, "订单支付".getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(exchangeName, "payment.error",
                null, "支付错误".getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(exchangeName, "database.error",
                null, "数据库错误".getBytes(StandardCharsets.UTF_8));
        System.out.println("消息发送完成");

        // 等待消费者接收消息
        Thread.sleep(2000);

        channel.close();
        connection.close();
        System.out.println("Topic Exchange 演示完成");
    }

    /**
     * 启动消费者
     */
    private void startConsumer(Channel channel, String queueName, String logType) throws Exception {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            String routingKey = delivery.getEnvelope().getRoutingKey();
            System.out.println("[" + logType + "] 收到 [" + routingKey + "]: " + message);
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
