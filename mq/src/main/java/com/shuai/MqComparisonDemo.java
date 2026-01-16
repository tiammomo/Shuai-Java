package com.shuai;

/**
 * 消息队列对比演示类
 *
 * 模块概述
 * ----------
 * 本模块对比主流消息队列的特性，帮助选择合适的 MQ。
 * 包含性能对比、可靠性分析、选型建议和使用注意事项。
 *
 * 核心内容
 * ----------
 *   - Kafka、RocketMQ、RabbitMQ 对比
 *   - 性能维度对比
 *   - 可靠性保障方案
 *   - 选型建议
 *   - 使用注意事项
 *
 * 代码位置
 * ----------
 * - 文件: [MqComparisonDemo.java](src/main/java/com/shuai/MqComparisonDemo.java)
 *
 * @author Shuai
 * @version 1.0
 */
public class MqComparisonDemo {

    public void runAllDemos() {
        /*
         * ====================
         *        消息队列对比
         * ====================
         *
         * 本演示包含以下内容：
         *   1. 主流消息队列特性对比
         *   2. 性能维度对比
         *   3. 可靠性保障方案
         *   4. 选型建议
         *   5. 使用注意事项
         */

        // 特性对比
        mqComparison();

        // 性能对比
        performanceComparison();

        // 可靠性保障
        reliabilityGuarantee();

        // 选择建议
        selectionGuide();

        // 使用注意事项
        usageNotes();
    }

    /**
     * 消息队列特性对比
     *
     * 【特性对比表】
     * +------------------+---------------+---------------+---------------+
     * |     特性         |    Kafka      |   RocketMQ    |    RabbitMQ   |
     * +------------------+---------------+---------------+---------------+
     * |  开发语言        |    Scala      |     Java      |     Erlang    |
     * |  吞吐量          |    极高       |      高       |      中       |
     * |  延迟            |    低         |      低       |      低       |
     * |  可靠性          |    高         |      高       |      高       |
     * |  顺序消息        |    支持       |      支持     |      支持     |
     * |  事务消息        |    较弱       |      支持     |      支持     |
     * |  消息堆积        |    极强       |      强       |      中       |
     * |  社区活跃度      |    极高       |      高       |      高       |
     * |  适用场景        |    日志/流处理 |    电商/金融  |    企业应用   |
     * +------------------+---------------+---------------+---------------+
     *
     * 【架构特点对比】
     *   Kafka:
     *     - 分布式架构，支持水平扩展
     *     - 基于日志追加，写入性能极高
     *     - 支持消息回溯（从任意 offset 开始消费）
     *
     *   RocketMQ:
     *     - 借鉴 Kafka 设计，更适合业务消息
     *   - 支持事务消息和延迟消息
     *   - 支持消息过滤（Tag/SQL）
     *
     *   RabbitMQ:
     *     - 基于 AMQP 协议，灵活路由
     *   - 支持多种消息模式
     *   - 管理界面友好
     */
    private void mqComparison() {
        /*
         * --- 1. 主流消息队列对比 ---
         *
         * +------------------+---------------+---------------+---------------+
         * |     特性         |    Kafka      |   RocketMQ    |    RabbitMQ   |
         * +------------------+---------------+---------------+---------------+
         * |  开发语言        |    Scala      |     Java      |     Erlang    |
         * |  吞吐量          |    极高       |      高       |      中       |
         * |  延迟            |    低         |      低       |      低       |
         * |  可靠性          |    高         |      高       |      高       |
         * |  顺序消息        |    支持       |      支持     |      支持     |
         * |  事务消息        |    较弱       |      支持     |      支持     |
         * |  消息堆积        |    极强       |      强       |      中       |
         * |  社区活跃度      |    极高       |      高       |      高       |
         * |  适用场景        |    日志/流处理 |    电商/金融  |    企业应用   |
         * +------------------+---------------+---------------+---------------+
         *
         * 【代码示例 - 选择依据】
         *   // 日志收集场景
         *   if (场景 == "日志收集" || 场景 == "大数据流处理") {
         *       return Kafka;  // 高吞吐、消息堆积能力强
         *   }
         *
         *   // 电商订单场景
         *   if (场景 == "订单处理" || 场景 == "金融交易") {
         *       return RocketMQ;  // 事务消息支持
         *   }
         *
         *   // 企业应用场景
         *   if (场景 == "企业集成" || 场景 == "复杂路由") {
         *       return RabbitMQ;  // AMQP 协议、灵活路由
         *   }
         */

        /*
         * --- 1. 主流消息队列对比 ---
         *
         * +----------+---------+-----------+----------+
         * | MQ       | 吞吐量  | 事务消息  | 适用场景 |
         * +----------+---------+-----------+----------+
         * | Kafka    | 极高    | 较弱      | 日志/流处理 |
         * | RocketMQ | 高      | 支持      | 电商/金融 |
         * | RabbitMQ | 中      | 支持      | 企业应用 |
         * +----------+---------+-----------+----------+
         *
         * 【选型建议】
         *   场景: 日志收集 -> 推荐: Kafka
         *   场景: 电商订单 -> 推荐: RocketMQ
         *   场景: 系统集成 -> 推荐: RabbitMQ
         *   场景: 大数据流处理 -> 推荐: Kafka
         *   场景: 支付交易 -> 推荐: RocketMQ
         */
    }

