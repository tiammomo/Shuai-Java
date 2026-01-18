package com.shuai.database.canal;

import com.shuai.database.canal.client.CanalClientDemo;
import com.shuai.database.canal.sync.KafkaSyncService;
import com.shuai.database.canal.sync.RedisSyncService;

/**
 * Canal 数据同步演示
 *
 * 模块结构：
 * - config/       - 配置管理
 * - client/       - Canal 客户端连接
 * - handler/      - 事件处理器接口
 * - sync/         - 数据同步服务
 * - model/        - 事件数据模型
 *
 * 核心功能：
 * - MySQL binlog 订阅
 * - 实时数据增量同步
 * - 数据库变更监听
 * - 消息队列集成（Redis/Kafka/ES）
 *
 * 依赖说明：
 * - Maven: com.alibaba.otter:canal.client:1.1.7
 * - Maven: com.alibaba.otter:canal.protocol:1.1.7
 *
 * @author Shuai
 */
public class CanalDemo {

    // Canal Server 配置
    private static final String CANAL_HOST = "localhost";
    private static final int CANAL_PORT = 11111;
    private static final String DESTINATION = "example";
    private static final String SUBSER_FILTER = ".*\\..*";

    public void runAllDemos() throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Canal 数据同步演示");
        System.out.println("=".repeat(50));

        // 1. Canal 原理介绍
        canalPrinciple();

        // 2. 配置要求
        canalConfiguration();

        // 3. 客户端订阅示例
        canalClientDemo();

        // 4. 数据同步模式
        syncModes();

        // 5. 同步服务演示
        syncServiceDemo();

