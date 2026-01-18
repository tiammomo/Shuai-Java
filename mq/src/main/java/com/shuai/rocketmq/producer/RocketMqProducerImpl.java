package com.shuai.rocketmq.producer;

import com.shuai.common.interfaces.MqProducer;
import com.shuai.model.Message;
import com.shuai.model.MessageResult;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

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
    private int retryTimesWhenAsyncFailed = 2;
    private int sendMsgTimeout = 3000;
    private int maxMessageSize = 1024 * 1024;
    private DefaultMQProducer producer;

    @Override
    public MessageResult send(Message message) {
        if (producer == null) {
            return MessageResult.fail(message.getMessageId(), "Producer not started");
        }

        try {
            org.apache.rocketmq.common.message.Message msg = new org.apache.rocketmq.common.message.Message(
                message.getTopic(),
                message.getTag(),
                message.getBody().getBytes()
            );
            SendResult result = producer.send(msg);
            return MessageResult.success(
                message.getMessageId(),
                message.getTopic(),
                result.getMessageQueue().getQueueId(),
                result.getQueueOffset()
            );
        } catch (Exception e) {
            return MessageResult.fail(message.getMessageId(), e.getMessage());
        }
    }

    @Override
    public void sendAsync(Message message, MqProducer.SendCallback callback) {
        if (producer == null) {
            if (callback != null) {
                callback.onFailure(MessageResult.fail(message.getMessageId(), "Producer not started"));
            }
            return;
        }

        try {
            org.apache.rocketmq.common.message.Message msg = new org.apache.rocketmq.common.message.Message(
                message.getTopic(),
                message.getTag(),
                message.getBody().getBytes()
            );

            producer.send(msg, new org.apache.rocketmq.client.producer.SendCallback() {
                @Override
                public void onSuccess(org.apache.rocketmq.client.producer.SendResult sendResult) {
                    if (callback != null) {
                        callback.onSuccess(MessageResult.success(
                            message.getMessageId(),
                            message.getTopic(),
                            sendResult.getMessageQueue().getQueueId(),
                            sendResult.getQueueOffset()
                        ));
                    }
                }

                @Override
                public void onException(Throwable e) {
                    if (callback != null) {
                        callback.onFailure(MessageResult.fail(message.getMessageId(), e.getMessage()));
                    }
                }
            });
        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure(MessageResult.fail(message.getMessageId(), e.getMessage()));
            }
        }
    }

    @Override
    public void sendOneWay(Message message) {
        if (producer != null) {
            try {
                org.apache.rocketmq.common.message.Message msg = new org.apache.rocketmq.common.message.Message(
                    message.getTopic(),
                    message.getTag(),
                    message.getBody().getBytes()
                );
                producer.sendOneway(msg);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 发送顺序消息
     */
    public MessageResult sendOrderly(Message message, String hashKey) {
        if (producer == null) {
            return MessageResult.fail(message.getMessageId(), "Producer not started");
        }

        try {
            org.apache.rocketmq.common.message.Message msg = new org.apache.rocketmq.common.message.Message(
                message.getTopic(),
                message.getTag(),
                message.getBody().getBytes()
            );
            SendResult result = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, org.apache.rocketmq.common.message.Message msg, Object arg) {
                    int queueId = Math.abs(hashKey.hashCode()) % mqs.size();
                    return mqs.get(queueId);
                }
            }, hashKey);
            return MessageResult.success(
                message.getMessageId(),
                message.getTopic(),
                result.getMessageQueue().getQueueId(),
                result.getQueueOffset()
            );
        } catch (Exception e) {
            return MessageResult.fail(message.getMessageId(), e.getMessage());
        }
    }

    /**
     * 发送延迟消息
     * @param delayLevel 延迟级别: 1=1s, 2=5s, 3=10s, 4=30s, 5=1m, 6=2m, 7=3m, 8=4m, 9=5m, 10=6m, 11=7m, 12=8m, 13=9m, 14=10m, 15=20m, 16=30m, 17=1h, 18=2h
     */
    public MessageResult sendDelayed(Message message, int delayLevel) {
        if (producer == null) {
            return MessageResult.fail(message.getMessageId(), "Producer not started");
        }

        try {
            org.apache.rocketmq.common.message.Message msg = new org.apache.rocketmq.common.message.Message(
                message.getTopic(),
                message.getTag(),
                message.getBody().getBytes()
            );
            msg.setDelayTimeLevel(delayLevel);
            SendResult result = producer.send(msg);
            return MessageResult.success(
                message.getMessageId(),
                message.getTopic(),
                result.getMessageQueue().getQueueId(),
                result.getQueueOffset()
            );
        } catch (Exception e) {
            return MessageResult.fail(message.getMessageId(), e.getMessage());
        }
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

    public void setRetryTimesWhenAsyncFailed(int retryTimes) {
        this.retryTimesWhenAsyncFailed = retryTimes;
    }

    public void setSendMsgTimeout(int timeout) {
        this.sendMsgTimeout = timeout;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    @Override
    public void start() {
        this.producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(namesrvAddr);
        producer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
        producer.setRetryTimesWhenSendAsyncFailed(retryTimesWhenAsyncFailed);
        producer.setSendMsgTimeout(sendMsgTimeout);
        producer.setMaxMessageSize(maxMessageSize);
        try {
            producer.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start RocketMQ producer: " + e.getMessage(), e);
        }
    }

    @Override
    public void shutdown() {
        if (producer != null) {
            producer.shutdown();
            producer = null;
        }
    }
}
