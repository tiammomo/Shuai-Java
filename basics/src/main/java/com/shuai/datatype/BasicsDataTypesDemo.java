package com.shuai.datatype;

/**
 * Java 数据类型演示类
 *
 * 涵盖内容：
 * - 基本数据类型：byte, short, int, long, float, double, char, boolean
 * - 引用数据类型：类、接口、数组、枚举
 * - 类型转换：自动转换与强制转换
 * - 变量作用域：成员变量、静态变量、局部变量
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsDataTypesDemo {

    /**
     * 执行所有数据类型演示
     */
    public void runAllDemos() {
        primitiveTypes();
        referenceTypes();
        typeConversion();
        variableScope();
    }

    /**
     * 演示基本数据类型
     *
     * 基本特点：
     * - 直接存储值，不涉及对象引用
     * - 有明确的取值范围
     * - 有默认值（成员变量时）
     */
    private void primitiveTypes() {
        // 整数型：byte(1字节), short(2字节), int(4字节), long(8字节)
        byte age = 25;                    // 范围：-128 ~ 127
        long population = 1400000000L;    // long 类型需要 L 后缀

        // 浮点型：float(单精度4字节), double(双精度8字节)
        float price = 19.99f;             // float 类型需要 f 后缀
        double pi = 3.14159265359;        // double 是默认的浮点类型

        // 字符型：2字节，存储单个 Unicode 字符
        char grade = 'A';                  // 使用单引号

        // 布尔型：只有 true 和 false 两个值
        boolean isActive = true;
    }

    /**
     * 演示引用数据类型
     *
     * 引用类型特点：
     * - 存储对象的内存地址（引用）
     * - 默认值是 null
     * - 包括类、接口、数组、枚举等
     */
    private void referenceTypes() {
        // 类类型：String 是最常用的引用类型
        String str = new String("Hello");

        // 接口类型：函数式接口可用 Lambda 表达式
        Runnable r = () -> {};

        // 数组类型：长度固定，元素类型相同
        int[] arr = new int[5];            // int 数组
        String[] names = {"A", "B"};       // 数组初始化

        // 枚举类型：预定义的常量集合
        // enum Season { SPRING, SUMMER, AUTUMN, WINTER }
    }

    /**
     * 演示类型转换
     *
     * 转换规则：
     * - 自动转换（隐式）：小类型 -> 大类型，不会丢失精度
     * - 强制转换（显式）：大类型 -> 小类型，可能丢失精度
     * - char 和数值类型可以互相转换
     */
    private void typeConversion() {
        // 自动转换（隐式）：小类型 -> 大类型
        int i = 100;
        long l = i;  // int 自动提升为 long

        // 强制转换（显式）：大类型 -> 小类型，可能丢失精度
        double d = 3.14;
        int j = (int) d;  // 3，丢失小数部分

        // 溢出示例：超出类型范围时会循环
        int k = 128;      // byte 范围是 -128 ~ 127
        byte b = (byte) k;  // 结果是 -128

        // 字符串与数值转换
        String s = String.valueOf(123);     // 数值转字符串
        int num = Integer.parseInt("123");  // 字符串转数值
    }

    /**
     * 演示变量作用域
     *
     * 三种变量类型：
     * - 成员变量（实例变量）：类中定义，随对象创建，有默认值
     * - 静态变量（类变量）：static 修饰，随类加载而存在，所有对象共享
     * - 局部变量：方法/代码块中定义，必须显式初始化才能使用
     */
    private void variableScope() {
        // 成员变量（实例变量）：类中定义，随对象创建，有默认值
        String memberVar = getMemberVariable();

        // 局部变量：方法/代码块中定义，需初始化才能使用
        int localVar = 10;

        // 静态变量（类变量）：static 修饰，所有对象共享
        int staticVar = getStaticVariable();
    }

    // ===== 成员变量 =====

    /** 成员变量示例：随对象创建，有默认值 null */
    private String memberVariable = "实例变量";

    /** 静态变量示例：随类加载而存在，所有对象共享 */
    private static int counter = 0;

    /**
     * 获取成员变量值
     * @return 成员变量的值
     */
    private String getMemberVariable() {
        return memberVariable;
    }

    /**
     * 获取并递增静态计数器
     * @return 计数器当前值
     */
    private static int getStaticVariable() {
        counter++;
        return counter;
    }
}
