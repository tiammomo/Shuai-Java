package com.shuai.database.mongodb;

/**
 * MongoDB 聚合操作演示类
 *
 * 核心内容
 * ----------
 *   - 聚合管道 (Aggregation Pipeline)
 *   - 聚合阶段：$match, $group, $sort 等
 *   - 分组统计
 *   - $lookup 连接
 *
 * @author Shuai
 * @version 1.0
 */
public class MongoDBAggregationDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MongoDB 聚合操作");
        System.out.println("=".repeat(50));

        aggregationPipeline();
        groupingStatistics();
        lookupJoin();
        advancedAggregation();
    }

    /**
     * 聚合管道
     */
    private void aggregationPipeline() {
        System.out.println("\n--- 聚合管道 (Aggregation Pipeline) ---");

        System.out.println("  聚合管道概念:");
        System.out.println("    文档依次经过多个处理阶段");
        System.out.println("    每个阶段的输出作为下一阶段的输入");
        System.out.println("    支持排序、过滤、分组、计算等操作");

        System.out.println("\n  常用聚合阶段:");
        System.out.println("    $match   - 过滤文档（应在最前，减少数据量）");
        System.out.println("    $project - 选择字段、重命名、计算");
        System.out.println("    $group   - 分组统计");
        System.out.println("    $sort    - 排序");
        System.out.println("    $limit   - 限制数量");
        System.out.println("    $skip    - 跳过数量");
        System.out.println("    $unwind  - 展开数组");
        System.out.println("    $lookup  - 连接其他集合");
        System.out.println("    $addFields - 添加字段");
        System.out.println("    $replaceRoot - 替换根文档");

        System.out.println("\n  聚合示例:");
        System.out.println("    Arrays.asList(");
        System.out.println("        Aggregates.match(Filters.gte(\"createdAt\", startDate)),");
        System.out.println("        Aggregates.group(\"$status\",");
        System.out.println("            Accumulators.count(\"count\", 1),");
        System.out.println("            Accumulators.avg(\"avgAge\", \"$age\")");
        System.out.println("        )");
        System.out.println("    )");
    }

    /**
     * 分组统计
     */
    private void groupingStatistics() {
        System.out.println("\n--- 分组统计 ---");

        System.out.println("  分组操作符:");
        System.out.println("    $sum  - 求和");
        System.out.println("    $avg  - 平均值");
        System.out.println("    $min  - 最小值");
        System.out.println("    $max  - 最大值");
        System.out.println("    $first - 第一个值");
        System.out.println("    $last - 最后一个值");
        System.out.println("    $push - 收集到数组");
        System.out.println("    $addToSet - 收集到集合（不重复）");

        System.out.println("\n  分组示例:");
        System.out.println("    // 按状态分组统计");
        System.out.println("    new Document(\"$group\",");
        System.out.println("        new Document(\"_id\", \"$status\")");
        System.out.println("            .append(\"count\", new Document(\"$sum\", 1))");
        System.out.println("            .append(\"avgAge\", new Document(\"$avg\", \"$age\"))");
        System.out.println("    )");

        System.out.println("\n  多字段分组:");
        System.out.println("    new Document(\"$group\",");
        System.out.println("        new Document(\"_id\",");
        System.out.println("            new Document(\"status\", \"$status\")");
        System.out.println("                .append(\"city\", \"$city\"))");
        System.out.println("    )");
    }

    /**
     * $lookup 连接
     */
    private void lookupJoin() {
        System.out.println("\n--- $lookup 连接 ---");

        System.out.println("  基本语法:");
        System.out.println("    {");
        System.out.println("      $lookup: {");
        System.out.println("        from: \"orders\",          // 连接的集合");
        System.out.println("        localField: \"userId\",    // 本地字段");
        System.out.println("        foreignField: \"userId\",  // 外部字段");
        System.out.println("        as: \"orders\"            // 输出字段名");
        System.out.println("      }");
        System.out.println("    }");

        System.out.println("\n  管道连接（复杂查询）:");
        System.out.println("    {");
        System.out.println("      $lookup: {");
        System.out.println("        from: \"orders\",");
        System.out.println("        let: { userId: \"$userId\" },");
        System.out.println("        pipeline: [");
        System.out.println("          { $match: { $expr: { $eq: [\"$userId\", \"$$userId\"] } } },");
        System.out.println("          { $sort: { createdAt: -1 } },");
        System.out.println("          { $limit: 5 }");
        System.out.println("        ],");
        System.out.println("        as: \"recentOrders\"");
        System.out.println("      }");
        System.out.println("    }");
    }

    /**
     * 高级聚合
     */
    private void advancedAggregation() {
        System.out.println("\n--- 高级聚合 ---");

        System.out.println("  $unwind 展开数组:");
        System.out.println("    { $unwind: \"$tags\" }");
        System.out.println("    // 每个文档的 tags 数组元素展开成多个文档");

        System.out.println("\n  $facet 多管道并行:");
        System.out.println("    {");
        System.out.println("      $facet: {");
        System.out.println("        \"byStatus\": [ { $match: { status: \"active\" } } ],");
        System.out.println("        \"totalCount\": [ { $count: \"total\" } ],");
        System.out.println("        \"topCities\": [");
        System.out.println("          { $group: { _id: \"$city\", count: { $sum: 1 } } },");
        System.out.println("          { $sort: { count: -1 } },");
        System.out.println("          { $limit: 5 }");
        System.out.println("        ]");
        System.out.println("      }");
        System.out.println("    }");

        System.out.println("\n  Accumulator 使用:");
        System.out.println("    // 使用 Accumulator");
        System.out.println("    Accumulators.sum(\"total\", \"$amount\")");
        System.out.println("    Accumulators.avg(\"average\", \"$score\")");
        System.out.println("    Accumulators.push(\"items\", \"$name\")");
    }
}
