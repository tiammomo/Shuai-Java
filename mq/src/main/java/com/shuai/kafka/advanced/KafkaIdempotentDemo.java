package com.shuai.kafka.advanced;

/**
 * Kafka 幂等与精确一次演示
 *
 * 包含内容：
 * - 幂等性配置
 * - 精确一次语义
 * - 重复消息处理
 *
 * 代码位置: [KafkaIdempotentDemo.java](src/main/java/com/shuai/kafka/advanced/KafkaIdempotentDemo.java)
 *
 * @author Shuai
 */
public class KafkaIdempotentDemo {

    /**
     * 幂等性配置
     *
     * 【配置参数】
     *   - enable.idempotence=true: 开启幂等性
     *   - acks=all: 所有副本确认
     *   - max.in.flight.requests.per.connection=5: 飞行请求数（必须 <= 5）
     *
     * 【代码示例】
     *   Properties props = new Properties();
     *   props.put("enable.idempotence", true);
     *   props.put("acks", "all");
     *   props.put("max.in.flight.requests.per.connection", 5);
     *   props.put("retries", Integer.MAX_VALUE);
     *   props.put("enable.auto.commit", false);
     *
     * 【幂等性原理】
     *   - PID (Producer ID): 生产者唯一标识
     *   - Sequence Number: 消息序列号
     *   - Broker 端去重
     *
     * 【作用范围】
     *   - 同一生产者 + 同一分区的消息去重
     *   - 不支持跨分区幂等
     */
    public void idempotentConfig() {
        MockKafkaProducer producer = new MockKafkaProducer();

        producer.send("topic", "key", "message1");
        producer.send("topic", "key", "message2");

        producer.close();
    }

    /**
     * 精确一次语义
     *
     * 【说明】
     *   精确一次 (Exactly-Once) = 幂等性 + 事务
     *
     * 【配置组合】
     *   生产者: enable.idempotence=true + acks=all
     *   消费者: isolation.level=read_committed
     *
     * 【代码示例】
     *   // 生产者配置
     *   props.put("enable.idempotence", true);
     *   props.put("acks", "all");
     *
     *   // 消费者配置
     *   props.put("isolation.level", "read_committed");
     *
     * 【语义保证】
     *   - 生产者: 消息不丢失、不重复
     *   - 消费者: 事务内的消息只消费一次
     *
     * 【性能影响】
     *   - 吞吐量降低
     *   - 延迟增加
     * - 建议仅关键业务使用
     */
    public void exactlyOnce() {
        MockKafkaProducer producer = new MockKafkaProducer();

        // 开启幂等性
        producer.send("topic", "key", "exactly-once-message");

        producer.close();
    }

    /**
     * 重复消息场景
     *
     * 【重试场景】
     *   1. 发送成功但响应超时
     *   2. 网络抖动导致重试
     *
     * 【代码示例 - 幂等发送】
     *   producer.send(record, (metadata, exception) -> {
     *       // 幂等性保证重复发送不重复
     *   });
     *
     * 【消息去重策略】
     *   - 业务唯一键
     *   - 消息 ID
     *   - 去重表
     *
     * 【使用场景】
     *   - 订单创建
     *   - 支付处理
     *   - 账户扣款
     */
    public void duplicateMessage() {
        MockKafkaProducer producer = new MockKafkaProducer();

        // 模拟重复发送
        for (int i = 0; i < 3; i++) {
            producer.send("topic", "dedup-key", "dedup-message");
        }

        producer.close();
    }

    // ========== 模拟类 ==========

    static class MockKafkaProducer {

        public void send(String topic, String key, String value) {
            /*
             * [Kafka Producer] 幂等发送
             *   ProducerRecord<String, String> record =
             *       new ProducerRecord<>(topic, key, value);
             *   producer.send(record);
             */
        }

        public void close() {
        }
    }
}
