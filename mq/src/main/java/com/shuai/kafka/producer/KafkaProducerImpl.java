package com.shuai.kafka.producer;

import com.shuai.common.interfaces.MqProducer;
import com.shuai.model.Message;
import com.shuai.model.MessageResult;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
    private int batchSize = 16384;
    private int lingerMs = 1;
    private KafkaProducer<String, String> producer;

    @Override
    public MessageResult send(Message message) {
        if (producer == null) {
            return MessageResult.fail(message.getMessageId(), "Producer not started");
        }

        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(
                message.getTopic(),
                message.getKey(),
                message.getBody()
            );
            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get();
            return MessageResult.success(
                message.getMessageId(),
                message.getTopic(),
                metadata.partition(),
                metadata.offset()
            );
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return MessageResult.fail(message.getMessageId(), e.getMessage());
        }
    }

    @Override
    public void sendAsync(Message message, SendCallback callback) {
        if (producer == null) {
            if (callback != null) {
                callback.onFailure(MessageResult.fail(message.getMessageId(), "Producer not started"));
            }
            return;
        }

        ProducerRecord<String, String> record = new ProducerRecord<>(
            message.getTopic(),
            message.getKey(),
            message.getBody()
        );

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                if (callback != null) {
                    callback.onFailure(MessageResult.fail(message.getMessageId(), exception.getMessage()));
                }
            } else {
                if (callback != null) {
                    callback.onSuccess(MessageResult.success(
                        message.getMessageId(),
                        message.getTopic(),
                        metadata.partition(),
                        metadata.offset()
                    ));
                }
            }
        });
    }

    @Override
    public void sendOneWay(Message message) {
        if (producer != null) {
            ProducerRecord<String, String> record = new ProducerRecord<>(
                message.getTopic(),
                message.getKey(),
                message.getBody()
            );
            producer.send(record);
        }
    }

    public void setBootstrapServers(String servers) {
        this.bootstrapServers = servers;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setLingerMs(int lingerMs) {
        this.lingerMs = lingerMs;
    }

    @Override
    public void start() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true".equals(acks));

        this.producer = new KafkaProducer<>(props);
    }

    @Override
    public void shutdown() {
        if (producer != null) {
            producer.close();
            producer = null;
        }
    }
}
