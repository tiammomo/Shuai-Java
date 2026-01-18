package com.shuai.database.postgresql;

import java.sql.Connection;

/**
 * PostgreSQL 数据库模块入口类
 *
 * @author Shuai
 * @version 1.0
 */
public class PostgreSqlDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       PostgreSQL 数据库");
        System.out.println("=".repeat(50));

        new PostgreSqlOverviewDemo().runAllDemos();
        new PostgreSqlJdbcDemo().runAllDemos();
        new PostgreSqlFeaturesDemo().runAllDemos();
        new PostgreSqlJsonDemo().runAllDemos();
    }
}
