package com.shuai.rabbitmq.consumer;

import com.shuai.common.interfaces.MqConsumer;
import com.shuai.model.Message;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

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
    private Connection connection;
    private Channel channel;
    private volatile boolean started = false;

    @Override
    public void subscribe(String topic) {
        this.queue = topic;
    }

    @Override
    public void subscribe(String topic, String tags) {
        this.queue = topic;
    }

    @Override
    public Message poll(long timeoutMs) {
        if (channel == null || !started) {
            return null;
        }

        try {
            GetResponse response = channel.basicGet(queue, autoAck);
            if (response != null) {
                return convertGetResponse(response);
            }
        } catch (IOException e) {
            // 忽略错误
        }

        return null;
    }

    @Override
    public Message[] pollBatch(long timeoutMs, int maxCount) {
        if (channel == null || !started) {
            return new Message[0];
        }

        List<Message> messages = new ArrayList<>();
        try {
            while (messages.size() < maxCount) {
                GetResponse response = channel.basicGet(queue, autoAck);
                if (response == null) {
                    break;
                }
                messages.add(convertGetResponse(response));
            }
        } catch (IOException e) {
            // 忽略错误
        }

        return messages.toArray(new Message[0]);
    }

    @Override
    public void commit(Message message) {
        // RabbitMQ 通过 ack 确认消息
    }

    private Message convertGetResponse(GetResponse response) {
        return Message.builder()
            .topic(queue)
            .body(new String(response.getBody(), StandardCharsets.UTF_8))
            .build();
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
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setUsername(username);
            factory.setPassword(password);
            factory.setVirtualHost(virtualHost);

            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
            channel.basicQos(1);
            this.started = true;
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Failed to start RabbitMQ consumer: " + e.getMessage(), e);
        }
    }

    @Override
    public void shutdown() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
                channel = null;
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
                connection = null;
            }
        } catch (IOException | TimeoutException ignored) {
        }
        this.started = false;
    }
}
