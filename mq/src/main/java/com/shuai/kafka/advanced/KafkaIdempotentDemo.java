package com.shuai.kafka.advanced;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Kafka 幂等与精确一次演示
 *
 * 【运行方式】
 *   1. 确保 Docker Kafka 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class KafkaIdempotentDemo {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public static void main(String[] args) {
        KafkaIdempotentDemo demo = new KafkaIdempotentDemo();
        try {
            demo.idempotentConfig();
            demo.duplicateMessage();
            System.out.println("Kafka 幂等性演示完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 幂等性配置
     *
     * 【配置参数】
     *   - enable.idempotence=true: 开启幂等性
     *   - acks=all: 所有副本确认
     *   - max.in.flight.requests.per.connection=5: 飞行请求数（必须 <= 5）
     */
    public void idempotentConfig() throws Exception {
        System.out.println("\n=== 幂等性配置演示 ===");

        KafkaProducer<String, String> producer = createIdempotentProducer();

        // 发送多条相同key的消息（幂等性保证不重复）
        for (int i = 0; i < 5; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>("idempotent-topic", "idempotent-key", "message-" + i);
            RecordMetadata metadata = producer.send(record).get();
            System.out.println("消息发送: partition=" + metadata.partition() + ", offset=" + metadata.offset());
        }

        producer.close();
        System.out.println("幂等消息发送完成");
    }

    /**
     * 重复消息场景演示
     *
     * 【重试场景】
     *   1. 发送成功但响应超时
     *   2. 网络抖动导致重试
     */
    public void duplicateMessage() throws Exception {
        System.out.println("\n=== 重复消息场景演示 ===");

        KafkaProducer<String, String> producer = createIdempotentProducer();

        // 模拟重复发送（网络重试场景）
        String[] messages = {"retry-1", "retry-2", "retry-3"};
        for (String msg : messages) {
            ProducerRecord<String, String> record = new ProducerRecord<>("dedup-topic", "dedup-key", msg);
            RecordMetadata metadata = producer.send(record).get();
            System.out.println("重复消息发送: " + msg + " -> partition=" + metadata.partition() + ", offset=" + metadata.offset());
        }

        producer.close();
    }

    /**
     * 创建幂等生产者
     */
    private KafkaProducer<String, String> createIdempotentProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 幂等性配置
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        props.put(ProducerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new KafkaProducer<>(props);
    }
}
