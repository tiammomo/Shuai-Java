package com.shuai.rocketmq.api;

import com.shuai.model.Message;
import com.shuai.model.MessageResult;
import com.shuai.rocketmq.producer.RocketMqProducerImpl;

/**
 * RocketMQ 生产者 API 演示
 *
 * 【运行方式】
 *   1. 确保 Docker RocketMQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * @author Shuai
 */
public class RocketMqProducerDemo {

    private static final String NAMESRV_ADDR = "localhost:9876";

    public static void main(String[] args) {
        RocketMqProducerDemo demo = new RocketMqProducerDemo();

        try {
            // 创建生产者
            demo.createProducer();

            // 同步发送
            demo.syncSend();

            // 异步发送
            demo.asyncSend();

            // 单向发送
            demo.onewaySend();

            // 延迟消息
            demo.delayMessage();

            // 顺序消息
            demo.orderedMessage();

            // 消息标签
            demo.messageTag();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建生产者
     */
    public void createProducer() {
        System.out.println("\n=== 创建生产者 ===");

        RocketMqProducerImpl producer = new RocketMqProducerImpl();
        producer.setProducerGroup("demo-producer-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.setRetryTimesWhenSendFailed(2);
        producer.setSendMsgTimeout(3000);

        producer.start();
        System.out.println("生产者创建成功: " + producer);

        producer.shutdown();
        System.out.println("生产者已关闭");
    }

    /**
     * 同步发送
     */
    public void syncSend() {
        System.out.println("\n=== 同步发送 ===");

        RocketMqProducerImpl producer = new RocketMqProducerImpl();
        producer.setProducerGroup("sync-producer-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.start();

        String topic = "sync-topic";

        for (int i = 0; i < 5; i++) {
            Message message = Message.builder()
                    .topic(topic)
                    .tag("sync-tag")
                    .body("同步消息 " + i)
                    .messageId("sync-" + i)
                    .build();

            MessageResult result = producer.send(message);
            System.out.println("发送结果: " + result.isSuccess() +
                    ", 消息ID: " + result.getMessageId() +
                    ", 分区: " + result.getPartition() +
                    ", Offset: " + result.getOffset());
        }

        producer.shutdown();
    }

    /**
     * 异步发送
     */
    public void asyncSend() {
        System.out.println("\n=== 异步发送 ===");

        RocketMqProducerImpl producer = new RocketMqProducerImpl();
        producer.setProducerGroup("async-producer-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.start();

        String topic = "async-topic";

        for (int i = 0; i < 5; i++) {
            final int idx = i;
            Message message = Message.builder()
                    .topic(topic)
                    .tag("async-tag")
                    .body("异步消息 " + i)
                    .messageId("async-" + i)
                    .build();

            producer.sendAsync(message, new RocketMqProducerImpl.SendCallback() {
                @Override
                public void onSuccess(MessageResult result) {
                    System.out.println("异步发送成功: " + idx + ", 消息ID: " + result.getMessageId());
                }

                @Override
                public void onFailure(MessageResult result) {
                    System.out.println("异步发送失败: " + idx + ", 错误: " + result.getErrorMessage());
                }
            });
        }

        // 等待异步发送完成
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        producer.shutdown();
    }

    /**
     * 单向发送
     */
    public void onewaySend() {
        System.out.println("\n=== 单向发送 ===");

        RocketMqProducerImpl producer = new RocketMqProducerImpl();
        producer.setProducerGroup("oneway-producer-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.start();

        String topic = "oneway-topic";

        for (int i = 0; i < 5; i++) {
            Message message = Message.builder()
                    .topic(topic)
                    .tag("oneway-tag")
                    .body("单向消息 " + i)
                    .messageId("oneway-" + i)
                    .build();

            producer.sendOneWay(message);
            System.out.println("单向发送: " + i);
        }

        producer.shutdown();
    }

    /**
     * 延迟消息
     */
    public void delayMessage() {
        System.out.println("\n=== 延迟消息 ===");

        RocketMqProducerImpl producer = new RocketMqProducerImpl();
        producer.setProducerGroup("delay-producer-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.start();

        String topic = "delay-topic";

        int[] delayLevels = {1, 2, 3, 4, 5};  // 1=1秒, 2=5秒, 3=10秒, 4=30秒, 5=1分钟
        for (int i = 0; i < delayLevels.length; i++) {
            Message message = Message.builder()
                    .topic(topic)
                    .tag("delay-tag")
                    .body("延迟消息 " + i + " (延迟级别: " + delayLevels[i] + ")")
                    .messageId("delay-" + i)
                    .build();

            MessageResult result = producer.sendDelayed(message, delayLevels[i]);
            System.out.println("延迟消息 " + i + ": 发送结果=" + result.isSuccess() +
                    ", 延迟级别=" + delayLevels[i]);
        }

        producer.shutdown();
    }

    /**
     * 顺序消息
     */
    public void orderedMessage() {
        System.out.println("\n=== 顺序消息 ===");

        RocketMqProducerImpl producer = new RocketMqProducerImpl();
        producer.setProducerGroup("order-producer-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.start();

        String topic = "order-topic";

        String[] orderIds = {"ORD-001", "ORD-002", "ORD-003"};
        String[] statuses = {"创建", "支付", "发货", "收货"};

        for (String orderId : orderIds) {
            System.out.println("\n订单 " + orderId + " 的状态流转:");
            for (int i = 0; i < statuses.length; i++) {
                Message message = Message.builder()
                        .topic(topic)
                        .tag("order-tag")
                        .body("订单" + orderId + ", 状态=" + statuses[i])
                        .messageId("order-" + orderId + "-" + i)
                        .build();

                MessageResult result = producer.sendOrderly(message, orderId);
                System.out.println("  -> " + statuses[i] + ": " + (result.isSuccess() ? "成功" : "失败"));
            }
        }

        producer.shutdown();
    }

    /**
     * 消息标签（Tag）
     */
    public void messageTag() {
        System.out.println("\n=== 消息标签 ===");

        RocketMqProducerImpl producer = new RocketMqProducerImpl();
        producer.setProducerGroup("tag-producer-group");
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.start();

        String topic = "tag-topic";
        String[] tags = {"create", "pay", "ship", "refund"};

        for (int i = 0; i < 8; i++) {
            String tag = tags[i % tags.length];
            Message message = Message.builder()
                    .topic(topic)
                    .tag(tag)
                    .body("消息 " + i + ", 类型=" + tag)
                    .messageId("tag-" + i)
                    .build();

            MessageResult result = producer.send(message);
            System.out.println("发送消息: tag=" + tag + ", 结果=" + result.isSuccess());
        }

        producer.shutdown();
    }
}
