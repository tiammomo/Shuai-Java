package com.shuai;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息队列概述演示类
 *
 * 模块概述
 * ----------
 * 本模块演示消息队列的基本概念和核心价值。
 *
 * 核心内容
 * ----------
 *   - 消息队列的定义
 *   - 核心优势：解耦、异步、削峰填谷、可靠传输
 *   - 基本概念：Producer、Consumer、Broker、Topic
 *   - 消息模式：点对点、发布/订阅
 *
 * 代码位置
 * ----------
 * - 文件: [MqOverviewDemo.java](src/main/java/com/shuai/MqOverviewDemo.java)
 *
 * @author Shuai
 * @version 1.0
 */
public class MqOverviewDemo {

    public void runAllDemos() {
        /*
         * ====================
         *      消息队列概述
         * ====================
         *
         * 本演示包含以下内容：
         *   1. 什么是消息队列
         *   2. 消息队列的作用
         *   3. 基本概念
         *   4. 消息模式
         */

        // 什么是消息队列
        whatIsMq();

        // 消息队列的作用
        mqBenefits();

        // 基本概念
        mqConcepts();

        // 消息模式
        mqPatterns();
    }

    /**
     * 什么是消息队列
     *
     * 消息队列（Message Queue）是一种异步通信机制，
     * 用于不同系统或组件之间的解耦、异步和削峰填谷。
     *
     * 【传统调用 vs 消息队列】
     *   传统调用（同步）:
     *     服务A →→→ 服务B (阻塞等待)
     *   消息队列方式（异步）:
     *     服务A →→→ [MQ] →→→ 服务B (非阻塞)
     *
     * 【主流消息队列】
     *   - Kafka: 高吞吐流处理平台
     *   - RocketMQ: 阿里分布式消息中间件
     *   - RabbitMQ: AMQP 协议消息代理
     */
    private void whatIsMq() {
        /*
         * --- 1. 什么是消息队列？ ---
         *
         * 消息队列（Message Queue）是一种异步通信机制
         * 用于不同系统或组件之间的解耦、异步和削峰填谷
         *
         * 【代码示例】
         *   // 传统同步调用
         *   Order order = orderService.createOrder(request);  // 阻塞
         *   inventoryService.deduct(order);                    // 等待
         *   paymentService.pay(order);                         // 等待
         *
         *   // 消息队列异步调用
         *   orderService.createOrder(request);                 // 非阻塞
         *   mq.send("order-topic", order);                     // 发送消息
         *   // 库存系统和支付系统异步消费消息
         */
    }

    /**
     * 消息队列的作用
     *
     * 【核心优势】
     *   - 解耦：生产者和消费者无需直接依赖
     *   - 异步：非阻塞等待，提高系统响应速度
     *   - 削峰填谷：应对流量突增，保护下游系统
     *   - 可靠传输：保证消息不丢失
     *
     * 【典型应用场景】
     *   - 订单系统：订单创建后异步通知库存、物流系统
     *   - 日志收集：各服务将日志发送到MQ，由日志处理服务统一消费
     *   - 异步任务：耗时操作放入MQ，后台Worker异步处理
     *   - 系统集成：不同语言、不同平台之间的数据交换
     *
     * 【削峰填谷示意】
     *   请求高峰期:
     *     直接调用:  10000 req/s → 系统崩溃
     *     MQ 方式:  10000 req/s → [MQ 缓冲] → 100 req/s → 系统稳定
     */
    private void mqBenefits() {
        /*
         * --- 2. 消息队列的作用 ---
         *
         * 核心优势：
         *   - 解耦：生产者和消费者无需直接依赖
         *   - 异步：非阻塞等待，提高系统响应速度
         *   - 削峰填谷：应对流量突增，保护下游系统
         *   - 可靠传输：保证消息不丢失
         *
         * 典型应用场景：
         *   - 订单系统、 日志收集、 异步任务、 系统集成
         *
         * 【代码示例 - 解耦】
         *   // 紧耦合
         *   orderService.createOrder() → inventoryService.deduct();
         *   orderService.createOrder() → paymentService.pay();
         *
         *   // 松耦合（通过 MQ）
         *   orderService.createOrder() → [order-topic]
         *   [order-topic] → inventoryService.deduct();
         *   [order-topic] → paymentService.pay();
         *
         * 【代码示例 - 异步】
         *   // 同步方式 - 用户等待时间长
         *   Order order = orderService.createOrder();  // 100ms
         *   inventoryService.deduct(order);            // 50ms
         *   paymentService.pay(order);                 // 50ms
         *   // 总耗时: 200ms
         *
         *   // 异步方式 - 用户等待时间短
         *   orderService.createOrder();                // 100ms
         *   mq.send("order-topic", order);             // 10ms
         *   // 总耗时: 110ms
         */
    }

