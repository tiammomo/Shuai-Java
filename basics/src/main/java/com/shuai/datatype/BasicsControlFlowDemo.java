package com.shuai.datatype;

import java.util.*;
import java.util.function.*;

/**
 * Java 流程控制演示类
 *
 * 涵盖内容：
 * - 条件语句：if-else, switch
 * - 循环语句：for, while, do-while, forEach
 * - 跳转语句：break, continue, return
 * - 异常处理：try-catch-finally, try-with-resources
 * - 函数式接口：Predicate, Function, Consumer, Supplier
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsControlFlowDemo {

    /**
     * 执行所有流程控制演示
     */
    public void runAllDemos() {
        conditionalStatements();
        switchStatements();
        loopStatements();
        jumpStatements();
        exceptionHandling();
        functionalInterfaces();
    }

    /**
     * 演示条件语句
     *
     * 包括：
     * - if-else 单条件分支
     * - else if 多条件分支
     * - 嵌套条件判断
     */
    private void conditionalStatements() {
        // if-else 条件语句
        int score = 85;
        if (score >= 90) {
            // A
        } else if (score >= 80) {
            // B
        } else {
            // C or D
        }

        // 嵌套条件
        int number = 15;
        if (number > 0) {
            if (number % 2 == 0) {
                // 正偶数
            } else {
                // 正奇数
            }
        } else if (number < 0) {
            // 负数
        } else {
            // 零
        }
    }

    /**
     * 演示 switch 表达式
     *
     * Java 14+ 特性：
     * - 箭头语法（case ->）
     * - 多标签合并（case A, B ->）
     * - switch 作为表达式返回值
     * - 模式匹配（Java 21+）
     */
    private void switchStatements() {
        // switch 表达式 (Java 14+)
        String color = "RED";
        String rgb = switch (color) {
            case "RED" -> "#FF0000";
            case "GREEN" -> "#00FF00";
            case "BLUE" -> "#0000FF";
            default -> "#000000";
        };

        // switch 与枚举
        Day enumDay = Day.TUESDAY;
        int workHours = switch (enumDay) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> 8;
            case SATURDAY, SUNDAY -> 0;
        };

        // switch 与对象模式匹配 (Java 21+)
        Object obj = "Hello";
        String result = switch (obj) {
            case String s -> "字符串，长度: " + s.length();
            case Integer i -> "整数: " + i;
            case null -> "空值";
            default -> "未知类型";
        };
    }

    /**
     * 演示循环语句
     *
     * 包括：
     * - for 循环：标准索引循环
     * - 增强 for 循环：foreach 遍历
     * - while 循环：先判断后执行
     * - do-while 循环：先执行后判断
     * - Stream API：函数式数据处理
     */
    private void loopStatements() {
        List<String> names = Arrays.asList("张三", "李四", "王五");

        // for 循环
        for (int i = 0; i < names.size(); i++) {
            names.get(i);
        }

        // 增强 for 循环
        for (String name : names) {
            String collected = name;
        }

        // forEach
        names.forEach(name -> {
            String n = name;
        });

        // while 循环
        int i = 0;
        while (i < 3) {
            i++;
        }

        // do-while 循环
        int j = 0;
        do {
            j++;
        } while (j < 3);

        // Stream API
        names.stream()
             .filter(name -> name.startsWith("张"))
             .map(name -> "贵姓: " + name)
             .forEach(System.out::println);
    }

    /**
     * 演示跳转语句
     *
     * 包括：
     * - break：跳出当前循环
     * - continue：跳过本次循环，继续下次
     * - 带标签的 break：跳出多层循环
     * - return：返回方法结果
     */
    private void jumpStatements() {
        // break：跳出循环
        for (int k = 0; k < 5; k++) {
            if (k == 3) break;
        }

        // continue：跳过本次循环
        for (int k = 0; k < 5; k++) {
            if (k == 2) continue;
        }

        // 带标签的 break：跳出多层循环
        outer:
        for (int m = 1; m <= 3; m++) {
            for (int n = 1; n <= 3; n++) {
                if (m == 2 && n == 2) {
                    break outer;
                }
            }
        }

        // return：返回方法结果
        findFirstEven(Arrays.asList(1, 3, 5, 7, 8));
    }

    private Integer findFirstEven(List<Integer> numbers) {
        for (Integer num : numbers) {
            if (num % 2 == 0) {
                return num;
            }
        }
        return null;
    }

    /**
     * 演示函数式接口
     *
     * Java 8+ 函数式接口：
     * - Predicate：条件判断，返回 boolean
     * - Function：转换，输入->输出
     * - Consumer：消费，无返回值
     * - Supplier：供给，无输入有输出
     * - Comparator：比较大小
     */
    private void functionalInterfaces() {
        // Predicate：条件判断
        Predicate<String> isNotEmpty = s -> !s.isEmpty();
        isNotEmpty.test("Hello");

        // Function：转换
        Function<String, Integer> lengthFunc = String::length;
        lengthFunc.apply("Hello");

        // Consumer：消费
        Consumer<String> consumer = System.out::println;
        consumer.accept("message");

        // Supplier：供给
        Supplier<Double> randomSupplier = () -> Math.random();
        randomSupplier.get();

        // Comparator：比较
        Comparator<Integer> reverseComparator = Comparator.reverseOrder();
        List<Integer> nums = Arrays.asList(3, 1, 4, 1, 5);
        nums.sort(reverseComparator);
    }

    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    /**
     * 演示异常处理
     *
     * 包括：
     * - try-catch-finally：捕获并处理异常
     * - try-with-resources：自动关闭资源（Java 7+）
     * - 自定义异常：throw 抛出
     * - 异常链：保留原始异常信息
     * - throws 声明：方法可能抛出的异常
     */
    private void exceptionHandling() {
        // try-catch-finally
        try {
            int _result = 10 / 2;
        } catch (ArithmeticException e) {
            // 除以零异常
        } catch (Exception e) {
            // 其他异常
        } finally {
            // 无论是否异常都执行
        }

        // try-with-resources 自动关闭资源
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(System.in))) {
            String _line = reader.readLine();
        } catch (java.io.IOException e) {
            // IO 异常处理
        }

        // 自定义异常
        try {
            validateAge(-5);
        } catch (IllegalArgumentException e) {
            // 年龄验证失败
        }

        // 异常链：保留原始异常信息
        RuntimeException _wrapped = null;
        try {
            throw new RuntimeException("原始异常");
        } catch (RuntimeException e) {
            _wrapped = new RuntimeException("包装异常", e);
        }

        // throws 声明异常
        try {
            readFile("test.txt");
        } catch (java.io.IOException e) {
            // 声明式异常可在调用处处理
        }
    }

    /**
     * 验证年龄是否有效
     * @param age 待验证的年龄
     * @throws IllegalArgumentException 年龄为负数时抛出
     */
    private void validateAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("年龄不能为负数");
        }
    }

    /**
     * 读取文件（声明可能抛出的异常）
     * @param path 文件路径
     * @throws java.io.IOException IO 异常
     */
    private void readFile(String path) throws java.io.IOException {
        // 方法声明可能抛出的异常
    }
}
