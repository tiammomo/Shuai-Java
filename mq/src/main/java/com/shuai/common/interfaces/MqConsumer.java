package com.shuai.common.interfaces;

import com.shuai.model.Message;

/**
 * 消息消费者接口
 *
 * @author Shuai
 */
public interface MqConsumer {

    /**
     * 订阅主题
     *
     * @param topic 主题名称
     */
    void subscribe(String topic);

    /**
     * 订阅主题（带标签过滤）
     *
     * @param topic 主题名称
     * @param tags 标签表达式，如 "tagA || tagB"
     */
    void subscribe(String topic, String tags);

    /**
     * 拉取消息
     *
     * @param timeoutMs 超时时间（毫秒）
     * @return 消息对象，null 表示无消息
     */
    Message poll(long timeoutMs);

    /**
     * 批量拉取消息
     *
     * @param timeoutMs 超时时间（毫秒）
     * @param maxCount 最大消息数量
     * @return 消息数组
     */
    Message[] pollBatch(long timeoutMs, int maxCount);

    /**
     * 提交消费位点
     *
     * @param message 消息对象
     */
    void commit(Message message);

    /**
     * 启动消费者
     */
    void start();

    /**
     * 关闭消费者
     */
    void shutdown();
}
