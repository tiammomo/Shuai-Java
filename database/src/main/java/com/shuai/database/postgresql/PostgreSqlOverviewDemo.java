package com.shuai.database.postgresql;

/**
 * PostgreSQL 概述
 */
public class PostgreSqlOverviewDemo {

    public void runAllDemos() {
        System.out.println("\n--- PostgreSQL 概述 ---");

        System.out.println("""
            PostgreSQL 特点:
            - 功能强大的开源关系型数据库
            - 支持复杂查询、外键、触发器、视图
            - 丰富的可扩展性 (Extension)
            - 完全 ACID 特性

            相比 MySQL 的优势:
            - 更强的复杂查询能力
            - 更好的数据完整性
            - 丰富的 JSON/JSONB 支持
            - 原生全文搜索
            - 窗口函数、CTE

            数据类型:
            - 数值: INT, BIGINT, DECIMAL, NUMERIC
            - 字符串: VARCHAR, TEXT, CHAR
            - 日期/时间: DATE, TIME, TIMESTAMP, INTERVAL
            - JSON/JSONB: JSON 数据类型
            - ARRAY: 数组类型
            - 自定义类型
            """);
    }
}
