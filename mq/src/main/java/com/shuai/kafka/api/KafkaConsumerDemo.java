package com.shuai.kafka.api;

import com.shuai.kafka.consumer.KafkaConsumerImpl;
import com.shuai.model.Message;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Kafka 消费者 API 演示
 *
 * 【运行方式】
 *   1. 确保 Docker Kafka 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class KafkaConsumerDemo {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public static void main(String[] args) {
        KafkaConsumerDemo demo = new KafkaConsumerDemo();

        try {
            // 创建消费者
            demo.createConsumer();

            // 拉取消息
            demo.pollMessages();

            // 手动提交
            demo.manualCommit();

            // 消费位点管理
            demo.seekOffset();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建消费者
     */
    public void createConsumer() {
        System.out.println("\n=== 创建消费者 ===");

        KafkaConsumerImpl consumer = new KafkaConsumerImpl();
        consumer.setBootstrapServers(BOOTSTRAP_SERVERS);
        consumer.setGroupId("demo-consumer-group");
        consumer.setAutoOffsetReset("earliest");
        consumer.subscribe("test-topic");

        consumer.start();
        System.out.println("Kafka 消费者创建成功");

        consumer.shutdown();
        System.out.println("消费者已关闭");
    }

    /**
     * 拉取消息
     */
    public void pollMessages() throws Exception {
        System.out.println("\n=== 拉取消息 ===");

        KafkaConsumerImpl consumer = new KafkaConsumerImpl();
        consumer.setBootstrapServers(BOOTSTRAP_SERVERS);
        consumer.setGroupId("poll-consumer-group");
        consumer.setAutoOffsetReset("earliest");
        consumer.subscribe("test-topic");
        consumer.start();

        System.out.println("开始拉取消息，等待 5 秒...");

        AtomicBoolean running = new AtomicBoolean(true);
        long endTime = System.currentTimeMillis() + 5000;

        while (running.get() && System.currentTimeMillis() < endTime) {
            Message message = consumer.poll(100);
            if (message != null) {
                System.out.println("收到消息: topic=" + message.getTopic() +
                        ", key=" + message.getKey() +
                        ", body=" + message.getBody());
            }
        }

        System.out.println("拉取消息完成");

        consumer.shutdown();
    }

    /**
     * 手动提交 offset
     */
    public void manualCommit() throws Exception {
        System.out.println("\n=== 手动提交 ===");

        KafkaConsumerImpl consumer = new KafkaConsumerImpl();
        consumer.setBootstrapServers(BOOTSTRAP_SERVERS);
        consumer.setGroupId("manual-commit-group");
        consumer.setAutoOffsetReset("earliest");
        consumer.setEnableAutoCommit(false);
        consumer.subscribe("test-topic");
        consumer.start();

        System.out.println("开始拉取消息（手动提交）...");

        long endTime = System.currentTimeMillis() + 5000;
        int count = 0;

        while (System.currentTimeMillis() < endTime) {
            Message message = consumer.poll(100);
            if (message != null) {
                System.out.println("收到消息: " + message.getBody());
                count++;

                // 手动提交
                consumer.commit(message);
            }
        }

        System.out.println("共消费 " + count + " 条消息");

        consumer.shutdown();
    }

    /**
     * 消费位点管理
     */
    public void seekOffset() throws Exception {
        System.out.println("\n=== 消费位点管理 ===");

        KafkaConsumerImpl consumer = new KafkaConsumerImpl();
        consumer.setBootstrapServers(BOOTSTRAP_SERVERS);
        consumer.setGroupId("seek-consumer-group");
        consumer.setAutoOffsetReset("earliest");
        consumer.subscribe("test-topic");
        consumer.start();

        System.out.println("演示位点管理操作");

        // 同步提交
        consumer.commitSync();
        System.out.println("同步提交 offset 成功");

        consumer.shutdown();
        System.out.println("位点管理演示完成");
    }
}
