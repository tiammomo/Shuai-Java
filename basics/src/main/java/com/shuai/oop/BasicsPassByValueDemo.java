package com.shuai.oop;

import java.util.*;
import java.util.function.*;

/**
 * Java 值传递演示类
 *
 * 涵盖内容：
 * - 基本类型传递：值副本，修改不影响原值
 * - 引用类型传递：引用副本，可修改对象状态
 * - 集合传递：可修改内容，重新赋值不影响原引用
 * - 不可变性：String、Record、Stream 的不可变特性
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsPassByValueDemo {

    /**
     * 执行所有值传递演示
     */
    public void runAllDemos() {
        primitivePassByValue();
        referencePassByValue();
        collectionPassByValue();
        immutabilityDemo();
    }

    /**
     * 演示基本类型的值传递
     *
     * 特点：
     * - 传递的是值的副本
     * - 方法内修改不影响原变量
     */
    private void primitivePassByValue() {
        // 基本类型传递值副本，方法内修改不影响原变量
        int number = 100;
        modifyPrimitive(number);
        // number 仍是 100
    }

    private void modifyPrimitive(int num) {
        num = 999;  // 只修改副本
    }

    private int incrementAndGet(Supplier<Integer> supplier) {
        return supplier.get() + 1;
    }

    /**
     * 演示引用类型的值传递
     *
     * 特点：
     * - 传递的是引用地址的副本
     * - 可以通过引用修改对象的内部状态
     * - 重新赋值引用不影响原引用
     */
    private void referencePassByValue() {
        // 引用类型传递引用地址副本，可通过引用修改对象状态
        Person person = new Person("张三", 25);
        modifyPerson(person);
        // person.age 变为 99

        // 重新赋值引用不影响原引用
        reassignPerson(person);
        // person 引用未改变

        modifyPersonWithConsumer(person, p -> p.setAge(99));
    }

    private void modifyPerson(Person p) {
        p.setAge(99);  // 修改对象状态
    }

    private void reassignPerson(Person p) {
        p = new Person("王五", 30);  // 只修改引用副本
    }

    private void modifyPersonWithConsumer(Person p, Consumer<Person> modifier) {
        modifier.accept(p);
    }

    /**
     * 演示集合的值传递
     *
     * 特点：
     * - 可以修改集合内容（增删改）
     * - 重新赋值集合不影响原引用
     * - 可创建不可修改集合（unmodifiableList）
     */
    private void collectionPassByValue() {
        // 集合：可通过引用修改内容
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));
        modifyList(list);
        // list 变为 [2, 3, 4]

        // 重新赋值不影响原引用
        replaceList(list);
        // list 引用未改变

        // 创建不可修改集合
        List<Integer> unmodifiable = Collections.unmodifiableList(list);
    }

    private void modifyList(List<Integer> list) {
        list.add(4);
        list.remove(0);
    }

    private void replaceList(List<Integer> list) {
        list = new ArrayList<>(Arrays.asList(9, 9, 9));
    }

    /**
     * 演示不可变性
     *
     * 不可变对象特点：
     * - 状态创建后永不改变
     * - String：每次修改创建新对象
     * - Record：自动生成不可变数据类（Java 14+）
     * - Stream：生成新集合，不修改原数据
     */
    private void immutabilityDemo() {
        // String 不可变性
        String str = "Hello";
        String newStr = str.concat(" World");
        // str 仍是 "Hello"

        // Function 接口
        Function<String, String> addSuffix = s -> s + "!";

        // Stream 保持不可变性
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> doubled = numbers.stream()
                .map(n -> n * 2)
                .toList();

        // Record (Java 14+)
        Point p = new Point(10, 20);
        p.x();
        p.y();
    }

    static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public void setAge(int age) { this.age = age; }
        public String getName() { return name; }
        public int getAge() { return age; }
    }

    record Point(int x, int y) {}
}
