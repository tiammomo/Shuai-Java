package com.shuai;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息队列对比演示类
 *
 * 【运行方式】
 *   1. 确保 Docker MQ 服务运行: docker-compose up -d
 *   2. 运行主方法
 *
 * 核心内容
 *   - Kafka、RocketMQ、RabbitMQ 对比
 *   - 性能维度对比
 *   - 可靠性保障方案
 *   - 选型建议
 *
 * @author Shuai
 */
public class MqComparisonDemo {

    public static void main(String[] args) {
        MqComparisonDemo demo = new MqComparisonDemo();
        try {
            demo.runAllDemos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runAllDemos() {
        System.out.println("=".repeat(60));
        System.out.println("           消息队列对比演示");
        System.out.println("=".repeat(60));

        // 1. 特性对比
        mqComparison();

        // 2. 性能对比
        performanceComparison();

        // 3. 可靠性保障
        reliabilityGuarantee();

        // 4. 选择建议
        selectionGuide();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("           消息队列对比演示完成");
        System.out.println("=".repeat(60));
    }

    /**
     * 消息队列特性对比
     */
    private void mqComparison() {
        System.out.println("\n=== 1. 主流消息队列特性对比 ===");

        printComparisonTable();

        demonstrateFeatureDifferences();
    }

    /**
     * 打印对比表格
     */
    private void printComparisonTable() {
        System.out.println("\n【特性对比表】");
        System.out.println("+------------------+---------------+---------------+---------------+");
        System.out.println("|     特性         |    Kafka      |   RocketMQ    |    RabbitMQ   |");
        System.out.println("+------------------+---------------+---------------+---------------+");
        System.out.println("|  开发语言        |    Scala      |     Java      |     Erlang    |");
        System.out.println("|  吞吐量          |    极高       |      高       |      中       |");
        System.out.println("|  延迟            |    低         |      低       |      低       |");
        System.out.println("|  可靠性          |    高         |      高       |      高       |");
        System.out.println("|  顺序消息        |    支持       |      支持     |      支持     |");
        System.out.println("|  事务消息        |    较弱       |      支持     |      支持     |");
        System.out.println("|  消息堆积        |    极强       |      强       |      中       |");
        System.out.println("|  社区活跃度      |    极高       |      高       |      高       |");
        System.out.println("|  适用场景        |   日志/流处理 |    电商/金融  |    企业应用   |");
        System.out.println("+------------------+---------------+---------------+---------------+");
    }

    /**
     * 演示各 MQ 的特性差异
     */
    private void demonstrateFeatureDifferences() {
        System.out.println("\n【架构特点对比】");

        System.out.println("\n  Kafka 特点:");
        System.out.println("    - 分布式架构，支持水平扩展");
        System.out.println("    - 基于日志追加，写入性能极高");
        System.out.println("    - 支持消息回溯（从任意 offset 开始消费）");
        demonstrateKafkaStyle();

        System.out.println("\n  RocketMQ 特点:");
        System.out.println("    - 借鉴 Kafka 设计，更适合业务消息");
        System.out.println("    - 支持事务消息和延迟消息");
        System.out.println("    - 支持消息过滤（Tag/SQL）");
        demonstrateRocketMQStyle();

        System.out.println("\n  RabbitMQ 特点:");
        System.out.println("    - 基于 AMQP 协议，灵活路由");
        System.out.println("    - 支持多种消息模式（Direct/Fanout/Topic/Headers）");
        System.out.println("    - 管理界面友好");
        demonstrateRabbitMQStyle();
    }

    /**
     * 性能维度对比
     */
    private void performanceComparison() {
        System.out.println("\n=== 2. 性能维度对比 ===");

        printPerformanceTable();

        demonstratePerformanceTuning();
    }

    /**
     * 打印性能对比表
     */
    private void printPerformanceTable() {
        System.out.println("\n【性能对比】");
        System.out.println("+------------------+------------------+------------------+------------------+");
        System.out.println("| MQ       | 吞吐量(msg/s) | 延迟 | 消息堆积 |");
        System.out.println("+------------------+------------------+------------------+------------------+");
        System.out.println("| Kafka    | 100万+        | 毫秒级 | 极强    |");
        System.out.println("| RocketMQ | 10万+         | 毫秒级 | 强      |");
        System.out.println("| RabbitMQ | 1万-5万       | 毫秒级 | 中      |");
        System.out.println("+------------------+------------------+------------------+------------------+");
    }

    /**
     * 演示性能调优配置
     */
    private void demonstratePerformanceTuning() {
        System.out.println("\n【性能调优配置示例】");

        // Kafka 性能调优
        System.out.println("\n  Kafka 性能调优:");
        Map<String, Object> kafkaProps = new HashMap<>();
        kafkaProps.put("batch.size", "32768");           // 32KB 批量
        kafkaProps.put("linger.ms", "5");                // 等待 5ms
        kafkaProps.put("compression.type", "lz4");        // 压缩
        kafkaProps.put("buffer.memory", "67108864");      // 64MB 缓冲
        kafkaProps.put("acks", "all");                    // 可靠性
        kafkaProps.put("retries", "3");                   // 重试

        System.out.println("    Properties props = new Properties();");
        kafkaProps.forEach((k, v) ->
            System.out.println("    props.put(\"" + k + "\", \"" + v + "\");"));

        // RabbitMQ 性能调优
        System.out.println("\n  RabbitMQ 性能调优:");
        System.out.println("    // 增加吞吐量");
        System.out.println("    channel.basicQos(100);  // 预取 100 条");
        System.out.println("    // 使用连接池复用连接");
    }

    /**
     * 可靠性保障方案
     */
    private void reliabilityGuarantee() {
        System.out.println("\n=== 3. 可靠性保障方案 ===");

        demonstrateReliabilityGuarantee();
    }

    /**
     * 演示可靠性保障
     */
    private void demonstrateReliabilityGuarantee() {
        System.out.println("\n【生产者端保障】");

        System.out.println("\n  Kafka:");
        System.out.println("    - acks=all：所有副本确认");
        System.out.println("    - retries > 0：重试机制");
        System.out.println("    - enable.idempotence=true：幂等性");

        System.out.println("\n  RocketMQ:");
        System.out.println("    - 同步发送");
        System.out.println("    - 消息重试配置");
        System.out.println("    - 事务消息");

        System.out.println("\n  RabbitMQ:");
        System.out.println("    - Publisher Confirm");
        System.out.println("    - 消息持久化");
        System.out.println("    - 备份交换器");

        System.out.println("\n【消费者端保障】");

        System.out.println("\n  Kafka:");
        System.out.println("    - 手动提交 offset");
        System.out.println("    - 幂等处理");

        System.out.println("\n  RocketMQ:");
        System.out.println("    - 手动 ACK");
        System.out.println("    - 消费重试");

        System.out.println("\n  RabbitMQ:");
        System.out.println("    - 手动确认");
        System.out.println("    - 死信队列");

        demonstrateIdempotentProcessing();
    }

    /**
     * 演示幂等处理
     */
    private void demonstrateIdempotentProcessing() {
        System.out.println("\n【幂等处理方案】");

        System.out.println("\n  1. 去重表方案:");
        System.out.println("    // 基于业务唯一键去重");
        System.out.println("    String dedupKey = \"msg:dedup:\" + message.getId();");
        System.out.println("    if (redis.setnx(dedupKey, \"1\")) {");
        System.out.println("        // 首次处理");
        System.out.println("        processMessage(message);");
        System.out.println("        redis.expire(dedupKey, 24 * 3600);");
        System.out.println("    } else {");
        System.out.println("        // 已处理，跳过");
        System.out.println("        return;");
        System.out.println("    }");

        System.out.println("\n  2. 状态机方案:");
        System.out.println("    // 基于状态变更去重");
        System.out.println("    if (order.getStatus() == OrderStatus.PAID) {");
        System.out.println("        return;  // 订单已支付，跳过");
        System.out.println("    }");
        System.out.println("    order.setStatus(OrderStatus.PAID);");
        System.out.println("    orderService.update(order);");
    }

    /**
     * 选择建议
     */
    private void selectionGuide() {
        System.out.println("\n=== 4. 选择建议 ===");

        demonstrateSelectionGuide();
    }

    /**
     * 演示选型决策
     */
    private void demonstrateSelectionGuide() {
        System.out.println("\n【选型决策树】");
        System.out.println("  日志收集/大数据流处理?");
        System.out.println("    ├─ 是 → Kafka（最佳选择）");
        System.out.println("    └─ 否 → 电商订单/金融交易?");
        System.out.println("            ├─ 是 → RocketMQ（事务消息支持）");
        System.out.println("            └─ 否 → 企业应用/复杂路由?");
        System.out.println("                    ├─ 是 → RabbitMQ（AMQP 协议）");
        System.out.println("                    └─ 否 → 根据具体需求选择");

        System.out.println("\n【具体场景推荐】");
        demonstrateScenarioRecommendation("日志收集", "Kafka");
        demonstrateScenarioRecommendation("大数据流处理", "Kafka");
        demonstrateScenarioRecommendation("电商订单", "RocketMQ");
        demonstrateScenarioRecommendation("金融交易", "RocketMQ");
        demonstrateScenarioRecommendation("企业应用", "RabbitMQ");
        demonstrateScenarioRecommendation("系统集成", "RabbitMQ");

        System.out.println("\n【多 MQ 共存架构】");
        System.out.println("  - 日志/监控 → Kafka");
        System.out.println("  - 订单/支付 → RocketMQ");
        System.out.println("  - 系统集成 → RabbitMQ");
    }

    private void demonstrateScenarioRecommendation(String scenario, String recommended) {
        String mq = selectMq(scenario);
        System.out.println("  " + scenario + ": " + mq + (mq.equals(recommended) ? " ✓" : ""));
    }

    /**
     * 根据场景选择合适的 MQ
     */
    public String selectMq(String scenario) {
        if (scenario.contains("日志") || scenario.contains("流处理") ||
            scenario.contains("监控") || scenario.contains("大数据")) {
            return "Kafka";
        }
        if (scenario.contains("订单") || scenario.contains("交易") ||
            scenario.contains("支付") || scenario.contains("金融")) {
            return "RocketMQ";
        }
        if (scenario.contains("路由") || scenario.contains("集成") ||
            scenario.contains("企业") || scenario.contains("通知")) {
            return "RabbitMQ";
        }
        return "根据具体需求评估";
    }

    /**
     * 计算消息积压数量
     */
    public long calculateBacklog(long produceRate, long consumeRate) {
        return produceRate - consumeRate;
    }

    // ========== 演示辅助方法 ==========

    private void demonstrateKafkaStyle() {
        System.out.println("\n    【Kafka 消息模型】");
        System.out.println("    Producer → [Partition 0] → Consumer Group A");
        System.out.println("              [Partition 1] → Consumer Group A");
        System.out.println("              [Partition 2] → Consumer Group B");
        System.out.println("    - 消息存储在分区内，按 offset 顺序消费");
        System.out.println("    - 消费者组内负载均衡");
    }

    private void demonstrateRocketMQStyle() {
        System.out.println("\n    【RocketMQ 消息模型】");
        System.out.println("    Producer → [Topic] → [Queue 0] → Consumer");
        System.out.println("                         [Queue 1] → Consumer");
        System.out.println("    - MessageQueue 水平扩展");
        System.out.println("    - 支持顺序消息（发往同一 Queue）");
    }

    private void demonstrateRabbitMQStyle() {
        System.out.println("\n    【RabbitMQ 消息模型】");
        System.out.println("    Producer → [Exchange] → [Queue 1] → Consumer");
        System.out.println("                            [Queue 2] → Consumer");
        System.out.println("    - Exchange 根据路由规则分发消息");
        System.out.println("    - 支持多种 Exchange 类型");
    }
}
