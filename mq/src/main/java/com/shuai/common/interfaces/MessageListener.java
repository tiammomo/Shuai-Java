package com.shuai.common.interfaces;

/**
 * 消息监听器接口
 *
 * @author Shuai
 */
public interface MessageListener {

    /**
     * 消费消息
     *
     * @param message 消息对象
     * @return 处理结果
     */
    ConsumeResult onMessage(com.shuai.model.Message message);

    /**
     * 消费结果枚举
     */
    enum ConsumeResult {
        /** 消费成功 */
        SUCCESS,
        /** 消费失败，稍后重试 */
        RETRY,
        /** 消费失败，进入死信队列 */
        FAILURE
    }
}
