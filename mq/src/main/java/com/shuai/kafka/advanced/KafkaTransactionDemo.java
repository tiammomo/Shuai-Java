package com.shuai.kafka.advanced;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Kafka 事务消息演示
 *
 * 【运行方式】
 *   1. 确保 Docker Kafka 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class KafkaTransactionDemo {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public static void main(String[] args) {
        KafkaTransactionDemo demo = new KafkaTransactionDemo();
        try {
            demo.basicTransaction();
            demo.rollbackTransaction();
            System.out.println("Kafka 事务消息演示完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 事务消息基本流程
     *
     * 【前置条件】
     *   - 必须开启幂等性 (enable.idempotence=true)
     *   - 必须设置 acks=all
     *   - transactionId 必须唯一
     */
    public void basicTransaction() throws Exception {
        System.out.println("\n=== Kafka 事务消息基本流程 ===");

        KafkaProducer<String, String> producer = createTransactionProducer();

        producer.initTransactions();

        try {
            producer.beginTransaction();

            // 发送事务消息
            ProducerRecord<String, String> record1 = new ProducerRecord<>("transaction-topic", "key1", "message1");
            ProducerRecord<String, String> record2 = new ProducerRecord<>("transaction-topic", "key2", "message2");

            RecordMetadata metadata1 = producer.send(record1).get();
            RecordMetadata metadata2 = producer.send(record2).get();

            System.out.println("消息1发送成功: partition=" + metadata1.partition() + ", offset=" + metadata1.offset());
            System.out.println("消息2发送成功: partition=" + metadata2.partition() + ", offset=" + metadata2.offset());

            // 模拟业务逻辑
            if (true) {
                throw new RuntimeException("业务处理失败");
            }

            producer.commitTransaction();
            System.out.println("事务提交成功");

        } catch (Exception e) {
            producer.abortTransaction();
            System.out.println("事务回滚: " + e.getMessage());
        } finally {
            producer.close();
        }
    }

    /**
     * 回滚事务
     */
    public void rollbackTransaction() throws Exception {
        System.out.println("\n=== 回滚事务演示 ===");

        KafkaProducer<String, String> producer = createTransactionProducer();

        producer.initTransactions();
        producer.beginTransaction();

        try {
            ProducerRecord<String, String> record = new ProducerRecord<>("transaction-topic", "key", "rollback-message");
            RecordMetadata metadata = producer.send(record).get();
            System.out.println("消息发送成功: partition=" + metadata.partition() + ", offset=" + metadata.offset());

            // 模拟业务失败
            throw new RuntimeException("业务逻辑失败，需要回滚");

        } catch (Exception e) {
            producer.abortTransaction();
            System.out.println("事务已回滚: " + e.getMessage());
        } finally {
            producer.close();
        }
    }

    /**
     * 创建事务生产者
     */
    private KafkaProducer<String, String> createTransactionProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 事务配置
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "tx-demo-" + System.currentTimeMillis());

        return new KafkaProducer<>(props);
    }
}
