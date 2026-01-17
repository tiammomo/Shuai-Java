package com.shuai.modern;

import java.util.*;
import java.util.function.*;

/**
 * Java 语法糖演示类
 *
 * 涵盖内容：
 * - 类型擦除：泛型在运行时的表现
 * - 自动装箱/拆箱：基本类型与包装类转换
 * - 增强 for 循环：foreach 语法
 * - Lambda 表达式：函数式编程
 * - Stream API：函数式数据处理
 * - Switch 表达式：Java 14+ 语法
 * - Record 和 Sealed：Java 14+ / 17+ 特性
 * - var 关键字：局部变量类型推断（Java 10+）
 * - 文本块：多行字符串（Java 15+）
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsSyntaxSugarDemo {

    /**
     * 执行所有语法糖演示
     */
    public void runAllDemos() {
        typeErasure();
        autoBoxing();
        enhancedForLoop();
        lambdaExpressions();
        streamApi();
        switchExpressions();
        recordsAndSealed();
        varKeyword();
        textBlocks();
    }

    /**
     * 演示类型擦除
     *
     * 泛型在编译后类型信息会丢失：
     * - List<String> 和 List<Integer> 在运行时都是 List
     * - 泛型仅在编译时提供类型检查
     * - 泛型数组创建受限
     */
    private void typeErasure() {
        // 类型擦除: 运行时泛型信息丢失
        List<String> stringList = new ArrayList<>();
        List<Integer> intList = new ArrayList<>();

        // stringList.getClass() == intList.getClass() (都是 ArrayList.class)
        // 泛型信息仅在编译时存在
    }

    /**
     * 演示自动装箱/拆箱
     *
     * 基本类型与包装类自动转换：
     * - 装箱（Boxing）：int -> Integer
     * - 拆箱（Unboxing）：Integer -> int
     * - Integer 缓存：-128 到 127 复用对象
     * - 注意：缓存范围外使用 equals 比较
     */
    private void autoBoxing() {
        // 自动装箱: int -> Integer
        Integer autoBox = 100;

        // 自动拆箱: Integer -> int
        int autoUnbox = autoBox;

        // Integer 缓存: -128 到 127
        Integer cached1 = 127;
        Integer cached2 = 127;
        // cached1 == cached2 (相同对象)

        Integer notCached1 = 128;
        Integer notCached2 = 128;
        // notCached1 != notCached2 (不同对象)

        // 使用 equals 比较
        boolean isEqual = cached1.equals(cached2);

        Supplier<Integer> randomInt = () -> (int) (Math.random() * 100);
    }

    /**
     * 演示增强 for 循环
     *
     * 简化集合/数组遍历：
     * - for (T item : collection)
     * - 不需要索引变量
     * - 更简洁易读
     */
    private void enhancedForLoop() {
        int[] numbers = {1, 2, 3, 4, 5};

        // 增强 for 循环
        for (int num : numbers) {
            int n = num;
        }

        List<String> fruits = Arrays.asList("苹果", "香蕉", "橙子");
        fruits.forEach(fruit -> {
            String f = fruit;
        });

        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.forEach((key, value) -> {
            String k = key;
            Integer v = value;
        });
    }

    /**
     * 演示 Lambda 表达式
     *
     * Java 8+ 函数式编程：
     * - (params) -> expression 或 (params) -> { statements }
     * - 方法引用：ClassName::methodName
     * - 简化函数式接口实现
     */
    private void lambdaExpressions() {
        List<String> names = Arrays.asList("张三", "李四", "王五");

        // Lambda 表达式
        names.forEach(name -> {
            String n = name;
        });

        // 方法引用
        names.forEach(System.out::println);

        Calculator add = (a, b) -> a + b;
        Calculator multiply = (a, b) -> a * b;
        int addResult = add.apply(10, 5);
        int multiplyResult = multiply.apply(10, 5);

        // Stream 过滤
        names.stream()
             .filter(name -> name.startsWith("张"))
             .map(name -> "贵姓: " + name)
             .forEach(System.out::println);
    }

    /**
     * 演示 Stream API
     *
     * 函数式数据处理（Java 8+）：
     * - filter：过滤元素
     * - map：转换元素
     * - sorted：排序
     * - collect：收集结果
     * - reduce：聚合计算
     * - match：anyMatch, allMatch, noneMatch
     * - find：findFirst, findAny
     */
    private void streamApi() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // filter: 过滤偶数
        List<Integer> evens = numbers.stream()
                .filter(n -> n % 2 == 0)
                .toList();

        // map: 每个元素乘以2
        List<Integer> doubled = numbers.stream()
                .map(n -> n * 2)
                .toList();

        // mapToInt: 转换为基本类型
        int sum = numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();

        // limit: 取前5个
        List<Integer> limited = numbers.stream()
                .limit(5)
                .toList();

        // skip: 跳过前3个
        List<Integer> skipped = numbers.stream()
                .skip(3)
                .toList();

        // distinct: 去重
        List<Integer> withDupes = Arrays.asList(1, 2, 2, 3, 3, 3);
        List<Integer> distinct = withDupes.stream()
                .distinct()
                .toList();

        // sorted: 排序
        List<Integer> unsorted = Arrays.asList(5, 2, 8, 1, 9);
        List<Integer> sorted = unsorted.stream()
                .sorted()
                .toList();

        // sorted with comparator
        List<String> strs = Arrays.asList("ccc", "a", "bb");
        List<String> sortedStrs = strs.stream()
                .sorted(Comparator.comparing(String::length))
                .toList();

        // anyMatch: 是否有偶数
        boolean hasEven = numbers.stream()
                .anyMatch(n -> n % 2 == 0);

        // allMatch: 是否全部大于0
        boolean allPositive = numbers.stream()
                .allMatch(n -> n > 0);

        // noneMatch: 是否没有负数
        boolean noNegative = numbers.stream()
                .noneMatch(n -> n < 0);

        // findFirst: 找第一个偶数
        Integer firstEven = numbers.stream()
                .filter(n -> n % 2 == 0)
                .findFirst()
                .orElse(null);

        // findAny: 找任意偶数
        Integer anyEven = numbers.stream()
                .filter(n -> n % 2 == 0)
                .findAny()
                .orElse(null);

        // count: 计数
        long evenCount = numbers.stream()
                .filter(n -> n % 2 == 0)
                .count();

        // reduce: 求和
        Integer reducedSum = numbers.stream()
                .reduce(0, Integer::sum);

        // collect: 收集到 List
        List<Integer> collected = numbers.stream()
                .filter(n -> n > 5)
                .collect(java.util.stream.Collectors.toList());

        // collect: 收集到 Set
        java.util.Set<Integer> toSet = numbers.stream()
                .collect(java.util.stream.Collectors.toSet());

        // collect: 分组
        java.util.Map<String, List<Integer>> grouped = numbers.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        n -> n % 2 == 0 ? "偶数" : "奇数"
                ));

        // collect: 统计
        java.util.IntSummaryStatistics stats = numbers.stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();
    }

    /**
     * 演示 Switch 表达式
     *
     * Java 14+ 特性：
     * - 箭头语法：case -> value
     * - 多标签合并：case A, B ->
     * - Switch 作为表达式返回值
     * - 模式匹配（Java 21+）
     */
    private void switchExpressions() {
        int score = 85;

        // Switch 表达式 (Java 14+)
        String grade = switch (score / 10) {
            case 9, 10 -> "A";
            case 8 -> "B";
            case 7 -> "C";
            default -> "D";
        };

        Day day = Day.FRIDAY;
        String workType = switch (day) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "工作日";
            case SATURDAY, SUNDAY -> "周末";
        };

        // 对象模式匹配 (Java 21+)
        Object obj = "Hello";
        String result = switch (obj) {
            case String s -> "字符串，长度: " + s.length();
            case Integer i -> "整数: " + i;
            case null -> "空值";
            default -> "未知类型";
        };
    }

    /**
     * 演示 Record 和 Sealed Class
     *
     * Record（Java 14+）：
     * - 自动生成 toString, equals, hashCode
     * - 自动生成 getter 方法
     * - 不可变数据结构
     *
     * Sealed Class（Java 17+）：
     * - 限制接口/抽象类的实现类
     * - permits 指定允许的实现类
     * - 用于模式匹配优化
     */
    private void recordsAndSealed() {
        // Record (Java 14+): 自动生成 equals/hashCode/toString
        Point p = new Point(10, 20);
        int x = p.x();
        int y = p.y();
        boolean equals = p.equals(new Point(10, 20));
        int hashCode = p.hashCode();

        // Sealed Class (Java 17+): 限制实现类
        GeoShape circle = new Circle(5.0);
        GeoShape rectangle = new Rectangle(3.0, 4.0);

        double circleArea = calculateArea(circle);
        double rectangleArea = calculateArea(rectangle);
    }

    private double calculateArea(GeoShape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.width() * r.height();
        };
    }

    /**
     * 演示 var 关键字
     *
     * Java 10+ 局部变量类型推断：
     * - var 替代具体类型声明
     * - 编译器根据初始化值推断类型
     * - 只适用于局部变量
     * - 提高代码简洁性
     */
    private void varKeyword() {
        // var 关键字 (Java 10+): 局部变量类型推断
        var message = "Hello World";
        var numbers = List.of(1, 2, 3, 4, 5);
        var map = new HashMap<String, Integer>();
        map.put("A", 1);

        var comparator = Comparator.<String, String>comparing(String::toLowerCase);
        List<String> strs = Arrays.asList("b", "A", "c");
        strs.sort(comparator);
    }

    /**
     * 演示文本块
     *
     * Java 15+ 多行字符串：
     * - 使用三引号 """ 定义
     * - 保留文本格式（换行、缩进）
     * - 适合 JSON、HTML、SQL 等
     */
    private void textBlocks() {
        // 文本块 (Java 15+): 多行字符串
        String json = """
            {
                "name": "张三",
                "age": 25,
                "city": "北京"
            }
            """;

        String html = """
            <html>
                <body>
                    <h1>标题</h1>
                </body>
            </html>
            """;
    }

    @FunctionalInterface
    interface Calculator {
        int apply(int a, int b);
    }

    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    record Point(int x, int y) {}

    sealed interface GeoShape permits Circle, Rectangle {}
    record Circle(double radius) implements GeoShape {}
    record Rectangle(double width, double height) implements GeoShape {}
}
