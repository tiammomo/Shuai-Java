package com.shuai.rocketmq.consumer;

import com.shuai.common.interfaces.MqConsumer;
import com.shuai.model.Message;

/**
 * RocketMQ 消费者实现
 *
 * 【配置示例】
 *   RocketMqConsumerImpl consumer = new RocketMqConsumerImpl();
 *   consumer.setNamesrvAddr("localhost:9876");
 *   consumer.setConsumerGroup("test-group");
 *   consumer.subscribe("test-topic", "*");
 *   consumer.start();
 *
 * @author Shuai
 */
public class RocketMqConsumerImpl implements MqConsumer {

    private String namesrvAddr;
    private String consumerGroup;
    private String messageModel = "CLUSTERING";
    private String subscribedTopic;
    private String subscribedTags = "*";

    @Override
    public void subscribe(String topic) {
        this.subscribedTopic = topic;
        this.subscribedTags = "*";
        /*
         * [RocketMQ Consumer] 订阅主题
         *   consumer.subscribe(topic, "*");
         */
    }

    @Override
    public void subscribe(String topic, String tags) {
        this.subscribedTopic = topic;
        this.subscribedTags = tags;
        /*
         * [RocketMQ Consumer] 订阅主题（带标签）
         *   consumer.subscribe(topic, tags);
         */
    }

    @Override
    public Message poll(long timeoutMs) {
        /*
         * [RocketMQ Consumer] 拉取消息
         *   List<MessageExt> msgs = consumer.poll(timeoutMs);
         *   if (!msgs.isEmpty()) {
         *       MessageExt msg = msgs.get(0);
         *       return Message.builder()
         *           .topic(msg.getTopic())
         *           .tag(msg.getTags())
         *           .body(new String(msg.getBody()))
         *           .build();
         *   }
         */
        return null;
    }

    @Override
    public Message[] pollBatch(long timeoutMs, int maxCount) {
        /*
         * [RocketMQ Consumer] 批量拉取
         *   List<MessageExt> msgs = consumer.poll(timeoutMs);
         *   return msgs.stream()
         *       .map(m -> Message.builder()
         *           .topic(m.getTopic())
         *           .tag(m.getTags())
         *           .body(new String(m.getBody()))
         *           .build())
         *       .toArray(Message[]::new);
         */
        return new Message[0];
    }

    @Override
    public void commit(Message message) {
        /*
         * [RocketMQ Consumer] 提交消息（Push 模式自动提交）
         */
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public void setMessageModel(String messageModel) {
        this.messageModel = messageModel;
    }

    @Override
    public void start() {
        /*
         * [RocketMQ Consumer] 启动
         *   DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
         *   consumer.setNamesrvAddr(namesrvAddr);
         *   consumer.setMessageModel(MessageModel.valueOf(messageModel));
         *   consumer.subscribe(subscribedTopic, subscribedTags);
         *   consumer.registerMessageListener(...);
         *   consumer.start();
         */
    }

    @Override
    public void shutdown() {
        /*
         * [RocketMQ Consumer] 关闭
         *   consumer.shutdown();
         */
    }
}
