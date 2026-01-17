package com.shuai.logging;

import java.util.function.Supplier;

/**
 * Java 日志框架演示
 *
 * @author Shuai
 * @version 1.0
 */
public class BasicsLoggingDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Java 日志框架");
        System.out.println("=".repeat(50));

        loggingOverview();
        slf4jDemo();
        logbackDemo();
        log4j2Demo();
        bestPractices();
    }

    /**
     * 日志框架概述
     */
    private void loggingOverview() {
        System.out.println("\n--- 日志框架概述 ---");

        System.out.println("\n  日志门面 (Facade):");
        System.out.println("    - SLF4J (Simple Logging Facade for Java)");
        System.out.println("    - JCL (Jakarta Commons Logging)");
        System.out.println("    - JUL (java.util.logging)");

        System.out.println("\n  日志实现:");
        System.out.println("    - Logback");
        System.out.println("    - Log4j 2");
        System.out.println("    - JUL (JDK Logging)");

        System.out.println("\n  日志级别 (从低到高):");
        System.out.println("    TRACE < DEBUG < INFO < WARN < ERROR < FATAL");
        System.out.println("    ");
        System.out.println("    - TRACE: 最详细的跟踪信息");
        System.out.println("    - DEBUG: 调试信息");
        System.out.println("    - INFO:  一般信息");
        System.out.println("    - WARN:  警告信息");
        System.out.println("    - ERROR: 错误信息");
        System.out.println("    - FATAL: 致命错误");

        System.out.println("\n  日志门面使用:");
        System.out.println("    // 使用 SLF4J");
        System.out.println("    import org.slf4j.Logger;");
        System.out.println("    import org.slf4j.LoggerFactory;");
        System.out.println("    ");
        System.out.println("    Logger logger = LoggerFactory.getLogger(MyClass.class);");
        System.out.println("    logger.info(\"用户 {} 登录成功\", username);");
    }

    /**
     * SLF4J API 演示
     */
    private void slf4jDemo() {
        System.out.println("\n--- SLF4J API ---");

        System.out.println("\n  日志级别:");
        System.out.println("    logger.trace(\"trace message\");");
        System.out.println("    logger.debug(\"debug message\");");
        System.out.println("    logger.info(\"info message\");");
        System.out.println("    logger.warn(\"warn message\");");
        System.out.println("    logger.error(\"error message\");");
        System.out.println("    logger.error(\"error with exception\", exception);");

        System.out.println("\n  占位符 (推荐):");
        System.out.println("    logger.info(\"用户 {} 登录, IP: {}\", username, ipAddress);");
        System.out.println("    logger.debug(\"Result: {}\", result);");

        System.out.println("\n  条件日志:");
        System.out.println("    // 方式1: if-else");
        System.out.println("    if (logger.isDebugEnabled()) {");
        System.out.println("        logger.debug(\"Debug info: \" + buildDebugInfo());");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    // 方式2: Lambda (SLF4J 2.1+)");
        System.out.println("    logger.debug(\"Debug info\", () -> buildDebugInfo());");

        System.out.println("\n  MDC (Mapped Diagnostic Context):");
        System.out.println("    // 设置上下文");
        System.out.println("    MDC.put(\"userId\", userId);");
        System.out.println("    MDC.put(\"traceId\", traceId);");
        System.out.println("    ");
        System.out.println("    // 日志中会包含这些信息");
        System.out.println("    logger.info(\"Operation completed\");");
        System.out.println("    ");
        System.out.println("    // 清除上下文");
        System.out.println("    MDC.clear();");
    }

    /**
     * Logback 演示
     */
    private void logbackDemo() {
        System.out.println("\n--- Logback 配置 ---");

        System.out.println("\n(");
        System.out.println("    <!-- pom.xml 依赖 -->");
        System.out.println("    <dependency>");
        System.out.println("        <groupId>ch.qos.logback</groupId>");
        System.out.println("        <artifactId>logback-classic</artifactId>");
        System.out.println("        <version>1.5.12</version>");
        System.out.println("    </dependency>");

        System.out.println("\n  logback.xml 配置:");
        System.out.println("    <?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        System.out.println("    <configuration>");
        System.out.println("        <property name=\"LOG_PATTERN\" value=\"%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n\"/>");
        System.out.println("        ");
        System.out.println("        <appender name=\"CONSOLE\" class=\"ch.qos.logback.core.ConsoleAppender\">");
        System.out.println("            <encoder>");
        System.out.println("                <pattern>${LOG_PATTERN}</pattern>");
        System.out.println("            </encoder>");
        System.out.println("        </appender>");
        System.out.println("        ");
        System.out.println("        <appender name=\"FILE\" class=\"ch.qos.logback.core.rolling.RollingFileAppender\">");
        System.out.println("            <file>logs/app.log</file>");
        System.out.println("            <rollingPolicy class=\"ch.qos.logback.core.rolling.TimeBasedRollingPolicy\">");
        System.out.println("                <fileNamePattern>logs/app.%d{yyyy-MM-dd}.log</fileNamePattern>");
        System.out.println("                <maxHistory>30</maxHistory>");
        System.out.println("            </rollingPolicy>");
        System.out.println("            <encoder>");
        System.out.println("                <pattern>${LOG_PATTERN}</pattern>");
        System.out.println("            </encoder>");
        System.out.println("        </appender>");
        System.out.println("        ");
        System.out.println("        <root level=\"INFO\">");
        System.out.println("            <appender-ref ref=\"CONSOLE\"/>");
        System.out.println("            <appender-ref ref=\"FILE\"/>");
        System.out.println("        </root>");
        System.out.println("    </configuration>");

        System.out.println("\n  Appender 类型:");
        System.out.println("    ConsoleAppender  - 控制台输出");
        System.out.println("    FileAppender    - 文件输出");
        System.out.println("    RollingFileAppender - 滚动文件");
        System.out.println("    SocketAppender  - 远程日志");
        System.out.println("    SMTPAppender    - 邮件发送");

        System.out.println("\n  Rolling Policy:");
        System.out.println("    TimeBasedRollingPolicy   - 基于时间");
        System.out.println("    SizeAndTimeBasedRollingPolicy - 时间和大小");
        System.out.println("    FixedWindowRollingPolicy - 基于固定窗口");
    }

    /**
     * Log4j2 演示
     */
    private void log4j2Demo() {
        System.out.println("\n--- Log4j 2 配置 ---");

        System.out.println("\n  Maven 依赖:");
        System.out.println("    <dependency>");
        System.out.println("        <groupId>org.apache.logging.log4j</groupId>");
        System.out.println("        <artifactId>log4j-core</artifactId>");
        System.out.println("        <version>2.24.1</version>");
        System.out.println("    </dependency>");

        System.out.println("\n  log4j2.xml 配置:");
        System.out.println("    <?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        System.out.println("    <Configuration status=\"WARN\">");
        System.out.println("        <Properties>");
        System.out.println("            <Property name=\"pattern\">%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n</Property>");
        System.out.println("        </Properties>");
        System.out.println("        ");
        System.out.println("        <Appenders>");
        System.out.println("            <Console name=\"Console\" target=\"SYSTEM_OUT\">");
        System.out.println("                <PatternLayout pattern=\"${pattern}\"/>");
        System.out.println("            </Console>");
        System.out.println("        </Appenders>");
        System.out.println("        ");
        System.out.println("        <Loggers>");
        System.out.println("            <Root level=\"info\">");
        System.out.println("                <AppenderRef ref=\"Console\"/>");
        System.out.println("            </Root>");
        System.out.println("        </Loggers>");
        System.out.println("    </Configuration>");

        System.out.println("\n  Log4j2 特性:");
        System.out.println("    - 异步日志 (Async Logger)");
        System.out.println("    - 无垃圾模式 (Garbage-free)");
        System.out.println("    - 配置自动重载");
        System.out.println("    - 插件化架构");
    }

    /**
     * 最佳实践
     */
    private void bestPractices() {
        System.out.println("\n--- 日志最佳实践 ---");

        System.out.println("\n  1. 使用 SLF4J 作为门面");
        System.out.println("    - 统一日志 API");
        System.out.println("    - 便于切换实现");

        System.out.println("\n  2. 使用占位符");
        System.out.println("    logger.info(\"User {} logged in\", username);");
        System.out.println("    // 避免: logger.info(\"User \" + username + \" logged in\");");

        System.out.println("\n(");
        System.out.println("    // 错误");
        System.out.println("    if (logger.isDebugEnabled()) {");
        System.out.println("        logger.debug(\"Debug info: \" + expensiveOperation());");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    // 推荐");
        System.out.println("    logger.debug(\"Debug info: {}\", () -> expensiveOperation());");

        System.out.println("\n  3. 日志内容规范:");
        System.out.println("    - 包含上下文信息");
        System.out.println("    - 敏感信息脱敏");
        System.out.println("    - 异常信息完整堆栈");

        System.out.println("\n(");
        System.out.println("    // 错误示例");
        System.out.println("    logger.error(\"Failed\");");
        System.out.println("    ");
        System.out.println("    // 正确示例");
        System.out.println("    logger.error(\"Failed to process order {} for user {}\", orderId, userId, e);");

        System.out.println("\n  4. 选择合适的级别:");
        System.out.println("    INFO:  业务流程关键节点");
        System.out.println("    DEBUG: 调试时查看的中间状态");
        System.out.println("    WARN:  需要关注但不影响功能");
        System.out.println("    ERROR: 影响单个请求的错误");

        System.out.println("\n  5. 生产环境配置:");
        System.out.println("    - 控制台输出: WARN 以上");
        System.out.println("    - 文件输出: INFO 以上");
        System.out.println("    - 保留历史日志 30 天");
        System.out.println("    - 开启日志滚动");
    }
}
