package com.shuai.database.mongodb;

/**
 * MongoDB 核心概念演示类
 *
 * 核心内容
 * ----------
 *   - MongoDB 概述与特点
 *   - 文档模型与 BSON 类型
 *   - 核心概念：Database、Collection、Document
 *   - MongoDB vs 关系型数据库
 *
 * @author Shuai
 * @version 1.0
 */
public class MongoDBOverviewDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MongoDB 核心概念");
        System.out.println("=".repeat(50));

        mongoDBOverview();
        documentModel();
        coreConcepts();
        vsRelationalDB();
    }

    /**
     * MongoDB 概述
     */
    private void mongoDBOverview() {
        System.out.println("\n--- 什么是 MongoDB？ ---");
        System.out.println("  MongoDB 是一个基于分布式文件存储的 NoSQL 数据库");
        System.out.println("  由 C++ 语言编写，旨在为 WEB 应用提供可扩展的高性能数据存储解决方案");

        System.out.println("\n--- 核心特点 ---");
        System.out.println("  - 文档型存储：使用 BSON 格式存储数据");
        System.out.println("  - 灵活模式：文档结构可以动态变化");
        System.out.println("  - 高可扩展：支持分片集群");
        System.out.println("  - 高性能：支持索引、内存计算");
        System.out.println("  - 强一致性：支持事务");
        System.out.println("  - 丰富的查询功能：支持聚合、文本搜索、地理空间查询");

        System.out.println("\n--- 应用场景 ---");
        System.out.println("  - 内容管理系统");
        System.out.println("  - 移动应用后端");
        System.out.println("  - 实时分析");
        System.out.println("  - 用户行为追踪");
        System.out.println("  - 日志存储");
    }

    /**
     * 文档模型
     */
    private void documentModel() {
        System.out.println("\n--- 文档模型 ---");

        System.out.println("  文档结构示例:");
        System.out.println("    {");
        System.out.println("      \"_id\": ObjectId(\"...\"),");
        System.out.println("      \"name\": \"张三\",");
        System.out.println("      \"age\": 25,");
        System.out.println("      \"email\": \"zhangsan@example.com\",");
        System.out.println("      \"skills\": [\"Java\", \"Python\", \"MongoDB\"],");
        System.out.println("      \"address\": {");
        System.out.println("        \"city\": \"北京\",");
        System.out.println("        \"district\": \"海淀区\"");
        System.out.println("      },");
        System.out.println("      \"createdAt\": ISODate(\"2024-01-01\")");
        System.out.println("    }");

        System.out.println("\n  BSON 数据类型:");
        System.out.println("    String, Integer, Long, Double, Boolean");
        System.out.println("    Date, ObjectId, Binary, Regex");
        System.out.println("    Array, Document (嵌套文档)");

        System.out.println("\n  嵌套文档:");
        System.out.println("    文档可以包含其他文档，形成树形结构");
        System.out.println("    支持数组、多级嵌套");
        System.out.println("    适合存储复杂的数据结构");
    }

    /**
     * 核心概念
     */
    private void coreConcepts() {
        System.out.println("\n--- 核心概念 ---");

        System.out.println("  Database（数据库）:");
        System.out.println("    - MongoDB 中最大的数据存储单位");
        System.out.println("    - 每个数据库有独立的权限控制");
        System.out.println("    - 物理上，数据存储在不同的文件中");

        System.out.println("\n  Collection（集合）:");
        System.out.println("    - 相当于关系型数据库的表");
        System.out.println("    - 存储一组相关的文档");
        System.out.println("    - 集合中的文档可以有不同的结构");
        System.out.println("    - 不需要预先定义 schema");

        System.out.println("\n  Document（文档）:");
        System.out.println("    - MongoDB 中的基本数据单元");
        System.out.println("    - 相当于关系型数据库的行");
        System.out.println("    - 使用 BSON 格式存储");
        System.out.println("    - 每个文档有一个唯一的 _id 字段");

        System.out.println("\n  索引（Index）:");
        System.out.println("    - 提高查询效率");
        System.out.println("    - 支持单字段、复合、文本、地理空间索引");
        System.out.println("    - 索引会占用额外的存储空间");
    }

    /**
     * MongoDB vs 关系型数据库
     */
    private void vsRelationalDB() {
        System.out.println("\n--- MongoDB vs 关系型数据库 ---");
        System.out.println("+------------------+----------------------------+----------------------------+");
        System.out.println("|     特性         |        MongoDB             |     关系型数据库           |");
        System.out.println("+------------------+----------------------------+----------------------------+");
        System.out.println("|  数据模型        |        文档型               |        表结构              |");
        System.out.println("|  Schema          |        灵活/无              |        固定                |");
        System.out.println("|  查询语言        |        JSON-like           |        SQL                 |");
        System.out.println("|  事务支持        |        单集合事务          |        多表事务            |");
        System.out.println("|  扩展性          |        水平扩展            |        垂直扩展            |");
        System.out.println("|  关联查询        |        有限（$lookup）     |        强大（JOIN）        |");
        System.out.println("+------------------+----------------------------+----------------------------+");

        System.out.println("\n  选择建议:");
        System.out.println("    - 灵活 schema、高并发读、JSON 数据：选择 MongoDB");
        System.out.println("    - 复杂关联查询、强一致性、ACID 事务：选择关系型数据库");
        System.out.println("    - 也可以混合使用，各取所长");
    }
}
