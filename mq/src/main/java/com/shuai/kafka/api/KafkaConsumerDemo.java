package com.shuai.kafka.api;

import java.util.Set;
import java.util.HashSet;

/**
 * Kafka 消费者 API 演示
 *
 * 包含内容：
 * - 消费者创建与配置
 * - 订阅主题
 * - 拉取消息
 * - 手动提交
 * - 自动提交
 * - 消费位点
 * - 消费者组
 *
 * 代码位置: [KafkaConsumerDemo.java](src/main/java/com/shuai/kafka/api/KafkaConsumerDemo.java)
 *
 * @author Shuai
 */
public class KafkaConsumerDemo {

    /**
     * 创建消费者
     *
     * 【核心配置】
     *   - bootstrap.servers: Kafka 集群地址
     *   - group.id: 消费者组 ID
     *   - key.deserializer: 键反序列化器
     *   - value.deserializer: 值反序列化器
     *   - auto.offset.reset: 消费位置策略
     *
     * 【代码示例】
     *   Properties props = new Properties();
     *   props.put("bootstrap.servers", "localhost:9092");
     *   props.put("group.id", "my-group");
     *   props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
     *   props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
     *   KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
     */
    public void createConsumer() {
        new MockKafkaConsumer().close();
    }

    /**
     * 订阅主题
     *
     * 【订阅方式】
     *   - subscribe(List): 订阅多个主题
     *   - subscribe(Pattern): 正则匹配主题
     *   - assign(): 手动分配分区
     *
     * 【代码示例 - 订阅单个主题】
     *   consumer.subscribe(Collections.singletonList("topic-test"));
     *
     * 【代码示例 - 订阅多个主题】
     *   consumer.subscribe(Arrays.asList("topic-a", "topic-b"));
     *
     * 【代码示例 - 正则匹配】
     *   consumer.subscribe(Pattern.compile("topic-.*"));
     *
     * 【代码示例 - 手动分配】
     *   consumer.assign(Collections.singletonList(new TopicPartition("topic", 0)));
     */
    public void subscribeTopic() {
        MockKafkaConsumer consumer = new MockKafkaConsumer();

        // 订阅单个主题
        consumer.subscribe("topic-test");

        // 订阅多个主题
        consumer.subscribe("topic-a", "topic-b");

        consumer.close();
    }

    /**
     * 拉取消息
     *
     * 【说明】
     *   poll() 方法拉取消息，返回消息列表
     *
     * 【代码示例】
     *   ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
     *   for (ConsumerRecord<String, String> record : records) {
     *       // 处理消息: record.topic(), record.value()
     *   }
     *
     * 【参数说明】
     *   - timeout: 最大等待时间
     *   - 返回: 消息记录集合
     *
     * 【注意事项】
     *   - poll 是非阻塞的
     *   - 循环调用 poll 保持消费
     *   - 两次 poll 间隔不能太长，否则会被踢出消费者组
     */
    public void pollMessages() {
        MockKafkaConsumer consumer = new MockKafkaConsumer();

        consumer.poll(1000);
        consumer.poll(1000);

        consumer.close();
    }

    /**
     * 手动提交 offset
     *
     * 【提交方式】
     *   - commitSync(): 同步阻塞提交
     *   - commitAsync(): 异步非阻塞提交
     *
     * 【代码示例 - 同步提交】
     *   consumer.commitSync();
     *
     * 【代码示例 - 异步提交】
     *   consumer.commitAsync((offsets, exception) -> {
     *       if (exception != null) {
     *           // 处理异常
     *       }
     *   });
     *
     * 【代码示例 - 按消息提交】
     *   Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
     *   offsets.put(record.partition(), new OffsetAndMetadata(record.offset() + 1));
     *   consumer.commitSync(offsets);
     *
     * 【使用场景】
     *   - 确保消息被正确处理后再提交
     *   - 精确控制消费位置
     *   - 支持消息重试
     */
    public void manualCommit() {
        MockKafkaConsumer consumer = new MockKafkaConsumer();

        // 同步提交
        consumer.commitSync();

        // 异步提交
        consumer.commitAsync();

        consumer.close();
    }

    /**
     * 自动提交 offset
     *
     * 【配置】
     *   - enable.auto.commit: 开启自动提交
     *   - auto.commit.interval.ms: 提交间隔
     *
     * 【代码示例】
     *   props.put("enable.auto.commit", true);
     *   props.put("auto.commit.interval.ms", 1000);
     *
     * 【说明】
     *   - 自动提交可能丢失消息
     *   - 提交间隔内处理的消息如果崩溃会重复消费
     *   - 适合对重复不敏感的场景
     *
     * 【使用场景】
     *   - 日志收集
     *   - 统计指标
     *   - 允许少量重复的消息
     */
    public void autoCommit() {
        MockKafkaConsumer consumer = new MockKafkaConsumer();

        consumer.poll(1000);

        consumer.close();
    }

