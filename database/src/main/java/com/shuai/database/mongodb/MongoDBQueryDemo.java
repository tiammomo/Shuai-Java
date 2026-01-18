package com.shuai.database.mongodb;

/**
 * MongoDB 查询操作演示类
 *
 * 核心内容
 * ----------
 *   - 查询过滤器 (Filters)
 *   - 逻辑操作符
 *   - 投影 (Projections)
 *   - 数组查询
 *
 * @author Shuai
 * @version 1.0
 */
public class MongoDBQueryDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MongoDB 查询操作");
        System.out.println("=".repeat(50));

        queryFilters();
        logicalOperators();
        arrayQuery();
        projections();
    }

    /**
     * 查询过滤器
     */
    private void queryFilters() {
        System.out.println("\n--- 查询过滤器 (Filters) ---");

        System.out.println("  比较操作符:");
        System.out.println("    eq(field, value)    - 等于");
        System.out.println("    ne(field, value)    - 不等于");
        System.out.println("    gt(field, value)    - 大于");
        System.out.println("    gte(field, value)   - 大于等于");
        System.out.println("    lt(field, value)    - 小于");
        System.out.println("    lte(field, value)   - 小于等于");

        System.out.println("\n  列表操作符:");
        System.out.println("    in(field, list)     - 在列表中");
        System.out.println("    nin(field, list)    - 不在列表中");

        System.out.println("\n  字符串操作符:");
        System.out.println("    regex(pattern)      - 正则匹配");
        System.out.println("    regex(pattern, options) - 带选项的正则");

        System.out.println("\n  查询示例:");
        System.out.println("    Filters.eq(\"name\", \"张三\")");
        System.out.println("    Filters.gt(\"age\", 18)");
        System.out.println("    Filters.in(\"status\", Arrays.asList(\"active\", \"pending\"))");
        System.out.println("    Filters.regex(\"email\", \"@example\\.com$\")");
    }

    /**
     * 逻辑操作符
     */
    private void logicalOperators() {
        System.out.println("\n--- 逻辑操作符 ---");

        System.out.println("  AND (且):");
        System.out.println("    Filters.and(");
        System.out.println("        Filters.eq(\"status\", \"active\"),");
        System.out.println("        Filters.gt(\"age\", 18)");
        System.out.println("    )");

        System.out.println("\n  OR (或):");
        System.out.println("    Filters.or(");
        System.out.println("        Filters.eq(\"name\", \"张三\"),");
        System.out.println("        Filters.eq(\"name\", \"李四\")");
        System.out.println("    )");

        System.out.println("\n  NOT (非):");
        System.out.println("    Filters.not(Filters.eq(\"status\", \"deleted\"))");

        System.out.println("\n  NOR (都不):");
        System.out.println("    Filters.nor(");
        System.out.println("        Filters.eq(\"status\", \"active\"),");
        System.out.println("        Filters.eq(\"age\", 25)");
        System.out.println("    )");
    }

    /**
     * 数组查询
     */
    private void arrayQuery() {
        System.out.println("\n--- 数组查询 ---");

        System.out.println("  数组匹配:");
        System.out.println("    Filters.all(\"skills\", \"Java\")           // 包含所有元素");
        System.out.println("    Filters.size(\"tags\", 3)                  // 数组长度为 3");
        System.out.println("    Filters.size(\"tags\", 0)                  // 空数组");
        System.out.println("    Filters.elemMatch(\"scores\",               // 元素匹配");
        System.out.println("        Filters.gt(\"score\", 90))");

        System.out.println("\n  数组元素查询:");
        System.out.println("    Filters.gt(\"scores.0\", 90)               // 第一个元素 > 90");
        System.out.println("    Filters.gt(\"scores.$[\"]\", 90)            // 任意元素 > 90");

        System.out.println("\n  嵌套文档数组:");
        System.out.println("    // comments 数组包含 author 为 \"张三\" 的文档");
        System.out.println("    Filters.eq(\"comments.author\", \"张三\")");
    }

    /**
     * 投影
     */
    private void projections() {
        System.out.println("\n--- 投影 (Projections) ---");

        System.out.println("  包含字段:");
        System.out.println("    Projections.include(\"name\", \"age\")");
        System.out.println("    Projections.include(\"address.city\")");

        System.out.println("\n  排除字段:");
        System.out.println("    Projections.exclude(\"password\")");
        System.out.println("    Projections.exclude(\"address.zipcode\")");

        System.out.println("\n  包含 _id 和指定字段:");
        System.out.println("    Projections.fields(");
        System.out.println("        Projections.include(\"name\", \"age\"),");
        System.out.println("        Projections.excludeId()");
        System.out.println("    )");

        System.out.println("\n  数组切片:");
        System.out.println("    Projections.slice(\"comments\", 5)          // 只返回前 5 个");
        System.out.println("    Projections.slice(\"comments\", 10, 5)      // 跳过 10 个，返回 5 个");
    }
}
