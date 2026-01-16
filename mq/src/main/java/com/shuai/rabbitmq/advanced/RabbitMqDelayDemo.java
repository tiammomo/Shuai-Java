package com.shuai.rabbitmq.advanced;

/**
 * RabbitMQ 延迟队列演示
 *
 * 代码位置: [RabbitMqDelayDemo.java](src/main/java/com/shuai/rabbitmq/advanced/RabbitMqDelayDemo.java)
 *
 * @author Shuai
 */
public class RabbitMqDelayDemo {

    /**
     * TTL + 死信队列实现延迟队列
     *
     * 【实现原理】
     *   1. 消息发送到延迟队列
     *   2. 消息等待 TTL 后过期
     *   3. 过期消息进入死信交换机
     *   4. 死信交换机路由到延迟死信队列
     *   5. 消费者从延迟死信队列消费
     *
     * 【代码示例】
     *   // 延迟队列配置
     *   Map<String, Object> args = new HashMap<>();
     *   args.put("x-dead-letter-exchange", "delay-dlx");
     *   args.put("x-dead-letter-routing-key", "delay-dlq");
     *   args.put("x-message-ttl", 30000);  // 30秒
     *   channel.queueDeclare("delay-queue", true, false, false, args);
     *
     *   // 死信队列
     *   channel.queueDeclare("delay-dlq", true, false, false, null);
     *
     * 【使用场景】
     *   - 订单超时取消
     *   - 延迟任务
     *   - 重试机制
     */
    public void delayWithTTL() {
        MockChannel channel = new MockChannel();

        // 延迟队列配置
        channel.declareDelayQueue(30000);

        // 死信队列
        channel.declareDeadLetterQueue();

        channel.close();
    }

    /**
     * 延迟消息插件
     *
     * 【说明】
     *   使用 rabbitmq_delayed_message_exchange 插件
     *
     * 【安装插件】
     *   rabbitmq-plugins enable rabbitmq_delayed_message_exchange
     *
     * 【代码示例】
     *   Map<String, Object> args = new HashMap<>();
     *   args.put("x-delayed-type", "direct");
     *   channel.exchangeDeclare("delayed-exchange", "x-delayed-message", true, false, args);
     *
     *   // 发送延迟消息
     *   AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
     *       .headers(Map.of("x-delay", 30000))  // 30秒
     *       .build();
     *   channel.basicPublish("delayed-exchange", "key", props, body);
     *
     * 【优点】
     *   - 精确延迟
     *   - 性能更好
     *   - 不需要死信队列
     */
    public void delayPlugin() {
        MockChannel channel = new MockChannel();

        // 声明延迟交换机
        channel.declareDelayedExchange();

        // 发送延迟消息
        channel.sendWithDelay(30000);

        channel.close();
    }

    /**
     * 订单超时取消
     *
     * 【业务场景】
     *   1. 用户下单，发送延迟30分钟消息
     *   2. 30分钟后消息进入死信队列
     *   3. 消费者检查订单状态
     *   4. 未支付则取消订单
     *
     * 【代码示例】
     *   // 下单时发送消息到延迟队列
     *   channel.basicPublish("", "delay-queue", props, orderId.getBytes());
     *
     *   // 延迟队列消费
     *   channel.basicConsume("delay-dlq", false, (tag, body) -> {
     *       String orderId = new String(body);
     *       Order order = orderService.getOrder(orderId);
     *       if (!order.isPaid()) {
     *           orderService.cancel(orderId);
     *       }
     *       channel.basicAck(deliveryTag, false);
     *   });
     */
    public void orderTimeoutCancel() {
        MockChannel channel = new MockChannel();

        // 发送订单超时消息
        channel.basicPublish("", "order-timeout-queue", "order-001");

        // 从死信队列消费
        channel.basicConsume("order-timeout-dlq", false, (tag, body) -> {});

        channel.close();
    }

    // ========== 模拟类 ==========

    static class MockChannel {

        public void declareDelayQueue(int ttlMs) {
            /*
             * [RabbitMQ] 延迟队列
             *   Map<String, Object> args = new HashMap<>();
             *   args.put("x-dead-letter-exchange", "delay-dlx");
             *   args.put("x-dead-letter-routing-key", "delay-dlq");
             *   args.put("x-message-ttl", ttlMs);
             *   channel.queueDeclare("delay-queue", true, false, false, args);
             */
        }

        public void declareDeadLetterQueue() {
            /*
             * [RabbitMQ] 死信队列
             *   channel.queueDeclare("delay-dlq", true, false, false, null);
             */
        }

        public void declareDelayedExchange() {
            /*
             * [RabbitMQ] 延迟交换机
             *   Map<String, Object> args = new HashMap<>();
             *   args.put("x-delayed-type", "direct");
             *   channel.exchangeDeclare("delayed-exchange", "x-delayed-message", true, false, args);
             */
        }

        public void sendWithDelay(int delayMs) {
            /*
             * [RabbitMQ] 发送延迟消息
             *   AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
             *       .headers(Map.of("x-delay", delayMs))
             *       .build();
             *   channel.basicPublish("delayed-exchange", "key", props, body);
             */
        }

        public void basicPublish(String exchange, String routingKey, String body) {
        }

        public void basicConsume(String queue, boolean autoAck, java.util.function.BiConsumer<String, String> callback) {
        }

        public void close() {
        }
    }
}
