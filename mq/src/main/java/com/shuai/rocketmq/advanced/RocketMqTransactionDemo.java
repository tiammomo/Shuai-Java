package com.shuai.rocketmq.advanced;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * RocketMQ 事务消息演示
 *
 * 【运行方式】
 *   1. 确保 Docker RocketMQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class RocketMqTransactionDemo {

    private static final String NAMESRV_ADDR = "localhost:9876";

    public static void main(String[] args) {
        RocketMqTransactionDemo demo = new RocketMqTransactionDemo();
        try {
            demo.fullTransactionFlow();
            System.out.println("RocketMQ 事务消息演示完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 事务消息完整流程
     *
     * 【原理】
     *   1. 发送半消息（Half Message），消息暂不可见
     *   2. 执行本地事务
     *   3. 根据本地事务结果，提交或回滚半消息
     *   4. Broker 定时检查未决事务，超时后回滚
     */
    public void fullTransactionFlow() throws Exception {
        System.out.println("\n=== RocketMQ 事务消息完整流程 ===");

        // 创建事务生产者
        TransactionMQProducer producer = new TransactionMQProducer("tx-transaction-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.setExecutorService(Executors.newFixedThreadPool(4));

        // 设置事务监听器
        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                String orderId = new String(msg.getBody());
                System.out.println("[本地事务] 执行订单: " + orderId);

                // 模拟本地事务处理
                try {
                    // 创建订单、扣减库存等操作
                    // 成功返回 COMMIT_MESSAGE
                    return LocalTransactionState.COMMIT_MESSAGE;
                } catch (Exception e) {
                    // 失败返回 ROLLBACK_MESSAGE
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                String orderId = new String(msg.getBody());
                System.out.println("[事务回查] 检查订单: " + orderId);

                // 查询订单状态，决定提交还是回滚
                // 假设订单已支付，返回 COMMIT_MESSAGE
                // 假设订单已取消，返回 ROLLBACK_MESSAGE
                // 未知状态返回 UNKNOWN（Broker 会定时回查）
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });

        producer.start();
        System.out.println("事务生产者启动");

        // 发送事务消息
        String[] orderIds = {"ORD-001", "ORD-002", "ORD-003"};
        for (String orderId : orderIds) {
            Message msg = new Message("TransactionTopic", orderId.getBytes(StandardCharsets.UTF_8));
            msg.setTransactionId("tx-" + orderId);

            try {
                org.apache.rocketmq.client.producer.SendResult result =
                    producer.sendMessageInTransaction(msg, orderId);
                System.out.println("事务消息发送: orderId=" + orderId +
                    ", status=" + result.getSendStatus());
            } catch (Exception e) {
                System.out.println("事务消息发送失败: " + orderId + ", error=" + e.getMessage());
            }
        }

        // 等待事务处理完成
        Thread.sleep(5000);

        producer.shutdown();
        System.out.println("事务生产者已关闭");
    }
}
