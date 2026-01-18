package com.shuai.database.postgresql;

import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.*;

/**
 * PostgreSQL JSON/JSONB 支持演示
 */
public class PostgreSqlJsonDemo {

    private static final String URL = "jdbc:postgresql://localhost:5432/test";
    private static final String USER = "ubuntu";
    private static final String PASSWORD = "ubuntu";

    public void runAllDemos() {
        System.out.println("\n--- PostgreSQL JSON/JSONB 操作演示 ---");

        try {
            jsonVsJsonbDemo();
            jsonbOperatorsDemo();
            jsonbContainmentDemo();
            jsonbIndexDemo();
            jsonFunctionsDemo();
            jsonbUpdateDemo();
        } catch (Exception e) {
            System.err.println("JSON 操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * JSON vs JSONB 对比演示
     */
    private void jsonVsJsonbDemo() throws SQLException {
        System.out.println("\n[1] JSON vs JSONB 对比");

        // 创建测试表
        String createSql = """
            CREATE TABLE IF NOT EXISTS json_demo (
                id SERIAL PRIMARY KEY,
                json_data JSON,
                jsonb_data JSONB
            )
            """;
        String insertSql = "INSERT INTO json_demo (json_data, jsonb_data) VALUES (?, ?::jsonb)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createSql);
        }

        String jsonString = """
            {"name": "John", "age": 30, "city": "New York", "hobbies": ["reading", "coding"]}
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // 使用 ::json 和 ::jsonb 类型转换
            stmt.execute("INSERT INTO json_demo (json_data, jsonb_data) VALUES ('" + jsonString.replace("'", "''") + "'::json, '" + jsonString.replace("'", "''") + "'::jsonb)");
            System.out.println("  插入 JSON 和 JSONB 数据成功");
        }

        // 查询对比
        String selectSql = """
            SELECT
                json_data,
                jsonb_data,
                json_typeof(json_data) as json_type,
                jsonb_typeof(jsonb_data) as jsonb_type
            FROM json_demo
            ORDER BY id DESC
            LIMIT 1
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            while (rs.next()) {
                System.out.println("  JSON 类型: " + rs.getString("json_type"));
                System.out.println("  JSONB 类型: " + rs.getString("jsonb_type"));
                System.out.println("  JSONB 数据: " + rs.getString("jsonb_data"));
            }
        }
    }

    /**
     * JSONB 操作符演示
     */
    private void jsonbOperatorsDemo() throws SQLException {
        System.out.println("\n[2] JSONB 操作符演示");

        // -> 获取 JSON 对象（返回 JSON 类型）
        String operatorArrowSql = """
            SELECT
                data->'name' as name_json,
                data->'age' as age_json,
                data->>'name' as name_text,
                data->>'age' as age_text
            FROM (
                SELECT '{\"name\": \"Alice\", \"age\": 25, \"address\": {\"city\": \"Beijing\"}}'::jsonb as data
            ) t
            """;

        printQueryResult(operatorArrowSql, "-> 和 ->> 操作符");

        // -> 获取嵌套对象
        String nestedSql = """
            SELECT
                data->'address' as address_obj,
                data->'address'->>'city' as city
            FROM (
                SELECT '{\"name\": \"Bob\", \"address\": {\"city\": \"Shanghai\", \"district\": \"Pudong\"}}'::jsonb as data
            ) t
            """;

        printQueryResult(nestedSql, "嵌套对象访问");

        // 获取数组元素
        String arraySql = """
            SELECT
                data->'tags' as tags_array,
                data->'tags'->0 as first_tag,
                data->'tags'->>-1 as last_tag
            FROM (
                SELECT '{\"name\": \"Charlie\", \"tags\": [\"java\", \"python\", \"go\"]}'::jsonb as data
            ) t
            """;

        printQueryResult(arraySql, "数组元素访问");
    }

    /**
     * JSONB 包含演示
     */
    private void jsonbContainmentDemo() throws SQLException {
        System.out.println("\n[3] JSONB 包含操作符演示");

        // 使用文章表的 tags 数组
        String containmentSql = """
            SELECT id, title, tags
            FROM articles
            WHERE tags @> ARRAY['database']
            """;

        printQueryResult(containmentSql, "包含 'database' 标签的文章");

        // 复杂包含查询
        String complexContainmentSql = """
            SELECT *
            FROM (
                SELECT '[
                    {\"name\": \"item1\", \"price\": 100},
                    {\"name\": \"item2\", \"price\": 200}
                ]'::jsonb as data
            ) t
            WHERE data @> '[{\"name\": \"item1\"}]'
            """;

        printQueryResult(complexContainmentSql, "复杂对象包含查询");

        // ? 操作符 - 检查键是否存在
        String keyExistsSql = """
            SELECT *
            FROM (
                SELECT '{\"name\": \"Test\", \"version\": \"1.0\", \"active\": true}'::jsonb as data
            ) t
            WHERE data ? 'version'
            """;

        printQueryResult(keyExistsSql, "检查键 'version' 是否存在");

        // ?| 任意键存在
        String anyKeySql = """
            SELECT *
            FROM (
                SELECT '{\"name\": \"Test\", \"version\": \"1.0\", \"active\": true}'::jsonb as data
            ) t
            WHERE data ?| ARRAY['version', 'status']
            """;

        printQueryResult(anyKeySql, "检查任意键存在");
    }

    /**
     * JSONB 索引演示
     */
    private void jsonbIndexDemo() throws SQLException {
        System.out.println("\n[4] JSONB 索引演示");

        // 创建带 JSONB 列的表
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS user_profiles (
                id SERIAL PRIMARY KEY,
                user_id INTEGER REFERENCES users(id),
                profile JSONB NOT NULL
            )
            """;

        // 创建 GIN 索引
        String createIndexSql = """
            CREATE INDEX IF NOT EXISTS idx_user_profiles_profile
            ON user_profiles USING GIN (profile)
            """;

        // 插入测试数据
        String insertSql = """
            INSERT INTO user_profiles (user_id, profile)
            VALUES (1, '{\"bio\": \"Software Developer\", \"skills\": [\"Java\", \"SQL\"], \"level\": \"senior\"}')
            ON CONFLICT (user_id) DO UPDATE SET profile = EXCLUDED.profile
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
            stmt.execute(createIndexSql);
            stmt.execute(insertSql);
            System.out.println("  GIN 索引已创建: idx_user_profiles_profile");
            System.out.println("  索引类型: GIN (profile)");
        }

        // 使用索引的查询
        String indexQuerySql = """
            SELECT user_id, profile->>'bio' as bio
            FROM user_profiles
            WHERE profile @> '{\"skills\": [\"Java\"]}'
            """;

        printQueryResult(indexQuerySql, "使用 GIN 索引查询");
    }

