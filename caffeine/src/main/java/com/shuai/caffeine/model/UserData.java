package com.shuai.caffeine.model;

/**
 * 用户数据模型
 * 用于 LoadingCache 自动加载演示
 */
public class UserData {
    final String id;
    final String name;
    final int age;

    public UserData(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }

    @Override
    public String toString() {
        return "UserData{id='" + id + "', name='" + name + "', age=" + age + "}";
    }
}
