package com.shuai.advanced;

/**
 * Java 泛型演示类
 *
 * 涵盖内容：
 * - 泛型类：类型参数化的类
 * - 泛型方法：类型参数化的方法
 * - 通配符：?, ? extends T, ? super T
 * - 泛型边界：extends 限制上限
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsGenericsDemo {

    /**
     * 执行所有泛型演示
     */
    public void runAllDemos() {
        genericClass();
        genericMethod();
        wildcard();
        genericBound();
    }

    /**
     * 演示泛型类
     *
     * 包括：
     * - 单个泛型参数（Box<T>）
     * - 多个泛型参数（Pair<K, V>）
     * - 泛型类型安全：编译时类型检查
     */
    private void genericClass() {
        // 泛型类定义
        Box<String> stringBox = new Box<>();
        stringBox.setContent("Hello");
        String content = stringBox.getContent();

        Box<Integer> intBox = new Box<>();
        intBox.setContent(100);
        Integer intContent = intBox.getContent();

        // 多个泛型参数
        Pair<String, Integer> pair = new Pair<>("年龄", 25);
        String key = pair.getKey();
        Integer value = pair.getValue();
    }

    /**
     * 演示泛型方法
     *
     * 特点：
     * - 方法声明中使用类型参数
     * - 可以在返回值和参数中使用泛型
     * - 支持不同类型的数组打印
     */
    private void genericMethod() {
        // 泛型方法
        String[] names = {"张三", "李四", "王五"};
        printArray(names);

        Integer[] nums = {1, 2, 3};
        printArray(nums);

        // 泛型返回值
        Number num = create(42);
    }

    /**
     * 演示通配符
     *
     * 通配符类型：
     * - 无界通配符（?）：接受任意类型
     * - 上界通配符（? extends Number）：只读，不可添加
     * - 下界通配符（? super Integer）：可添加 Number 子类
     */
    private void wildcard() {
        // 无界通配符
        printList(java.util.Arrays.asList(1, 2, 3));
        printList(java.util.Arrays.asList("a", "b", "c"));

        // 上界通配符（只读）
        java.util.List<? extends Number> numbers = java.util.Arrays.asList(1, 2, 3);
        processNumbers(numbers);

        // 下界通配符（可添加）
        java.util.List<java.lang.Integer> integers = new java.util.ArrayList<>();
        addNumbers(integers);
    }

    /**
     * 演示泛型边界
     *
     * 上界限制：
     * - T extends Number：T 只能是 Number 或其子类
     * - 可以使用类型的方法（如 doubleValue）
     * - Comparable<T>：限制可比较的类型
     */
    private void genericBound() {
        // 上界限制
        Calculator<Integer> intCalc = new Calculator<>(10);
        double intDouble = intCalc.doubleValue();

        Calculator<Double> dblCalc = new Calculator<>(3.14);
        double dblDouble = dblCalc.doubleValue();

        // 可比较排序
        String[] strings = {"apple", "banana", "cherry"};
        Sorter<String> stringSorter = new Sorter<>();
        stringSorter.sort(strings);
    }

    // 泛型类
    static class Box<T> {
        private T content;

        public void setContent(T content) { this.content = content; }
        public T getContent() { return content; }
    }

    // 多个泛型参数
    static class Pair<K, V> {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }
    }

    // 泛型方法
    private <T> void printArray(T[] array) {
        for (T element : array) {
            T e = element;
        }
    }

    // 泛型返回值
    private <T> T create(T value) {
        return value;
    }

    // 无界通配符
    private void printList(java.util.List<?> list) {
        for (Object obj : list) {
            Object o = obj;
        }
    }

    // 上界通配符（只读）
    private void processNumbers(java.util.List<? extends Number> list) {
        for (Number n : list) {
            Number num = n;
        }
    }

    // 下界通配符（可添加）
    private void addNumbers(java.util.List<? super java.lang.Integer> list) {
        list.add(100);
        list.add(200);
    }

    // 泛型边界限制
    static class Calculator<T extends java.lang.Number> {
        private T num;

        public Calculator(T num) {
            this.num = num;
        }

        public double doubleValue() {
            return num.doubleValue();
        }
    }

    // 上界限制接口
    static class Sorter<T extends java.lang.Comparable<T>> {
        public void sort(T[] arr) {
            java.util.Arrays.sort(arr);
        }
    }
}