    /**
     * 性能维度对比
     *
     * 【吞吐量对比】
     *   Kafka: 100万+ msg/s（单机）
     *   RocketMQ: 10万+ msg/s（单机）
     *   RabbitMQ: 1万-5万 msg/s（单机）
     *
     * 【延迟对比】
     *   Kafka: 毫秒级（取决于批次大小）
     *   RocketMQ: 毫秒级
     *   RabbitMQ: 毫秒级
     *
     * 【消息堆积能力】
     *   Kafka: 极强（基于磁盘，持久化）
     *   RocketMQ: 强（支持大消息堆积）
     *   RabbitMQ: 中等（内存有限制）
     *
     * 【性能优化建议】
     *   Kafka:
     *     - 增加分区数提高吞吐
     *     - 调整 batch.size 和 linger.ms
     *     - 启用压缩（compression.type）
     *
     *   RocketMQ:
     *     - 增加队列数
     *     - 合理设置线程数
     *     - 使用顺序写入
     *
     *   RabbitMQ:
     *     - 增加消费者数量
     *     - 设置合适的 prefetch
     *     - 使用镜像队列提升可用性
     */
    private void performanceComparison() {
        /*
         * --- 2. 性能维度对比 ---
         *
         * 【吞吐量对比】
         *   Kafka: 100万+ msg/s（单机）
         *   RocketMQ: 10万+ msg/s（单机）
         *   RabbitMQ: 1万-5万 msg/s（单机）
         *
         * 【延迟对比】
         *   Kafka: 毫秒级（取决于批次大小）
         *   RocketMQ: 毫秒级
         *   RabbitMQ: 毫秒级
         *
         * 【消息堆积能力】
         *   Kafka: 极强（基于磁盘，持久化）
         *   RocketMQ: 强（支持大消息堆积）
         *   RabbitMQ: 中等（内存有限制）
         *
         * 【Kafka 性能调优】
         *   // 增加吞吐量
         *   props.put("batch.size", 32768);     // 32KB 批量
         *   props.put("linger.ms", 5);           // 等待 5ms
         *   props.put("compression.type", "lz4"); // 压缩
         *   props.put("buffer.memory", 67108864); // 64MB 缓冲
         *
         *   // 降低延迟
         *   props.put("linger.ms", 0);          // 不等待
         *   props.put("batch.size", 0);          // 不批量
         *
         * 【RabbitMQ 性能调优】
         *   // 增加吞吐量
         *   channel.basicQos(100);  // 预取 100 条
         *   // 使用连接池复用连接
         */

        /*
         * --- 2. 性能维度对比 ---
         *
         * +------------------+------------+-------------+------------+
         * | MQ       | 吞吐量(msg/s) | 延迟 | 消息堆积 |
         * +------------------+------------+-------------+------------+
         * | Kafka    | 100万+      | 毫秒级      | 极强       |
         * | RocketMQ | 10万+       | 毫秒级      | 强         |
         * | RabbitMQ | 1万-5万     | 毫秒级      | 中         |
         * +------------------+------------+-------------+------------+
         *
         * 【消息积压计算】
         *   生产速率: 10000 msg/s
         *   消费速率: 8000 msg/s
         *   积压速率: 2000 msg/s
         *   1小时后积压: 7200000 条消息
         */
    }

