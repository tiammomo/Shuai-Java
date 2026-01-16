package com.shuai.rabbitmq.consumer;

import com.shuai.common.interfaces.MqConsumer;
import com.shuai.model.Message;

/**
 * RabbitMQ 消费者实现
 *
 * 【配置示例】
 *   RabbitMqConsumerImpl consumer = new RabbitMqConsumerImpl();
 *   consumer.setHost("localhost");
 *   consumer.setQueue("test-queue");
 *   consumer.start();
 *
 * @author Shuai
 */
public class RabbitMqConsumerImpl implements MqConsumer {

    private String host = "localhost";
    private int port = 5672;
    private String username = "guest";
    private String password = "guest";
    private String virtualHost = "/";
    private String queue;
    private boolean autoAck = false;

    @Override
    public void subscribe(String topic) {
        this.queue = topic;
        /*
         * [RabbitMQ Consumer] 订阅队列
         *   channel.basicConsume(queue, autoAck, deliverCallback, cancelCallback);
         */
    }

    @Override
    public void subscribe(String topic, String tags) {
        // RabbitMQ 使用 queue 不是 topic，tags 通过 binding 实现
        this.queue = topic;
        /*
         * [RabbitMQ Consumer] 标签过滤通过 Binding 实现
         *   channel.queueBind(queue, exchange, tags);
         */
    }

    @Override
    public Message poll(long timeoutMs) {
        /*
         * [RabbitMQ Consumer] 拉取消息
         *   GetResponse response = channel.basicGet(queue, autoAck);
         *   if (response != null) {
         *       return Message.builder()
         *           .topic(queue)
         *           .body(new String(response.getBody()))
         *           .build();
         *   }
         */
        return null;
    }

    @Override
    public Message[] pollBatch(long timeoutMs, int maxCount) {
        /*
         * [RabbitMQ Consumer] 批量拉取
         *   List<Message> messages = new ArrayList<>();
         *   for (int i = 0; i < maxCount; i++) {
         *       GetResponse response = channel.basicGet(queue, autoAck);
         *       if (response == null) break;
         *       messages.add(Message.builder()
         *           .topic(queue)
         *           .body(new String(response.getBody()))
         *           .build());
         *   }
         *   return messages.toArray(new Message[0]);
         */
        return new Message[0];
    }

    @Override
    public void commit(Message message) {
        /*
         * [RabbitMQ Consumer] 确认消息
         *   channel.basicAck(deliveryTag, false);
         */
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setAutoAck(boolean autoAck) {
        this.autoAck = autoAck;
    }

    @Override
    public void start() {
        /*
         * [RabbitMQ Consumer] 启动
         *   ConnectionFactory factory = new ConnectionFactory();
         *   factory.setHost(host);
         *   factory.setPort(port);
         *   factory.setUsername(username);
         *   factory.setPassword(password);
         *   Connection connection = factory.newConnection();
         *   Channel channel = connection.createChannel();
         *   channel.basicConsume(queue, autoAck, deliverCallback, cancelCallback);
         */
    }

    @Override
    public void shutdown() {
        /*
         * [RabbitMQ Consumer] 关闭
         *   channel.close();
         *   connection.close();
         */
    }
}
