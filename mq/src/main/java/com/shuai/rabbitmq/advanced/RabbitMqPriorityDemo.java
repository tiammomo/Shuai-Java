package com.shuai.rabbitmq.advanced;

/**
 * RabbitMQ 优先级队列演示
 *
 * 代码位置: [RabbitMqPriorityDemo.java](src/main/java/com/shuai/rabbitmq/advanced/RabbitMqPriorityDemo.java)
 *
 * @author Shuai
 */
public class RabbitMqPriorityDemo {

    /**
     * 创建优先级队列
     *
     * 【配置】
     *   Map<String, Object> args = new HashMap<>();
     *   args.put("x-max-priority", 10);  // 最大优先级 10
     *   channel.queueDeclare("priority-queue", true, false, false, args);
     *
     * 【优先级范围】
     *   - 0-10 (默认 0)
     *   - 数字越大优先级越高
     *   - 建议设置 1-5，避免过多优先级
     *
     * 【使用场景】
     *   - VIP 客户消息优先处理
     *   - 紧急任务优先执行
     *   - 故障告警优先通知
     */
    public void createPriorityQueue() {
        MockChannel channel = new MockChannel();

        channel.declarePriorityQueue(10);

        channel.close();
    }

    /**
     * 发送优先级消息
     *
     * 【代码示例】
     *   AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
     *       .priority(5)  // 优先级 0-10
     *       .build();
     *   channel.basicPublish("", "priority-queue", props, message.getBytes());
     *
     * 【优先级消费】
     *   - 高优先级消息优先被消费
     *   - 同优先级按入队顺序消费
     *   - 消费者需要支持优先级消费
     *
     * 【注意事项】
     *   - 优先级只在同一队列内有效
     *   - 消息过多时性能下降
     *   - 需要所有消费者都支持优先级
     */
    public void sendPriorityMessage() {
        MockChannel channel = new MockChannel();

        // 发送不同优先级消息
        channel.sendWithPriority(1, "普通消息");
        channel.sendWithPriority(5, "重要消息");
        channel.sendWithPriority(10, "紧急消息");

        channel.close();
    }

    // ========== 模拟类 ==========

    static class MockChannel {

        public void declarePriorityQueue(int maxPriority) {
            /*
             * [RabbitMQ] 声明优先级队列
             *   Map<String, Object> args = new HashMap<>();
             *   args.put("x-max-priority", maxPriority);
             *   channel.queueDeclare("priority-queue", true, false, false, args);
             */
        }

        public void sendWithPriority(int priority, String body) {
            /*
             * [RabbitMQ] 发送优先级消息
             *   AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
             *       .priority(priority)
             *       .build();
             *   channel.basicPublish("", "priority-queue", props, body.getBytes());
             */
        }

        public void close() {
        }
    }
}