    /**
     * 可靠性保障方案
     *
     * 【生产者端保障】
     *   Kafka:
     *     - acks=all：所有副本确认
     *     - retries > 0：重试机制
     *     - enable.idempotence=true：幂等性
     *
     *   RocketMQ:
     *     - 同步发送
     *     - 消息重试配置
     *     - 事务消息
     *
     *   RabbitMQ:
     *     - Publisher Confirm
     *     - 消息持久化
     *     - 备份交换器
     *
     * 【消费者端保障】
     *   Kafka:
     *     - 手动提交 offset
     *     - 幂等处理
     *
     *   RocketMQ:
     *     - 手动 ACK
     *     - 消费重试
     *
     *   RabbitMQ:
     *     - 手动确认
     *     - 死信队列
     *
     * 【幂等处理方案】
     *   - 去重表：基于业务唯一键
     *   - 状态机：基于状态变更
     * - Redis 缓存：短期去重
     */
    private void reliabilityGuarantee() {
        /*
         * --- 3. 可靠性保障方案 ---
         *
         * 【生产者端保障】
         *   Kafka:
         *     - acks=all：所有副本确认
         *     - retries > 0：重试机制
         *     - enable.idempotence=true：幂等性
         *
         *   RocketMQ:
         *     - 同步发送
         *     - 消息重试配置
         *     - 事务消息
         *
         *   RabbitMQ:
         *     - Publisher Confirm
         *     - 消息持久化
         *     - 备份交换器
         *
         * 【消费者端保障】
         *   Kafka:
         *     - 手动提交 offset
         *     - 幂等处理
         *
         *   RocketMQ:
         *     - 手动 ACK
         *     - 消费重试
         *
         *   RabbitMQ:
         *     - 手动确认
         *     - 死信队列
         *
         * 【代码示例 - Kafka 可靠发送】
         *   Properties props = new Properties();
         *   props.put("bootstrap.servers", "localhost:9092");
         *   props.put("key.serializer", "StringSerializer");
         *   props.put("value.serializer", "StringSerializer");
         *   props.put("acks", "all");           // 所有副本确认
         *   props.put("retries", 3);            // 重试 3 次
         *   props.put("retry.backoff.ms", 100); // 重试间隔
         *   props.put("enable.idempotence", true); // 幂等性
         *
         * 【代码示例 - RabbitMQ 可靠发送】
         *   channel.confirmSelect();  // 开启确认
         *   channel.addConfirmListener(...);  // 监听确认
         *   channel.basicPublish(exchange, routingKey, true, props, message);  // 持久化
         *
         * 【代码示例 - 消费者幂等处理】
         *   // 基于 Redis 去重
         *   String dedupKey = "msg:dedup:" + message.getId();
         *   Boolean existed = redis.setnx(dedupKey, "1");
         *   if (existed) {
         *       // 已处理，跳过
         *       return;
         *   }
         *   // 处理消息
         *   processMessage(message);
         *   redis.expire(dedupKey, 24 * 3600);  // 24小时过期
         */

        /*
         * --- 3. 可靠性保障方案 ---
         *
         * +----------+------------------+------------------+
         * | MQ       | 生产者保障       | 消费者保障       |
         * +----------+------------------+------------------+
         * | Kafka    | acks=all, 幂等   | 手动提交 offset  |
         * | RocketMQ | 同步发送, 事务   | 手动 ACK, 重试   |
         * | RabbitMQ | Publisher Confirm | 手动确认, 死信   |
         * +----------+------------------+------------------+
         *
         * 【消息丢失场景及对策】
         *   场景1: 网络抖动导致发送失败
         *     对策: 配置 retries > 0, retry.backoff.ms
         *   场景2: 消费者未处理完就提交 offset
         *     对策: 手动提交 offset, 处理成功后提交
         *   场景3: 消息未持久化就丢失
         *     对策: acks=all, 消息持久化
         */
    }

