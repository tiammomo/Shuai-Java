package com.shuai.rabbitmq.advanced;

/**
 * RabbitMQ 死信队列演示
 *
 * 代码位置: [RabbitMqDLXDemo.java](src/main/java/com/shuai/rabbitmq/advanced/RabbitMqDLXDemo.java)
 *
 * @author Shuai
 */
public class RabbitMqDLXDemo {

    /**
     * 死信队列配置
     *
     * 【进入死信队列的条件】
     *   1. 消息被拒绝(basicReject/basicNack)，且 requeue=false
     *   2. 消息过期（TTL），无论是消息 TTL 还是队列 TTL
     *   3. 队列达到最大长度(x-max-length)
     *
     * 【配置死信队列】
     *   Map<String, Object> args = new HashMap<>();
     *   args.put("x-dead-letter-exchange", "dlx-exchange");
     *   args.put("x-dead-letter-routing-key", "dlq");
     *   channel.queueDeclare("work-queue", true, false, false, args);
     *
     *   // 死信队列
     *   channel.queueDeclare("dead-letter-queue", true, false, false, null);
     *
     * 【使用场景】
     *   - 失败消息处理
     *   - 超时消息处理
     *   - 异常消息监控
     *   - 消息重试
     */
    public void configureDLQ() {
        MockChannel channel = new MockChannel();

        // 工作队列配置死信
        channel.declareWithDLX();

        // 死信队列
        channel.declareDLQ();

        channel.close();
    }

    /**
     * 消息拒绝进入死信队列
     *
     * 【代码示例】
     *   // 拒绝消息，不重入队
     *   channel.basicReject(deliveryTag, false);
     *
     *   // 批量拒绝，不重入队
     *   channel.basicNack(deliveryTag, true, false);
     *
     * 【区别】
     *   - requeue=false: 进入死信队列
     *   - requeue=true: 重新入队
     *
     * 【使用场景】
     *   - 消息格式错误
     *   - 处理异常
     *   - 需要人工处理的消息
     */
    public void rejectToDLQ() {
        MockChannel channel = new MockChannel();

        // 拒绝消息进入死信队列
        channel.basicReject(1, false);
        channel.basicNack(2, true, false);

        channel.close();
    }

    /**
     * 消息过期进入死信队列
     *
     * 【消息 TTL】
     *   AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
     *       .expiration("30000")  // 30秒
     *       .build();
     *
     * 【队列 TTL】
     *   Map<String, Object> args = new HashMap<>();
     *   args.put("x-message-ttl", 60000);  // 60秒
     *
     * 【区别】
     *   - 消息 TTL: 每条消息独立过期时间
     *   - 队列 TTL: 队首消息过期时间（先进先出）
     */
    public void messageTTLToDLQ() {
        MockChannel channel = new MockChannel();

        // 消息级别 TTL
        channel.sendWithMessageTTL(30000);

        // 队列级别 TTL
        channel.declareQueueWithTTL(60000);

        channel.close();
    }

    /**
     * 队列满载进入死信队列
     *
     * 【配置】
     *   Map<String, Object> args = new HashMap<>();
     *   args.put("x-max-length", 1000);  // 队列最大长度
     *   channel.queueDeclare("work-queue", true, false, false, args);
     *
     * 【溢出行为】
     *   - drop: 丢弃最旧的消息
     *   - reject-publish: 拒绝新消息
     *   - reject-publish-dlx: 拒绝的消息进入死信队列
     */
    public void maxLengthToDLQ() {
        MockChannel channel = new MockChannel();

        channel.declareWithMaxLength(1000);

        channel.close();
    }

    // ========== 模拟类 ==========

    static class MockChannel {

        public void declareWithDLX() {
            /*
             * [RabbitMQ] 工作队列配置死信
             *   Map<String, Object> args = new HashMap<>();
             *   args.put("x-dead-letter-exchange", "dlx-exchange");
             *   args.put("x-dead-letter-routing-key", "dlq");
             *   channel.queueDeclare("work-queue", true, false, false, args);
             */
        }

        public void declareDLQ() {
            /*
             * [RabbitMQ] 死信队列
             *   channel.queueDeclare("dead-letter-queue", true, false, false, null);
             */
        }

        public void basicReject(long deliveryTag, boolean requeue) {
        }

        public void basicNack(long deliveryTag, boolean multiple, boolean requeue) {
        }

        public void sendWithMessageTTL(int ttlMs) {
            /*
             * [RabbitMQ] 消息级别 TTL
             *   AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
             *       .expiration(String.valueOf(ttlMs))
             *       .build();
             */
        }

        public void declareQueueWithTTL(int ttlMs) {
            /*
             * [RabbitMQ] 队列级别 TTL
             *   Map<String, Object> args = new HashMap<>();
             *   args.put("x-message-ttl", ttlMs);
             *   channel.queueDeclare("ttl-queue", true, false, false, args);
             */
        }

        public void declareWithMaxLength(int maxLength) {
            /*
             * [RabbitMQ] 队列最大长度
             *   Map<String, Object> args = new HashMap<>();
             *   args.put("x-max-length", maxLength);
             *   channel.queueDeclare("max-length-queue", true, false, false, args);
             */
        }

        public void close() {
        }
    }
}
