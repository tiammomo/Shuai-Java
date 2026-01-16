package com.shuai.rabbitmq.producer;

import com.shuai.common.interfaces.MqProducer;
import com.shuai.model.Message;
import com.shuai.model.MessageResult;

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

    @Override
    public MessageResult send(Message message) {
        /*
         * [RabbitMQ Producer] 发送消息
         *   channel.basicPublish(
         *       exchange,
         *       message.getTag(),
         *       null,
         *       message.getBody().getBytes()
         *   );
         */
        return MessageResult.success(message.getMessageId(), message.getTopic(), 0, 0);
    }

    @Override
    public void sendAsync(Message message, SendCallback callback) {
        /*
         * [RabbitMQ Producer] 异步发送需要通过 publisher confirms 实现
         *   channel.confirmSelect();
         *   channel.addConfirmListener(...);
         */
        if (callback != null) {
            callback.onSuccess(MessageResult.success(message.getMessageId(), message.getTopic(), 0, 0));
        }
    }

    @Override
    public void sendOneWay(Message message) {
        /*
         * [RabbitMQ Producer] 单向发送
         *   channel.basicPublish(exchange, routingKey, null, body);
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

    @Override
    public void start() {
        /*
         * [RabbitMQ Producer] 启动
         *   ConnectionFactory factory = new ConnectionFactory();
         *   factory.setHost(host);
         *   factory.setPort(port);
         *   factory.setUsername(username);
         *   factory.setPassword(password);
         *   factory.setVirtualHost(virtualHost);
         *   Connection connection = factory.newConnection();
         *   Channel channel = connection.createChannel();
         */
    }

    @Override
    public void shutdown() {
        /*
         * [RabbitMQ Producer] 关闭
         *   channel.close();
         *   connection.close();
         */
    }
}
