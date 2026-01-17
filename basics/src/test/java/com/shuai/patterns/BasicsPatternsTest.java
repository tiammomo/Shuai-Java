package com.shuai.patterns;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 设计模式测试类
 */
@DisplayName("设计模式测试")
class BasicsPatternsTest {

    @Test
    @DisplayName("单例模式 - 饿汉式")
    void singletonEagerTest() {
        SingletonEager instance1 = SingletonEager.getInstance();
        SingletonEager instance2 = SingletonEager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("单例模式 - 懒汉式")
    void singletonLazyTest() {
        SingletonLazy instance1 = SingletonLazy.getInstance();
        SingletonLazy instance2 = SingletonLazy.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("工厂模式")
    void factoryPatternTest() {
        Product productA = ProductFactory.createProduct("A");
        Product productB = ProductFactory.createProduct("B");

        assertNotNull(productA);
        assertNotNull(productB);
        assertEquals("ProductA", productA.getName());
        assertEquals("ProductB", productB.getName());
    }

    @Test
    @DisplayName("建造者模式")
    void builderPatternTest() {
        User user = new User.UserBuilder()
                .name("张三")
                .age(25)
                .email("zhangsan@example.com")
                .build();

        assertEquals("张三", user.getName());
        assertEquals(25, user.getAge());
        assertEquals("zhangsan@example.com, null, null", user.getContactInfo());
    }

    @Test
    @DisplayName("原型模式")
    void prototypePatternTest() {
        PrototypeDocument original = new PrototypeDocument("测试文档", 100);
        PrototypeDocument cloned = (PrototypeDocument) original.clone();

        assertEquals(original.getTitle(), cloned.getTitle());
        assertEquals(original.getPages(), cloned.getPages());
        assertNotSame(original, cloned);
    }

    @Test
    @DisplayName("适配器模式")
    void adapterPatternTest() {
        OldCalculator oldCalculator = new OldCalculator();
        CalculatorAdapter adapter = new CalculatorAdapter(oldCalculator);

        int result = adapter.calculate(10, 5, "add");
        assertEquals(15, result);
    }

    @Test
    @DisplayName("装饰器模式")
    void decoratorPatternTest() {
        Coffee coffee = new SimpleCoffee();
        coffee = new MilkDecorator(coffee);
        coffee = new SugarDecorator(coffee);

        assertEquals("SimpleCoffee + Milk + Sugar", coffee.getDescription());
        assertEquals(15, coffee.getCost());
    }

    @Test
    @DisplayName("观察者模式")
    void observerPatternTest() {
        Subject subject = new ConcreteSubject();
        Observer observer1 = new ConcreteObserver("观察者1");
        Observer observer2 = new ConcreteObserver("观察者2");

        subject.attach(observer1);
        subject.attach(observer2);

        subject.notifyObservers("测试消息");

        assertEquals(1, ((ConcreteObserver) observer1).getLastMessageCount());
        assertEquals(1, ((ConcreteObserver) observer2).getLastMessageCount());
    }

    // 测试辅助类

    static class SingletonEager {
        private static final SingletonEager instance = new SingletonEager();

        private SingletonEager() {}

        public static SingletonEager getInstance() {
            return instance;
        }
    }

    static class SingletonLazy {
        private static SingletonLazy instance;

        private SingletonLazy() {}

        public static synchronized SingletonLazy getInstance() {
            if (instance == null) {
                instance = new SingletonLazy();
            }
            return instance;
        }
    }

    interface Product {
        String getName();
    }

    static class ProductA implements Product {
        @Override
        public String getName() {
            return "ProductA";
        }
    }

    static class ProductB implements Product {
        @Override
        public String getName() {
            return "ProductB";
        }
    }

    static class ProductFactory {
        public static Product createProduct(String type) {
            return switch (type) {
                case "A" -> new ProductA();
                case "B" -> new ProductB();
                default -> throw new IllegalArgumentException("Unknown type");
            };
        }
    }

    static class User {
        private final String name;
        private final int age;
        private final String contactInfo;

        private User(UserBuilder builder) {
            this.name = builder.name;
            this.age = builder.age;
            this.contactInfo = builder.contactInfo;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getContactInfo() {
            return contactInfo;
        }

        static class UserBuilder {
            private String name;
            private int age;
            private String email;
            private String phone;
            private String address;
            private String contactInfo;

            UserBuilder name(String name) {
                this.name = name;
                return this;
            }

            UserBuilder age(int age) {
                this.age = age;
                return this;
            }

            UserBuilder email(String email) {
                this.email = email;
                return this;
            }

            UserBuilder phone(String phone) {
                this.phone = phone;
                return this;
            }

            UserBuilder address(String address) {
                this.address = address;
                return this;
            }

            User build() {
                this.contactInfo = String.format("%s, %s, %s", email, phone, address);
                return new User(this);
            }
        }
    }

    static class PrototypeDocument implements Cloneable {
        private String title;
        private int pages;

        PrototypeDocument(String title, int pages) {
            this.title = title;
            this.pages = pages;
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        public String getTitle() {
            return title;
        }

        public int getPages() {
            return pages;
        }
    }

    static class OldCalculator {
        public int operation(int a, int b, String operation) {
            return switch (operation) {
                case "add" -> a + b;
                case "sub" -> a - b;
                default -> 0;
            };
        }
    }

    static class CalculatorAdapter {
        private final OldCalculator oldCalculator;

        CalculatorAdapter(OldCalculator oldCalculator) {
            this.oldCalculator = oldCalculator;
        }

        public int calculate(int a, int b, String operation) {
            return oldCalculator.operation(a, b, operation);
        }
    }

    interface Coffee {
        String getDescription();
        int getCost();
    }

    static class SimpleCoffee implements Coffee {
        @Override
        public String getDescription() {
            return "SimpleCoffee";
        }

        @Override
        public int getCost() {
            return 10;
        }
    }

    static class MilkDecorator implements Coffee {
        private final Coffee coffee;

        MilkDecorator(Coffee coffee) {
            this.coffee = coffee;
        }

        @Override
        public String getDescription() {
            return coffee.getDescription() + " + Milk";
        }

        @Override
        public int getCost() {
            return coffee.getCost() + 3;
        }
    }

    static class SugarDecorator implements Coffee {
        private final Coffee coffee;

        SugarDecorator(Coffee coffee) {
            this.coffee = coffee;
        }

        @Override
        public String getDescription() {
            return coffee.getDescription() + " + Sugar";
        }

        @Override
        public int getCost() {
            return coffee.getCost() + 2;
        }
    }

    interface Subject {
        void attach(Observer observer);
        void detach(Observer observer);
        void notifyObservers(String message);
    }

    interface Observer {
        void update(String message);
    }

    static class ConcreteSubject implements Subject {
        private java.util.List<Observer> observers = new java.util.ArrayList<>();

        @Override
        public void attach(Observer observer) {
            observers.add(observer);
        }

        @Override
        public void detach(Observer observer) {
            observers.remove(observer);
        }

        @Override
        public void notifyObservers(String message) {
            for (Observer observer : observers) {
                observer.update(message);
            }
        }
    }

    static class ConcreteObserver implements Observer {
        private final String name;
        private int lastMessageCount = 0;

        ConcreteObserver(String name) {
            this.name = name;
        }

        @Override
        public void update(String message) {
            lastMessageCount++;
        }

        public int getLastMessageCount() {
            return lastMessageCount;
        }
    }
}
