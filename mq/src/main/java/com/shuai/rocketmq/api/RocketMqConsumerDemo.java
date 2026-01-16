package com.shuai.rocketmq.api;

/**
 * RocketMQ 消费者 API 演示
 *
 * 包含内容：
 * - Push 消费
 * - Pull 消费
 * - 消息过滤
 * - 顺序消费
 * - 并发消费
 *
 * 代码位置: [RocketMqConsumerDemo.java](src/main/java/com/shuai/rocketmq/api/RocketMqConsumerDemo.java)
 *
 * @author Shuai
 */
public class RocketMqConsumerDemo {

    /**
     * 创建 Push 消费者
     *
     * 【说明】
     *   Push 模式由 SDK 负责拉取消息，实时推送给消费者
     *
     * 【代码示例】
     *   DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer-group");
     *   consumer.setNamesrvAddr("localhost:9876");
     *   consumer.subscribe("TopicTest", "*");
     *   consumer.registerMessageListener(new MessageListenerConcurrently() {
     *       @Override
     *       public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
     *           return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
     *       }
     *   });
     *   consumer.start();
     *
     * 【核心配置】
     *   - consumerGroup: 消费者组
     *   - namesrvAddr: NameServer 地址
     *   - messageModel: 消息模型（广播/集群）
     */
    public void createPushConsumer() {
        MockPushConsumer consumer = new MockPushConsumer("push-consumer-group");
        consumer.start();
        consumer.shutdown();
    }

    /**
     * 创建 Pull 消费者
     *
     * 【说明】
     *   Pull 模式由消费者主动拉取消息，灵活控制消费时机
     *
     * 【代码示例】
     *   DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("pull-group");
     *   consumer.setNamesrvAddr("localhost:9876");
     *   consumer.start();
     *
     *   Set<MessageQueue> queues = consumer.fetchSubscribeMessageQueues("TopicTest");
     *   for (MessageQueue queue : queues) {
     *       PullResult pullResult = consumer.pull(queue, null, getMessageQueueOffset(queue), 64);
     *       // 处理消息
     *   }
     *
     * 【使用场景】
     *   - 批量消费
     *   - 精确控制消费速率
     *   - 自定义负载均衡策略
     */
    public void createPullConsumer() {
        MockPullConsumer consumer = new MockPullConsumer("pull-consumer-group");
        consumer.start();
        consumer.shutdown();
    }

    /**
     * 消息过滤
     *
     * 【过滤方式】
     *   - Tag 过滤: "TagA || TagB"
     *   - SQL 过滤: "age > 18 AND sex = 'M'"
     *
     * 【代码示例 - Tag 过滤】
     *   consumer.subscribe("TopicTest", "TagA || TagB");
     *
     * 【代码示例 - SQL 过滤】
     *   consumer.subscribe("TopicTest", MessageSelector.bySql("age > 18"));
     *
     * 【SQL 过滤语法】
     *   - 比较: =, !=, >, >=, <, <=
     *   - 逻辑: AND, OR, NOT
     *   - 范围: BETWEEN, IN
     *
     * 【使用场景】
     *   - 按类型过滤
     *   - 按属性过滤
     *   - 复杂条件过滤
     */
    public void messageFilter() {
        MockPushConsumer consumer = new MockPushConsumer("filter-consumer-group");

        // Tag 过滤
        consumer.subscribeByTag("TopicTest", "TagA || TagB");

        // SQL 过滤
        consumer.subscribeBySql("TopicTest", "age > 18");

        consumer.shutdown();
    }

    /**
     * 顺序消费
     *
     * 【说明】
     *   按消息发送顺序消费，同一订单的消息按顺序处理
     *
     * 【代码示例】
     *   consumer.registerMessageListener(new MessageListenerOrderly() {
     *       @Override
     *       public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
     *           for (MessageExt msg : msgs) {
     *               // 处理消息
     *           }
     *           return ConsumeOrderlyStatus.SUCCESS;
     *       }
     *   });
     *
     * 【特点】
     *   - 同一队列内消息有序
     *   - 消费失败会继续重试
     *   - 性能低于并发消费
     *
     * 【使用场景】
     *   - 订单状态流转
     *   - 交易流水处理
     *   - 库存状态更新
     */
    public void orderedConsume() {
        MockPushConsumer consumer = new MockPushConsumer("order-consumer-group");

        consumer.subscribe("OrderTopic", "*");
        consumer.setMessageListener("orderly");

        consumer.shutdown();
    }

