package com.shuai.database.mysql.mybatis;

import com.shuai.database.mysql.mybatis.entity.User;
import com.shuai.database.mysql.mybatis.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MyBatis 演示
 */
public class MybatisDemo {

    private static SqlSessionFactory sqlSessionFactory;

    public void runAllDemos() {
        System.out.println("\n--- MyBatis 演示 ---");

        try {
            initSqlSessionFactory();
            annotationMapperDemo();
            xmlMapperDemo();
            dynamicSqlDemo();
        } catch (Exception e) {
            System.err.println("MyBatis 操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 初始化 SqlSessionFactory
     */
    private void initSqlSessionFactory() throws Exception {
        String resource = "mybatis/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        System.out.println("[MyBatis] SqlSessionFactory 初始化完成");
    }

    /**
     * 注解映射器演示
     */
    private void annotationMapperDemo() throws Exception {
        System.out.println("\n[1] 注解映射器演示");

        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);

            // 查询所有用户
            System.out.println("  查询所有用户:");
            List<User> users = mapper.findAll();
            users.stream().limit(3).forEach(u -> System.out.println("    " + u));

            // 根据 ID 查询
            System.out.println("\n  根据 ID 查询:");
            User user = mapper.findById(1L);
            System.out.println("    " + user);

            // 统计用户数量
            System.out.println("\n  统计用户数量:");
            int count = mapper.count();
            System.out.println("    用户总数: " + count);
        }
    }

    /**
     * XML 映射器演示
     */
    private void xmlMapperDemo() throws Exception {
        System.out.println("\n[2] XML 映射器演示");

        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);

            // 插入用户
            System.out.println("  插入用户:");
            User newUser = new User("mybatis_user_" + System.currentTimeMillis(),
                    "mybatis@example.com", "password123");
            int rows = mapper.insert(newUser);
            session.commit();
            System.out.println("    插入行数: " + rows);
            System.out.println("    生成的用户ID: " + newUser.getId());

            // 根据 ID 查询
            System.out.println("\n  查询插入的用户:");
            User insertedUser = mapper.findById(newUser.getId());
            System.out.println("    " + insertedUser);

            // 更新用户
            System.out.println("\n  更新用户:");
            insertedUser.setEmail("updated@example.com");
            insertedUser.setStatus(1);
            rows = mapper.update(insertedUser);
            session.commit();
            System.out.println("    更新行数: " + rows);

            // 验证更新
            User updatedUser = mapper.findById(newUser.getId());
            System.out.println("    更新后: " + updatedUser);

            // 删除用户
            System.out.println("\n  删除用户:");
            rows = mapper.delete(newUser.getId());
            session.commit();
            System.out.println("    删除行数: " + rows);
        }
    }

    /**
     * 动态 SQL 演示
     */
    private void dynamicSqlDemo() throws Exception {
        System.out.println("\n[3] 动态 SQL 演示");

        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);

            // 方式1: 使用注解方式查询
            System.out.println("  按状态查询用户 (status=1):");
            List<User> users = mapper.findByStatus(1);
            users.stream().limit(3).forEach(u -> System.out.println("    " + u));

            // 方式2: 使用 XML 动态 SQL
            System.out.println("\n  动态条件查询:");
            // 通过 session 调用 XML 中定义的动态 SQL
            Map<String, Object> params = new HashMap<>();
            params.put("status", 1);

            // 查询状态为 1 的用户
            List<User> dynamicUsers = session.selectList("com.shuai.database.mysql.mybatis.mapper.UserMapper.findByCondition", params);
            System.out.println("    找到 " + dynamicUsers.size() + " 个用户");
        }
    }

    /**
     * 核心组件说明
     */
    private void componentIntro() {
        System.out.println("""
            MyBatis 核心组件:
            - SqlSessionFactoryBuilder: 构建 SqlSessionFactory
            - SqlSessionFactory: 创建 SqlSession
            - SqlSession: 执行 SQL, 获取 Mapper
            - Mapper: 接口映射器

            配置方式:
            - XML 配置: mybatis-config.xml
            - 映射文件: UserMapper.xml
            - 注解映射: @Select, @Insert 等
            """);
    }
}
