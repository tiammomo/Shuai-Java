package com.shuai.kafka.advanced;

/**
 * Kafka 事务消息演示
 *
 * 包含内容：
 * - 事务初始化
 * - 开始事务
 * - 提交事务
 * - 回滚事务
 * - 消费事务
 *
 * 代码位置: [KafkaTransactionDemo.java](src/main/java/com/shuai/kafka/advanced/KafkaTransactionDemo.java)
 *
 * @author Shuai
 */
public class KafkaTransactionDemo {

    /**
     * 事务消息基本流程
     *
     * 【事务 API】
     *   - initTransactions(): 初始化事务
     *   - beginTransaction(): 开始事务
     *   - commitTransaction(): 提交事务
     *   - abortTransaction(): 回滚事务
     *
     * 【代码示例】
     *   producer.initTransactions();
     *   producer.beginTransaction();
     *   try {
     *       producer.send(record1);
     *       producer.send(record2);
     *       producer.commitTransaction();
     *   } catch (Exception e) {
     *       producer.abortTransaction();
     *   }
     *
     * 【前置条件】
     *   - 必须开启幂等性 (enable.idempotence=true)
     *   - 必须设置 acks=all
     *   - transactionId 必须唯一
     *
     * 【使用场景】
     *   - 多消息原子性发送
     *   - 消费-生产事务
     *   - 跨分区事务
     */
    public void basicTransaction() {
        MockTransactionProducer producer = new MockTransactionProducer();

        producer.initTransactions();
        producer.beginTransaction();

        producer.send("topic", "key1", "message1");
        producer.send("topic", "key2", "message2");

        producer.commitTransaction();

        producer.close();
    }

    /**
     * 回滚事务
     *
     * 【代码示例】
     *   producer.beginTransaction();
     *   try {
     *       producer.send(record1);
     *       throw new RuntimeException("error");
     *   } catch (Exception e) {
     *       producer.abortTransaction();
     *   }
     */
    public void rollbackTransaction() {
        MockTransactionProducer producer = new MockTransactionProducer();

        producer.initTransactions();
        producer.beginTransaction();

        producer.send("topic", "key1", "message1");

        producer.abortTransaction();

        producer.close();
    }

    /**
     * 消费-生产事务
     *
     * 【说明】
     *   消费者消费消息，生产者发送消息，要么都成功，要么都失败
     *
     * 【代码示例】
     *   producer.initTransactions();
     *   consumer.subscribe(Collections.singletonList("input-topic"));
     *
     *   while (true) {
     *       ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
     *       if (records.isEmpty()) continue;
     *
     *       producer.beginTransaction();
     *       for (ConsumerRecord<String, String> record : records) {
     *           ProducerRecord<String, String> output = transform(record);
     *           producer.send(output);
     *       }
     *       producer.sendOffsetsToTransaction(getOffsets(records), consumer.groupMetadata());
     *       producer.commitTransaction();
     *   }
     *
     * 【使用场景】
     *   - ETL 数据转换
     *   - 消息路由
     *   - 数据同步
     */
    public void consumeProduceTransaction() {
        MockTransactionProducer producer = new MockTransactionProducer();

        producer.initTransactions();
        producer.beginTransaction();

        producer.send("output-topic", "key", "transformed-message");
        producer.sendOffsetsToTransaction();

        producer.commitTransaction();

        producer.close();
    }

    // ========== 模拟类 ==========

    static class MockTransactionProducer {

        public void initTransactions() {
            /*
             * [Kafka Producer] 初始化事务
             *   producer.initTransactions();
             */
        }

        public void beginTransaction() {
            /*
             * [Kafka Producer] 开始事务
             *   producer.beginTransaction();
             */
        }

        public void commitTransaction() {
            /*
             * [Kafka Producer] 提交事务
             *   producer.commitTransaction();
             */
        }

        public void abortTransaction() {
            /*
             * [Kafka Producer] 回滚事务
             *   producer.abortTransaction();
             */
        }

        public void send(String topic, String key, String value) {
            /*
             * [Kafka Producer] 发送消息
             *   ProducerRecord<String, String> record =
             *       new ProducerRecord<>(topic, key, value);
             *   producer.send(record);
             */
        }

        public void sendOffsetsToTransaction() {
            /*
             * [Kafka Producer] 提交消费位点
             *   Map<TopicPartition, OffsetAndMetadata> offsets = ...;
             *   producer.sendOffsetsToTransaction(offsets, consumerGroupMetadata);
             */
        }

        public void close() {
        }
    }
}
