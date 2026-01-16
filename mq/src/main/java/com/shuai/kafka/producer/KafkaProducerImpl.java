package com.shuai.kafka.producer;

import com.shuai.common.interfaces.MqProducer;
import com.shuai.model.Message;
import com.shuai.model.MessageResult;

import java.util.Properties;

/**
 * Kafka 生产者实现
 *
 * 【配置示例】
 *   KafkaProducerImpl producer = new KafkaProducerImpl();
 *   producer.setBootstrapServers("localhost:9092");
 *   producer.setAcks("all");
 *   producer.start();
 *
 * @author Shuai
 */
public class KafkaProducerImpl implements MqProducer {

    private String bootstrapServers;
    private String acks = "all";
    private int retries = 3;
    private Properties properties;

    public KafkaProducerImpl() {
        this.properties = new Properties();
    }

    public void setBootstrapServers(String servers) {
        this.bootstrapServers = servers;
        this.properties.put("bootstrap.servers", servers);
    }

    public void setAcks(String acks) {
        this.acks = acks;
        this.properties.put("acks", acks);
    }

    public void setRetries(int retries) {
        this.retries = retries;
        this.properties.put("retries", retries);
    }

    @Override
    public MessageResult send(Message message) {
        /*
         * [Kafka Producer] 同步发送
         *   ProducerRecord<String, String> record = new ProducerRecord<>(
         *       message.getTopic(),
         *       message.getKey(),
         *       message.getBody()
         *   );
         *   RecordMetadata metadata = producer.send(record).get();
         *   return MessageResult.success(message.getMessageId(), message.getTopic(),
         *       metadata.partition(), metadata.offset());
         */
        return MessageResult.success(message.getMessageId(), message.getTopic(), 0, 0);
    }

    @Override
    public void sendAsync(Message message, SendCallback callback) {
        /*
         * [Kafka Producer] 异步发送
         *   producer.send(record, (metadata, exception) -> {
         *       if (exception != null) {
         *           callback.onFailure(new MessageResult(message.getMessageId(), exception.getMessage()));
         *       } else {
         *           callback.onSuccess(MessageResult.success(...));
         *       }
         *   });
         */
        if (callback != null) {
            callback.onSuccess(MessageResult.success(message.getMessageId(), message.getTopic(), 0, 0));
        }
    }

    @Override
    public void sendOneWay(Message message) {
        /*
         * [Kafka Producer] 单向发送
         *   producer.send(record);
         */
    }

    @Override
    public void start() {
        /*
         * [Kafka Producer] 启动
         *   properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
         *   properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
         *   KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
         */
    }

    @Override
    public void shutdown() {
        /*
         * [Kafka Producer] 关闭
         *   producer.close();
         */
    }
}
