package com.shuai;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息队列概述演示类
 *
 * 【运行方式】
 *   1. 确保 Docker MQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * 核心内容
 *   - 消息队列的定义
 *   - 核心优势：解耦、异步、削峰填谷、可靠传输
 *   - 基本概念：Producer、Consumer、Broker、Topic
 *   - 消息模式：点对点、发布/订阅
 *
 * @author Shuai
 */
public class MqOverviewDemo {

    public static void main(String[] args) {
        MqOverviewDemo demo = new MqOverviewDemo();
        try {
            demo.runAllDemos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runAllDemos() {
        System.out.println("=".repeat(60));
        System.out.println("           消息队列概述演示");
        System.out.println("=".repeat(60));

        // 1. 什么是消息队列
        whatIsMq();

        // 2. 消息队列的作用
        mqBenefits();

        // 3. 基本概念
        mqConcepts();

        // 4. 消息模式
        mqPatterns();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("           消息队列概述演示完成");
        System.out.println("=".repeat(60));
    }

    /**
     * 什么是消息队列
     *
     * 【演示】
     *   模拟同步调用 vs 异步消息调用
     */
    private void whatIsMq() {
        System.out.println("\n=== 1. 什么是消息队列 ===");

        // 模拟传统同步调用
        System.out.println("\n【同步调用方式】");
        long startTime = System.currentTimeMillis();
        String orderResult = syncCreateOrder("ORD-001");
        String inventoryResult = syncDeductInventory("ORD-001");
        String paymentResult = syncProcessPayment("ORD-001");
        long syncTime = System.currentTimeMillis() - startTime;

        System.out.println("  订单结果: " + orderResult);
        System.out.println("  库存结果: " + inventoryResult);
        System.out.println("  支付结果: " + paymentResult);
        System.out.println("  总耗时: " + syncTime + "ms");

        // 模拟异步消息调用
        System.out.println("\n【异步消息调用方式】");
        startTime = System.currentTimeMillis();

        asyncSendOrderMessage("ORD-002");

        long asyncTime = System.currentTimeMillis() - startTime;
        System.out.println("  总耗时: " + asyncTime + "ms (非阻塞)");
        System.out.println("  订单消息已发送，库存和支付系统异步处理");

        // 对比结果
        System.out.println("\n【性能对比】");
        System.out.println("  同步调用: " + syncTime + "ms (阻塞等待所有操作完成)");
        System.out.println("  异步调用: " + asyncTime + "ms (立即返回，异步处理)");
    }

    /**
     * 消息队列的作用
     *
     * 【演示】
     *   1. 解耦效果
     *   2. 异步处理
     *   3. 削峰填谷
     */
    private void mqBenefits() {
        System.out.println("\n=== 2. 消息队列的作用 ===");

        // 1. 解耦演示
        System.out.println("\n【解耦效果】");
        System.out.println("  传统方式: OrderService 直接调用 InventoryService、PaymentService");
        System.out.println("  消息队列: OrderService -> [order-topic] -> InventoryService, PaymentService");
        demonstrateDecoupling();

        // 2. 异步处理演示
        System.out.println("\n【异步处理】");
        demonstrateAsync();

        // 3. 削峰填谷演示
        System.out.println("\n【削峰填谷】");
        demonstratePeakShaving();
    }

    /**
     * 消息队列基本概念
     */
    private void mqConcepts() {
        System.out.println("\n=== 3. 消息队列基本概念 ===");

        System.out.println("\n【核心概念】");
        System.out.println("  - Producer（生产者）：发送消息的一方");
        System.out.println("  - Consumer（消费者）：接收消息的一方");
        System.out.println("  - Broker（代理）：消息队列服务器");
        System.out.println("  - Topic（主题）：消息的分类逻辑单元");
        System.out.println("  - Partition（分区）：Topic 的物理分片（Kafka）");
        System.out.println("  - Queue（队列）：存储消息的容器");
        System.out.println("  - Message（消息）：传输的数据单元");

        // 模拟消息流转
        System.out.println("\n【消息流转演示】");
        MockMessageQueue mq = new MockMessageQueue("test-topic");

        // Producer 发送消息
        System.out.println("  Producer 发送消息到 Broker...");
        mq.send("order-created", "Order{orderId='ORD-001', amount=99.9}");
        mq.send("order-created", "Order{orderId='ORD-002', amount=199.9}");

        // Consumer 消费消息
        System.out.println("  Consumer 从 Broker 拉取消息...");
        String msg;
        while ((msg = mq.poll()) != null) {
            System.out.println("  收到消息: " + msg);
        }

        System.out.println("\n【消息结构】");
        System.out.println("  Message{");
        System.out.println("    topic: 'order-created'");
        System.out.println("    key: 'ORD-001'");
        System.out.println("    value: 'Order{...}'");
        System.out.println("    timestamp: 1705675200000");
        System.out.println("    partition: 0");
        System.out.println("    offset: 0");
        System.out.println("  }");
    }

    /**
     * 消息模式
     *
     * 【演示】
     *   1. 点对点模式（Queue）
     *   2. 发布/订阅模式（Topic）
     */
    private void mqPatterns() {
        System.out.println("\n=== 4. 消息模式 ===");

        // 点对点模式
        System.out.println("\n【点对点模式（Point-to-Point）】");
        System.out.println("  Producer -> [Queue] -> Consumer");
        System.out.println("  特点: 消息只被消费一次");
        demonstratePointToPoint();

        // 发布/订阅模式
        System.out.println("\n【发布/订阅模式（Publish-Subscribe）】");
        System.out.println("  Producer -> [Topic] -> Subscriber1");
        System.out.println("                    -> Subscriber2");
        System.out.println("  特点: 消息被所有订阅者接收");
        demonstratePublishSubscribe();
    }

    // ========== 演示方法 ==========

    private void demonstrateDecoupling() {
        System.out.println("\n  【解耦演示】");
        MockMessageQueue orderQueue = new MockMessageQueue("order-topic");
        MockMessageQueue notificationQueue = new MockMessageQueue("notification-topic");

        // 订单服务只负责发送消息，不关心谁消费
        orderQueue.send("order-topic", "订单创建: ORD-001");
        notificationQueue.send("notification-topic", "通知: 订单创建成功");

        System.out.println("  订单服务发送消息完成，无需调用库存和通知服务");
        System.out.println("  库存服务和通知服务独立消费消息");
    }

    private void demonstrateAsync() {
        System.out.println("\n  【异步处理演示】");
        long startTime = System.currentTimeMillis();

        // 模拟异步发送
        for (int i = 1; i <= 5; i++) {
            final int msgId = i;
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(100);  // 模拟处理时间
                    System.out.println("    异步处理消息: " + msgId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        long asyncTime = System.currentTimeMillis() - startTime;
        System.out.println("  发送 5 条异步消息耗时: " + asyncTime + "ms");

        // 等待异步任务完成
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void demonstratePeakShaving() {
        System.out.println("\n  【削峰填谷演示】");

        MockMessageQueue queue = new MockMessageQueue("request-queue");
        AtomicInteger processed = new AtomicInteger(0);

        // 模拟高峰期：1000 个请求瞬间涌入
        System.out.println("  高峰期: 1000 个请求瞬间涌入");
        for (int i = 0; i < 1000; i++) {
            queue.send("request-queue", "Request-" + i);
        }
        System.out.println("  队列积压: " + queue.size() + " 条消息");

        // 模拟消费者：以稳定速率消费
        System.out.println("  消费者: 每秒处理 100 条消息");
        while (queue.size() > 0 && processed.get() < 100) {
            queue.poll();
            processed.incrementAndGet();
        }
        System.out.println("  1秒后处理: " + processed.get() + " 条消息");
        System.out.println("  队列剩余: " + queue.size() + " 条消息");
        System.out.println("  削峰效果: 1000 条峰值 -> 100 条/秒 稳定消费");
    }

    private void demonstratePointToPoint() {
        MockMessageQueue queue = new MockMessageQueue("task-queue");

        // 发送多个任务
        queue.send("task-queue", "Task-1: 处理订单");
        queue.send("task-queue", "Task-2: 发送邮件");
        queue.send("task-queue", "Task-3: 更新库存");

        System.out.println("  发送 3 个任务到队列");
        System.out.println("  消费者 1 消费: " + queue.poll());
        System.out.println("  消费者 1 消费: " + queue.poll());
        System.out.println("  消费者 1 消费: " + queue.poll());
        System.out.println("  队列剩余: " + queue.size() + " 条");
    }

    private void demonstratePublishSubscribe() {
        MockMessageQueue topic = new MockMessageQueue("notification-topic");
        List<String> subscriber1 = new ArrayList<>();
        List<String> subscriber2 = new ArrayList<>();

        // 发布通知
        topic.send("notification-topic", "系统公告: 新功能上线");
        topic.send("notification-topic", "系统公告: 维护通知");

        System.out.println("  发布 2 条通知到 Topic");

        // 订阅者 1 消费
        System.out.println("  订阅者 1 收到:");
        String msg;
        while ((msg = topic.poll()) != null) {
            subscriber1.add(msg);
            System.out.println("    -> " + msg);
        }

        // 重新发布让订阅者 2 消费
        topic.send("notification-topic", "系统公告: 活动通知");
        System.out.println("  订阅者 2 收到:");
        while ((msg = topic.poll()) != null) {
            subscriber2.add(msg);
            System.out.println("    -> " + msg);
        }

        System.out.println("  订阅者 1 收到 " + subscriber1.size() + " 条");
        System.out.println("  订阅者 2 收到 " + subscriber2.size() + " 条");
    }

    // ========== 模拟方法 ==========

    private String syncCreateOrder(String orderId) {
        try {
            Thread.sleep(100);  // 模拟创建订单耗时
            return "订单创建成功: " + orderId;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "订单创建失败";
        }
    }

    private String syncDeductInventory(String orderId) {
        try {
            Thread.sleep(50);  // 模拟库存扣减耗时
            return "库存扣减成功: " + orderId;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "库存扣减失败";
        }
    }

    private String syncProcessPayment(String orderId) {
        try {
            Thread.sleep(50);  // 模拟支付处理耗时
            return "支付处理成功: " + orderId;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "支付处理失败";
        }
    }

    private void asyncSendOrderMessage(String orderId) {
        // 模拟异步发送消息
        System.out.println("  发送订单消息: " + orderId);
        System.out.println("  订单服务继续处理其他请求...");
    }

    // ========== 模拟类 ==========

    /**
     * 模拟消息队列
     */
    static class MockMessageQueue {
        private final List<String> messages = new ArrayList<>();

        public void send(String topic, String message) {
            messages.add(message);
        }

        public String poll() {
            if (!messages.isEmpty()) {
                return messages.remove(0);
            }
            return null;
        }

        public int size() {
            return messages.size();
        }
    }
}
