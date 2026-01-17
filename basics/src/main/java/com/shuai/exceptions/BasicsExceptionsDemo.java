package com.shuai.exceptions;

import java.io.*;
import java.util.*;

/**
 * Java 异常处理演示类
 *
 * 涵盖内容：
 * - 异常层次结构：Throwable、Error、Exception、RuntimeException
 * - 异常类型：受检异常（Checked）与非受检异常（Unchecked）
 * - try-catch-finally：捕获异常
 * - try-with-resources：自动资源关闭
 * - 自定义异常：创建业务异常
 * - 异常链：保留原始异常信息
 * - 最佳实践：异常处理规范
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsExceptionsDemo {

    // 自定义受检异常
    static class BusinessException extends Exception {
        public BusinessException() {
            super();
        }
        public BusinessException(String message) {
            super(message);
        }
        public BusinessException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // 自定义非受检异常
    static class BusinessRuntimeException extends RuntimeException {
        public BusinessRuntimeException() {
            super();
        }
        public BusinessRuntimeException(String message) {
            super(message);
        }
        public BusinessRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // 演示用辅助类
    static class Resource1 implements AutoCloseable {
        @Override
        public void close() {}
    }
    static class Resource2 implements AutoCloseable {
        @Override
        public void close() {}
    }

    // 演示用异常类
    static class HighLevelException extends RuntimeException {
        public HighLevelException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    static class LowLevelException extends RuntimeException {}

    /**
     * 执行所有异常处理演示
     */
    public void runAllDemos() {
        exceptionHierarchy();
        checkedVsUncheckedDemo();
        tryCatchFinally();
        tryWithResources();
        customExceptions();
        exceptionChain();
        bestPractices();
    }

    /**
     * 演示异常层次结构
     *
     * Throwable（可抛出）
     * ├── Error（错误，程序无法处理）
     * │   ├── OutOfMemoryError
     * │   ├── StackOverflowError
     * │   └── ...
     * └── Exception（异常，可处理）
     *       ├── RuntimeException（运行时异常，非受检）
     *       │   ├── NullPointerException
     *       │   ├── IllegalArgumentException
     *       │   └── ...
     *       └── 其他受检异常
     *         ├── IOException
     *         ├── SQLException
     *         └── ...
     */
    private void exceptionHierarchy() {
        // Error 类型（程序无法恢复的错误）
        Error error = new OutOfMemoryError("内存不足");
        Error stackOverflow = new StackOverflowError("栈溢出");
    }

    /**
     * 演示受检异常与非受检异常
     *
     * 受检异常（Checked Exception）：
     * - 编译时强制处理
     * - 必须 catch 或 throws
     * - 代表可恢复的错误
     * - 如：IOException、SQLException
     *
     * 非受检异常（Unchecked Exception）：
     * - 编译时不强制处理
     * - RuntimeException 及其子类
     * - Error 及其子类
     * - 代表程序错误，不可恢复
     */
    private void checkedVsUncheckedDemo() {
        // 非受检异常示例：运行时异常
        try {
            throw new NullPointerException("空指针");
        } catch (NullPointerException e) {
            // 处理
        }

        try {
            throw new IllegalArgumentException("参数不合法");
        } catch (IllegalArgumentException e) {
            // 处理
        }
    }

    /**
     * 演示 try-catch-finally
     *
     * 结构：
     * - try：可能抛出异常的代码
     * - catch：捕获特定异常并处理
     * - finally：无论是否异常都执行的代码
     *
     * 多 catch 顺序：子类在前，父类在后
     */
    private void tryCatchFinally() {
        try {
            // 可能抛出异常的代码
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            // 捕获特定异常
            // 获取异常信息
            String message = e.getMessage();
            // 打印堆栈跟踪
            e.printStackTrace();
        } catch (NullPointerException e) {
            // 另一个异常处理
        } catch (Exception e) {
            // 捕获所有其他异常
        } finally {
            // 无论是否异常都执行
            // 释放资源、清理工作
        }

        // 多异常捕获（Java 7+）
        try {
            throw new IOException("IO 错误");
        } catch (IOException | ArithmeticException e) {
            // 同时捕获多个异常
        }

        // 资源在 finally 中关闭
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("file.txt"));
            reader.readLine();
        } catch (IOException e) {
            // 处理异常
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // 关闭失败
                }
            }
        }
    }

    /**
     * 演示 try-with-resources
     *
     * Java 7+ 特性：
     * - 自动关闭实现 AutoCloseable 的资源
     * - 无需手动写 finally
     * - 资源关闭顺序与声明顺序相反
     * - 更简洁、更安全
     */
    private void tryWithResources() {
        // 基本用法
        try (BufferedReader reader = new BufferedReader(new FileReader("file.txt"))) {
            String line = reader.readLine();
        } catch (IOException e) {
            // 处理异常
        }

        // 多个资源
        try (
            FileInputStream fis = new FileInputStream("a.txt");
            FileOutputStream fos = new FileOutputStream("b.txt")
        ) {
            // 使用两个资源
        } catch (IOException e) {
            // 处理异常
        }

        // 资源在 try 结束后自动关闭
        // 即使发生异常也会关闭
        try (Scanner scanner = new Scanner(System.in)) {
            String input = scanner.nextLine();
        }

        // 自定义资源（实现 AutoCloseable）
        class Resource implements AutoCloseable {
            @Override
            public void close() {
                // 释放资源
            }
        }
        try (Resource res = new Resource()) {
            // 使用资源
        }
    }

    /**
     * 演示自定义异常
     *
     * 步骤：
     * - 继承 Exception（受检）或 RuntimeException（非受检）
     * - 提供无参构造
     * - 提供带消息的构造
     * - 提供带消息和 cause 的构造
     */
    private void customExceptions() {
        // 使用自定义异常
        try {
            validateAge(-5);
        } catch (BusinessException e) {
            String message = e.getMessage();
        }

        // 抛出运行时异常
        class Service {
            void process(int data) {
                if (data < 0) {
                    throw new IllegalArgumentException("数据不能为负");
                }
            }
        }
    }

    /**
     * 验证年龄（演示自定义受检异常）
     * @param age 年龄
     * @throws BusinessException 业务异常
     */
    private void validateAge(int age) throws BusinessException {
        if (age < 0) {
            throw new BusinessException("年龄不能为负数");
        }
    }

    /**
     * 演示异常链
     *
     * 保留原始异常信息：
     * - 在包装异常时保留 cause
     * - 使用 initCause 或构造方法
     * - 便于追踪问题根源
     */
    private void exceptionChain() {
        // 方式1：使用构造方法
        System.out.println("    // 方式1：使用构造方法");
        System.out.println("    throw new BusinessException(\"业务处理失败\", e);");

        // 方式2：使用 initCause
        System.out.println("    // 方式2：使用 initCause");
        System.out.println("    BusinessRuntimeException bre = new BusinessRuntimeException(\"业务错误\");");
        System.out.println("    bre.initCause(e);");
        System.out.println("    throw bre;");

        // 方式3：使用 addSuppressed
        System.out.println("    // 方式3：使用 addSuppressed");
        System.out.println("    try (Resource1 r1 = new Resource1(); Resource2 r2 = new Resource2()) {");
        System.out.println("        throw new RuntimeException(\"主异常\");");
        System.out.println("    } catch (RuntimeException e) {");
        System.out.println("        Throwable[] suppressed = e.getSuppressed();");
        System.out.println("    }");
    }

    /**
     * 演示异常处理最佳实践
     */
    private void bestPractices() {
        // 1. 不要吞掉异常
        try {
            // 可能出错的代码
        } catch (Exception e) {
            // 错误：什么都不做
            // e.printStackTrace(); // 至少打印日志
        }

        // 2. 记录日志后重新抛出
        try {
            // 可能出错的代码
        } catch (Exception e) {
            // 记录日志
            // throw e; // 重新抛出，让上层处理
        }

        // 3. 使用具体异常类型
        System.out.println("    // 3. 使用具体异常类型");
        System.out.println("    try {");
        System.out.println("        // 可能出错的代码");
        System.out.println("    } catch (IllegalArgumentException e) {");
        System.out.println("        // 处理参数错误");
        System.out.println("    }");
        System.out.println("    // 注意：实际捕获时需要处理可能抛出的异常类型");

        // 4. 不要在 finally 中抛出异常
        // finally {
        //     throw new RuntimeException(); // 可能覆盖 try 中的异常
        // }

        // 5. 使用局部变量捕获异常信息
        // 而不是依赖全局状态

        // 6. 异常转换：在高层将低层异常转换为高层异常
        try {
            // 调用低层 API
        } catch (LowLevelException e) {
            throw new HighLevelException("高层描述", e);
        }

        // 7. 不要使用异常控制流程
        // if (list.isEmpty()) {
        //     throw new NoSuchElementException();
        // }
        // 应改为：
        // if (list.isEmpty()) return null; 或返回 Optional
    }
}
