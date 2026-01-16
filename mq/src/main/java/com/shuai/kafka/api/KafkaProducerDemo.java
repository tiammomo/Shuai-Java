package com.shuai.kafka.api;

/**
 * Kafka 生产者 API 演示
 *
 * 包含内容：
 * - 生产者创建与配置
 * - 同步发送
 * - 异步发送
 * - 分区策略
 * - 批量发送
 * - 可靠发送
 *
 * 代码位置: [KafkaProducerDemo.java](src/main/java/com/shuai/kafka/api/KafkaProducerDemo.java)
 *
 * @author Shuai
 */
public class KafkaProducerDemo {

    /**
     * 创建生产者
     *
     * 【核心配置】
     *   - bootstrap.servers: Kafka 集群地址，多个用逗号分隔
     *   - key.serializer: 键序列化器
     *   - value.serializer: 值序列化器
     *   - acks: 确认机制（0, 1, all）
     *   - retries: 重试次数
     *
     * 【可靠性配置】
     *   - acks=all: 所有副本确认
     *   - enable.idempotence: 幂等性保证
     *   - max.in.flight.requests.per.connection: 飞行请求数
     *
     * 【代码示例】
     *   Properties props = new Properties();
     *   props.put("bootstrap.servers", MqConstants.KAFKA_BOOTSTRAP_SERVERS);
     *   props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
     *   props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
     *   props.put("acks", "all");
     *   props.put("retries", 3);
     *   KafkaProducer<String, String> producer = new KafkaProducer<>(props);
     */
    public void createProducer() {
        new MockKafkaProducer().close();
    }

    /**
     * 同步发送消息
     *
     * 【说明】
     *   发送后等待结果返回，可靠性高，但会阻塞
     *
     * 【代码示例】
     *   ProducerRecord<String, String> record =
     *       new ProducerRecord<>("topic", "key", "value");
     *   RecordMetadata metadata = producer.send(record).get();
     *   // metadata.partition() - 分区号
     *   // metadata.offset() - 偏移量
     *
     * 【使用场景】
     *   - 重要消息通知
     *   - 订单创建
     *   - 支付确认
     */
    public void syncSend() {
        MockKafkaProducer producer = new MockKafkaProducer();
        String topic = "test-topic";

        for (int i = 0; i < 5; i++) {
            producer.sendSync(topic, "key-" + i, "同步消息 " + i);
        }

        producer.close();
    }

    /**
     * 异步发送消息
     *
     * 【说明】
     *   发送后立即返回，通过回调处理结果
     *
     * 【代码示例】
     *   producer.send(record, new Callback() {
     *       public void onCompletion(RecordMetadata metadata, Exception e) {
     *           if (e != null) {
     *               // 处理异常
     *           } else {
     *               // 处理成功
     *           }
     *       }
     *   });
     *
     * 【使用场景】
     *   - 对响应时间敏感
     *   - 批量发送
     */
    public void asyncSend() {
        MockKafkaProducer producer = new MockKafkaProducer();
        String topic = "async-topic";

        for (int i = 0; i < 5; i++) {
            producer.sendAsync(topic, "key-" + i, "异步消息 " + i, null);
        }

        producer.close();
    }

    /**
     * 分区策略演示
     *
     * 【默认策略】
     *   - 如果指定了 key，使用 key 的哈希值选择分区
     *   - 如果没有指定 key，使用轮询策略
     *
     * 【代码示例 - 指定分区】
     *   new ProducerRecord<>("topic", 0, "key", "value")  // 指定 partition=0
     *
     * 【代码示例 - 自定义分区器】
     *   props.put("partitioner.class", "com.example.MyPartitioner");
     *
     * 【常见分区器】
     *   - DefaultPartitioner: 基于 key 哈希
     *   - RoundRobinPartitioner: 轮询
     */
    public void partitionStrategy() {
        MockKafkaProducer producer = new MockKafkaProducer();
        String topic = "partition-topic";

        // 按 Key 哈希分区: 相同 key 发送到相同分区
        String[] keys = {"userId-1001", "userId-1002", "userId-1003"};
        for (String key : keys) {
            producer.send(topic, key, "消息");
        }

        // 轮询分区（不指定 key）: 自动分配到不同分区
        for (int i = 0; i < 3; i++) {
            producer.send(topic, null, "轮询消息 " + i);
        }

        producer.close();
    }

    /**
     * 批量发送演示
     *
     * 【配置参数】
     *   - batch.size: 批量大小（字节），默认 16KB
     *   - linger.ms: 等待时间，默认 0ms
     *
     * 【代码示例】
     *   props.put("batch.size", 32768);  // 32KB 批量
     *   props.put("linger.ms", 5);        // 等待 5ms
     *   props.put("compression.type", "lz4");  // 压缩
     *
     * 【说明】
     *   批量发送可以提高吞吐量，但会增加延迟
     */
    public void batchSend() {
        MockKafkaProducer producer = new MockKafkaProducer();
        String topic = "batch-topic";

        int batchSize = 100;
        for (int i = 0; i < batchSize; i++) {
            producer.send(topic, "batch-key-" + i, "批量消息 " + i);
        }

        producer.close();
    }

    /**
     * 可靠发送（幂等发送）
     *
     * 【配置】
     *   - enable.idempotence=true: 开启幂等性
     *   - acks=all: 所有副本确认
     *   - max.in.flight.requests.per.connection=5: 飞行请求数
     *
     * 【代码示例】
     *   props.put("enable.idempotence", true);
     *   props.put("acks", "all");
     *   props.put("max.in.flight.requests.per.connection", 5);
     *
     * 【说明】
     *   幂等发送可以防止消息重复，确保 Exactly-Once 语义
     */
    public void reliableSend() {
        MockKafkaProducer producer = new MockKafkaProducer();
        String topic = "reliable-topic";

        for (int i = 0; i < 3; i++) {
            producer.send(topic, "reliable-key-" + i, "可靠消息 " + i);
        }

        producer.close();
    }

    // ========== 模拟类 ==========

    /**
     * 模拟 Kafka 生产者
     *
     * 【功能说明】
     *   模拟消息发送，不连接真实 Kafka 集群
     */
    static class MockKafkaProducer {

        /**
         * 同步发送
         */
        public void sendSync(String topic, String key, String value) {
            /*
             * [Kafka Producer] 同步发送
             *   ProducerRecord<String, String> record =
             *       new ProducerRecord<>(topic, key, value);
             *   RecordMetadata metadata = producer.send(record).get();
             */
        }

        /**
         * 异步发送
         */
        public void sendAsync(String topic, String key, String value, java.util.function.Consumer<Boolean> callback) {
            /*
             * [Kafka Producer] 异步发送
             *   producer.send(record, (metadata, exception) -> {
             *       // 处理结果
             *   });
             */
        }

        /**
         * 发送消息
         */
        public void send(String topic, String key, String value) {
            /*
             * [Kafka Producer] 发送消息
             *   ProducerRecord<String, String> record =
             *       new ProducerRecord<>(topic, key, value);
             *   producer.send(record);
             */
        }

        public void close() {
            /*
             * [Kafka Producer] 关闭连接
             *   producer.flush();
             *   producer.close(Duration.ofSeconds(30));
             */
        }
    }
}
