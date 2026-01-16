package com.shuai.rabbitmq.api;

/**
 * RabbitMQ 消费者 API 演示
 *
 * 包含内容：
 * - 消费者创建
 * - 推模式消费
 * - 拉模式消费
 * - 消息确认
 * - 拒绝消息
 * - QoS 设置
 * - 消费者标签
 * - 取消消费者
 *
 * 代码位置: [RabbitMqConsumerDemo.java](src/main/java/com/shuai/rabbitmq/api/RabbitMqConsumerDemo.java)
 *
 * @author Shuai
 */
public class RabbitMqConsumerDemo {

    /**
     * 创建消费者
     *
     * 【代码示例】
     *   Connection connection = factory.newConnection();
     *   Channel channel = connection.createChannel();
     *   DeliverCallback deliverCallback = (consumerTag, delivery) -> {
     *       String message = new String(delivery.getBody());
     *       channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
     *   };
     *   String consumerTag = channel.basicConsume("my-queue", false, deliverCallback, consumerTag -> {});
     *
     * 【配置选项】
     *   autoAck: false (手动确认，更可靠)
     *   autoAck: true (自动确认，可能丢失消息)
     */
    public void createConsumer() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();

        channel.basicConsume("my-queue", false, (tag, body) -> {});

        channel.close();
        connection.close();
    }

    /**
     * 推模式消费
     *
     * 【说明】
     *   SDK 负责拉取消息，自动推送给消费者
     *
     * 【代码示例】
     *   DeliverCallback deliverCallback = (consumerTag, delivery) -> {
     *       String message = new String(delivery.getBody());
     *       // 处理消息
     *       channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
     *   };
     *   CancelCallback cancelCallback = (consumerTag) -> {
     *       // 消费者被取消时调用
     *   };
     *   channel.basicConsume("my-queue", false, deliverCallback, cancelCallback);
     *
     * 【特点】
     *   - SDK 自动拉取消息
     *   - 自动推送消息给消费者
     *   - 适合简单场景
     *   - 实时性好
     */
    public void pushConsume() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();

        channel.basicConsume("test-queue", false, (tag, body) -> {});

        channel.close();
        connection.close();
    }

    /**
     * 拉模式消费
     *
     * 【说明】
     *   手动拉取消息，精确控制消费时机
     *
     * 【代码示例】
     *   GetResponse response = channel.basicGet("my-queue", false);
     *   if (response != null) {
     *       String message = new String(response.getBody());
     *       long deliveryTag = response.getEnvelope().getDeliveryTag();
     *       channel.basicAck(deliveryTag, false);
     *   }
     *
     * 【特点】
     *   - 手动拉取消息
     *   - 精确控制消费时机
     *   - 适合批量消费
     *   - 适合定时消费
     */
    public void pullConsume() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();

        channel.basicGet("test-queue", false);
        channel.basicGet("test-queue", false);

        channel.close();
        connection.close();
    }

    /**
     * 消息确认
     *
     * 【手动确认】
     *   // 单条确认
     *   channel.basicAck(deliveryTag, false);
     *   // 批量确认
     *   channel.basicAck(deliveryTag, true);
     *
     * 【否定确认】
     *   // 不重入队
     *   channel.basicNack(deliveryTag, false, false);
     *   // 重入队
     *   channel.basicNack(deliveryTag, false, true);
     *
     * 【自动确认】
     *   channel.basicConsume("queue", true, callback, ...);
     *
     * 【说明】
     *   手动确认更可靠，建议重要消息使用
     *   消息处理完成后确认，避免消息丢失
     */
    public void messageAck() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();

        channel.basicAck(1, false);
        channel.basicAck(100, true);
        channel.basicNack(2, false, false);
        channel.basicNack(3, false, true);

        channel.close();
        connection.close();
    }

    /**
     * 拒绝消息
     *
     * 【代码示例】
     *   // 拒绝，不重入队（进入死信队列）
     *   channel.basicReject(deliveryTag, false);
     *   // 拒绝，重入队
     *   channel.basicReject(deliveryTag, true);
     *
     *   // 批量拒绝
     *   channel.basicNack(deliveryTag, multiple, requeue);
     *
     * 【使用场景】
     *   - 消息格式错误，无法处理
     *   - 权限不足
     *   - 需要进入死信队列
     *   - 临时故障，重试处理
     */
    public void rejectMessage() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();

        channel.basicReject(1, false);
        channel.basicReject(2, true);
        channel.basicNack(3, true, false);

        channel.close();
        connection.close();
    }

    /**
     * QoS 设置
     *
     * 【代码示例】
     *   // 只设置预取数量
     *   channel.basicQos(prefetchCount);
     *   // 设置完整参数
     *   channel.basicQos(prefetchSize, prefetchCount, global);
     *
     * 【参数说明】
     *   prefetchSize: 预取消息大小（字节），0 表示无限制
     *   prefetchCount: 预取消息数量
     *   global: false=消费者级别，true=通道级别
     *
     * 【作用】
     *   - 平衡处理速度和内存使用
     *   - 避免消费者过载
     *   - prefetchCount 建议设置 10-100
     */
    public void qosSettings() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();

        channel.basicQos(10);
        channel.basicQos(0, 50, false);

        channel.close();
        connection.close();
    }

    /**
     * 消费者标签
     *
     * 【代码示例】
     *   String consumerTag = channel.basicConsume("queue", autoAck,
     *       deliverCallback, CancelCallback);
     *   // consumerTag - 消费者标签标识符
     *
     *   // 取消消费者
     *   channel.basicCancel(consumerTag);
     *
     * 【说明】
     *   - 消费者标签是标识符
     *   - 同一队列可有多个消费者
     *   - 消费者下线时标签失效
     */
    public void consumerTag() {
        MockConnection connection = new MockConnection();
        MockChannel channel = connection.createChannel();

        channel.basicConsume("queue-1", false, (tag, body) -> {});
        channel.basicConsume("queue-2", false, (tag, body) -> {});
        channel.basicCancel("consumer-tag-001");

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

        public void basicConsume(String queue, boolean autoAck, java.util.function.BiConsumer<String, String> deliverCallback) {
        }

        public String basicConsume(String queue, boolean autoAck, java.util.function.BiConsumer<String, String> deliverCallback, java.util.function.Consumer<String> cancelCallback) {
            return "consumer-tag";
        }

        public void basicGet(String queue, boolean autoAck) {
        }

        public void basicAck(long deliveryTag, boolean multiple) {
        }

        public void basicNack(long deliveryTag, boolean multiple, boolean requeue) {
        }

        public void basicReject(long deliveryTag, boolean requeue) {
        }

        public void basicQos(int prefetchCount) {
        }

        public void basicQos(int prefetchSize, int prefetchCount, boolean global) {
        }

        public void basicCancel(String consumerTag) {
        }

        public void close() {
        }
    }
}
