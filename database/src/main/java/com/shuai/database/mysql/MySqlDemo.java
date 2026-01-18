package com.shuai.database.mysql;

import com.shuai.database.mysql.MySqlConnectionManager;

/**
 * MySQL 数据库模块入口类
 *
 * @author Shuai
 * @version 1.0
 */
public class MySqlDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MySQL 数据库");
        System.out.println("=".repeat(50));

        new MySqlOverviewDemo().runAllDemos();
        new CrudDemo().runAllDemos();
        new TransactionDemo().runAllDemos();
        new AdvancedQueryDemo().runAllDemos();
        new DruidDemo().runAllDemos();
        // MyBatis 演示请参考 mybatis 模块
        System.out.println("\n[提示] MyBatis 演示请运行 mybatis 模块");
    }
}
