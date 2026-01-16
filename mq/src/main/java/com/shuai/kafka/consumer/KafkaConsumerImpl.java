package com.shuai.kafka.consumer;

import com.shuai.common.interfaces.MqConsumer;
import com.shuai.model.Message;
import java.util.HashSet;
import java.util.Set;

/**
 * Kafka 消费者实现
 *
 * 【配置示例】
 *   KafkaConsumerImpl consumer = new KafkaConsumerImpl();
 *   consumer.setBootstrapServers("localhost:9092");
 *   consumer.setGroupId("test-group");
 *   consumer.subscribe("test-topic");
 *   consumer.start();
 *
 * @author Shuai
 */
public class KafkaConsumerImpl implements MqConsumer {

    private String bootstrapServers;
    private String groupId;
    private String autoOffsetReset = "earliest";
    private boolean enableAutoCommit = true;
    private Set<String> subscribedTopics = new HashSet<>();

    @Override
    public void subscribe(String topic) {
        this.subscribedTopics.add(topic);
        /*
         * [Kafka Consumer] 订阅主题
         *   consumer.subscribe(Collections.singletonList(topic));
         */
    }

    @Override
    public void subscribe(String topic, String tags) {
        this.subscribedTopics.add(topic);
        /*
         * [Kafka Consumer] 订阅主题（带标签过滤仅 RocketMQ 支持）
         *   Kafka 不支持按标签过滤，需要在消费时过滤
         */
    }

    @Override
    public Message poll(long timeoutMs) {
        /*
         * [Kafka Consumer] 拉取消息
         *   ConsumerRecords<String, String> records =
         *       consumer.poll(Duration.ofMillis(timeoutMs));
         *   if (!records.isEmpty()) {
         *       ConsumerRecord<String, String> record = records.iterator().next();
         *       return Message.builder()
         *           .topic(record.topic())
         *           .key(record.key())
         *           .body(record.value())
         *           .build();
         *   }
         */
        return null;
    }

    @Override
    public Message[] pollBatch(long timeoutMs, int maxCount) {
        /*
         * [Kafka Consumer] 批量拉取消息
         *   ConsumerRecords<String, String> records =
         *       consumer.poll(Duration.ofMillis(timeoutMs));
         *   return records.stream()
         *       .map(r -> Message.builder()
         *           .topic(r.topic())
         *           .key(r.key())
         *           .body(r.value())
         *           .build())
         *       .toArray(Message[]::new);
         */
        return new Message[0];
    }

    @Override
    public void commit(Message message) {
        /*
         * [Kafka Consumer] 提交 offset
         *   consumer.commitSync();
         */
    }

    public void setBootstrapServers(String servers) {
        this.bootstrapServers = servers;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setAutoOffsetReset(String autoOffsetReset) {
        this.autoOffsetReset = autoOffsetReset;
    }

    public void setEnableAutoCommit(boolean enableAutoCommit) {
        this.enableAutoCommit = enableAutoCommit;
    }

    @Override
    public void start() {
        /*
         * [Kafka Consumer] 启动
         *   Properties props = new Properties();
         *   props.put("bootstrap.servers", bootstrapServers);
         *   props.put("group.id", groupId);
         *   props.put("auto.offset.reset", autoOffsetReset);
         *   props.put("enable.auto.commit", enableAutoCommit);
         *   props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
         *   props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
         *   KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
         */
    }

    @Override
    public void shutdown() {
        /*
         * [Kafka Consumer] 关闭
         *   consumer.close();
         */
    }
}