        System.out.println("\n[成功] Canal 演示完成！");
        System.out.println("\n[提示] 使用 Docker 启动完整环境:");
        System.out.println("  cd database/docker/canal");
        System.out.println("  docker-compose up -d");
    }

    /**
     * Canal 工作原理
     */
    private void canalPrinciple() {
        System.out.println("\n--- Canal 工作原理 ---");

        System.out.println("  架构图：");
        System.out.println("  +-------------+");
        System.out.println("  |   MySQL     |");
        System.out.println("  | (Master)    |");
        System.out.println("  +------+------+");
        System.out.println("         | Binlog");
        System.out.println("  +------v------+");
        System.out.println("  |   Canal     |");
        System.out.println("  |   Server    |");
        System.out.println("  +------+------+");
        System.out.println("         | TCP/HTTP");
        System.out.println("  +------v------+");
        System.out.println("  |  Client     |");
        System.out.println("  | (应用端)    |");
        System.out.println("  +-------------+");

        System.out.println("\n  工作流程：");
        System.out.println("  1. MySQL Master 产生 binlog");
        System.out.println("  2. Canal 模拟 MySQL slave 协议拉取 binlog");
        System.out.println("  3. Canal 解析 binlog，转换为可订阅的事件");
        System.out.println("  4. Client 订阅并消费变更事件");
        System.out.println("  5. 应用根据变更事件做相应处理");

        System.out.println("\n  支持的事件类型：");
        System.out.println("  - INSERT: 新增数据");
        System.out.println("  - UPDATE: 更新数据");
        System.out.println("  - DELETE: 删除数据");
        System.out.println("  - DDL: 表结构变更");
    }

    /**
     * Canal 配置要求
     */
    private void canalConfiguration() {
        System.out.println("\n--- Canal 配置要求 ---");

        System.out.println("\n  1. MySQL 配置：");
        System.out.println("     # 开启 binlog");
        System.out.println("     log-bin=mysql-bin");
        System.out.println("     binlog-format=ROW");
        System.out.println("     server-id=1");

        System.out.println("\n  2. 创建 Canal 账号：");
        System.out.println("     CREATE USER 'canal'@'%' IDENTIFIED BY 'canal';");
        System.out.println("     GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';");
        System.out.println("     FLUSH PRIVILEGES;");

        System.out.println("\n  3. Canal Server 配置 (conf/example/instance.properties)：");
        System.out.println("     canal.instance.master.address=127.0.0.1:3306");
        System.out.println("     canal.instance.dbUsername=canal");
        System.out.println("     canal.instance.dbPassword=canal");
    }

    /**
     * Canal 客户端演示
     */
    private void canalClientDemo() {
        System.out.println("\n--- Canal 客户端订阅示例 ---");

        System.out.println("  连接配置：");
        System.out.println("  - Host: " + CANAL_HOST);
        System.out.println("  - Port: " + CANAL_PORT);
        System.out.println("  - Destination: " + DESTINATION);

        System.out.println("\n  示例代码结构：");
        System.out.println("  ```java");
        System.out.println("  // 1. 创建连接");
        System.out.println("  CanalConnector connector = CanalConnectors.newSingleConnector(");
        System.out.println("      new InetSocketAddress(\"" + CANAL_HOST + "\", " + CANAL_PORT + "),");
        System.out.println("      \"" + DESTINATION + "\", \"\", \"\");");
        System.out.println("  ");
        System.out.println("  // 2. 订阅变更");
        System.out.println("  connector.subscribe(\"" + SUBSER_FILTER + "\");");
        System.out.println("  ");
        System.out.println("  // 3. 获取消息");
        System.out.println("  Message message = connector.get(100);");
        System.out.println("  ");
        System.out.println("  // 4. 解析 binlog");
        System.out.println("  for (Entry entry : message.getEntries()) {");
        System.out.println("      // 处理变更事件");
        System.out.println("  }");
        System.out.println("  ```");

        System.out.println("\n  数据变更示例：");
        System.out.println("  假设 MySQL 执行：UPDATE user SET age = 25 WHERE id = 1");
        System.out.println("  ");
        System.out.println("  Canal 事件内容：");
        System.out.println("  - Table: user");
        System.out.println("  - EventType: UPDATE");
        System.out.println("  - Before: {id: 1, name: '张三', age: 24}");
        System.out.println("  - After:  {id: 1, name: '张三', age: 25}");
    }

    /**
     * 数据同步模式
     */
    private void syncModes() {
        System.out.println("\n--- 数据同步模式 ---");

        System.out.println("\n  1. 同步缓存：");
        System.out.println("     binlog -> Canal -> Redis");
        System.out.println("     用途：保持缓存与数据库一致");

        System.out.println("\n  2. 跨库同步：");
        System.out.println("     MySQL(binlog) -> Canal -> ES/HBase");
        System.out.println("     用途：搜索、报表、大数据场景");

        System.out.println("\n  3. 业务解耦：");
        System.out.println("     binlog -> Canal -> MQ -> 多业务");
        System.out.println("     用途：多个下游系统订阅同一变更");

        System.out.println("\n  4. 数据订阅：");
        System.out.println("     binlog -> Canal -> 业务系统");
        System.out.println("     用途：监听数据变更，触发业务逻辑");

        System.out.println("\n  对比其它方案：");
        System.out.println("  +-------------+----------+-------------+-----------+");
        System.out.println("  |   方案      |  实时性  |   复杂度    |   适用场景 |");
        System.out.println("  +-------------+----------+-------------+-----------+");
        System.out.println("  |   Canal     |  秒级    |    中等     |  MySQL    |");
        System.out.println("  |   Debezium  |  秒级    |    中等     |  多数据库  |");
        System.out.println("  |   Maxwell   |  秒级    |    低       |  MySQL    |");
        System.out.println("  |   DataX     |  批量    |    低       |  离线同步  |");
        System.out.println("  +-------------+----------+-------------+-----------+");
    }

    /**
     * 同步服务演示
     */
    private void syncServiceDemo() {
        System.out.println("\n--- 同步服务演示 ---");

        // Redis 同步服务
        System.out.println("\n  [1] Redis 同步服务:");
        RedisSyncService redisSync = new RedisSyncService();
        System.out.println("    - createOrderChangeHandler(): 订单变更处理器");
        System.out.println("    - createUserChangeHandler(): 用户变更处理器");
        System.out.println("    - getStats(): 获取同步统计");

        // Kafka 同步服务
        System.out.println("\n  [2] Kafka 同步服务:");
        KafkaSyncService kafkaSync = new KafkaSyncService();
        System.out.println("    - createOrderChangeHandler(): 订单事件发送到 canal-order-events");
        System.out.println("    - createUserChangeHandler(): 用户事件发送到 canal-user-events");
        System.out.println("    - createDdlHandler(): DDL 事件发送到 canal-ddl-events");

        System.out.println("\n  [3] 集成示例:");
        System.out.println("    CanalClientDemo client = new CanalClientDemo();");
        System.out.println("    KafkaSyncService kafka = new KafkaSyncService();");
        System.out.println("    ");
        System.out.println("    // 注册事件处理器");
        System.out.println("    client.setEntryHandler(entry -> {");
        System.out.println("        // 自定义 Entry 处理逻辑");
        System.out.println("    });");
        System.out.println("    ");
        System.out.println("    // 运行客户端");
        System.out.println("    client.run();");
    }

    public static void main(String[] args) throws Exception {
        new CanalDemo().runAllDemos();
    }
}
