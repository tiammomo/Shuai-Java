package com.shuai.rocketmq.config;

/**
 * RocketMQ 生产者配置
 *
 * 代码位置: [RocketMqProducerConfig.java](src/main/java/com/shuai/rocketmq/config/RocketMqProducerConfig.java)
 *
 * @author Shuai
 */
public class RocketMqProducerConfig {

    /**
     * 创建可靠生产者配置
     *
     * 【代码示例】
     *   RocketMqProducerConfig config = new RocketMqProducerConfig();
     *   DefaultMQProducer producer = config.createReliableProducer("producer-group");
     *
     * @param producerGroup 生产者组名
     * @return DefaultMQProducer 实例
     */
    public DefaultMQProducer createReliableProducer(String producerGroup) {
        /*
         * [RocketMQ Producer] 可靠配置
         *   DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
         *   producer.setNamesrvAddr("localhost:9876");
         *   producer.setRetryTimesWhenSendFailed(3);
         *   producer.setRetryTimesWhenSendAsyncFailed(3);
         *   producer.setDefaultTopicQueueNums(4);
         *   producer.start();
         */
        return null;
    }

    /**
     * 创建事务生产者配置
     *
     * 【代码示例】
     *   RocketMqProducerConfig config = new RocketMqProducerConfig();
     *   TransactionMQProducer producer = config.createTransactionProducer("tx-group");
     *
     * @param producerGroup 生产者组名
     * @return TransactionMQProducer 实例
     */
    public TransactionMQProducer createTransactionProducer(String producerGroup) {
        /*
         * [RocketMQ Producer] 事务配置
         *   TransactionMQProducer producer = new TransactionMQProducer(producerGroup);
         *   producer.setNamesrvAddr("localhost:9876");
         *   producer.setTransactionListener(transactionListener);
         *   producer.setRetryTimesWhenSendAsyncFailed(3);
         *   producer.start();
         */
        return null;
    }

    /**
     * 消费者配置
     *
     * 【代码示例】
     *   RocketMqProducerConfig config = new RocketMqProducerConfig();
     *   DefaultMQPushConsumer consumer = config.createPushConsumer("consumer-group");
     *
     * @param consumerGroup 消费者组名
     * @return DefaultMQPushConsumer 实例
     */
    public DefaultMQPushConsumer createPushConsumer(String consumerGroup) {
        /*
         * [RocketMQ Consumer] Push 配置
         *   DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
         *   consumer.setNamesrvAddr("localhost:9876");
         *   consumer.setConsumeMessageBatchMaxSize(16);
         *   consumer.setConsumeThreadMin(10);
         *   consumer.setConsumeThreadMax(20);
         *   consumer.setMessageModel(MessageModel.CLUSTERING);
         *   consumer.subscribe("TopicTest", "*");
         *   consumer.registerMessageListener(listener);
         *   consumer.start();
         */
        return null;
    }

    // ========== 模拟类 ==========

    static class DefaultMQProducer {
        public void setNamesrvAddr(String addr) {}
        public void setRetryTimesWhenSendFailed(int times) {}
        public void setRetryTimesWhenSendAsyncFailed(int times) {}
        public void setDefaultTopicQueueNums(int nums) {}
        public void start() {}
        public void shutdown() {}
    }

    static class TransactionMQProducer extends DefaultMQProducer {
        public void setTransactionListener(TransactionListener listener) {}
    }

    static class DefaultMQPushConsumer {
        public void setNamesrvAddr(String addr) {}
        public void setConsumeMessageBatchMaxSize(int size) {}
        public void setConsumeThreadMin(int min) {}
        public void setConsumeThreadMax(int max) {}
        public void setMessageModel(Object model) {}
        public void subscribe(String topic, String subExpression) {}
        public void registerMessageListener(Object listener) {}
        public void start() {}
        public void shutdown() {}
    }

    interface TransactionListener {
    }
}
