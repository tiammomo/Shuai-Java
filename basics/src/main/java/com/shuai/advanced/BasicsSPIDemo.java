package com.shuai.advanced;

import com.shuai.spi.LogService;
import com.shuai.spi.SerializationService;

import java.util.ServiceLoader;

/**
 * Java SPI 演示类
 *
 * 涵盖内容：
 * - SPI 概念：Service Provider Interface 服务发现机制
 * - ServiceLoader：动态加载服务实现
 * - 自定义 SPI：创建服务接口和实现
 * - JDBC 驱动：SPI 在 JDBC 中的应用
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsSPIDemo {

    /**
     * 执行所有 SPI 演示
     */
    public void runAllDemos() {
        spiConcepts();
        serviceLoaderDemo();
        customSpiImpl();
        jdbcDriverDemo();
    }

    /**
     * 演示 SPI 概念
     *
     * SPI 工作原理：
     * - 1. 定义服务接口
     * - 2. 在 META-INF/services/ 创建接口全限定名文件
     * - 3. 文件中写入实现类的全限定名（每行一个）
     * - 4. 使用 ServiceLoader 加载实现类
     *
     * 应用场景：
     * - JDBC 驱动加载
     * - SLF4J 日志框架
     * - 插件式架构
     */
    private void spiConcepts() {
        // SPI (Service Provider Interface) 是一种服务发现机制
        // 允许模块化设计中的服务提供者实现接口

        // SPI 工作原理:
        // 1. 定义服务接口
        // 2. 在 META-INF/services 目录下创建接口全限定名文件
        // 3. 文件中写入实现类的全限定名（每行一个）
        // 4. 使用 ServiceLoader 加载实现类

        // SPI vs API:
        // API: 应用程序调用接口
        // SPI: 框架提供接口，第三方实现

        // SPI 应用场景:
        // - JDBC 驱动加载 (java.sql.Driver)
        // - SLF4J 日志框架
        // - 插件式架构
    }

    /**
     * 演示 ServiceLoader 使用
     *
     * 基本用法：
     * - ServiceLoader.load(ServiceInterface) 加载服务
     * - 遍历加载器获取服务实例
     * - 使用 stream() 处理服务提供者
     * - reload() 重新加载服务
     */
    private void serviceLoaderDemo() {
        ServiceLoader<LogService> loader = ServiceLoader.load(LogService.class);

        // 遍历服务实现
        for (LogService service : loader) {
            service.log("SPI 测试消息");
        }

        // 使用 Stream 处理
        ServiceLoader<LogService> loader2 = ServiceLoader.load(LogService.class);
        loader2.stream()
               .forEach(provider -> {
                   LogService service = provider.get();
               });

        // 重新加载
        ServiceLoader<LogService> reloadableLoader = ServiceLoader.load(LogService.class);
        reloadableLoader.reload();
    }

    /**
     * 演示自定义 SPI 实现
     *
     * 示例：SerializationService
     * - 遍历加载的序列化服务
     * - 调用服务的 serialize/deserialize 方法
     * - 可动态切换不同的序列化实现
     */
    private void customSpiImpl() {
        ServiceLoader<SerializationService> serializerLoader = ServiceLoader.load(SerializationService.class);

        for (SerializationService service : serializerLoader) {
            String data = "Hello SPI";
            byte[] serialized = service.serialize(data);
            String deserialized = service.deserialize(serialized);
        }
    }

    /**
     * 演示 JDBC 驱动加载
     *
     * JDBC 使用 SPI 机制：
     * - 驱动包在 META-INF/services/java.sql.Driver 注册
     * - DriverManager 初始化时自动加载所有驱动
     * - 无需手动 Class.forName() 加载驱动
     */
    private void jdbcDriverDemo() {
        // JDBC 使用 SPI 自动加载驱动
        // 1. 驱动包在 META-INF/services/java.sql.Driver 文件中注册
        // 2. DriverManager 初始化时自动加载所有驱动

        try {
            java.sql.DriverManager.getDrivers().asIterator().forEachRemaining(driver -> {
                // 获取驱动类名
            });
        } catch (Exception e) {
            // 无法获取驱动列表
        }

        // 配置示例:
        // 文件: META-INF/services/java.sql.Driver
        // 内容: com.mysql.cj.jdbc.Driver
    }
}
