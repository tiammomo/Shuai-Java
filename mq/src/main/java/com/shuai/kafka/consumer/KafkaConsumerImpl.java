package com.shuai.kafka.consumer;

import com.shuai.common.interfaces.MqConsumer;
import com.shuai.model.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

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
    private int maxPollRecords = 100;
    private Set<String> subscribedTopics = new HashSet<>();
    private volatile boolean started = false;
    private KafkaConsumer<String, String> consumer;

    @Override
    public void subscribe(String topic) {
        this.subscribedTopics.add(topic);
        if (consumer != null) {
            consumer.subscribe(Arrays.asList(topic));
        }
    }

    @Override
    public void subscribe(String topic, String tags) {
        // Kafka 不支持按标签过滤，通过消费时过滤实现
        this.subscribedTopics.add(topic);
        if (consumer != null) {
            consumer.subscribe(Arrays.asList(topic));
        }
    }

    @Override
    public Message poll(long timeoutMs) {
        if (consumer == null || !started) {
            return null;
        }

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(timeoutMs));
        if (!records.isEmpty()) {
            org.apache.kafka.clients.consumer.ConsumerRecord<String, String> record =
                records.iterator().next();
            return Message.builder()
                .topic(record.topic())
                .key(record.key())
                .body(record.value())
                .build();
        }
        return null;
    }

    @Override
    public Message[] pollBatch(long timeoutMs, int maxCount) {
        if (consumer == null || !started) {
            return new Message[0];
        }

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(timeoutMs));
        List<Message> messages = new ArrayList<>();
        for (org.apache.kafka.clients.consumer.ConsumerRecord<String, String> r : records) {
            if (messages.size() >= maxCount) break;
            messages.add(Message.builder()
                .topic(r.topic())
                .key(r.key())
                .body(r.value())
                .build());
        }
        return messages.toArray(new Message[0]);
    }

    @Override
    public void commit(Message message) {
        if (consumer != null && enableAutoCommit) {
            consumer.commitSync();
        }
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

    public void setMaxPollRecords(int maxPollRecords) {
        this.maxPollRecords = maxPollRecords;
    }

    public boolean isStarted() {
        return started;
    }

    public String getGroupId() {
        return groupId;
    }

    public void commitSync() {
        if (consumer != null) {
            consumer.commitSync();
        }
    }

    @Override
    public void start() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        this.consumer = new KafkaConsumer<>(props);
        if (!subscribedTopics.isEmpty()) {
            consumer.subscribe(new ArrayList<>(subscribedTopics));
        }
        this.started = true;
    }

    @Override
    public void shutdown() {
        if (consumer != null) {
            consumer.close();
            consumer = null;
        }
        this.started = false;
    }
}
