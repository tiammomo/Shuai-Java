package com.shuai.model;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;

/**
 * 通用消息模型
 *
 * @author Shuai
 */
public class Message {

    private String topic;
    private String tag;
    private String key;
    private String body;
    private Map<String, String> properties;
    private long timestamp;
    private String messageId;

    public Message() {
        this.properties = new HashMap<>();
        this.timestamp = Instant.now().toEpochMilli();
    }

    public Message(String topic, String body) {
        this();
        this.topic = topic;
        this.body = body;
    }

    public Message(String topic, String tag, String body) {
        this(topic, body);
        this.tag = tag;
    }

    // ========== Builder 模式 ==========

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Message message = new Message();

        public Builder topic(String topic) {
            message.topic = topic;
            return this;
        }

        public Builder tag(String tag) {
            message.tag = tag;
            return this;
        }

        public Builder key(String key) {
            message.key = key;
            return this;
        }

        public Builder body(String body) {
            message.body = body;
            return this;
        }

        public Builder property(String key, String value) {
            message.properties.put(key, value);
            return this;
        }

        public Builder properties(Map<String, String> properties) {
            message.properties.putAll(properties);
            return this;
        }

        public Builder messageId(String messageId) {
            message.messageId = messageId;
            return this;
        }

        public Message build() {
            return message;
        }
    }

    // ========== Getters & Setters ==========

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public String toString() {
        return "Message{" +
                "topic='" + topic + '\'' +
                ", tag='" + tag + '\'' +
                ", key='" + key + '\'' +
                ", body='" + body + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
