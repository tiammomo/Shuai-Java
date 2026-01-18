package com.shuai.kafka.api;

import com.shuai.kafka.producer.KafkaProducerImpl;
import com.shuai.model.Message;
import com.shuai.model.MessageResult;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Kafka 生产者 API 演示
 *
 * 【运行方式】
 *   1. 确保 Docker Kafka 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class KafkaProducerDemo {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public static void main(String[] args) {
        KafkaProducerDemo demo = new KafkaProducerDemo();

        try {
            // 创建生产者
            demo.createProducer();

            // 同步发送
            demo.syncSend();

            // 异步发送
            demo.asyncSend();

            // 分区策略
            demo.partitionStrategy();

            // 批量发送
            demo.batchSend();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建生产者
     */
    public void createProducer() {
        System.out.println("\n=== 创建生产者 ===");

        KafkaProducerImpl producer = new KafkaProducerImpl();
        producer.setBootstrapServers(BOOTSTRAP_SERVERS);
        producer.setAcks("all");

        producer.start();
        System.out.println("Kafka 生产者创建成功");

        producer.shutdown();
        System.out.println("生产者已关闭");
    }

    /**
     * 同步发送
     */
    public void syncSend() throws Exception {
        System.out.println("\n=== 同步发送 ===");

        KafkaProducerImpl producer = new KafkaProducerImpl();
        producer.setBootstrapServers(BOOTSTRAP_SERVERS);
        producer.setAcks("all");
        producer.start();

        String topic = "sync-topic";

        for (int i = 0; i < 5; i++) {
            Message message = Message.builder()
                    .topic(topic)
                    .key("sync-key-" + i)
                    .body("同步消息 " + i)
                    .messageId("sync-" + i)
                    .build();

            MessageResult result = producer.send(message);
            System.out.println("发送结果: " + result.isSuccess() +
                    ", 消息ID: " + result.getMessageId() +
                    ", 分区: " + result.getPartition() +
                    ", Offset: " + result.getOffset());
        }

        producer.shutdown();
    }

    /**
     * 异步发送
     */
    public void asyncSend() throws Exception {
        System.out.println("\n=== 异步发送 ===");

        KafkaProducerImpl producer = new KafkaProducerImpl();
        producer.setBootstrapServers(BOOTSTRAP_SERVERS);
        producer.start();

        String topic = "async-topic";

        for (int i = 0; i < 5; i++) {
            final int idx = i;
            Message message = Message.builder()
                    .topic(topic)
                    .key("async-key-" + i)
                    .body("异步消息 " + i)
                    .messageId("async-" + i)
                    .build();

            producer.sendAsync(message, new KafkaProducerImpl.SendCallback() {
                @Override
                public void onSuccess(MessageResult result) {
                    System.out.println("异步发送成功: " + idx + ", 分区: " + result.getPartition());
                }

                @Override
                public void onFailure(MessageResult result) {
                    System.out.println("异步发送失败: " + idx + ", 错误: " + result.getErrorMessage());
                }
            });
        }

        // 等待异步发送完成
        Thread.sleep(2000);

        producer.shutdown();
    }

    /**
     * 分区策略
     */
    public void partitionStrategy() throws Exception {
        System.out.println("\n=== 分区策略 ===");

        KafkaProducerImpl producer = new KafkaProducerImpl();
        producer.setBootstrapServers(BOOTSTRAP_SERVERS);
        producer.start();

        String topic = "partition-topic";

        // 按 Key 哈希分区：相同 key 发送到相同分区
        String[] userIds = {"user-1001", "user-1002", "user-1003"};
        for (int i = 0; i < userIds.length; i++) {
            Message message = Message.builder()
                    .topic(topic)
                    .key(userIds[i])
                    .body("用户 " + userIds[i] + " 的消息 " + i)
                    .messageId("user-msg-" + i)
                    .build();

            MessageResult result = producer.send(message);
            System.out.println("用户 " + userIds[i] + " -> 分区: " + result.getPartition());
        }

        // 轮询分区（不指定 key）
        System.out.println("\n不指定 key 的消息:");
        for (int i = 0; i < 3; i++) {
            Message message = Message.builder()
                    .topic(topic)
                    .body("轮询消息 " + i)
                    .messageId("round-robin-" + i)
                    .build();

            MessageResult result = producer.send(message);
            System.out.println("轮询消息 " + i + " -> 分区: " + result.getPartition());
        }

        producer.shutdown();
    }

    /**
     * 批量发送
     */
    public void batchSend() throws Exception {
        System.out.println("\n=== 批量发送 ===");

        KafkaProducerImpl producer = new KafkaProducerImpl();
        producer.setBootstrapServers(BOOTSTRAP_SERVERS);
        producer.setAcks("all");
        producer.setBatchSize(16384);
        producer.setLingerMs(5);
        producer.start();

        String topic = "batch-topic";
        int count = 0;

        for (int i = 0; i < 20; i++) {
            Message message = Message.builder()
                    .topic(topic)
                    .key("batch-key-" + i)
                    .body("批量消息 " + i)
                    .messageId("batch-" + i)
                    .build();

            producer.send(message);
            count++;

            if (count % 10 == 0) {
                System.out.println("已发送 " + count + " 条消息");
            }
        }

        System.out.println("批量发送完成，共 " + count + " 条消息");

        producer.shutdown();
    }
}