    /**
     * 并发消费
     *
     * 【说明】
     *   并发处理消息，性能高但不保证顺序
     *
     * 【代码示例】
     *   consumer.registerMessageListener(new MessageListenerConcurrently() {
     *       @Override
     *       public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
     *           for (MessageExt msg : msgs) {
     *               // 处理消息: new String(msg.getBody())
     *           }
     *           return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
     *       }
     *   });
     *
     * 【配置参数】
     *   - consumeThreadMin: 最小消费线程数
     *   - consumeThreadMax: 最大消费线程数
     *   - consumeMessageBatchMaxSize: 批量消费大小
     *
     * 【使用场景】
     *   - 日志处理
     *   - 统计上报
     *   - 允许无序的业务
     */
    public void concurrentConsume() {
        MockPushConsumer consumer = new MockPushConsumer("concurrent-consumer-group");

        consumer.subscribe("TopicTest", "*");
        consumer.setMessageListener("concurrently");

        consumer.shutdown();
    }

    /**
     * 消费模式
     *
     * 【集群消费】
     *   - 默认模式
     *   - 同一消费者组内，消息均分
     *   - 一条消息只被一个消费者消费
     *
     * 【广播消费】
     *   - 每条消息被所有消费者消费
     *   - 适合消息推送场景
     *
     * 【代码示例 - 集群消费】
     *   consumer.setMessageModel(MessageModel.CLUSTERING);
     *
     * 【代码示例 - 广播消费】
     *   consumer.setMessageModel(MessageModel.BROADCASTING);
     *
     * 【选择建议】
     *   - 集群: 高可用、负载均衡
     *   - 广播: 通知所有节点
     */
    public void consumeMode() {
        MockPushConsumer consumer = new MockPushConsumer("mode-consumer-group");

        // 集群消费
        consumer.setMessageModel("CLUSTERING");

        // 广播消费
        consumer.setMessageModel("BROADCASTING");

        consumer.shutdown();
    }

    // ========== 模拟类 ==========

    static class MockPushConsumer {
        private String groupId;

        public MockPushConsumer(String groupId) {
            this.groupId = groupId;
        }

        public void start() {
        }

        public void subscribe(String topic, String subExpression) {
            /*
             * [RocketMQ Push Consumer] 订阅主题
             *   consumer.subscribe(topic, subExpression);
             */
        }

        public void subscribeByTag(String topic, String tags) {
            /*
             * [RocketMQ Push Consumer] Tag 过滤
             *   consumer.subscribe(topic, tags);
             */
        }

        public void subscribeBySql(String topic, String sql) {
            /*
             * [RocketMQ Push Consumer] SQL 过滤
             *   consumer.subscribe(topic, MessageSelector.bySql(sql));
             */
        }

        public void setMessageListener(String type) {
            /*
             * [RocketMQ Push Consumer] 设置消息监听器
             *   // 并发消费
             *   consumer.registerMessageListener(new MessageListenerConcurrently() {
             *       public ConsumeConcurrentlyStatus consumeMessage(...) {}
             *   });
             *   // 顺序消费
             *   consumer.registerMessageListener(new MessageListenerOrderly() {
             *       public ConsumeOrderlyStatus consumeMessage(...) {}
             *   });
             */
        }

        public void setMessageModel(String model) {
            /*
             * [RocketMQ Push Consumer] 设置消费模式
             *   consumer.setMessageModel(MessageModel.CLUSTERING);
             */
        }

        public void shutdown() {
        }
    }

    static class MockPullConsumer {
        private String groupId;

        public MockPullConsumer(String groupId) {
            this.groupId = groupId;
        }

        public void start() {
        }

        public void pull(String topic) {
            /*
             * [RocketMQ Pull Consumer] 拉取消息
             *   Set<MessageQueue> queues = consumer.fetchSubscribeMessageQueues(topic);
             *   for (MessageQueue queue : queues) {
             *       PullResult pullResult = consumer.pull(queue, null, offset, 64);
             *   }
             */
        }

        public void shutdown() {
        }
    }
}
