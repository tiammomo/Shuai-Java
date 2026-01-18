package com.shuai.database.mongodb;

import com.shuai.database.mongodb.aggregation.AggregationDemo;
import com.shuai.database.mongodb.basic.BasicOpsDemo;
import com.shuai.database.mongodb.index.IndexDemo;
import com.shuai.database.mongodb.query.QueryDemo;

/**
 * MongoDB 数据库模块入口类
 *
 * 模块结构：
 * - MongoDbConfig      - 配置管理
 * - MongoDbTemplate    - 操作模板
 * - basic/             - 基本操作
 * - query/             - 查询操作
 * - aggregation/       - 聚合管道
 * - index/             - 索引操作
 *
 * @author Shuai
 * @version 1.0
 */
public class MongoDbDemo {

    public void runAllDemos() {
        System.out.println("=".repeat(50));
        System.out.println("         MongoDB 模块");
        System.out.println("=".repeat(50));

        try {
            // 1. 基本操作演示
            BasicOpsDemo.run();

            // 2. 查询操作演示
            QueryDemo.run();

            // 3. 聚合管道演示
            AggregationDemo.run();

            // 4. 索引操作演示
            IndexDemo.run();

            System.out.println("\n" + "=".repeat(50));
            System.out.println("       MongoDB 模块演示完成！");
            System.out.println("=".repeat(50));
        } catch (Exception e) {
            System.err.println("MongoDB 演示失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭连接
            MongoDbConfig.shutdown();
        }
    }

    public static void main(String[] args) {
        new MongoDbDemo().runAllDemos();
    }
}
