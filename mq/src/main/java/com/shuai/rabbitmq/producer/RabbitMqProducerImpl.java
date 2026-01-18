package com.shuai.rabbitmq.producer;

import com.shuai.common.interfaces.MqProducer;
import com.shuai.model.Message;
import com.shuai.model.MessageResult;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ 生产者实现
 *
 * 【配置示例】
 *   RabbitMqProducerImpl producer = new RabbitMqProducerImpl();
 *   producer.setHost("localhost");
 *   producer.setPort(5672);
 *   producer.start();
 *
 * @author Shuai
 */
public class RabbitMqProducerImpl implements MqProducer {

    private String host = "localhost";
    private int port = 5672;
    private String username = "guest";
    private String password = "guest";
    private String virtualHost = "/";
    private String exchange = "";
    private boolean mandatory = false;
    private Connection connection;
    private Channel channel;

    @Override
    public MessageResult send(Message message) {
        if (channel == null) {
            return MessageResult.fail(message.getMessageId(), "Channel not initialized");
        }

        try {
            String routingKey = message.getTag() != null ? message.getTag() : message.getTopic();
            channel.basicPublish(
                exchange,
                routingKey,
                mandatory,
                null,
                message.getBody().getBytes(StandardCharsets.UTF_8)
            );
            return MessageResult.success(message.getMessageId(), message.getTopic(), 0, 0);
        } catch (IOException e) {
            return MessageResult.fail(message.getMessageId(), e.getMessage());
        }
    }

    @Override
    public void sendAsync(Message message, SendCallback callback) {
        if (channel == null) {
            if (callback != null) {
                callback.onFailure(MessageResult.fail(message.getMessageId(), "Channel not initialized"));
            }
            return;
        }

        try {
            String routingKey = message.getTag() != null ? message.getTag() : message.getTopic();
            channel.basicPublish(
                exchange,
                routingKey,
                mandatory,
                null,
                message.getBody().getBytes(StandardCharsets.UTF_8)
            );
            if (callback != null) {
                callback.onSuccess(MessageResult.success(message.getMessageId(), message.getTopic(), 0, 0));
            }
        } catch (IOException e) {
            if (callback != null) {
                callback.onFailure(MessageResult.fail(message.getMessageId(), e.getMessage()));
            }
        }
    }

    @Override
    public void sendOneWay(Message message) {
        if (channel != null) {
            try {
                String routingKey = message.getTag() != null ? message.getTag() : message.getTopic();
                channel.basicPublish(
                    exchange,
                    routingKey,
                    mandatory,
                    null,
                    message.getBody().getBytes(StandardCharsets.UTF_8)
                );
            } catch (IOException ignored) {
            }
        }
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

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
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
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Failed to start RabbitMQ producer: " + e.getMessage(), e);
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
    }
}
