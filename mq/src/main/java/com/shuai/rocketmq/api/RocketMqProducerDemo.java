package com.shuai.rocketmq.api;

/**
 * RocketMQ 生产者 API 演示
 *
 * 包含内容：
 * - 生产者创建与配置
 * - 同步发送
 * - 异步发送
 * - 单向发送
 * - 延迟消息
 * - 顺序消息
 * - 事务消息
 * - 消息标签（Tag）
 *
 * 代码位置: [RocketMqProducerDemo.java](src/main/java/com/shuai/rocketmq/api/RocketMqProducerDemo.java)
 *
 * @author Shuai
 */
public class RocketMqProducerDemo {

    /**
     * 创建生产者
     *
     * 【核心配置】
     *   - producerGroup: 生产者组名
     *   - namesrvAddr: NameServer 地址
     *   - retryTimesWhenSendFailed: 发送失败重试次数
     *   - retryTimesWhenSendAsyncFailed: 异步发送失败重试次数
     *
     * 【代码示例】
     *   DefaultMQProducer producer = new DefaultMQProducer("producer-group");
     *   producer.setNamesrvAddr("localhost:9876");
     *   producer.setRetryTimesWhenSendFailed(2);
     *   producer.setRetryTimesWhenSendAsyncFailed(2);
     *   producer.start();
     */
    public void createProducer() {
        MockRocketMqProducer producer = new MockRocketMqProducer("producer-group");
        producer.start();
        producer.shutdown();
    }

    /**
     * 同步发送
     *
     * 【说明】
     *   发送后等待结果返回，可靠性高
     *
     * 【代码示例】
     *   Message msg = new Message("TopicTest", "TagA", ("Hello").getBytes());
     *   SendResult result = producer.send(msg);
     *   // result.getSendStatus() - 发送状态
     *   // result.getMsgId() - 消息ID
     *   // result.getOffsetMsgId() - 偏移量ID
     *
     * 【使用场景】
     *   - 重要消息通知
     *   - 订单创建
     *   - 支付确认
     */
    public void syncSend() {
        MockRocketMqProducer producer = new MockRocketMqProducer("sync-producer");
        producer.start();
        String topic = "sync-topic";

        for (int i = 0; i < 5; i++) {
            producer.sendSync(topic, "sync-tag", "同步消息 " + i);
        }

        producer.shutdown();
    }

    /**
     * 异步发送
     *
     * 【说明】
     *   发送后立即返回，通过回调处理结果
     *
     * 【代码示例】
     *   producer.send(msg, new SendCallback() {
     *       @Override
     *       public void onSuccess(SendResult sendResult) {
     *           // 发送成功
     *       }
     *       @Override
     *       public void onException(Exception e) {
     *           // 发送失败
     *       }
     *   });
     *
     * 【使用场景】
     *   - 对响应时间敏感
     *   - 批量发送
     */
    public void asyncSend() {
        MockRocketMqProducer producer = new MockRocketMqProducer("async-producer");
        producer.start();
        String topic = "async-topic";

        for (int i = 0; i < 5; i++) {
            producer.sendAsync(topic, "async-tag", "异步消息 " + i);
        }

        producer.shutdown();
    }

    /**
     * 单向发送
     *
     * 【说明】
     *   只管发送，不等待结果，适用于日志等场景
     *
     * 【代码示例】
     *   producer.sendOneway(msg);
     *
     * 【使用场景】
     *   - 日志收集
     *   - 统计数据上报
     *   - 容忍少量丢失
     */
    public void onewaySend() {
        MockRocketMqProducer producer = new MockRocketMqProducer("oneway-producer");
        producer.start();
        String topic = "oneway-topic";

        for (int i = 0; i < 5; i++) {
            producer.sendOneway(topic, "oneway-tag", "单向消息 " + i);
        }

        producer.shutdown();
    }

    /**
     * 延迟消息
     *
     * 【延迟级别】
     *   1: 1秒  2: 5秒  3: 10秒  4: 30秒  5: 1分钟
     *   6: 2分钟 7: 3分钟 ... 18: 2小时
     *
     * 【代码示例】
     *   Message msg = new Message("DelayTopic", body);
     *   msg.setDelayTimeLevel(3);  // 10秒后投递
     *   producer.send(msg);
     *
     * 【使用场景】
     *   - 订单超时取消
     *   - 延迟任务
     *   - 重试机制
     */
    public void delayMessage() {
        MockRocketMqProducer producer = new MockRocketMqProducer("delay-producer");
        producer.start();
        String topic = "delay-topic";

        int[] delayLevels = {1, 2, 3, 4, 5};
        for (int i = 0; i < delayLevels.length; i++) {
            producer.sendDelay(topic, "delay-tag", "延迟消息 " + i, delayLevels[i]);
        }

        producer.shutdown();
    }

