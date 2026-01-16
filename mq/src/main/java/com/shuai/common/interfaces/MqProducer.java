package com.shuai.common.interfaces;

import com.shuai.model.Message;
import com.shuai.model.MessageResult;

/**
 * 消息生产者接口
 *
 * @author Shuai
 */
public interface MqProducer {

    /**
     * 同步发送消息
     *
     * @param message 消息对象
     * @return 发送结果
     */
    MessageResult send(Message message);

    /**
     * 异步发送消息
     *
     * @param message 消息对象
     * @param callback 回调函数
     */
    void sendAsync(Message message, SendCallback callback);

    /**
     * 发送单向消息（不等待结果）
     *
     * @param message 消息对象
     */
    void sendOneWay(Message message);

    /**
     * 启动生产者
     */
    void start();

    /**
     * 关闭生产者
     */
    void shutdown();

    /**
     * 发送回调接口
     */
    interface SendCallback {
        void onSuccess(MessageResult result);
        void onFailure(MessageResult result);
    }
}