    /**
     * JSON 函数演示
     */
    private void jsonFunctionsDemo() throws SQLException {
        System.out.println("\n[5] JSON 函数演示");

        // jsonb_set - 更新 JSONB
        String jsonbSetSql = """
            SELECT
                original,
                jsonb_set(original, '{level}', '"expert"') as updated
            FROM (
                SELECT '{\"name\": \"User\", \"level\": \"junior\", \"score\": 100}'::jsonb as original
            ) t
            """;

        printQueryResult(jsonbSetSql, "jsonb_set 更新字段");

        // jsonb_insert - 插入新字段
        String jsonbInsertSql = """
            SELECT
                original,
                jsonb_insert(original, '{email}', '"user@example.com"', true) as inserted
            FROM (
                SELECT '{\"name\": \"User\", \"level\": \"junior\"}'::jsonb as original
            ) t
            """;

        printQueryResult(jsonbInsertSql, "jsonb_insert 插入字段");

        // jsonb_pretty - 格式化输出
        String jsonbPrettySql = """
            SELECT jsonb_pretty('{\"name\": \"Test\", \"nested\": {\"key\": \"value\"}}'::jsonb)
            """;

        printQueryResult(jsonbPrettySql, "jsonb_pretty 格式化");

        // jsonb_array_elements - 展开数组
        String arrayElementsSql = """
            SELECT
                id,
                tags,
                tag
            FROM articles,
            LATERAL jsonb_array_elements(tags) as tag
            WHERE id = 1
            """;

        printQueryResult(arrayElementsSql, "jsonb_array_elements 展开数组");

        // jsonb_each - 展开对象
        String eachSql = """
            SELECT
                key,
                value
            FROM (
                SELECT '{\"name\": \"John\", \"age\": 30}'::jsonb as obj
            ) t,
            LATERAL jsonb_each(obj)
            """;

        printQueryResult(eachSql, "jsonb_each 展开对象");
    }

    /**
     * JSONB 更新演示
     */
    private void jsonbUpdateDemo() throws SQLException {
        System.out.println("\n[6] JSONB 更新操作演示");

        // 使用 PGobject 更新 JSONB
        String updateSql = """
            INSERT INTO user_profiles (user_id, profile)
            VALUES (2, '{"bio": "Data Scientist", "skills": ["Python", "ML"], "level": "mid"}')
            ON CONFLICT (user_id) DO UPDATE SET profile = EXCLUDED.profile
            RETURNING user_id, profile
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(updateSql);
            while (rs.next()) {
                System.out.println("  用户 " + rs.getInt("user_id") + " profile:");
                System.out.println("    " + rs.getString("profile"));
            }
        }

        // 复杂更新 - 添加数组元素
        String addArrayElementSql = """
            UPDATE user_profiles
            SET profile = profile || '{\"new_field\": \"added\"}'::jsonb
            WHERE user_id = 1
            RETURNING user_id, profile
            """;

        printQueryResult(addArrayElementSql, "添加新字段");

        // 删除字段
        String deleteFieldSql = """
            UPDATE user_profiles
            SET profile = profile - 'new_field'
            WHERE user_id = 1
            RETURNING user_id, profile
            """;

        printQueryResult(deleteFieldSql, "删除字段");
    }

    /**
     * 打印查询结果
     */
    private void printQueryResult(String sql, String label) throws SQLException {
        System.out.println("  " + label + ":");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 打印列名
            StringBuilder header = new StringBuilder("    ");
            for (int i = 1; i <= columnCount; i++) {
                if (i > 1) header.append(" | ");
                header.append(metaData.getColumnLabel(i));
            }
            System.out.println(header);

            // 打印数据
            int rowCount = 0;
            while (rs.next() && rowCount < 5) {
                StringBuilder row = new StringBuilder("    ");
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) row.append(" | ");
                    Object value = rs.getObject(i);
                    String str = value != null ? value.toString() : "NULL";
                    row.append(truncate(str, 30));
                }
                System.out.println(row);
                rowCount++;
            }
        }
    }

    private String truncate(String str, int maxLen) {
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 2) + "..";
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
