package com.shuai;

import com.shuai.kafka.api.KafkaProducerDemo;
import com.shuai.kafka.api.KafkaConsumerDemo;
import com.shuai.kafka.advanced.KafkaTransactionDemo;
import com.shuai.kafka.advanced.KafkaIdempotentDemo;
import com.shuai.rocketmq.api.RocketMqProducerDemo;
import com.shuai.rocketmq.api.RocketMqConsumerDemo;
import com.shuai.rocketmq.advanced.RocketMqTransactionDemo;
import com.shuai.rocketmq.advanced.RocketMqDelayDemo;
import com.shuai.rabbitmq.api.RabbitMqProducerDemo;
import com.shuai.rabbitmq.api.RabbitMqConsumerDemo;
import com.shuai.rabbitmq.advanced.RabbitMqDelayDemo;
import com.shuai.rabbitmq.advanced.RabbitMqPriorityDemo;
import com.shuai.rabbitmq.advanced.RabbitMqDLXDemo;
import com.shuai.rabbitmq.exchange.DirectExchangeDemo;
import com.shuai.rabbitmq.exchange.FanoutExchangeDemo;
import com.shuai.rabbitmq.exchange.TopicExchangeDemo;
import com.shuai.rabbitmq.exchange.HeadersExchangeDemo;

/**
 * 消息队列模块入口类
 *
 * 运行所有 MQ 演示：
 *   mvn exec:java -Dexec.mainClass=com.shuai.MqDemo
 *
 * 包结构：
 *   - com.shuai.kafka.api: Kafka 生产者/消费者 API
 *   - com.shuai.kafka.advanced: Kafka 高级特性
 *   - com.shuai.rocketmq.api: RocketMQ 生产者/消费者 API
 *   - com.shuai.rocketmq.advanced: RocketMQ 高级特性
 *   - com.shuai.rabbitmq.api: RabbitMQ 生产者/消费者 API
 *   - com.shuai.rabbitmq.advanced: RabbitMQ 高级特性
 *   - com.shuai.rabbitmq.exchange: RabbitMQ Exchange 类型
 *
 * @author Shuai
 */
public class MqDemo {

    public static void main(String[] args) {
        new MqDemo().runAllDemos();
    }

    public void runAllDemos() {
        // 概述
        new MqOverviewDemo().runAllDemos();

        // Kafka 演示
        runKafkaDemos();

        // RocketMQ 演示
        runRocketMqDemos();

        // RabbitMQ 演示
        runRabbitMqDemos();

        // MQ 对比
        new MqComparisonDemo().runAllDemos();
    }

    private void runKafkaDemos() {
        KafkaProducerDemo producer = new KafkaProducerDemo();
        KafkaConsumerDemo consumer = new KafkaConsumerDemo();
        KafkaTransactionDemo transaction = new KafkaTransactionDemo();
        KafkaIdempotentDemo idempotent = new KafkaIdempotentDemo();

        producer.createProducer();
        producer.syncSend();
        producer.asyncSend();
        producer.partitionStrategy();
        producer.batchSend();
        producer.reliableSend();

        consumer.createConsumer();
        consumer.subscribeTopic();
        consumer.pollMessages();
        consumer.manualCommit();
        consumer.seekOffset();
        consumer.consumerGroup();

        transaction.basicTransaction();
        transaction.rollbackTransaction();
        transaction.consumeProduceTransaction();

        idempotent.idempotentConfig();
        idempotent.exactlyOnce();
        idempotent.duplicateMessage();
    }

    private void runRocketMqDemos() {
        RocketMqProducerDemo producer = new RocketMqProducerDemo();
        RocketMqConsumerDemo consumer = new RocketMqConsumerDemo();
        RocketMqTransactionDemo txDemo = new RocketMqTransactionDemo();
        RocketMqDelayDemo delayDemo = new RocketMqDelayDemo();

        producer.createProducer();
        producer.syncSend();
        producer.asyncSend();
        producer.onewaySend();
        producer.delayMessage();
        producer.orderedMessage();
        producer.transactionMessage();
        producer.messageTag();

        consumer.createPushConsumer();
        consumer.createPullConsumer();
        consumer.messageFilter();
        consumer.orderedConsume();
        consumer.concurrentConsume();
        consumer.consumeMode();

        txDemo.fullTransactionFlow();
        txDemo.localTransaction();
        txDemo.transactionCheck();

        delayDemo.basicDelayMessage();
        delayDemo.orderTimeoutCancel();
        delayDemo.retryWithDelay();
    }

    private void runRabbitMqDemos() {
        RabbitMqProducerDemo producer = new RabbitMqProducerDemo();
        RabbitMqConsumerDemo consumer = new RabbitMqConsumerDemo();
        RabbitMqDelayDemo delayDemo = new RabbitMqDelayDemo();
        RabbitMqPriorityDemo priorityDemo = new RabbitMqPriorityDemo();
        RabbitMqDLXDemo dlxDemo = new RabbitMqDLXDemo();
        DirectExchangeDemo directDemo = new DirectExchangeDemo();
        FanoutExchangeDemo fanoutDemo = new FanoutExchangeDemo();
        TopicExchangeDemo topicDemo = new TopicExchangeDemo();
        HeadersExchangeDemo headersDemo = new HeadersExchangeDemo();

        producer.createConnection();
        producer.channelOperation();
        producer.declareExchange();
        producer.declareQueue();
        producer.createBinding();
        producer.sendMessage();
        producer.publishConfirm();
        producer.transactionMessage();

        consumer.createConsumer();
        consumer.pushConsume();
        consumer.pullConsume();
        consumer.messageAck();
        consumer.qosSettings();
        consumer.rejectMessage();
        consumer.consumerTag();

        delayDemo.delayWithTTL();
        delayDemo.delayPlugin();
        delayDemo.orderTimeoutCancel();

        priorityDemo.createPriorityQueue();
        priorityDemo.sendPriorityMessage();

        dlxDemo.configureDLQ();
        dlxDemo.rejectToDLQ();
        dlxDemo.messageTTLToDLQ();
        dlxDemo.maxLengthToDLQ();

        directDemo.demo();
        fanoutDemo.demo();
        topicDemo.demo();
        headersDemo.demo();
    }
}
