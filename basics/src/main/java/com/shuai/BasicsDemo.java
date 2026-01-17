package com.shuai;

import com.shuai.datatype.BasicsControlFlowDemo;
import com.shuai.datatype.BasicsDataTypesDemo;
import com.shuai.datatype.BasicsOperatorsDemo;
import com.shuai.collections.BasicsStringArrayDemo;
import com.shuai.oop.BasicsOOPDemo;
import com.shuai.oop.BasicsPassByValueDemo;
import com.shuai.advanced.BasicsBigDecimalDemo;
import com.shuai.advanced.BasicsGenericsDemo;
import com.shuai.advanced.BasicsReflectionDemo;
import com.shuai.advanced.BasicsSPIDemo;
import com.shuai.modern.BasicsSyntaxSugarDemo;
import com.shuai.threads.BasicsThreadsDemo;
import com.shuai.exceptions.BasicsExceptionsDemo;
import com.shuai.datetime.BasicsDateTimeDemo;
import com.shuai.optional.BasicsOptionalDemo;
import com.shuai.annotations.BasicsAnnotationsDemo;
import com.shuai.patterns.BasicsDesignPatternsDemo;
import com.shuai.testing.BasicsTestingDemo;
import com.shuai.database.BasicsJdbcDemo;
import com.shuai.network.BasicsNetworkDemo;
import com.shuai.json.BasicsJsonDemo;

// 整合后的模块
import com.shuai.io.BasicsIoDemo;
import com.shuai.collections.BasicsCollectionDemo;
import com.shuai.concurrent.BasicsThreadDemo;
import com.shuai.jvm.BasicsMemoryDemo;
import com.shuai.logging.BasicsLoggingDemo;

// 新整合的模块
import com.shuai.guava.BasicsGuavaCollectionDemo;
import com.shuai.guava.BasicsGuavaCacheDemo;
import com.shuai.json.BasicsGsonDemo;
import com.shuai.netty.BasicsNettyDemo;

// 整合 socket/config 模块
import com.shuai.network.BasicsTcpSocketDemo;
import com.shuai.network.BasicsUdpSocketDemo;
import com.shuai.config.BasicsPropertiesDemo;

/**
 * Java 基础语法演示入口类
 *
 * 整合所有演示模块：
 * - 数据类型：基本类型、引用类型、类型转换
 * - 运算符：算术、关系、逻辑、位运算
 * - 字符串与数组：String、StringBuilder、数组
 * - 流程控制：条件、循环、异常处理
 * - 面向对象：封装、继承、多态
 * - 值传递：基本类型与引用类型的传递
 * - 泛型：泛型类、方法、通配符
 * - 反射：动态操作类与对象
 * - BigDecimal：精确数值计算
 * - SPI：服务发现机制
 * - 语法糖：Lambda、Stream、Record 等
 * - 多线程：线程创建、同步、线程池
 * - 异常处理：try-catch、自定义异常
 * - IO 流：字节流、字符流、NIO
 * - 日期时间：LocalDateTime、ZonedDateTime
 * - Optional：避免空指针
 * - 注解：自定义注解、注解处理器
 *
 * 整合模块：
 * - IO 操作：文件流、NIO、Files 工具
 * - 集合框架：List、Set、Map、Queue
 * - 并发编程：线程、线程池、并发工具
 * - JVM 知识：内存区域、类加载器、GC
 * - 日志框架：SLF4J、Logback、Log4j2
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsDemo {

    /**
     * 主方法 - 执行所有演示
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // ===== Java 基础语法模块 =====
        new BasicsDataTypesDemo().runAllDemos();
        new BasicsOperatorsDemo().runAllDemos();
        new BasicsStringArrayDemo().runAllDemos();
        new BasicsControlFlowDemo().runAllDemos();
        new BasicsOOPDemo().runAllDemos();
        new BasicsPassByValueDemo().runAllDemos();
        new BasicsGenericsDemo().runAllDemos();
        new BasicsReflectionDemo().runAllDemos();
        new BasicsBigDecimalDemo().runAllDemos();
        new BasicsSPIDemo().runAllDemos();
        new BasicsSyntaxSugarDemo().runAllDemos();

        // ===== 扩展模块 =====
        new BasicsThreadsDemo().runAllDemos();
        new BasicsExceptionsDemo().runAllDemos();
        new BasicsDateTimeDemo().runAllDemos();
        new BasicsOptionalDemo().runAllDemos();
        new BasicsAnnotationsDemo().runAllDemos();

        // ===== 新增模块 =====
        new BasicsDesignPatternsDemo().runAllDemos();
        new BasicsTestingDemo().runAllDemos();
        new BasicsJdbcDemo().runAllDemos();
        new BasicsNetworkDemo().runAllDemos();
        new BasicsJsonDemo().runAllDemos();

        // ===== 整合模块 (原 io/jvm/collection/concurrent/logging) =====
        new BasicsIoDemo().runAllDemos();
        new BasicsCollectionDemo().runAllDemos();
        new BasicsThreadDemo().runAllDemos();
        new BasicsMemoryDemo().runAllDemos();
        new BasicsLoggingDemo().runAllDemos();

        // ===== 新整合模块 (原 guava/gson/jackson/design-patterns/netty) =====
        new BasicsGuavaCollectionDemo().runAllDemos();
        try {
            new BasicsGuavaCacheDemo().runAllDemos();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        new BasicsGsonDemo().runAllDemos();
        new BasicsNettyDemo().runAllDemos();

        // ===== 整合 socket/config 模块 =====
        try {
            new BasicsTcpSocketDemo().runAllDemos();
            new BasicsUdpSocketDemo().runAllDemos();
        } catch (Exception e) {
            // 演示用，异常不影响主要功能
        }
        new BasicsPropertiesDemo().runAllDemos();
    }
}
