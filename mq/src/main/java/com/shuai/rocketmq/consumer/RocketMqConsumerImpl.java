package com.shuai.rocketmq.consumer;

import com.shuai.common.interfaces.MqConsumer;
import com.shuai.model.Message;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.ArrayList;
import java.util.List;

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
    private int consumeMessageBatchMaxSize = 1;
    private int pullThresholdForQueue = 1000;
    private long pullInterval = 0;
    private DefaultMQPushConsumer consumer;
    private volatile boolean started = false;
    private final List<Message> messageBuffer = new ArrayList<>();
    private Object bufferLock = new Object();

    @Override
    public void subscribe(String topic) {
        this.subscribedTopic = topic;
        this.subscribedTags = "*";
        if (consumer != null) {
            try {
                consumer.subscribe(topic, "*");
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void subscribe(String topic, String tags) {
        this.subscribedTopic = topic;
        this.subscribedTags = tags;
        if (consumer != null) {
            try {
                consumer.subscribe(topic, tags);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public Message poll(long timeoutMs) {
        if (consumer == null || !started) {
            return null;
        }

        synchronized (bufferLock) {
            if (!messageBuffer.isEmpty()) {
                return messageBuffer.remove(0);
            }
        }

        // RocketMQ Push 模式不支持主动 poll，使用 MessageListener 异步接收
        // 这里返回 null，实际使用时应该注册 MessageListener
        return null;
    }

    @Override
    public Message[] pollBatch(long timeoutMs, int maxCount) {
        if (consumer == null || !started) {
            return new Message[0];
        }

        synchronized (bufferLock) {
            if (messageBuffer.isEmpty()) {
                return new Message[0];
            }
            int size = Math.min(maxCount, messageBuffer.size());
            List<Message> result = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                result.add(messageBuffer.remove(0));
            }
            return result.toArray(new Message[0]);
        }
    }

    @Override
    public void commit(Message message) {
        // RocketMQ Push 模式自动提交，不需要手动 commit
    }

    /**
     * 注册并发消息监听器
     */
    public void registerMessageListener(MessageListenerConcurrently listener) {
        if (consumer != null) {
            consumer.registerMessageListener(listener);
        }
    }

    /**
     * 注册顺序消息监听器
     */
    public void registerMessageListener(MessageListenerOrderly listener) {
        if (consumer != null) {
            consumer.registerMessageListener(listener);
        }
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

    public void setConsumeMessageBatchMaxSize(int batchSize) {
        this.consumeMessageBatchMaxSize = batchSize;
    }

    public void setPullThresholdForQueue(int threshold) {
        this.pullThresholdForQueue = threshold;
    }

    public void setPullInterval(long interval) {
        this.pullInterval = interval;
    }

    @Override
    public void start() {
        if (subscribedTopic == null) {
            throw new RuntimeException("Topic not subscribed");
        }

        try {
            this.consumer = new DefaultMQPushConsumer(consumerGroup);
            consumer.setNamesrvAddr(namesrvAddr);
            consumer.setMessageModel(MessageModel.valueOf(messageModel));
            consumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueAveragely());
            consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
            consumer.setPullThresholdForQueue(pullThresholdForQueue);
            consumer.setPullInterval(pullInterval);

            // 注册默认的消息监听器
            consumer.subscribe(subscribedTopic, subscribedTags);

            this.started = true;
            consumer.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start RocketMQ consumer: " + e.getMessage(), e);
        }
    }

    @Override
    public void shutdown() {
        if (consumer != null) {
            consumer.shutdown();
            consumer = null;
        }
        this.started = false;
    }
}
