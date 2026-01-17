package com.shuai.collections;

/**
 * Java 字符串与数组演示类
 *
 * 涵盖内容：
 * - String：不可变特性、常量池、常用方法
 * - StringBuilder：可变字符串、高效拼接
 * - 数组：一维数组、二维数组、数组遍历
 * - Arrays 工具类：排序、查找、复制
 * - Objects、Collections、Math 工具类
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsStringArrayDemo {

    /**
     * 执行所有字符串与数组演示
     */
    public void runAllDemos() {
        stringFeatures();
        stringBuilderDemo();
        arrayDemo();
        arraysUtility();
        utilityClasses();
    }

    /**
     * 演示 String 特性
     *
     * String 特点：
     * - 不可变性：每次修改都会创建新对象
     * - 字符串常量池：相同字面量复用对象
     * - 常用方法：length, charAt, substring, split 等
     */
    private void stringFeatures() {
        // String 不可变：每次"修改"都会创建新对象
        String s = "Hello";
        s = s + " World";  // 新建对象，原对象不变

        // 字符串常量池：相同字面量复用同一对象
        String s1 = "abc";
        String s2 = "abc";
        // s1 == s2  true（常量池）

        // new 创建：新对象，不走常量池
        String s3 = new String("abc");
        // s1 == s3 false（new 创建新对象）

        // 常用方法
        String str = "Hello World";
        str.length();           // 长度
        str.charAt(0);          // 指定位置字符
        str.substring(0, 5);    // 子字符串
        str.toUpperCase();      // 转大写
        str.trim();             // 去除两端空格
        str.split(" ");         // 分割
    }

    /**
     * 演示 StringBuilder
     *
     * StringBuilder 特点：
     * - 可变字符串：不需要创建新对象
     * - 高效拼接：避免 String 频繁拼接的性能问题
     * - 常用方法：append, insert, delete, reverse
     * - StringBuilder vs StringBuffer
     */
    private void stringBuilderDemo() {
        // StringBuilder：可变字符串，拼接效率高
        StringBuilder sb = new StringBuilder();
        sb.append("Hello");
        sb.append(" ");
        sb.append("World");
        // sb.toString() -> "Hello World"

        // 常用方法
        sb.insert(5, ",");   // 插入
        sb.delete(5, 6);     // 删除
        sb.reverse();        // 反转
        sb.replace(0, 5, "Hi"); // 替换

        // StringBuilder vs StringBuffer
        // StringBuilder: 非线程安全，性能高（单线程）
        // StringBuffer: 线程安全， synchronized（多线程）
    }

    /**
     * 演示数组
     *
     * 数组特点：
     * - 长度固定：创建后不可改变
     * - 索引从 0 开始
     * - 访问越界会抛出 ArrayIndexOutOfBoundsException
     * - 支持一维和多维数组
     */
    private void arrayDemo() {
        // 一维数组：长度固定，索引从0开始
        int[] nums = new int[5];
        nums[0] = 1;
        nums[1] = 2;

        int[] nums2 = {1, 2, 3, 4, 5};

        // 二维数组
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6}
        };

        // 访问越界会抛出 ArrayIndexOutOfBoundsException
    }

    /**
     * 演示 Arrays 工具类
     *
     * 常用方法：
     * - sort：排序
     * - binarySearch：二分查找
     * - toString：数组转字符串
     * - equals：比较数组内容
     * - fill：填充数组
     * - copyOf：复制数组
     */
    private void arraysUtility() {
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};

        java.util.Arrays.sort(arr);           // 排序
        java.util.Arrays.binarySearch(arr, 5); // 二分查找
        java.util.Arrays.toString(arr);       // 转字符串
        java.util.Arrays.equals(arr, arr);    // 比较数组
        java.util.Arrays.fill(arr, 0);        // 填充
        java.util.Arrays.copyOf(arr, 10);     // 复制
        java.util.Arrays.asList(arr);         // 转为 List
    }

    /**
     * 演示工具类
     *
     * 包括：
     * - Objects：null 安全操作（Java 7+）
     * - Collections：集合操作工具类
     * - Math：数学运算工具类
     */
    private void utilityClasses() {
        // Objects 工具类（Java 7+）
        String nullStr = null;
        String notNullStr = "Hello";

        java.util.Objects.isNull(nullStr);        // true
        java.util.Objects.nonNull(notNullStr);    // true
        java.util.Objects.equals(nullStr, notNullStr);
        java.util.Objects.hashCode(notNullStr);
        java.util.Objects.toString(nullStr, "默认值");  // 第二个参数是 null 时的默认值

        // Collections 工具类
        java.util.List<String> list = new java.util.ArrayList<>();
        list.add("A");

        // 只读集合
        java.util.List<String> unmodifiable = java.util.Collections.unmodifiableList(list);

        // 同步集合
        java.util.List<String> synchronizedList = java.util.Collections.synchronizedList(list);

        // 空集合/空Map
        java.util.List<String> emptyList = java.util.Collections.emptyList();
        java.util.Map<String, Integer> emptyMap = java.util.Collections.emptyMap();

        // Collections 常用操作
        java.util.Collections.sort(list);
        java.util.Collections.reverse(list);
        java.util.Collections.shuffle(list);
        java.util.Collections.max(list);
        java.util.Collections.min(list);
        java.util.Collections.frequency(list, "A");
        java.util.Collections.binarySearch(list, "A");

        // Collections 批量操作
        java.util.Collections.fill(list, "DEFAULT");
        java.util.Collections.replaceAll(list, "A", "B");

        // Math 工具类（Java 1.0+）
        int _abs = java.lang.Math.abs(-5);           // 绝对值
        double _sqrt = java.lang.Math.sqrt(16);      // 平方根
        double _pow = java.lang.Math.pow(2, 3);      // 幂运算
        int _max = java.lang.Math.max(1, 2);         // 最大值
        int _min = java.lang.Math.min(1, 2);         // 最小值
        double _round = java.lang.Math.round(3.5);   // 四舍五入
        double _random = java.lang.Math.random();    // 随机数
        long _floor = java.lang.Math.floorDiv(10, 3); // 向下取整除法

        // Math 静态方法引用（方法引用示例）
        java.util.function.UnaryOperator<Double> sqrtOp = java.lang.Math::sqrt;
        java.util.function.Supplier<Double> randomOp = java.lang.Math::random;
    }
}
