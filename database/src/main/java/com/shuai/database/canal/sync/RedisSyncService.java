package com.shuai.database.canal.sync;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.shuai.database.canal.handler.RowDataHandler;
import com.shuai.database.canal.model.OrderChangeEvent;
import com.shuai.database.canal.model.UserChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis 同步服务
 * 用于将 Canal 变更事件同步到 Redis 缓存
 *
 * 使用场景：
 * 1. 订单状态变更时同步更新 Redis 缓存
 * 2. 用户信息变更时同步更新 Redis 缓存
 * 3. 商品库存变更时同步更新 Redis 缓存
 *
 * 同步策略：
 * - INSERT：新增缓存
 * - UPDATE：更新缓存
 * - DELETE：删除缓存
 *
 * @author Shuai
 */
public class RedisSyncService {

    private static final Logger logger = LoggerFactory.getLogger(RedisSyncService.class);

    // 模拟 Redis 存储（实际使用请替换为 Redis client）
    private final Map<String, Object> redisStore = new ConcurrentHashMap<>();

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 创建订单变更事件处理器
     */
    public RowDataHandler createOrderChangeHandler() {
        return (eventType, beforeColumns, afterColumns) -> {
            try {
                OrderChangeEvent event = parseOrderChangeEvent(eventType.name(), beforeColumns, afterColumns);

                switch (event.getEventType().toUpperCase()) {
                    case "INSERT" -> doInsertOrder(event);
                    case "UPDATE" -> doUpdateOrder(event, beforeColumns, afterColumns);
                    case "DELETE" -> doDeleteOrder(event);
                }

                System.out.println("  [Redis同步] " + event.getChangeSummary());

            } catch (Exception e) {
                logger.error("同步订单变更失败", e);
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

                switch (event.getEventType().toUpperCase()) {
                    case "INSERT" -> doInsertUser(event);
                    case "UPDATE" -> doUpdateUser(event, beforeColumns, afterColumns);
                    case "DELETE" -> doDeleteUser(event);
                }

                System.out.println("  [Redis同步] " + event.getChangeSummary());

            } catch (Exception e) {
                logger.error("同步用户变更失败", e);
            }
        };
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

    // INSERT 操作
    private void doInsertOrder(OrderChangeEvent event) {
        String key = event.toRedisHashKey();
        redisStore.put(key, event);
        logger.info("新增订单缓存: {}", key);
    }

    private void doInsertUser(UserChangeEvent event) {
        String key = event.toRedisHashKey();
        redisStore.put(key, event);
        logger.info("新增用户缓存: {}", key);
    }

    // UPDATE 操作
    private void doUpdateOrder(OrderChangeEvent event,
                               List<CanalEntry.Column> beforeColumns,
                               List<CanalEntry.Column> afterColumns) {
        String key = event.toRedisHashKey();
        if (redisStore.containsKey(key)) {
            // 更新变更的字段
            redisStore.put(key, event);
            logger.info("更新订单缓存: {}", key);
        }
    }

    private void doUpdateUser(UserChangeEvent event,
                              List<CanalEntry.Column> beforeColumns,
                              List<CanalEntry.Column> afterColumns) {
        String key = event.toRedisHashKey();
        if (redisStore.containsKey(key)) {
            // 更新变更的字段
            redisStore.put(key, event);
            logger.info("更新用户缓存: {}", key);
        }
    }

    // DELETE 操作
    private void doDeleteOrder(OrderChangeEvent event) {
        String key = event.toRedisHashKey();
        Object removed = redisStore.remove(key);
        if (removed != null) {
            logger.info("删除订单缓存: {}", key);
        }
    }

    private void doDeleteUser(UserChangeEvent event) {
        String key = event.toRedisHashKey();
        Object removed = redisStore.remove(key);
        if (removed != null) {
            logger.info("删除用户缓存: {}", key);
        }
    }

    /**
     * 获取缓存统计信息
     */
    public Map<String, Object> getStats() {
        return Map.of(
            "orderCount", redisStore.values().stream()
                .filter(o -> o instanceof OrderChangeEvent).count(),
            "userCount", redisStore.values().stream()
                .filter(o -> o instanceof UserChangeEvent).count(),
            "totalCount", redisStore.size()
        );
    }

    /**
     * 获取缓存内容（用于演示）
     */
    public Map<String, Object> getAllCache() {
        return new ConcurrentHashMap<>(redisStore);
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
