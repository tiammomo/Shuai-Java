package com.shuai.common;

/**
 * MQ 模块常量定义
 *
 * @author Shuai
 */
public final class MqConstants {

    private MqConstants() {}

    // ========== Kafka 常量 ==========

    public static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String KAFKA_GROUP_ID = "mq-demo-group";
    public static final String KAFKA_TOPIC_PREFIX = "kafka-";
    public static final int KAFKA_PARTITIONS = 3;
    public static final short KAFKA_REPLICATION_FACTOR = 1;

    // ========== RocketMQ 常量 ==========

    public static final String ROCKETMQ_NAMESRV_ADDR = "localhost:9876";
    public static final String ROCKETMQ_GROUP_PREFIX = "mq-demo-";
    public static final String ROCKETMQ_TOPIC_PREFIX = "rmq-";
    public static final int ROCKETMQ_QUEUE_COUNT = 4;

    // ========== RabbitMQ 常量 ==========

    public static final String RABBITMQ_HOST = "localhost";
    public static final int RABBITMQ_PORT = 5672;
    public static final String RABBITMQ_USERNAME = "guest";
    public static final String RABBITMQ_PASSWORD = "guest";
    public static final String RABBITMQ_VHOST = "/";
    public static final String RABBITMQ_QUEUE_PREFIX = "mq-";

    // ========== 消息属性 ==========

    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_TEXT = "text/plain";
    public static final int DELIVERY_MODE_PERSISTENT = 2;
    public static final int DELIVERY_MODE_NON_PERSISTENT = 1;

    // ========== 重试配置 ==========

    public static final int MAX_RETRY_COUNT = 3;
    public static final long RETRY_INTERVAL_MS = 1000L;
}
