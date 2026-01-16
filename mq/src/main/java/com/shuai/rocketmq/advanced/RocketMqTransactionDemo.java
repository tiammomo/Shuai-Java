package com.shuai.rocketmq.advanced;

/**
 * RocketMQ 事务消息演示
 *
 * 代码位置: [RocketMqTransactionDemo.java](src/main/java/com/shuai/rocketmq/advanced/RocketMqTransactionDemo.java)
 *
 * @author Shuai
 */
public class RocketMqTransactionDemo {

    /**
     * 事务消息完整流程
     *
     * 【原理】
     *   1. 发送半消息（Half Message），消息暂不可见
     *   2. 执行本地事务
     *   3. 根据本地事务结果，提交或回滚半消息
     *   4. Broker 定时检查未决事务，超时后回滚
     *
     * 【代码示例】
     *   TransactionMQProducer producer = new TransactionMQProducer("tx-group");
     *   producer.setTransactionListener(new TransactionListener() {
     *       @Override
     *       public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
     *           // 本地事务逻辑
     *           // 返回 COMMIT / ROLLBACK / UNKNOWN
     *       }
     *       @Override
     *       public LocalTransactionState checkLocalTransaction(MessageExt msg) {
     *           // 检查未决事务
     *           // 返回 COMMIT / ROLLBACK / UNKNOWN
     *       }
     *   });
     *
     * 【事务状态】
     *   - COMMIT_MESSAGE: 提交消息，消息可被消费
     *   - ROLLBACK_MESSAGE: 回滚消息，消息丢弃
     *   - UNKNOWN: 中间状态，需要定时检查
     *
     * 【使用场景】
     *   - 订单创建 + 库存扣减
     *   - 转账 + 账户变动
     *   - 分布式事务
     */
    public void fullTransactionFlow() {
        MockTransactionProducer producer = new MockTransactionProducer("tx-group");

        producer.sendHalfMessage("order-topic", "order-001");
        producer.executeLocalTransaction("order-001");

        producer.commitMessage("order-001");

        producer.shutdown();
    }

    /**
     * 本地事务执行
     *
     * 【代码示例】
     *   public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
     *       String orderId = (String) arg;
     *       try {
     *           // 创建订单
     *           orderService.createOrder(orderId);
     *           // 扣减库存
     *           inventoryService.deduct(orderId);
     *           return LocalTransactionState.COMMIT_MESSAGE;
     *       } catch (Exception e) {
     *           return LocalTransactionState.ROLLBACK_MESSAGE;
     *       }
     *   }
     */
    public void localTransaction() {
        MockTransactionProducer producer = new MockTransactionProducer("tx-local-group");

        String orderId = "ORD-001";
        producer.executeLocalTransaction(orderId);

        producer.shutdown();
    }

    /**
     * 事务状态回查
     *
     * 【说明】
     *   当本地事务返回 UNKNOWN 或事务超时后，Broker 会回查
     *
     * 【代码示例】
     *   public LocalTransactionState checkLocalTransaction(MessageExt msg) {
     *       String orderId = msg.getUserProperty("orderId");
     *       Order order = orderService.getOrder(orderId);
     *       if (order.isPaid()) {
     *           return LocalTransactionState.COMMIT_MESSAGE;
     *       } else if (order.isCancelled()) {
     *           return LocalTransactionState.ROLLBACK_MESSAGE;
     *       } else {
     *           return LocalTransactionState.UNKNOWN;
     *       }
     *   }
     *
     * 【注意事项】
     *   - 回查次数可配置（默认15次）
     *   - 回查间隔可配置（默认60秒）
     *   - 建议使用幂等处理
     */
    public void transactionCheck() {
        MockTransactionProducer producer = new MockTransactionProducer("tx-check-group");

        producer.checkTransaction("msg-001");

        producer.shutdown();
    }

    // ========== 模拟类 ==========

    static class MockTransactionProducer {
        private String groupId;

        public MockTransactionProducer(String groupId) {
            this.groupId = groupId;
        }

        public void sendHalfMessage(String topic, String orderId) {
            /*
             * [RocketMQ Producer] 发送半消息
             *   Message msg = new Message(topic, orderId.getBytes());
             *   // 可设置事务ID
             *   // msg.putUserProperty("orderId", orderId);
             *   SendResult result = producer.sendMessageInTransaction(msg, orderId);
             */
        }

        public void executeLocalTransaction(String orderId) {
            /*
             * [TransactionListener] 执行本地事务
             *   return LocalTransactionState.COMMIT_MESSAGE;
             *   // 或 return LocalTransactionState.ROLLBACK_MESSAGE;
             *   // 或 return LocalTransactionState.UNKNOWN;
             */
        }

        public void commitMessage(String orderId) {
        }

        public void checkTransaction(String messageId) {
            /*
             * [TransactionListener] 回查事务
             *   return LocalTransactionState.COMMIT_MESSAGE;
             */
        }

        public void shutdown() {
        }
    }
}
