package com.shuai.patterns;

import java.util.*;

/**
 * Java 设计模式演示类
 *
 * 涵盖内容：
 * - 创建型模式：单例、工厂方法、抽象工厂、建造者
 * - 结构型模式：适配器、装饰器、外观、代理
 * - 行为型模式：观察者、策略、命令、模板方法、状态
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsDesignPatternsDemo {

    /**
     * 执行所有设计模式演示
     */
    public void runAllDemos() {
        // 创建型模式
        singletonDemo();
        factoryDemo();
        builderDemo();
        prototypeDemo();

        // 结构型模式
        adapterDemo();
        decoratorDemo();
        facadeDemo();

        // 行为型模式
        observerDemo();
        strategyDemo();
        commandDemo();
    }

    // ==================== 创建型模式 ====================

    /**
     * 演示单例模式 (Singleton)
     *
     * 特点：
     * - 只有一个实例
     * - 自行创建实例
     * - 向整个系统提供实例
     */
    private void singletonDemo() {
        // 饿汉式
        HungrySingleton instance1 = HungrySingleton.getInstance();

        // 懒汉式（线程安全）
        LazySingleton instance2 = LazySingleton.getInstance();

        // 双重检查锁
        DoubleCheckSingleton instance3 = DoubleCheckSingleton.getInstance();

        // 枚举式（推荐）
        EnumSingleton instance4 = EnumSingleton.INSTANCE;
    }

    /**
     * 演示工厂模式 (Factory)
     *
     * 特点：
     * - 定义创建对象的接口
     * - 让子类决定实例化哪个类
     * - 延迟实例化
     */
    private void factoryDemo() {
        // 简单工厂
        Product product1 = SimpleFactory.createProduct("A");
        Product product2 = SimpleFactory.createProduct("B");

        // 工厂方法
        Factory factoryA = new ConcreteFactoryA();
        Product product3 = factoryA.createProduct();

        // 抽象工厂
        AbstractFactory abstractFactory = new ConcreteFactory();
        Product productA = abstractFactory.createProductA();
        Product productB = abstractFactory.createProductB();
    }

    /**
     * 演示建造者模式 (Builder)
     *
     * 特点：
     * - 将复杂对象的构建与表示分离
     * - 相同的构建过程可以创建不同的表示
     * - 支持链式调用
     */
    private void builderDemo() {
        // 传统建造者
        User user1 = new User.UserBuilder("张三", 25)
                .email("zhang@example.com")
                .phone("13800138000")
                .address("北京")
                .build();

        // Lombok @Builder（如果使用）
        // User user2 = User.builder()
        //     .name("李四")
        //     .age(30)
        //     .build();
    }

    /**
     * 演示原型模式 (Prototype)
     *
     * 特点：
     * - 通过复制已有对象来创建新对象
     * - 避免重复初始化对象
     * - 深拷贝 vs 浅拷贝
     */
    private void prototypeDemo() {
        // 创建原型对象
        Circle circle = new Circle(10, "red");

        // 克隆对象
        Circle clonedCircle = (Circle) circle.clone();
        System.out.println("Original: " + circle);
        System.out.println("Cloned: " + clonedCircle);

        // 修改克隆对象不影响原对象
        clonedCircle.setRadius(20);
        clonedCircle.setColor("blue");
        System.out.println("After modification:");
        System.out.println("Original: " + circle);
        System.out.println("Cloned: " + clonedCircle);
    }

    // ==================== 结构型模式 ====================

    /**
     * 演示适配器模式 (Adapter)
     *
     * 特点：
     * - 将一个类的接口转换成客户期望的另一个接口
     * - 使原本不兼容的类可以合作
     * - 类适配器（继承）和对象适配器（组合）
     */
    private void adapterDemo() {
        // 对象适配器
        MediaPlayer player = new AudioPlayer();
        player.play("mp3", "song.mp3");

        // 使用适配器播放新格式
        AdvancedMediaPlayer advancedPlayer = new VlcPlayer();
        MediaAdapter adapter = new MediaAdapter(advancedPlayer);
        adapter.play("vlc", "movie.vlc");
    }

    /**
     * 演示装饰器模式 (Decorator)
     *
     * 特点：
     * - 动态添加对象额外功能
     * - 比继承更灵活
     * - 运行时组合功能
     */
    private void decoratorDemo() {
        // 基础组件
        Coffee coffee = new SimpleCoffee();
        System.out.println(coffee.getDescription() + " - $" + coffee.getCost());

        // 添加牛奶
        coffee = new MilkDecorator(coffee);
        System.out.println(coffee.getDescription() + " - $" + coffee.getCost());

        // 添加糖
        coffee = new SugarDecorator(coffee);
        System.out.println(coffee.getDescription() + " - $" + coffee.getCost());
    }

    /**
     * 演示外观模式 (Facade)
     *
     * 特点：
     * - 为子系统中的一组接口提供统一接口
     * - 定义高层接口，使子系统更易使用
     * - 简化客户与子系统的交互
     */
    private void facadeDemo() {
        // 不使用外观：需要逐个调用子系统
        Computer cpu = new Computer();
        Memory memory = new Memory();
        HardDrive hardDrive = new HardDrive();
        cpu.startUp();
        memory.load();
        hardDrive.read();

        // 使用外观：一键启动
        ComputerFacade computer = new ComputerFacade();
        computer.start();
    }

    // ==================== 行为型模式 ====================

    /**
     * 演示观察者模式 (Observer)
     *
     * 特点：
     * - 定义对象间的一对多依赖
     * - 当对象状态改变时，所有依赖者收到通知
     * - 解耦主题和观察者
     */
    private void observerDemo() {
        // 主题
        NewsSubject subject = new NewsSubject();

        // 观察者
        NewsObserver observer1 = new NewsObserver("用户A");
        NewsObserver observer2 = new NewsObserver("用户B");

        // 订阅
        subject.attach(observer1);
        subject.attach(observer2);

        // 发布新闻
        subject.publishNews("Java 21 正式发布！");

        // 取消订阅
        subject.detach(observer1);
    }

    /**
     * 演示策略模式 (Strategy)
     *
     * 特点：
     * - 定义一系列算法
     * - 封装每个算法
     - 使它们可以互换
     * - 算法的变化不影响使用它的客户
     */
    private void strategyDemo() {
        // 使用不同策略
        PaymentContext context = new PaymentContext();

        context.setStrategy(new AlipayStrategy());
        context.pay(100.0);

        context.setStrategy(new WechatPayStrategy());
        context.pay(200.0);

        context.setStrategy(new CreditCardStrategy());
        context.pay(300.0);
    }

    /**
     * 演示命令模式 (Command)
     *
     * 特点：
     * - 将请求封装为对象
     * - 支持撤销和重做操作
     * - 解耦请求发送者和接收者
     */
    private void commandDemo() {
        // 接收者
        Light light = new Light();

        // 命令
        Command onCommand = new LightOnCommand(light);
        Command offCommand = new LightOffCommand(light);

        // 调用者
        RemoteControl remote = new RemoteControl();

        // 执行命令
        remote.setCommand(onCommand);
        remote.pressButton();

        remote.setCommand(offCommand);
        remote.pressButton();
    }

    // ==================== 内部类定义 ====================

    // --- 单例模式 ---

    static class HungrySingleton {
        private static final HungrySingleton INSTANCE = new HungrySingleton();
        private HungrySingleton() {}
        public static HungrySingleton getInstance() { return INSTANCE; }
    }

    static class LazySingleton {
        private static volatile LazySingleton INSTANCE;
        private LazySingleton() {}
        public static synchronized LazySingleton getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new LazySingleton();
            }
            return INSTANCE;
        }
    }

    static class DoubleCheckSingleton {
        private static volatile DoubleCheckSingleton INSTANCE;
        private DoubleCheckSingleton() {}
        public static DoubleCheckSingleton getInstance() {
            if (INSTANCE == null) {
                synchronized (DoubleCheckSingleton.class) {
                    if (INSTANCE == null) {
                        INSTANCE = new DoubleCheckSingleton();
                    }
                }
            }
            return INSTANCE;
        }
    }

    enum EnumSingleton {
        INSTANCE
    }

    // --- 工厂模式 ---

    interface Product {
        String getName();
    }

    static class ConcreteProductA implements Product {
        public String getName() { return "Product A"; }
    }

    static class ConcreteProductB implements Product {
        public String getName() { return "Product B"; }
    }

    static class SimpleFactory {
        public static Product createProduct(String type) {
            return switch (type) {
                case "A" -> new ConcreteProductA();
                case "B" -> new ConcreteProductB();
                default -> throw new IllegalArgumentException("Unknown type");
            };
        }
    }

    interface Factory {
        Product createProduct();
    }

    static class ConcreteFactoryA implements Factory {
        public Product createProduct() { return new ConcreteProductA(); }
    }

    interface AbstractFactory {
        Product createProductA();
        Product createProductB();
    }

    static class ConcreteFactory implements AbstractFactory {
        public Product createProductA() { return new ConcreteProductA(); }
        public Product createProductB() { return new ConcreteProductB(); }
    }

    // --- 建造者模式 ---

    static class User {
        private final String name;
        private final int age;
        private final String email;
        private final String phone;
        private final String address;

        private User(UserBuilder builder) {
            this.name = builder.name;
            this.age = builder.age;
            this.email = builder.email;
            this.phone = builder.phone;
            this.address = builder.address;
        }

        static class UserBuilder {
            private final String name;
            private final int age;
            private String email;
            private String phone;
            private String address;

            public UserBuilder(String name, int age) {
                this.name = name;
                this.age = age;
            }

            public UserBuilder email(String email) {
                this.email = email;
                return this;
            }

            public UserBuilder phone(String phone) {
                this.phone = phone;
                return this;
            }

            public UserBuilder address(String address) {
                this.address = address;
                return this;
            }

            public User build() {
                return new User(this);
            }
        }
    }

    // --- 适配器模式 ---

    interface MediaPlayer {
        void play(String audioType, String fileName);
    }

    class AudioPlayer implements MediaPlayer {
        public void play(String audioType, String fileName) {
            System.out.println("Playing " + audioType + " file: " + fileName);
        }
    }

    interface AdvancedMediaPlayer {
        void playVlc(String fileName);
        void playMp4(String fileName);
    }

    class VlcPlayer implements AdvancedMediaPlayer {
        public void playVlc(String fileName) { System.out.println("Playing vlc: " + fileName); }
        public void playMp4(String fileName) {}
    }

    class Mp4Player implements AdvancedMediaPlayer {
        public void playVlc(String fileName) {}
        public void playMp4(String fileName) { System.out.println("Playing mp4: " + fileName); }
    }

    class MediaAdapter implements MediaPlayer {
        private AdvancedMediaPlayer advancedPlayer;

        public MediaAdapter(AdvancedMediaPlayer player) {
            this.advancedPlayer = player;
        }

        public void play(String audioType, String fileName) {
            if ("vlc".equalsIgnoreCase(audioType)) {
                advancedPlayer.playVlc(fileName);
            } else if ("mp4".equalsIgnoreCase(audioType)) {
                advancedPlayer.playMp4(fileName);
            }
        }
    }

    // --- 装饰器模式 ---

    interface Coffee {
        String getDescription();
        double getCost();
    }

    static class SimpleCoffee implements Coffee {
        public String getDescription() { return "Simple Coffee"; }
        public double getCost() { return 5.0; }
    }

    static abstract class CoffeeDecorator implements Coffee {
        protected Coffee decoratedCoffee;

        public CoffeeDecorator(Coffee coffee) {
            this.decoratedCoffee = coffee;
        }

        public String getDescription() { return decoratedCoffee.getDescription(); }
        public double getCost() { return decoratedCoffee.getCost(); }
    }

    static class MilkDecorator extends CoffeeDecorator {
        public MilkDecorator(Coffee coffee) { super(coffee); }

        public String getDescription() { return super.getDescription() + ", Milk"; }
        public double getCost() { return super.getCost() + 1.5; }
    }

    static class SugarDecorator extends CoffeeDecorator {
        public SugarDecorator(Coffee coffee) { super(coffee); }

        public String getDescription() { return super.getDescription() + ", Sugar"; }
        public double getCost() { return super.getCost() + 0.5; }
    }

    // --- 外观模式 ---

    static class Computer {
        public void startUp() { System.out.println("Computer starting..."); }
    }

    static class Memory {
        public void load() { System.out.println("Memory loading..."); }
    }

    static class HardDrive {
        public void read() { System.out.println("HardDrive reading..."); }
    }

    static class ComputerFacade {
        private Computer computer = new Computer();
        private Memory memory = new Memory();
        private HardDrive hardDrive = new HardDrive();

        public void start() {
            computer.startUp();
            memory.load();
            hardDrive.read();
        }
    }

    // --- 观察者模式 ---

    interface Observer {
        void update(String news);
    }

    static class NewsObserver implements Observer {
        private String name;

        public NewsObserver(String name) { this.name = name; }

        public void update(String news) {
            System.out.println(name + " received news: " + news);
        }
    }

    static class NewsSubject {
        private List<Observer> observers = new ArrayList<>();

        public void attach(Observer observer) {
            observers.add(observer);
        }

        public void detach(Observer observer) {
            observers.remove(observer);
        }

        public void publishNews(String news) {
            for (Observer observer : observers) {
                observer.update(news);
            }
        }
    }

    // --- 策略模式 ---

    interface PaymentStrategy {
        void pay(double amount);
    }

    static class AlipayStrategy implements PaymentStrategy {
        public void pay(double amount) { System.out.println("Paid " + amount + " via Alipay"); }
    }

    static class WechatPayStrategy implements PaymentStrategy {
        public void pay(double amount) { System.out.println("Paid " + amount + " via WeChat"); }
    }

    static class CreditCardStrategy implements PaymentStrategy {
        public void pay(double amount) { System.out.println("Paid " + amount + " via Credit Card"); }
    }

    static class PaymentContext {
        private PaymentStrategy strategy;

        public void setStrategy(PaymentStrategy strategy) {
            this.strategy = strategy;
        }

        public void pay(double amount) {
            if (strategy != null) {
                strategy.pay(amount);
            }
        }
    }

    // --- 命令模式 ---

    interface Command {
        void execute();
    }

    static class Light {
        public void on() { System.out.println("Light is ON"); }
        public void off() { System.out.println("Light is OFF"); }
    }

    static class LightOnCommand implements Command {
        private Light light;

        public LightOnCommand(Light light) { this.light = light; }

        public void execute() { light.on(); }
    }

    static class LightOffCommand implements Command {
        private Light light;

        public LightOffCommand(Light light) { this.light = light; }

        public void execute() { light.off(); }
    }

    static class RemoteControl {
        private Command command;

        public void setCommand(Command command) { this.command = command; }

        public void pressButton() {
            if (command != null) {
                command.execute();
            }
        }
    }

    // --- 原型模式 ---

    static abstract class Shape implements Cloneable {
        protected String type;
        protected String color;

        public Shape(String type, String color) {
            this.type = type;
            this.color = color;
        }

        public String getType() { return type; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public void setRadius(int radius) { }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return "Shape{type='" + type + "', color='" + color + "'}";
        }
    }

    static class Circle extends Shape {
        private int radius;

        public Circle(int radius, String color) {
            super("Circle", color);
            this.radius = radius;
        }

        public int getRadius() { return radius; }
        public void setRadius(int radius) { this.radius = radius; }

        @Override
        public Object clone() {
            return super.clone();
        }

        @Override
        public String toString() {
            return "Circle{radius=" + radius + ", color='" + color + "'}";
        }
    }

    static class Rectangle extends Shape {
        private int width;
        private int height;

        public Rectangle(int width, int height, String color) {
            super("Rectangle", color);
            this.width = width;
            this.height = height;
        }

        @Override
        public Object clone() {
            return super.clone();
        }

        @Override
        public String toString() {
            return "Rectangle{width=" + width + ", height=" + height + ", color='" + color + "'}";
        }
    }
}
