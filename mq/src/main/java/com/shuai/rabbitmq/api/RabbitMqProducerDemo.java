package com.shuai.rabbitmq.api;

import java.util.Map;
import java.util.HashMap;

/**
 * RabbitMQ 生产者 API 演示
 *
 * 包含内容：
 * - 连接创建
 * - Channel 操作
 * - Exchange 声明
 * - Queue 声明
 * - Binding 创建
 * - 消息发送
 * - 发布确认
 * - 事务消息
 *
 * 代码位置: [RabbitMqProducerDemo.java](src/main/java/com/shuai/rabbitmq/api/RabbitMqProducerDemo.java)
 *
 * @author Shuai
 */
public class RabbitMqProducerDemo {

    /**
     * 创建连接
     *
     * 【核心配置】
     *   - host: RabbitMQ 服务器地址
     *   - port: 端口（5672 默认）
     *   - username: 用户名
     *   - password: 密码
     *   - virtualHost: 虚拟主机
     *
     * 【代码示例】
     *   ConnectionFactory factory = new ConnectionFactory();
     *   factory.setHost("localhost");
     *   factory.setPort(5672);
     *   factory.setUsername("guest");
     *   factory.setPassword("guest");
     *   Connection connection = factory.newConnection();
     */
    public void createConnection() {
        MockConnection connection = new MockConnection();
        connection.close();
    }

    /**
     * Channel 操作
     *
     * 【生命周期】
     *   - 创建: connection.createChannel()
     *   - 使用: 发送消息、声明队列等
     *   - 关闭: channel.close()
     *
     * 【代码示例】
     *   Channel channel = connection.createChannel();
     *   channel.basicPublish(exchange, routingKey, props, message);
     */
    public void channelOperation() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();
        channel.basicPublish("exchange", "routing-key", null, "message".getBytes());
        channel.confirmSelect();
        channel.close();
        connection.close();
    }

    /**
     * Exchange 声明
     *
     * 【Exchange 类型】
     *   - DIRECT: 精确匹配路由键
     *   - FANOUT: 忽略路由键，广播到所有队列
     *   - TOPIC: 通配符匹配（* 匹配一个词，# 匹配零个或多个词）
     *   - HEADERS: 基于消息头属性匹配
     *
     * 【代码示例】
     *   channel.exchangeDeclare("my-exchange", BuiltinExchangeType.DIRECT, true);
     *
     * 【参数说明】
     *   durable: 持久化
     *   autoDelete: 自动删除
     *   internal: 内部使用
     */
    public void declareExchange() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();

        channel.exchangeDeclare("direct-exchange", "DIRECT", true);
        channel.exchangeDeclare("fanout-exchange", "FANOUT", true);
        channel.exchangeDeclare("topic-exchange", "TOPIC", true);
        channel.exchangeDeclare("headers-exchange", "HEADERS", true);

        channel.close();
        connection.close();
    }

    /**
     * Queue 声明
     *
     * 【代码示例】
     *   channel.queueDeclare("my-queue", true, false, false, null);
     *
     * 【参数说明】
     *   durable: 队列持久化
     *   exclusive: 独占队列
     *   autoDelete: 自动删除
     *   arguments: 扩展参数
     *
     * 【扩展参数】
     *   x-message-ttl: 消息过期时间（毫秒）
     *   x-expires: 队列过期时间
     *   x-max-length: 队列最大长度
     *   x-dead-letter-exchange: 死信交换机
     */
    public void declareQueue() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();

        channel.queueDeclare("my-queue", true, false, false, null);
        channel.queueDeclare("temp-queue", false, true, true, null);

        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 60000);
        args.put("x-max-length", 10000);
        channel.queueDeclare("ttl-queue", true, false, false, args);

        channel.close();
        connection.close();
    }

    /**
     * Binding 创建
     *
     * 【Binding 类型】
     *   - Queue Binding: 队列绑定到 Exchange
     *   - Exchange Binding: Exchange 绑定到 Exchange
     *
     * 【代码示例】
     *   channel.queueBind("my-queue", "my-exchange", "my-routing-key");
     *
     * 【通配符规则】
     *   *: 匹配一个单词
     *   #: 匹配零个或多个单词
     */
    public void createBinding() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();

        channel.queueBind("order-queue", "order-exchange", "order.created");
        channel.queueBind("payment-queue", "payment-exchange", "payment.*");
        channel.queueBind("log-queue", "log-exchange", "#");

        channel.close();
        connection.close();
    }

    /**
     * 发送消息
     *
     * 【代码示例】
     *   channel.basicPublish("exchange", "routing-key", null, message.getBytes());
     *
     * 【参数说明】
     *   exchange: 交换机名称
     *   routingKey: 路由键
     *   props: 消息属性
     *   body: 消息体
     *
     * 【发送选项】
     *   - mandatory: 消息不可路由时是否返回
     *   - immediate: 是否有消费者立即消费
     */
    public void sendMessage() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();

        channel.basicPublish("test-exchange", "test-key", "消息1".getBytes());
        channel.basicPublish("test-exchange", "urgent", "紧急消息".getBytes());

        channel.close();
        connection.close();
    }

    /**
     * 发布确认
     *
     * 【确认机制】
     *   - 同步确认: channel.waitForConfirms()
     *   - 异步确认: ConfirmListener
     *
     * 【代码示例】
     *   channel.confirmSelect();
     *   channel.addConfirmListener(new ConfirmListener() {
     *       public void handleAck(long seq, boolean multiple) {}
     *       public void handleNack(long seq, boolean multiple) {}
     *   });
     *   boolean confirmed = channel.waitForConfirms();
     *
     * 【说明】
     *   发布确认确保消息已到达 Broker
     *   同步确认会阻塞，异步确认性能更好
     */
    public void publishConfirm() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();

        channel.confirmSelect();
        channel.addConfirmListener();
        channel.basicPublish("exchange", "key", "message".getBytes());
        channel.waitForConfirms();

        channel.close();
        connection.close();
    }

    /**
     * 事务消息
     *
     * 【代码示例】
     *   channel.txSelect();
     *   try {
     *       channel.basicPublish("exchange", "key", null, message.getBytes());
     *       channel.txCommit();
     *   } catch (Exception e) {
     *       channel.txRollback();
     *   }
     *
     * 【说明】
     *   - 性能较低，不推荐高吞吐场景
     *   - 与发布确认互斥
     *   - 确保消息原子性
     */
    public void transactionMessage() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();

        channel.txSelect();
        channel.basicPublish("tx-exchange", "tx-key", null, "事务消息1".getBytes());
        channel.basicPublish("tx-exchange", "tx-key", null, "事务消息2".getBytes());
        channel.txCommit();

        channel.close();
        connection.close();
    }

    // ========== 模拟类 ==========

    static class MockConnection {
        public MockChannel createChannel() {
            return new MockChannel();
        }

        public void close() {
        }
    }

    static class MockChannel {

        public void basicPublish(String exchange, String routingKey, byte[] props, byte[] body) {
        }

        public void basicPublish(String exchange, String routingKey, byte[] body) {
        }

        public void confirmSelect() {
        }

        public void addConfirmListener() {
        }

        public boolean waitForConfirms() {
            return true;
        }

        public void exchangeDeclare(String name, String type, boolean durable) {
        }

        public void queueDeclare(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> args) {
        }

        public void queueBind(String queue, String exchange, String routingKey) {
        }

        public void txSelect() {
        }

        public void txCommit() {
        }

        public void txRollback() {
        }

        public void close() {
        }
    }
}