    /**
     * 顺序消息
     *
     * 【说明】
     *   相同订单的消息发送到同一队列，消费者按队列顺序消费
     *
     * 【代码示例】
     *   producer.send(msg, new MessageQueueSelector() {
     *       @Override
     *       public int select(List<MessageQueue> mqs, Message msg, Object arg) {
     *           Long orderId = (Long) arg;
     *           return (int) (orderId % mqs.size());
     *       }
     *   }, orderId);
     *
     * 【使用场景】
     *   - 订单状态流转（创建→支付→发货→收货）
     *   - 金融交易（充值→转账→提现）
     */
    public void orderedMessage() {
        MockRocketMqProducer producer = new MockRocketMqProducer("order-producer");
        producer.start();
        String topic = "order-topic";

        String[] orderIds = {"ORD-001", "ORD-002", "ORD-003"};
        String[] statuses = {"创建", "支付", "发货", "收货"};
        for (String orderId : orderIds) {
            for (int i = 0; i < statuses.length; i++) {
                producer.sendOrderly(topic, "order-tag",
                    "订单" + orderId + ", 状态=" + statuses[i], orderId);
            }
        }

        producer.shutdown();
    }

    /**
     * 事务消息
     *
     * 【原理】
     *   1. 发送半消息（prepare）
     *   2. 执行本地事务
     *   3. 提交或回滚消息
     *   4. 定时检查未决事务
     *
     * 【代码示例】
     *   TransactionMQProducer producer = new TransactionMQProducer("tx-group");
     *   producer.setTransactionListener(new TransactionListener() {
     *       @Override
     *       public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
     *           return LocalTransactionState.COMMIT_MESSAGE;
     *       }
     *       @Override
     *       public LocalTransactionState checkLocalTransaction(MessageExt msg) {
     *           return LocalTransactionState.COMMIT_MESSAGE;
     *       }
     *   });
     *
     * 【使用场景】
     *   - 订单创建 + 库存扣减
     *   - 转账 + 账户变动
     */
    public void transactionMessage() {
        MockTransactionProducer producer = new MockTransactionProducer("tx-producer");

        String[] orderIds = {"ORD-TX-001", "ORD-TX-002", "ORD-TX-003"};
        for (String orderId : orderIds) {
            producer.sendTransactionMessage("tx-topic", "tx-tag", orderId);
        }

        producer.shutdown();
    }

    /**
     * 消息标签（Tag）
     *
     * 【说明】
     *   Tag 是消息的二级分类，消费者可按 Tag 过滤
     *
     * 【代码示例】
     *   // 发送时指定 Tag
     *   Message msg = new Message("Topic", "TagA", body.getBytes());
     *
     *   // 消费者按 Tag 订阅
     *   consumer.subscribe("Topic", "TagA || TagB");
     */
    public void messageTag() {
        MockRocketMqProducer producer = new MockRocketMqProducer("tag-producer");
        producer.start();
        String topic = "tag-topic";
        String[] tags = {"create", "pay", "ship", "refund"};

        for (int i = 0; i < 8; i++) {
            String tag = tags[i % tags.length];
            producer.send(topic, tag, "消息 " + i);
        }

        producer.shutdown();
    }

    // ========== 模拟类 ==========

    static class MockRocketMqProducer {
        private String groupId;

        public MockRocketMqProducer(String groupId) {
            this.groupId = groupId;
        }

        public void start() {
        }

        public void sendSync(String topic, String tag, String body) {
            /*
             * [RocketMQ Producer] 同步发送
             *   Message msg = new Message(topic, tag, body.getBytes());
             *   SendResult result = producer.send(msg);
             */
        }

        public void sendAsync(String topic, String tag, String body) {
            /*
             * [RocketMQ Producer] 异步发送
             *   producer.send(msg, new SendCallback() {
             *       public void onSuccess(SendResult result) {}
             *       public void onException(Exception e) {}
             *   });
             */
        }

        public void sendOneway(String topic, String tag, String body) {
            /*
             * [RocketMQ Producer] 单向发送
             *   producer.sendOneway(msg);
             */
        }

        public void send(String topic, String tag, String body) {
            /*
             * [RocketMQ Producer] 发送消息
             *   Message msg = new Message(topic, tag, body.getBytes());
             *   producer.send(msg);
             */
        }

        public void sendDelay(String topic, String tag, String body, int delayLevel) {
            /*
             * [RocketMQ Producer] 延迟消息
             *   Message msg = new Message(topic, tag, body.getBytes());
             *   msg.setDelayTimeLevel(delayLevel);
             *   producer.send(msg);
             */
        }

        public void sendOrderly(String topic, String tag, String body, Object queueSelector) {
            /*
             * [RocketMQ Producer] 顺序消息
             *   producer.send(msg, new MessageQueueSelector() {
             *       public int select(List<MessageQueue> mqs, Message msg, Object arg) {
             *           return (int) arg % mqs.size();
             *       }
             *   }, orderId);
             */
        }

        public void shutdown() {
        }
    }

    static class MockTransactionProducer {
        private String groupId;

        public MockTransactionProducer(String groupId) {
            this.groupId = groupId;
        }

        public void sendTransactionMessage(String topic, String tag, String orderId) {
            /*
             * [RocketMQ Producer] 事务消息
             *   Message msg = new Message(topic, tag, orderId.getBytes());
             *   producer.sendMessageInTransaction(msg, orderId);
             */
        }

        public void shutdown() {
        }
    }
}
