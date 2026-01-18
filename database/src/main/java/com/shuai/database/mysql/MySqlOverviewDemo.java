package com.shuai.database.mysql;

/**
 * MySQL 基础概念和特性演示
 */
public class MySqlOverviewDemo {

    public void runAllDemos() {
        System.out.println("\n--- MySQL 概述 ---");

        System.out.println("""
            MySQL 特点:
            - 开源关系型数据库
            - 最流行的 Web 数据库
            - 成熟的存储引擎架构
            - 丰富的生态系统

            常用存储引擎:
            - InnoDB: 默认，支持事务、外键、行级锁
            - MyISAM: 不支持事务，表级锁
            - Memory: 内存存储

            数据类型:
            - 数值: INT, BIGINT, DECIMAL, DOUBLE
            - 字符串: VARCHAR, TEXT, CHAR
            - 日期: DATE, DATETIME, TIMESTAMP
            """);
    }
}
