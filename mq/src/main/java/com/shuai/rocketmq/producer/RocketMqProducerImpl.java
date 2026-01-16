package com.shuai.rocketmq.producer;

import com.shuai.common.interfaces.MqProducer;
import com.shuai.model.Message;
import com.shuai.model.MessageResult;

/**
 * RocketMQ 生产者实现
 *
 * 【配置示例】
 *   RocketMqProducerImpl producer = new RocketMqProducerImpl();
 *   producer.setProducerGroup("producer-group");
 *   producer.setNamesrvAddr("localhost:9876");
 *   producer.start();
 *
 * @author Shuai
 */
public class RocketMqProducerImpl implements MqProducer {

    private String producerGroup;
    private String namesrvAddr;
    private int retryTimesWhenSendFailed = 2;

    @Override
    public MessageResult send(Message message) {
        /*
         * [RocketMQ Producer] 同步发送
         *   Message msg = new Message(
         *       message.getTopic(),
         *       message.getTag(),
         *       message.getBody().getBytes()
         *   );
         *   SendResult result = producer.send(msg);
         *   return MessageResult.success(message.getMessageId(), message.getTopic(),
         *       result.getMessageQueue().getQueueId(), result.getQueueOffset());
         */
        return MessageResult.success(message.getMessageId(), message.getTopic(), 0, 0);
    }

    @Override
    public void sendAsync(Message message, SendCallback callback) {
        /*
         * [RocketMQ Producer] 异步发送
         *   producer.send(msg, new SendCallback() {
         *       public void onSuccess(SendResult result) {
         *           callback.onSuccess(...);
         *       }
         *       public void onException(Exception e) {
         *           callback.onFailure(...);
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
         * [RocketMQ Producer] 单向发送
         *   producer.sendOneway(msg);
         */
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setRetryTimesWhenSendFailed(int retryTimes) {
        this.retryTimesWhenSendFailed = retryTimes;
    }

    @Override
    public void start() {
        /*
         * [RocketMQ Producer] 启动
         *   DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
         *   producer.setNamesrvAddr(namesrvAddr);
         *   producer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
         *   producer.start();
         */
    }

    @Override
    public void shutdown() {
        /*
         * [RocketMQ Producer] 关闭
         *   producer.shutdown();
         */
    }
}