    /**
     * 选择建议
     *
     * 【选型决策树】
     *   日志收集/大数据流处理?
     *     ├─ 是 → Kafka（最佳选择）
     *     └─ 否 → 电商订单/金融交易?
     *             ├─ 是 → RocketMQ（事务消息支持）
     *             └─ 否 → 企业应用/复杂路由?
     *                     ├─ 是 → RabbitMQ（AMQP 协议）
     *                     └─ 否 → 根据具体需求选择
     *
     * 【具体场景推荐】
     *   - 日志分析、大数据流处理：选择 Kafka
     *   - 电商订单、金融交易：选择 RocketMQ
     *   - 企业级应用、复杂路由：选择 RabbitMQ
     *
     * 【多 MQ 共存场景】
     *   - 日志/监控 → Kafka
     *   - 订单/支付 → RocketMQ
     *   - 系统集成 → RabbitMQ
     *
     * 【技术栈匹配】
     *   - Java 为主：RocketMQ / Kafka
     *   - 多语言混合：Kafka / RabbitMQ
     * - 运维能力强：Kafka
     * - 运维能力弱：RocketMQ / RabbitMQ
     */
    private void selectionGuide() {
        /*
         * --- 4. 选择建议 ---
         *
         * 选型决策树：
         *   日志收集/大数据流处理?
         *     ├─ 是 → Kafka（最佳选择）
         *     └─ 否 → 电商订单/金融交易?
         *             ├─ 是 → RocketMQ（事务消息支持）
         *             └─ 否 → 企业应用/复杂路由?
         *                     ├─ 是 → RabbitMQ（AMQP 协议）
         *                     └─ 否 → 根据具体需求选择
         *
         * 具体场景推荐：
         *   - 日志分析、大数据流处理：选择 Kafka
         *   - 电商订单、金融交易：选择 RocketMQ
         *   - 企业级应用、复杂路由：选择 RabbitMQ
         *
         * 【代码示例 - 多 MQ 架构】
         *   // 日志系统
         *   Application → [Kafka: logs-topic] → ELK Stack
         *
         *   // 订单系统
         *   OrderService → [RocketMQ: order-topic] → InventoryService
         *                                                → PaymentService
         *
         *   // 通知系统
         *   NotificationService → [RabbitMQ: notification-exchange] → EmailQueue
         *                                                      → SMSQueue
         */

        /*
         * --- 4. 多 MQ 架构示例 ---
         *
         * 日志系统:
         *   Application → [Kafka: logs-topic] → ELK Stack
         * 订单系统:
         *   OrderService → [RocketMQ: order-topic] → InventoryService
         *                                              → PaymentService
         * 通知系统:
         *   NotificationService → [RabbitMQ: notification-exchange]
         *                        → EmailQueue
         *                        → SMSQueue
         */
    }

