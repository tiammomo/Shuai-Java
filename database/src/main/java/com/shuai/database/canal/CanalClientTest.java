package com.shuai.database.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.shuai.database.canal.handler.RowDataHandler;
import com.shuai.database.canal.model.OrderChangeEvent;
import com.shuai.database.canal.model.UserChangeEvent;
import com.shuai.database.canal.sync.KafkaSyncService;
import com.shuai.database.canal.sync.RedisSyncService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Canal 模块测试类
 *
 * 测试内容：
 * 1. Canal 客户端连接测试
 * 2. 事件处理器测试
 * 3. 数据模型转换测试
 * 4. 同步服务测试
 *
 * 使用前请确保 Docker 环境已启动：
 * cd database/docker/canal && docker-compose up -d
 *
 * @author Shuai
 */
public class CanalClientTest {

    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(50));
        System.out.println("       Canal 模块测试");
        System.out.println("=".repeat(50));

        // 检查 Docker 环境
        checkDockerEnvironment();

        // 测试数据模型
        testDataModels();

        // 测试同步服务
        testSyncServices();

        // 测试事件处理器
        testEventHandlers();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("       测试完成！");
        System.out.println("=".repeat(50));
    }

    /**
     * 检查 Docker 环境
     */
    private static void checkDockerEnvironment() {
        System.out.println("\n--- Docker 环境检查 ---");
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"docker", "ps", "--format", "{{.Names}}"});
            p.waitFor();
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(p.getInputStream()));
            String line;
            boolean hasMysql = false;
            boolean hasCanal = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("canal-mysql")) hasMysql = true;
                if (line.contains("canal-server")) hasCanal = true;
            }
            if (hasMysql && hasCanal) {
                System.out.println("  [OK] MySQL 和 Canal Server 均已运行");
            } else {
                System.out.println("  [WARN] 部分服务未运行，请执行: cd database/docker/canal && docker-compose up -d");
            }
        } catch (Exception e) {
            System.out.println("  [INFO] 无法检查 Docker 环境，请确保 MySQL 和 Canal 已启动");
        }
    }

    /**
     * 测试数据模型
     */
    private static void testDataModels() {
        System.out.println("\n--- 数据模型测试 ---");

        // 测试 OrderChangeEvent
        OrderChangeEvent orderEvent = new OrderChangeEvent();
        orderEvent.setOrderId(1001L);
        orderEvent.setUserId(1L);
        orderEvent.setAmount(new BigDecimal("199.99"));
        orderEvent.setStatus("PAID");
        orderEvent.setEventType("INSERT");

        System.out.println("  OrderChangeEvent:");
        System.out.println("    " + orderEvent);
        System.out.println("    Redis Key: " + orderEvent.toRedisHashKey());
        System.out.println("    Change Summary: " + orderEvent.getChangeSummary());
        System.out.println("    Event Type: " + orderEvent.getEventTypeDisplay());

        // 测试 UserChangeEvent
        UserChangeEvent userEvent = new UserChangeEvent();
        userEvent.setUserId(1L);
        userEvent.setUsername("testuser");
        userEvent.setEmail("test@example.com");
        userEvent.setStatus(1);
        userEvent.setEventType("UPDATE");

        System.out.println("\n  UserChangeEvent:");
        System.out.println("    userId: " + userEvent.getUserId());
        System.out.println("    username: " + userEvent.getUsername());
        System.out.println("    eventType: " + userEvent.getEventTypeDisplay());
    }

    /**
     * 测试同步服务
     */
    private static void testSyncServices() throws Exception {
        System.out.println("\n--- 同步服务测试 ---");

        // Redis 同步服务测试
        System.out.println("\n  [1] Redis 同步服务测试:");
        RedisSyncService redisSync = new RedisSyncService();

        RowDataHandler redisHandler = redisSync.createOrderChangeHandler();
        // 模拟 INSERT 事件
        simulateInsertEvent(redisHandler);
        // 模拟 UPDATE 事件
        simulateUpdateEvent(redisHandler);
        // 模拟 DELETE 事件
        simulateDeleteEvent(redisHandler);

        var redisStats = redisSync.getStats();
        System.out.println("    Redis 同步统计: " + redisStats);

        // Kafka 同步服务测试
        System.out.println("\n  [2] Kafka 同步服务测试:");
        KafkaSyncService kafkaSync = new KafkaSyncService();

        RowDataHandler kafkaHandler = kafkaSync.createOrderChangeHandler();
        simulateInsertEvent(kafkaHandler);

        RowDataHandler userHandler = kafkaSync.createUserChangeHandler();
        simulateUserInsertEvent(userHandler);

        var kafkaStats = kafkaSync.getStats();
        System.out.println("    Kafka 同步统计: " + kafkaStats);
    }

    /**
     * 测试事件处理器
     */
    private static void testEventHandlers() {
        System.out.println("\n--- 事件处理器测试 ---");

        System.out.println("  [1] EntryHandler 函数式接口测试");
        CanalEntry.Entry mockEntry = CanalEntry.Entry.newBuilder()
            .setHeader(CanalEntry.Header.newBuilder()
                .setTableName("t_order")
                .setEventType(CanalEntry.EventType.INSERT)
                .build())
            .build();

        System.out.println("    Mock Entry created: " + mockEntry.getHeader().getTableName());

        System.out.println("  [2] RowDataHandler 事件类型测试");
        for (CanalEntry.EventType type : CanalEntry.EventType.values()) {
            System.out.println("    支持事件类型: " + type.name());
        }
    }

    /**
     * 创建 Column
     */
    private static CanalEntry.Column createColumn(String name, String value, int sqlType) {
        return CanalEntry.Column.newBuilder()
            .setName(name)
            .setValue(value)
            .setSqlType(sqlType)
            .build();
    }

    /**
     * 模拟 INSERT 事件
     */
    private static void simulateInsertEvent(RowDataHandler handler) {
        List<CanalEntry.Column> afterColumns = new ArrayList<>();
        afterColumns.add(createColumn("order_id", "1001", java.sql.Types.BIGINT));
        afterColumns.add(createColumn("user_id", "1", java.sql.Types.BIGINT));
        afterColumns.add(createColumn("amount", "199.99", java.sql.Types.DECIMAL));
        afterColumns.add(createColumn("status", "PAID", java.sql.Types.VARCHAR));

        handler.handle(CanalEntry.EventType.INSERT, new ArrayList<>(), afterColumns);
        System.out.println("    [模拟] INSERT 事件已处理");
    }

    /**
     * 模拟 UPDATE 事件
     */
    private static void simulateUpdateEvent(RowDataHandler handler) {
        List<CanalEntry.Column> beforeColumns = new ArrayList<>();
        beforeColumns.add(createColumn("status", "PENDING", java.sql.Types.VARCHAR));

        List<CanalEntry.Column> afterColumns = new ArrayList<>();
        afterColumns.add(createColumn("status", "PAID", java.sql.Types.VARCHAR));

        handler.handle(CanalEntry.EventType.UPDATE, beforeColumns, afterColumns);
        System.out.println("    [模拟] UPDATE 事件已处理");
    }

    /**
     * 模拟 DELETE 事件
     */
    private static void simulateDeleteEvent(RowDataHandler handler) {
        List<CanalEntry.Column> beforeColumns = new ArrayList<>();
        beforeColumns.add(createColumn("order_id", "1001", java.sql.Types.BIGINT));

        handler.handle(CanalEntry.EventType.DELETE, beforeColumns, new ArrayList<>());
        System.out.println("    [模拟] DELETE 事件已处理");
    }

    /**
     * 模拟用户 INSERT 事件
     */
    private static void simulateUserInsertEvent(RowDataHandler handler) {
        List<CanalEntry.Column> afterColumns = new ArrayList<>();
        afterColumns.add(createColumn("user_id", "1", java.sql.Types.BIGINT));
        afterColumns.add(createColumn("username", "testuser", java.sql.Types.VARCHAR));
        afterColumns.add(createColumn("email", "test@example.com", java.sql.Types.VARCHAR));

        handler.handle(CanalEntry.EventType.INSERT, new ArrayList<>(), afterColumns);
        System.out.println("    [模拟] 用户 INSERT 事件已处理");
    }
}
