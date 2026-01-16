package com.shuai.rocketmq.advanced;

/**
 * RocketMQ 延迟消息演示
 *
 * 代码位置: [RocketMqDelayDemo.java](src/main/java/com/shuai/rocketmq/advanced/RocketMqDelayDemo.java)
 *
 * @author Shuai
 */
public class RocketMqDelayDemo {

    /**
     * 延迟消息基本用法
     *
     * 【延迟级别】
     *   Level 1: 1s     Level 2: 5s     Level 3: 10s
     *   Level 4: 30s    Level 5: 1m     Level 6: 2m
     *   Level 7: 3m     Level 8: 4m     Level 9: 5m
     *   Level 10: 6m    Level 11: 7m    Level 12: 8m
     *   Level 13: 9m    Level 14: 10m   Level 15: 20m
     *   Level 16: 30m   Level 17: 1h    Level 18: 2h
     *
     * 【代码示例】
     *   Message msg = new Message("DelayTopic", body.getBytes());
     *   msg.setDelayTimeLevel(3);  // 10秒后投递
     *   producer.send(msg);
     *
     * 【使用场景】
     *   - 订单超时取消
     *   - 延迟任务调度
     *   - 失败重试
     *   - 缓存预热
     */
    public void basicDelayMessage() {
        MockProducer producer = new MockProducer();

        // 10秒延迟
        producer.sendDelay("delay-topic", "延迟10秒", 3);

        producer.shutdown();
    }

    /**
     * 订单超时取消
     *
     * 【业务场景】
     *   1. 用户下单，创建订单
     *   2. 发送延迟30分钟的消息
     *   3. 30分钟后检查订单状态
     *   4. 未支付则取消订单
     *
     * 【代码示例】
     *   // 下单时发送延迟消息
     *   Message msg = new Message("OrderTimeoutTopic", orderId.getBytes());
     *   msg.setDelayTimeLevel(5);  // 1分钟后检查
     *   producer.send(msg);
     *
     *   // 消费者处理
     *   consumer.registerMessageListener((msgs, ctx) -> {
     *       String orderId = new String(msgs.get(0).getBody());
     *       Order order = orderService.getOrder(orderId);
     *       if (!order.isPaid()) {
     *           orderService.cancel(orderId);
     *       }
     *       return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
     *   });
     */
    public void orderTimeoutCancel() {
        MockProducer producer = new MockProducer();

        // 30分钟延迟（Level 16）
        producer.sendDelay("order-timeout-topic", "order-001", 16);

        producer.shutdown();
    }

    /**
     * 延迟重试机制
     *
     * 【说明】
     *   消息处理失败后，延迟一定时间再重试
     *
     * 【代码示例】
     *   // 首次失败，1秒后重试
     *   Message msg = new Message("RetryTopic", body.getBytes());
     *   msg.setDelayTimeLevel(1);
     *   producer.send(msg);
     *
     *   // 第二次失败，5秒后重试
     *   msg.setDelayTimeLevel(2);
     *   producer.send(msg);
     *
     *   // 超过最大次数，进入死信队列
     */
    public void retryWithDelay() {
        MockProducer producer = new MockProducer();

        // 逐级延迟重试
        int[] delayLevels = {1, 2, 3, 4, 5};  // 1s, 5s, 10s, 30s, 1m

        for (int level : delayLevels) {
            producer.sendDelay("retry-topic", "重试消息", level);
        }

        producer.shutdown();
    }

    // ========== 模拟类 ==========

    static class MockProducer {

        public void sendDelay(String topic, String body, int delayLevel) {
            /*
             * [RocketMQ Producer] 发送延迟消息
             *   Message msg = new Message(topic, body.getBytes());
             *   msg.setDelayTimeLevel(delayLevel);
             *   producer.send(msg);
             */
        }

        public void shutdown() {
        }
    }
}
