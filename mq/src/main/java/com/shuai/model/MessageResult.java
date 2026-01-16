package com.shuai.model;

/**
 * 消息发送结果
 *
 * @author Shuai
 */
public class MessageResult {

    private String messageId;
    private String topic;
    private int partition;
    private long offset;
    private boolean success;
    private String errorMessage;

    public MessageResult() {}

    public static MessageResult success(String messageId, String topic, int partition, long offset) {
        MessageResult result = new MessageResult();
        result.messageId = messageId;
        result.topic = topic;
        result.partition = partition;
        result.offset = offset;
        result.success = true;
        return result;
    }

    public static MessageResult fail(String messageId, String errorMessage) {
        MessageResult result = new MessageResult();
        result.messageId = messageId;
        result.success = false;
        result.errorMessage = errorMessage;
        return result;
    }

    // ========== Getters & Setters ==========

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "MessageResult{" +
                "messageId='" + messageId + '\'' +
                ", topic='" + topic + '\'' +
                ", partition=" + partition +
                ", offset=" + offset +
                ", success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
