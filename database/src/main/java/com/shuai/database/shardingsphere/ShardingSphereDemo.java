package com.shuai.database.shardingsphere;

import com.shuai.database.shardingsphere.basic.PureJdbcDemo;
import com.shuai.database.shardingsphere.basic.ShardingBasicDemo;
import com.shuai.database.shardingsphere.readwrite.ReadWriteDemo;
import com.shuai.database.shardingsphere.sharding.TableShardingDemo;

/**
 * ShardingSphere 模块入口类
 *
 * 模块结构：
 * - ShardingConfig      - 配置管理
 * - basic/              - 基本 CRUD 操作
 * - readwrite/          - 读写分离
 * - sharding/           - 分库分表
 * - transaction/        - 分布式事务（待实现）
 *
 * 注意: ShardingSphere JDBC 5.4.1 与某些环境存在 snakeyaml 类冲突。
 *       如遇 "Representer: method 'void <init>()' not found" 错误：
 *       1. 使用 shaded jar: mvn package -pl database -DskipTests && java -jar database/target/database-1.0-SNAPSHOT.jar
 *       2. 或通过 mvn exec:java 运行（纯 JDBC 测试可正常工作）
 *
 * @author Shuai
 */
public class ShardingSphereDemo {

    public void runAllDemos() throws Exception {
        System.out.println("=".repeat(50));
        System.out.println("         ShardingSphere 模块");
        System.out.println("=".repeat(50));

        try {
            // 1. 纯 JDBC 测试 - 验证 MySQL 主从集群（无需 ShardingSphere）
            System.out.println("\n[INFO] 运行纯 JDBC 测试（验证 MySQL 主从集群）...");
            PureJdbcDemo.run();

            // 尝试运行 ShardingSphere 演示（可能因 snakeyaml 冲突失败）
            System.out.println("\n[INFO] 尝试运行 ShardingSphere 演示...");
            runShardingSphereDemos();

            System.out.println("\n" + "=".repeat(50));
            System.out.println("       ShardingSphere 模块演示完成！");
            System.out.println("=".repeat(50));
        } catch (Exception e) {
            System.err.println("\n[错误] ShardingSphere 演示失败: " + e.getMessage());
            System.err.println("提示: 请确保 MySQL 主从集群已启动 (端口 3307/3308/3309)");
            e.printStackTrace();
        }
    }

    private void runShardingSphereDemos() {
        try {
            // 2. 基本 CRUD 操作演示
            ShardingBasicDemo.run();

            // 3. 读写分离演示
            ReadWriteDemo.run();

            // 4. 分库分表演示
            TableShardingDemo.run();

        } catch (NoSuchMethodError e) {
            // snakeyaml 类冲突 - 这是已知问题
            System.err.println("\n[警告] ShardingSphere 初始化失败: " + e.getMessage());
            System.err.println("  这是 ShardingSphere 与 snakeyaml 的类冲突问题");
            System.err.println("  解决方案:");
            System.err.println("  1. 使用 maven shade 插件创建 shaded jar:");
            System.err.println("     mvn package -pl database -DskipTests");
            System.err.println("     java -jar database/target/database-1.0-SNAPSHOT.jar");
            System.err.println("  2. 纯 JDBC 演示已成功运行，可正常测试 MySQL 主从集群功能");
        } catch (Exception e) {
            System.err.println("\n[错误] ShardingSphere 演示失败: " + e.getMessage());
            System.err.println("  提示: 请确保 MySQL 主从集群已启动 (端口 3307/3308/3309)");
        }
    }

    public static void main(String[] args) throws Exception {
        new ShardingSphereDemo().runAllDemos();
    }
}
