package com.shuai.kafka.config;

import java.util.Properties;

/**
 * Kafka 生产者配置
 *
 * 代码位置: [KafkaProducerConfig.java](src/main/java/com/shuai/kafka/config/KafkaProducerConfig.java)
 *
 * @author Shuai
 */
public class KafkaProducerConfig {

    /**
     * 创建可靠生产者配置
     *
     * 【代码示例】
     *   KafkaProducerConfig config = new KafkaProducerConfig();
     *   Properties props = config.createReliableProperties();
     *   KafkaProducer<String, String> producer = new KafkaProducer<>(props);
     *
     * @return 生产者配置
     */
    public Properties createReliableProperties() {
        Properties props = new Properties();

        // 基础配置
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 可靠性配置
        props.put("acks", "all");
        props.put("retries", Integer.MAX_VALUE);
        props.put("enable.idempotence", true);
        props.put("max.in.flight.requests.per.connection", 5);

        // 性能优化
        props.put("batch.size", 32768);
        props.put("linger.ms", 5);
        props.put("buffer.memory", 33554432);
        props.put("compression.type", "lz4");

        return props;
    }

    /**
     * 创建高性能生产者配置
     *
     * @return 生产者配置
     */
    public Properties createHighPerformanceProperties() {
        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 高吞吐配置
        props.put("acks", "1");
        props.put("retries", 0);
        props.put("batch.size", 65536);
        props.put("linger.ms", 10);
        props.put("buffer.memory", 67108864);
        props.put("compression.type", "lz4");

        return props;
    }
}