    /**
     * 使用注意事项
     *
     * 【核心注意事项】
     *   1. 消息顺序：确保消息发送到同一分区/队列
     *   2. 消息幂等：消费者端做好幂等处理
     *   3. 消息丢失：配置合适的确认机制
     *   4. 消息积压：监控队列深度，及时扩容
     *   5. 死信处理：配置死信队列处理失败消息
     *
     * 【常见问题】
     *   - 消息重复：生产者重试导致
     *   - 消息丢失：确认机制配置不当
     *   - 消费延迟：消费者处理慢
     *   - 消息积压：生产快、消费慢
     *
     * 【最佳实践】
     *   - 消息ID：每个消息携带唯一ID
     *   - 超时控制：设置消息超时时间
     * - 监控告警：监控生产和消费速率
     *   - 降级方案：异常情况下的处理策略
     *
     * 【运维建议】
     *   - 集群部署：多副本保证高可用
     *   - 容量规划：预估消息量，预留空间
     *   - 定期巡检：检查磁盘、内存使用
     *   - 灰度发布：新版本先灰度验证
     */
    private void usageNotes() {
        /*
         * --- 5. 使用注意事项 ---
         *
         * 核心注意事项：
         *   1. 消息顺序：确保消息发送到同一分区/队列
         *   2. 消息幂等：消费者端做好幂等处理
         *   3. 消息丢失：配置合适的确认机制
         *   4. 消息积压：监控队列深度，及时扩容
         *   5. 死信处理：配置死信队列处理失败消息
         *
         * 常见问题：
         *   - 消息重复：生产者重试导致
         *   - 消息丢失：确认机制配置不当
         *   - 消费延迟：消费者处理慢
         *   - 消息积压：生产快、消费慢
         *
         * 【代码示例 - 消息ID生成】
         *   String messageId = UUID.randomUUID().toString();
         *   // 或雪花算法
         *   long messageId = SnowflakeIdGenerator.nextId();
         *
         * 【代码示例 - 超时控制】
         *   // RocketMQ 延迟消息
         *   msg.setDelayTimeLevel(3);  // 10秒后超时
         *
         *   // RabbitMQ TTL
         *   AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
         *       .expiration("10000")  // 10秒
         *       .build();
         *
         * 【代码示例 - 监控指标】
         *   // Kafka 监控
         *   - under.replicated.partitions
         *   - isr.expands.rate
         *   - consumer.lag
         *
         *   // RocketMQ 监控
         *   - brokerTps
         *   - pullThreadNum
         *   - getMessageTransferedTime
         *
         *   // RabbitMQ 监控
         *   - queue.messages
         *   - queue.messages_ready
         *   - queue.messages_unacknowledged
         */

        /*
         * --- 5. 使用注意事项 ---
         *
         * 1. 消息顺序: 确保消息发送到同一分区/队列
         * 2. 消息幂等: 消费者端做好幂等处理
         * 3. 消息丢失: 配置合适的确认机制
         * 4. 消息积压: 监控队列深度，及时扩容
         * 5. 死信处理: 配置死信队列处理失败消息
         *
         * 【消息ID生成示例】
         *   UUID: 550e8400-e29b-41d4-a716-446655440000
         *   雪花算法: 1640995200000
         *
         * 【监控告警阈值】
         *   Kafka: consumer.lag > 10000 告警
         *   RocketMQ: queue.threads > 256 告警
         *   RabbitMQ: queue.messages > 100000 告警
         */
    }

    // ========== 辅助方法 ==========

    /**
     * 根据场景选择合适的 MQ
     *
     * @param scenario 场景描述
     * @return 推荐的 MQ 类型
     */
    public String selectMq(String scenario) {
        /*
         * 【选型逻辑】
         *   if (scenario.contains("日志") || scenario.contains("流处理")) {
         *       return "Kafka";
         *   }
         *   if (scenario.contains("订单") || scenario.contains("交易")) {
         *       return "RocketMQ";
         *   }
         *   if (scenario.contains("路由") || scenario.contains("集成")) {
         *       return "RabbitMQ";
         *   }
         *   return "根据具体需求评估";
         */
        return null;
    }

    /**
     * 计算消息积压数量
     *
     * @param produceRate 生产速率
     * @param consumeRate 消费速率
     * @return 积压数量
     */
    public long calculateBacklog(long produceRate, long consumeRate) {
        /*
         * 公式: backlog = produceRate - consumeRate
         *
         * 示例:
         *   produceRate = 10000 msg/s
         *   consumeRate = 8000 msg/s
         *   backlog = 2000 msg/s
         */
        return produceRate - consumeRate;
    }
}