    /**
     * 消费位点管理
     *
     * 【seek 方法】
     *   - 指定分区消费起始位置
     *   - 用于回溯消费或跳过消息
     *
     * 【代码示例 - 指定 offset】
     *   consumer.seek(new TopicPartition("topic", 0), 100L);
     *
     * 【代码示例 - 指定时间戳】
     *   Map<TopicPartition, Long> timestamps = new HashMap<>();
     *   timestamps.put(new TopicPartition("topic", 0), System.currentTimeMillis() - 3600000);
     *   consumer.offsetsForTimes(timestamps);
     *
     * 【代码示例 - 消费最新消息】
     *   consumer.seekToEnd(Collections.singletonList(partition));
     *
     * 【代码示例 - 消费最早消息】
     *   consumer.seekToBeginning(Collections.singletonList(partition));
     *
     * 【使用场景】
     *   - 数据回溯：重新消费历史数据
     *   - 故障恢复：从指定位置继续消费
     *   - 数据跳过：跳过部分消息
     */
    public void seekOffset() {
        MockKafkaConsumer consumer = new MockKafkaConsumer();

        // 指定 offset
        consumer.seek("topic", 0, 100L);

        // 消费最新
        consumer.seekToEnd("topic", 0);

        // 消费最早
        consumer.seekToBeginning("topic", 0);

        consumer.close();
    }

    /**
     * 消费者组演示
     *
     * 【分区分配策略】
     *   - RangeAssignor: 范围分配（默认）
     *   - RoundRobinAssignor: 轮询分配
     *   - StickyAssignor: 粘性分配
     *   - CooperativeStickyAssignor: 协作式粘性分配
     *
     * 【代码示例 - 设置分配策略】
     *   props.put("partition.assignment.strategy", "org.apache.kafka.clients.consumer.RoundRobinAssignor");
     *
     * 【消费者组特点】
     *   - 同一消费者组内，一条消息只被一个消费者消费
     *   - 不同消费者组可以独立消费同一消息
     *   - 消费者数量变化会触发再平衡
     *
     * 【使用场景】
     *   - 水平扩展消费者
     *   - 消息负载均衡
     *   - 高可用消费
     */
    public void consumerGroup() {
        MockKafkaConsumer consumer = new MockKafkaConsumer();

        // 消费者组分配
        consumer.subscribe("topic-group");

        consumer.close();
    }

    // ========== 模拟类 ==========

    /**
     * 模拟 Kafka 消费者
     *
     * 【功能说明】
     *   模拟消息消费，不连接真实 Kafka 集群
     */
    static class MockKafkaConsumer {

        public void subscribe(String... topics) {
            Set<String> topicSet = new HashSet<>();
            for (String topic : topics) {
                topicSet.add(topic);
            }
            /*
             * [Kafka Consumer] 订阅主题
             *   consumer.subscribe(topicSet);
             *   // 或使用正则
             *   consumer.subscribe(Pattern.compile("topic-.*"));
             */
        }

        public void poll(long timeoutMs) {
            /*
             * [Kafka Consumer] 拉取消息
             *   ConsumerRecords<String, String> records =
             *       consumer.poll(Duration.ofMillis(timeoutMs));
             *   for (ConsumerRecord<String, String> record : records) {
             *       // 处理消息: record.topic(), record.value()
             *   }
             */
        }

        public void commitSync() {
            /*
             * [Kafka Consumer] 同步提交
             *   consumer.commitSync();
             */
        }

        public void commitAsync() {
            /*
             * [Kafka Consumer] 异步提交
             *   consumer.commitAsync((offsets, exception) -> {
             *       // 处理结果
             *   });
             */
        }

        public void seek(String topic, int partition, long offset) {
            /*
             * [Kafka Consumer] 指定消费位置
             *   consumer.seek(new TopicPartition(topic, partition), offset);
             */
        }

        public void seekToEnd(String topic, int partition) {
            /*
             * [Kafka Consumer] 消费最新消息
             *   consumer.seekToEnd(Collections.singletonList(new TopicPartition(topic, partition)));
             */
        }

        public void seekToBeginning(String topic, int partition) {
            /*
             * [Kafka Consumer] 消费最早消息
             *   consumer.seekToBeginning(Collections.singletonList(new TopicPartition(topic, partition)));
             */
        }

        public void close() {
            /*
             * [Kafka Consumer] 关闭消费者
             *   consumer.close();
             */
        }
    }
}
