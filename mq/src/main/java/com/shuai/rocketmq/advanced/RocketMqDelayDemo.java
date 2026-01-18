package com.shuai.rocketmq.advanced;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * RocketMQ 延迟消息演示
 *
 * 【延迟级别】
 *   Level 1: 1s     Level 2: 5s     Level 3: 10s
 *   Level 4: 30s    Level 5: 1m     Level 6: 2m
 *   Level 7: 3m     Level 8: 4m     Level 9: 5m
 *   Level 10: 6m    Level 11: 7m    Level 12: 8m
 *   Level 13: 9m    Level 14: 10m   Level 15: 20m
 *   Level 16: 30m   Level 17: 1h    Level 18: 2h
 *
 * 【运行方式】
 *   1. 确保 Docker RocketMQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class RocketMqDelayDemo {

    private static final String NAMESRV_ADDR = "localhost:9876";

    public static void main(String[] args) {
        RocketMqDelayDemo demo = new RocketMqDelayDemo();
        try {
            demo.basicDelayMessage();
            demo.orderTimeoutCancel();
            demo.retryWithDelay();
            System.out.println("RocketMQ 延迟消息演示完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 延迟消息基本用法
     */
    public void basicDelayMessage() throws Exception {
        System.out.println("\n=== 延迟消息基本用法 ===");

        DefaultMQProducer producer = new DefaultMQProducer("delay-producer-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.start();
        System.out.println("生产者启动");

        // 发送延迟消息（Level 3 = 10秒后投递）
        Message msg = new Message("DelayTopic", "延迟10秒的消息".getBytes(StandardCharsets.UTF_8));
        msg.setDelayTimeLevel(3);  // 10秒

        SendResult result = producer.send(msg);
        System.out.println("延迟消息发送成功: msgId=" + result.getMsgId() +
            ", delayLevel=" + msg.getDelayTimeLevel());

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
     */
    public void orderTimeoutCancel() throws Exception {
        System.out.println("\n=== 订单超时取消演示 ===");

        DefaultMQProducer producer = new DefaultMQProducer("order-producer-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.start();
        System.out.println("订单生产者启动");

        String orderId = "ORD-20240119-001";

        // 下单时发送延迟消息（Level 16 = 30分钟后投递）
        Message msg = new Message("OrderTimeoutTopic",
            ("订单超时检查: " + orderId).getBytes(StandardCharsets.UTF_8));
        msg.setDelayTimeLevel(16);  // 30分钟

        SendResult result = producer.send(msg);
        System.out.println("订单超时消息已发送: orderId=" + orderId +
            ", msgId=" + result.getMsgId());
        System.out.println("消息将在30分钟后投递给消费者进行超时检查");

        producer.shutdown();
    }

    /**
     * 延迟重试机制
     *
     * 【说明】
     *   消息处理失败后，延迟一定时间再重试
     */
    public void retryWithDelay() throws Exception {
        System.out.println("\n=== 延迟重试机制演示 ===");

        DefaultMQProducer producer = new DefaultMQProducer("retry-producer-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.start();
        System.out.println("重试生产者启动");

        // 逐级延迟重试
        int[] delayLevels = {1, 2, 3, 4, 5};  // 1s, 5s, 10s, 30s, 1m
        String[] descriptions = {"立即重试", "1秒后重试", "5秒后重试", "30秒后重试", "1分钟后重试"};

        for (int i = 0; i < delayLevels.length; i++) {
            Message msg = new Message("RetryTopic",
                ("重试消息 - " + descriptions[i]).getBytes(StandardCharsets.UTF_8));
            msg.setDelayTimeLevel(delayLevels[i]);

            SendResult result = producer.send(msg);
            System.out.println("重试消息发送: " + descriptions[i] +
                ", msgId=" + result.getMsgId());
        }

        producer.shutdown();
    }
}
