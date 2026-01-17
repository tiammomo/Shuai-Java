package com.shuai.guava;

import com.google.common.collect.*;
import java.util.*;

/**
 * Guava 集合工具演示
 *
 * @author Shuai
 * @version 1.0
 */
public class BasicsGuavaCollectionDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Guava 集合工具");
        System.out.println("=".repeat(50));

        listOperations();
        setOperations();
        mapOperations();
        multimapDemo();
        bimapDemo();
        tableDemo();
    }

    /**
     * List 操作
     */
    private void listOperations() {
        System.out.println("\n--- List 操作 ---");

        List<String> list = Lists.newArrayList("a", "b", "c", "d", "e");
        List<Integer> intList = Lists.newArrayListWithCapacity(100);
        List<String> linkedList = Lists.newLinkedList();

        System.out.println("  ArrayList: " + list);
        System.out.println("  LinkedList: " + linkedList);

        // 逆序
        List<String> reversed = Lists.reverse(list);
        System.out.println("  逆序: " + reversed);

        // 分区
        List<List<String>> partitions = Lists.partition(list, 2);
        System.out.println("  分区(每2个): " + partitions);

        // 笛卡尔积
        List<String> list1 = Lists.newArrayList("1", "2");
        List<String> list2 = Lists.newArrayList("a", "b");
        List<List<String>> cartesian = Lists.cartesianProduct(list1, list2);
        System.out.println("  笛卡尔积: " + cartesian);
    }

    /**
     * Set 操作
     */
    private void setOperations() {
        System.out.println("\n--- Set 操作 ---");

        Set<String> set1 = Sets.newHashSet("a", "b", "c");
        Set<String> set2 = Sets.newHashSet("b", "c", "d");

        System.out.println("  Set1: " + set1);
        System.out.println("  Set2: " + set2);

        // 集合操作
        Set<String> union = Sets.union(set1, set2);
        Set<String> intersection = Sets.intersection(set1, set2);
        Set<String> difference = Sets.difference(set1, set2);

        System.out.println("  并集: " + union);
        System.out.println("  交集: " + intersection);
        System.out.println("  差集(set1-set2): " + difference);

        // 交集差集
        Set<String> symmetricDifference = Sets.symmetricDifference(set1, set2);
        System.out.println("  对称差集: " + symmetricDifference);
    }

    /**
     * Map 操作
     */
    private void mapOperations() {
        System.out.println("\n--- Map 操作 ---");

        Map<String, Integer> map = Maps.newHashMap();
        map.put("Java", 1);
        map.put("Python", 2);
        map.put("Go", 3);

        System.out.println("  Map: " + map);

        // Maps.newHashMap() 快速创建
        Map<String, String> map2 = new HashMap<>();
        map2.put("key1", "value_key1");
        map2.put("key2", "value_key2");
        System.out.println("  HashMap 创建: " + map2);

        // uniqueIndex: Map<索引值, 原元素>
        Map<Integer, String> uniqueIndex = Maps.uniqueIndex(
            Lists.newArrayList("a", "bb", "ccc"),
            String::length
        );
        System.out.println("  uniqueIndex (按长度): " + uniqueIndex);
    }

    /**
     * Multimap 演示
     */
    private void multimapDemo() {
        System.out.println("\n--- Multimap (一键多值) ---");

        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("language", "Java");
        multimap.put("language", "Kotlin");
        multimap.put("framework", "Spring");

        System.out.println("  Multimap: " + multimap);
        System.out.println("  language 对应的值: " + multimap.get("language"));
        System.out.println("  移除后: " + multimap.removeAll("language"));

        // LinkedListMultimap (保持顺序)
        LinkedListMultimap<String, String> linkedMultimap = LinkedListMultimap.create();
        linkedMultimap.put("order", "first");
        linkedMultimap.put("order", "second");
        System.out.println("  LinkedListMultimap: " + linkedMultimap.get("order"));
    }

    /**
     * BiMap 演示
     */
    private void bimapDemo() {
        System.out.println("\n--- BiMap (双向映射) ---");

        BiMap<String, Integer> biMap = HashBiMap.create();
        biMap.put("Java", 1);
        biMap.put("Python", 2);

        System.out.println("  BiMap: " + biMap);
        System.out.println("  key -> value: Java -> " + biMap.get("Java"));
        System.out.println("  value -> key: 2 -> " + biMap.inverse().get(2));

        // 强制替换
        BiMap<String, Integer> biMap2 = HashBiMap.create();
        biMap2.put("Java", 1);
        biMap2.forcePut("Python", 1);  // 强制覆盖
        System.out.println("  forcePut 后: " + biMap2);
    }

    /**
     * Table 演示 (二维表格)
     */
    private void tableDemo() {
        System.out.println("\n--- Table (二维表格) ---");

        Table<String, String, Integer> table = HashBasedTable.create();
        table.put("Java", "2024", 1);
        table.put("Python", "2024", 2);
        table.put("Go", "2024", 3);
        table.put("Java", "2023", 1);

        System.out.println("  Table: " + table);
        System.out.println("  获取 (Java, 2024): " + table.get("Java", "2024"));
        System.out.println("  行 Map: " + table.row("Java"));
        System.out.println("  列 Map: " + table.column("2024"));
    }
}
