package com.shuai.oop;

/**
 * Java 面向对象演示类
 *
 * 涵盖内容：
 * - 类与对象：定义、创建、使用
 * - 封装：private 属性 + getter/setter
 * - 继承：extends, super, 方法重写
 * - 多态：方法重载、方法重写、向上/向下转型
 * - 抽象类与接口：abstract class vs interface
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsOOPDemo {

    /**
     * 执行所有面向对象演示
     */
    public void runAllDemos() {
        classAndObject();
        encapsulation();
        inheritance();
        polymorphism();
        abstractClassVsInterface();
    }

    /**
     * 演示类与对象
     *
     * 包括：
     * - 类的定义：属性、构造方法、普通方法
     * - 对象的创建：new 关键字
     * - 对象的使用：调用属性和方法
     */
    private void classAndObject() {
        // 定义类
        Person person = new Person("张三", 25);
        String name = person.getName();
        int age = person.getAge();
        person.sayHello();
    }

    /**
     * 演示封装
     *
     * 封装原则：
     * - 将属性声明为 private（隐藏内部状态）
     * - 提供 public 的 getter/setter 方法（受控访问）
     * - 可以在 setter 中添加验证逻辑
     */
    private void encapsulation() {
        // 使用封装好的类
        User user = new User();
        user.setName("李四");
        user.setEmail("li@example.com");
        String name = user.getName();
        String email = user.getEmail();
    }

    /**
     * 演示继承
     *
     * 继承特点：
     * - 子类继承父类的属性和方法
     * - 使用 super 调用父类构造方法
     * - 可以重写（Override）父类方法
     * - 向上转型：父类引用指向子类对象
     * - 向下转型：需要 instanceof 检查
     */
    private void inheritance() {
        // 子类继承父类
        Dog dog = new Dog("旺财");
        dog.eat();
        dog.bark();

        // 向上转型
        Animal animal = new Dog("小白");
        animal.eat();  // 调用 Dog 的 eat

        // 向下转型
        if (animal instanceof Dog) {
            Dog dog2 = (Dog) animal;
            dog2.bark();
        }
    }

    /**
     * 演示多态
     *
     * 多态类型：
     * - 编译时多态：方法重载（Overload）
     * - 运行时多态：方法重写（Override）
     * - 同一方法调用根据对象实际类型执行不同逻辑
     */
    private void polymorphism() {
        // 方法重载（编译时多态）
        Calculator calc = new Calculator();
        int sum = calc.add(1, 2);
        int sum3 = calc.add(1, 2, 3);

        // 方法重写（运行时多态）
        Shape circle = new Circle(5.0);
        double area1 = circle.area();

        Shape rectangle = new Rectangle(3.0, 4.0);
        double area2 = rectangle.area();
    }

    /**
     * 演示抽象类与接口的区别
     *
     * 抽象类：
     * - 用 abstract 修饰，可以有抽象方法和具体方法
     * - 可以有实例变量、构造方法
     * - 只能单继承
     *
     * 接口：
     * - 所有方法默认是抽象的（Java 8前）
     * - Java 8+ 支持默认方法和静态方法
     * - Java 9+ 支持私有方法
     * - 可以多实现
     */
    private void abstractClassVsInterface() {
        // 使用抽象类
        Shape circle = new Circle(2.0);
        double area = circle.area();
        circle.printInfo();

        // 使用接口
        Flyable bird = new Bird();
        bird.fly();
        bird.land();
    }

    // 内部类定义

    public static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
        public void sayHello() {
            // Hello, 我是 {name}
        }
    }

    public static class User {
        private String name;
        private String email;

        public void setName(String name) { this.name = name; }
        public String getName() { return name; }
        public void setEmail(String email) { this.email = email; }
        public String getEmail() { return email; }
    }

    public static class Animal {
        protected String name;

        public Animal(String name) {
            this.name = name;
        }

        public String getName() { return name; }

        public void eat() {
            // {name} 在吃饭
        }
    }

    public static class Dog extends Animal {
        public Dog(String name) {
            super(name);
        }

        @Override
        public void eat() {
            // {name} 吃狗粮
        }

        public void bark() {
            // {name} 汪汪叫
        }
    }

    public static class Calculator {
        public int add(int a, int b) { return a + b; }
        public int add(int a, int b, int c) { return a + b + c; }
    }

    public abstract static class Shape {
        public abstract double area();

        public void printInfo() {
            // 面积为: {area()}
        }
    }

    public static class Circle extends Shape {
        private double radius;

        public Circle(double radius) {
            this.radius = radius;
        }

        @Override
        public double area() {
            return Math.PI * radius * radius;
        }
    }

    public static class Rectangle extends Shape {
        private double width;
        private double height;

        public Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public double area() {
            return width * height;
        }
    }

    public interface Flyable {
        void fly();
        default void land() { /* 着陆 */ }
    }

    public static class Bird implements Flyable {
        @Override
        public void fly() {
            // 小鸟飞翔
        }
    }
}