    /**
     * 消息队列基本概念
     *
     * 【核心概念】
     *   - Producer（生产者）：发送消息的一方
     *   - Consumer（消费者）：接收消息的一方
     *   - Broker（代理）：消息队列服务器
     *   - Topic（主题）：消息的分类逻辑单元
     *   - Partition（分区）：Topic 的物理分片（Kafka）
     *   - Queue（队列）：存储消息的容器
     *   - Message（消息）：传输的数据单元
     *
     * 【消息流转】
     *   Producer → Broker → Consumer
     *              ↓
     *         [Topic/Queue]
     *              ↓
     *         [Message]
     *
     * 【消息生命周期】
     *   1. Producer 发送消息到 Broker
     *   2. Broker 将消息存储到 Topic/Queue
     *   3. Consumer 从 Topic/Queue 拉取消息
     *   4. Consumer 处理消息并确认
     *   5. Broker 删除或标记消息
     */
    private void mqConcepts() {
        /*
         * --- 3. 消息队列基本概念 ---
         *
         * 核心概念：
         *   - Producer（生产者）：发送消息的一方
         *   - Consumer（消费者）：接收消息的一方
         *   - Broker（代理）：消息队列服务器
         *   - Topic（主题）：消息的分类逻辑单元
         *   - Partition（分区）：Topic 的物理分片
         *   - Queue（队列）：存储消息的容器
         *   - Message（消息）：传输的数据单元
         *
         * 【代码示例 - 生产者】
         *   Producer producer = new KafkaProducer<>(props);
         *   producer.send(new ProducerRecord<>("topic", "key", "value"));
         *
         * 【代码示例 - 消费者】
         *   Consumer consumer = new KafkaConsumer<>(props);
         *   consumer.subscribe(Arrays.asList("topic"));
         *   ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
         *
         * 【消息结构】
         *   Message:
         *     - Headers: 消息头（元数据）
         *     - Key: 消息键（用于分区路由）
         *     - Value: 消息内容
         *     - Timestamp: 时间戳
         *     - Offset: 消息位移
         */
    }

    /**
     * 消息模式
     *
     * 【两种消息模式】
     *   - 点对点（Point-to-Point）：一个生产者对应一个消费者
     *   - 发布/订阅（Publish-Subscribe）：一个生产者对应多个消费者
     *
     * 【点对点模式】
     *   Producer → [Queue] → Consumer
     *   特点: 消息只被消费一次
     *
     * 【发布/订阅模式】
     *   Producer → [Topic] → Subscriber1
     *                    → Subscriber2
     *   特点: 消息被所有订阅者接收
     *
     * 【两种模式对比】
     *   | 特性 | 点对点 | 发布/订阅 |
     *   |------|--------|-----------|
     *   | 消息消费 | 一次 | 多次 |
     *   | 消费者关系 | 一对一 | 一对多 |
     *   | 消息保留 | 消费后删除 | 保留至所有消费者消费 |
     */
    private void mqPatterns() {
        /*
         * --- 4. 消息模式 ---
         *
         * 两种消息模式：
         *   - 点对点（Point-to-Point）：一个生产者对应一个消费者
         *   - 发布/订阅（Publish-Subscribe）：一个生产者对应多个消费者
         *
         * 【代码示例 - 点对点】
         *   // 队列模式
         *   channel.queueDeclare("task_queue", true, false, false, null);
         *   channel.basicConsume("task_queue", false, deliverCallback, cancelCallback);
         *
         * 【代码示例 - 发布/订阅】
         *   // 主题模式
         *   channel.exchangeDeclare("logs", BuiltinExchangeType.FANOUT);
         *   channel.basicPublish("logs", "", null, message.getBytes());
         *   // 多个消费者绑定到同一 Exchange
         *
         * 【实际应用场景】
         *   - 点对点: 订单处理、任务执行
         *   - 发布/订阅: 日志广播、事件通知
         */
    }

    // ========== 模拟类 ==========

    /**
     * 模拟消息队列
     *
     * 【功能】
     *   - 模拟消息存储
     *   - 模拟消息发送
     *   - 模拟消息消费
     */
    static class MockMessageQueue {
        private final List<String> messages = new ArrayList<>();

        /**
         * 发送消息
         *
         * @param topic 主题
         * @param message 消息内容
         */
        public void send(String topic, String message) {
            messages.add(message);
            /*
             * [MQ] 发送消息
             *   topic=test-topic
             *   message=Hello World
             */
        }

        /**
         * 消费消息
         *
         * @return 消息内容
         */
        public String poll() {
            if (!messages.isEmpty()) {
                return messages.remove(0);
            }
            return null;
        }

        /**
         * 获取消息数量
         */
        public int size() {
            return messages.size();
        }
    }
}
