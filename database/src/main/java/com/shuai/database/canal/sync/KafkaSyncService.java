package com.shuai.database.canal.sync;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.shuai.database.canal.handler.RowDataHandler;
import com.shuai.database.canal.model.OrderChangeEvent;
import com.shuai.database.canal.model.UserChangeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Kafka 同步服务
 * 用于将 Canal 变更事件发送到 Kafka 消息队列
 *
 * 使用场景：
 * 1. 数据异构：MySQL -> Canal -> Kafka -> ES/HBase/Doris
 * 2. 业务解耦：数据变更 -> 消息队列 -> 多业务消费
 * 3. 事件驱动：基于数据变更触发业务流程
 *
 * 消息格式：
 * {
 *   "eventType": "INSERT",
 *   "tableName": "t_order",
 *   "data": {...},
 *   "timestamp": 1702867200000
 * }
 *
 * @author Shuai
 */
public class KafkaSyncService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaSyncService.class);

    // 模拟 Kafka topic 存储（实际使用请替换为 Kafka producer）
    private final Map<String, List<String>> topicStore = new ConcurrentHashMap<>();
    private final AtomicLong messageCount = new AtomicLong(0);

    private static final String ORDER_TOPIC = "canal-order-events";
    private static final String USER_TOPIC = "canal-user-events";
    private static final String DDL_TOPIC = "canal-ddl-events";

    private final ObjectMapper objectMapper = new ObjectMapper();
    {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 创建订单变更事件处理器
     */
    public RowDataHandler createOrderChangeHandler() {
        return (eventType, beforeColumns, afterColumns) -> {
            try {
                OrderChangeEvent event = parseOrderChangeEvent(eventType.name(), beforeColumns, afterColumns);
                String topic = ORDER_TOPIC;

                // DELETE 事件发送到删除topic
                if ("DELETE".equalsIgnoreCase(eventType.name())) {
                    topic = ORDER_TOPIC + "-delete";
                }

                String message = buildMessage(eventType.name(), "t_order", event);
                sendToTopic(topic, message);

                System.out.println("  [Kafka同步] 订单事件 -> " + topic);

            } catch (Exception e) {
                logger.error("发送订单事件到Kafka失败", e);
            }
        };
    }

    /**
     * 创建用户变更事件处理器
     */
    public RowDataHandler createUserChangeHandler() {
        return (eventType, beforeColumns, afterColumns) -> {
            try {
                UserChangeEvent event = parseUserChangeEvent(eventType.name(), beforeColumns, afterColumns);
                String topic = USER_TOPIC;

                // DELETE 事件发送到删除topic
                if ("DELETE".equalsIgnoreCase(eventType.name())) {
                    topic = USER_TOPIC + "-delete";
                }

                String message = buildMessage(eventType.name(), "t_user", event);
                sendToTopic(topic, message);

                System.out.println("  [Kafka同步] 用户事件 -> " + topic);

            } catch (Exception e) {
                logger.error("发送用户事件到Kafka失败", e);
            }
        };
    }

    /**
     * 创建 DDL 事件处理器
     */
    public RowDataHandler createDdlHandler() {
        return (eventType, beforeColumns, afterColumns) -> {
            try {
                String ddlSql = beforeColumns.isEmpty() ? "" : beforeColumns.get(0).getValue();
                String tableName = afterColumns.isEmpty() ? "" : afterColumns.get(0).getValue();

                String message = String.format(
                    "{\"eventType\":\"%s\",\"ddl\":\"%s\",\"tableName\":\"%s\",\"timestamp\":%d}",
                    eventType.name(), ddlSql, tableName, System.currentTimeMillis()
                );

                sendToTopic(DDL_TOPIC, message);
                System.out.println("  [Kafka同步] DDL事件 -> " + DDL_TOPIC);

            } catch (Exception e) {
                logger.error("发送DDL事件到Kafka失败", e);
            }
        };
    }

    /**
     * 构建消息内容
     */
    private String buildMessage(String eventType, String tableName, Object data) {
        try {
            Map<String, Object> message = Map.of(
                "eventType", eventType,
                "tableName", tableName,
                "data", data,
                "timestamp", System.currentTimeMillis()
            );
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            logger.error("构建消息失败", e);
            return "{}";
        }
    }

    /**
     * 发送消息到 topic
     */
    private void sendToTopic(String topic, String message) {
        topicStore.computeIfAbsent(topic, k -> new ArrayList<>()).add(message);
        messageCount.incrementAndGet();
        logger.debug("发送消息到 {}: {}", topic, message);
    }

    /**
     * 解析订单变更事件
     */
    private OrderChangeEvent parseOrderChangeEvent(String eventType,
                                                   List<CanalEntry.Column> beforeColumns,
                                                   List<CanalEntry.Column> afterColumns) {
        OrderChangeEvent event = new OrderChangeEvent();
        event.setEventType(eventType);

        List<CanalEntry.Column> columns = afterColumns.isEmpty() ? beforeColumns : afterColumns;

        for (CanalEntry.Column column : columns) {
            String name = column.getName().toLowerCase();
            String value = column.getValue();

            switch (name) {
                case "order_id" -> event.setOrderId(Long.parseLong(value));
                case "user_id" -> event.setUserId(Long.parseLong(value));
                case "amount" -> event.setAmount(new BigDecimal(value));
                case "status" -> event.setStatus(value);
                case "created_at" -> event.setCreatedAt(parseDateTime(value));
                case "updated_at" -> event.setUpdatedAt(parseDateTime(value));
            }
        }

        return event;
    }

    /**
     * 解析用户变更事件
     */
    private UserChangeEvent parseUserChangeEvent(String eventType,
                                                  List<CanalEntry.Column> beforeColumns,
                                                  List<CanalEntry.Column> afterColumns) {
        UserChangeEvent event = new UserChangeEvent();
        event.setEventType(eventType);

        List<CanalEntry.Column> columns = afterColumns.isEmpty() ? beforeColumns : afterColumns;

        for (CanalEntry.Column column : columns) {
            String name = column.getName().toLowerCase();
            String value = column.getValue();

            switch (name) {
                case "user_id" -> event.setUserId(Long.parseLong(value));
                case "username" -> event.setUsername(value);
                case "email" -> event.setEmail(value);
                case "phone" -> event.setPhone(value);
                case "status" -> event.setStatus(Integer.parseInt(value));
                case "created_at" -> event.setCreatedAt(parseDateTime(value));
                case "updated_at" -> event.setUpdatedAt(parseDateTime(value));
            }
        }

        return event;
    }

    /**
     * 获取消息统计
     */
    public Map<String, Object> getStats() {
        return Map.of(
            "totalMessages", messageCount.get(),
            "topics", topicStore.keySet().size(),
            "topicDetails", topicStore.entrySet().stream()
                .map(e -> Map.of("topic", e.getKey(), "count", e.getValue().size()))
                .toList()
        );
    }

    /**
     * 获取指定 topic 的消息（用于演示）
     */
    public List<String> getTopicMessages(String topic) {
        return topicStore.getOrDefault(topic, new ArrayList<>());
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return LocalDateTime.parse(value, FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
}
