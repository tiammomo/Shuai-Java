package com.shuai.advanced;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

/**
 * Java 反射演示类
 *
 * 涵盖内容：
 * - 获取 Class 对象：三种方式
 * - 检查类信息：类名、包名、父类、修饰符
 * - 动态创建对象：newInstance、getDeclaredConstructor
 * - 访问字段：getField、getDeclaredField
 * - 调用方法：getMethod、invoke
 * - 访问私有成员：setAccessible(true)
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsReflectionDemo {

    /**
     * 执行所有反射演示
     */
    public void runAllDemos() {
        getClassObjects();
        inspectClass();
        createObjectsDynamically();
        accessFields();
        invokeMethods();
        privateAccess();
    }

    /**
     * 演示获取 Class 对象
     *
     * 三种方式：
     * - ClassName.class：类字面量
     * - instance.getClass()：对象方法
     * - Class.forName()：动态加载
     */
    private void getClassObjects() {
        // 获取 Class 对象的三种方式:
        Class<String> stringClass = String.class;
        Class<?> strClass = "Hello".getClass();

        try {
            // Class.forName() 方式
            Class<?> dateClass = Class.forName("java.util.Date");
            // ClassLoader 方式
            Class<?> intClass = getClass().getClassLoader().loadClass("java.lang.Integer");
        } catch (ClassNotFoundException e) {
            // 类未找到
        }
    }

    /**
     * 演示检查类信息
     *
     * 可获取信息：
     * - 类名：getSimpleName, getName
     * - 包名：getPackage
     * - 父类：getSuperclass
     * - 修饰符：Modifier.toString
     * - 构造器、字段、方法列表
     */
    private void inspectClass() {
        Class<Person> personClass = Person.class;

        // 获取类信息
        String className = personClass.getSimpleName();
        String packageName = personClass.getPackage().getName();
        Class<?> superClass = personClass.getSuperclass();
        String modifiers = Modifier.toString(personClass.getModifiers());
        boolean isInterface = Modifier.isInterface(personClass.getModifiers());

        // 获取构造器
        Constructor<?>[] constructors = personClass.getConstructors();

        // 获取字段
        Field[] fields = personClass.getDeclaredFields();

        // 获取方法
        Method[] methods = personClass.getDeclaredMethods();
    }

    /**
     * 演示动态创建对象
     *
     * 方式：
     * - getDeclaredConstructor().newInstance()：无参构造
     * - getDeclaredConstructor(types).newInstance(args)：有参构造
     * - 可绕过编译时类型检查
     */
    private void createObjectsDynamically() {
        try {
            Class<Person> personClass = Person.class;
            Person person = personClass.getDeclaredConstructor().newInstance();

            Field nameField = personClass.getField("name");
            nameField.set(person, "反射创建");
            person.setAge(25);

            // 有参构造创建
            Person person2 = personClass.getDeclaredConstructor(String.class, int.class)
                                        .newInstance("动态创建", 30);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 演示访问字段
     *
     * API：
     * - getField：获取公共字段（包括继承）
     * - getDeclaredField：获取所有字段（包括私有）
     * - setAccessible(true)：访问私有字段
     */
    private void accessFields() {
        try {
            Person person = new Person("张三", 25);
            Class<Person> personClass = Person.class;

            // getField 获取公共字段
            Field nameField = personClass.getField("name");
            String name = (String) nameField.get(person);

            // getDeclaredField 获取所有字段（包括私有）
            Field ageField = personClass.getDeclaredField("age");
            ageField.setAccessible(true);
            int age = (int) ageField.get(person);

            // 修改字段值
            ageField.set(person, 30);

            Function<Person, String> getInfo = p -> {
                try {
                    return "Name: " + nameField.get(p) + ", Age: " + ageField.get(p);
                } catch (Exception e) {
                    return "Error";
                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 演示调用方法
     *
     * 方式：
     * - getMethod：获取公共方法
     * - invoke(obj, args)：调用方法
     * - 支持方法参数传递
     */
    private void invokeMethods() {
        try {
            Person person = new Person("李四", 25);
            Class<Person> personClass = Person.class;

            Method sayHelloMethod = personClass.getMethod("sayHello");
            sayHelloMethod.invoke(person);

            Method setAgeMethod = personClass.getMethod("setAge", int.class);
            setAgeMethod.invoke(person, 35);

            Method getInfoMethod = personClass.getMethod("getInfo");
            String info = (String) getInfoMethod.invoke(person);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 演示访问私有成员
     *
     * 通过 setAccessible(true) 访问：
     * - 私有构造器
     * - 私有字段
     * - 私有方法
     *
     * 注意：会影响性能，应谨慎使用
     */
    private void privateAccess() {
        try {
            Class<PrivateClass> clazz = PrivateClass.class;
            Constructor<PrivateClass> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            PrivateClass privateObj = constructor.newInstance();

            Field secretField = clazz.getDeclaredField("secret");
            secretField.setAccessible(true);
            secretField.set(privateObj, "秘密信息");
            String secret = (String) secretField.get(privateObj);

            Method privateMethod = clazz.getDeclaredMethod("secretMethod");
            privateMethod.setAccessible(true);
            privateMethod.invoke(privateObj);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Person {
        public String name;
        private int age;

        public Person() {}

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public void setAge(int age) { this.age = age; }
        public String getName() { return name; }
        public int getAge() { return age; }

        public void sayHello() {
            // Hello, 我是 {name}
        }

        public String getInfo() {
            return "Person{name='" + name + "', age=" + age + "}";
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }

    private static class PrivateClass {
        private String secret = "默认秘密";

        private PrivateClass() {}

        private void secretMethod() {}

        @Override
        public String toString() {
            return "PrivateClass{secret='" + secret + "'}";
        }
    }
}
