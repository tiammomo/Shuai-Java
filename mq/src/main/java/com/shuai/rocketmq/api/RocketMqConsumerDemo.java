package com.shuai.rocketmq.api;

import com.shuai.model.Message;
import com.shuai.rocketmq.consumer.RocketMqConsumerImpl;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.protocol.heartbeat.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.protocol.heartbeat.ConsumeOrderlyStatus;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RocketMQ 消费者 API 演示
 *
 * 【运行方式】
 *   1. 确保 Docker RocketMQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class RocketMqConsumerDemo {

    private static final String NAMESRV_ADDR = "localhost:9876";

    public static void main(String[] args) {
        RocketMqConsumerDemo demo = new RocketMqConsumerDemo();

        try {
            // 创建 Push 消费者
            demo.createPushConsumer();

            // 并发消费
            demo.concurrentConsume();

            // 顺序消费
            demo.orderedConsume();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建 Push 消费者
     */
    public void createPushConsumer() {
        System.out.println("\n=== 创建 Push 消费者 ===");

        RocketMqConsumerImpl consumer = new RocketMqConsumerImpl();
        consumer.setNamesrvAddr(NAMESRV_ADDR);
        consumer.setConsumerGroup("push-consumer-group");
        consumer.setMessageModel("CLUSTERING");
        consumer.subscribe("test-topic", "*");

        consumer.start();
        System.out.println("Push 消费者创建成功");

        consumer.shutdown();
        System.out.println("消费者已关闭");
    }

    /**
     * 并发消费
     */
    public void concurrentConsume() {
        System.out.println("\n=== 并发消费 ===");

        RocketMqConsumerImpl consumer = new RocketMqConsumerImpl();
        consumer.setNamesrvAddr(NAMESRV_ADDR);
        consumer.setConsumerGroup("concurrent-consumer-group");
        consumer.setMessageModel("CLUSTERING");
        consumer.setConsumeMessageBatchMaxSize(1);
        consumer.subscribe("test-topic", "*");

        // 注册并发消息监听器
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (org.apache.rocketmq.common.message.MessageExt msg : msgs) {
                String body = new String(msg.getBody());
                System.out.println("并发消费: " + body + ", QueueID: " + msg.getQueueId());
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();
        System.out.println("并发消费者启动，订阅 test-topic");

        // 等待 5 秒接收消息
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        consumer.shutdown();
    }

    /**
     * 顺序消费
     */
    public void orderedConsume() {
        System.out.println("\n=== 顺序消费 ===");

        RocketMqConsumerImpl consumer = new RocketMqConsumerImpl();
        consumer.setNamesrvAddr(NAMESRV_ADDR);
        consumer.setConsumerGroup("order-consumer-group");
        consumer.setMessageModel("CLUSTERING");
        consumer.subscribe("order-topic", "*");

        // 注册顺序消息监听器
        consumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
            for (org.apache.rocketmq.common.message.MessageExt msg : msgs) {
                String body = new String(msg.getBody());
                System.out.println("顺序消费: " + body + ", QueueID: " + msg.getQueueId());
            }
            return ConsumeOrderlyStatus.SUCCESS;
        });

        consumer.start();
        System.out.println("顺序消费者启动，订阅 order-topic");

        // 等待 5 秒接收消息
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        consumer.shutdown();
    }
}
