package com.shuai.collections;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Java 集合框架演示
 *
 * @author Shuai
 * @version 1.0
 */
public class BasicsCollectionDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Java 集合框架");
        System.out.println("=".repeat(50));

        collectionOverview();
        listDemo();
        setDemo();
        mapDemo();
        queueDemo();
        collectionsUtility();
    }

    /**
     * 集合概述
     */
    private void collectionOverview() {
        System.out.println("\n--- 集合框架概述 ---");

        System.out.println("\n  Collection 接口层次:");
        System.out.println("    Iterable<T>                    - 可迭代接口");
        System.out.println("        ├── Collection<E>          - 集合接口");
        System.out.println("        │   ├── List<E>            - 有序列表");
        System.out.println("        │   ├── Set<E>             - 无重复集合");
        System.out.println("        │   └── Queue<E>           - 队列");
        System.out.println("        └── Map<K,V>               - 键值对");

        System.out.println("\n  集合特点:");
        System.out.println("    List    - 有序、可重复、按索引访问");
        System.out.println("    Set     - 无序、不可重复、哈希/树结构");
        System.out.println("    Queue   - 先进先出、阻塞队列");
        System.out.println("    Map     - 键值对、键唯一");

        System.out.println("\n  常用实现类:");
        System.out.println("    List:    ArrayList, LinkedList, Vector, Stack");
        System.out.println("    Set:     HashSet, LinkedHashSet, TreeSet, EnumSet");
        System.out.println("    Queue:   LinkedList, PriorityQueue, ArrayDeque");
        System.out.println("    Map:     HashMap, LinkedHashMap, TreeMap, Hashtable, EnumMap");
    }

    /**
     * List 演示
     */
    private void listDemo() {
        System.out.println("\n--- List 接口 ---");

        System.out.println("\n  ArrayList (数组实现):");
        System.out.println("    - 随机访问 O(1)");
        System.out.println("    - 尾部插入 O(1)");
        System.out.println("    - 中间插入 O(n)");
        System.out.println("    - 线程不安全");

        System.out.println("\n  LinkedList (双向链表):");
        System.out.println("    - 头部/尾部插入 O(1)");
        System.out.println("    - 随机访问 O(n)");
        System.out.println("    - 可作为 List/Deque 使用");

        System.out.println("\n  Vector (同步实现):");
        System.out.println("    - 线程安全");
        System.out.println("    - 性能较差");
        System.out.println("    - 已被 Collections.synchronizedList 替代");

        System.out.println("\n  List 基本操作:");
        List<String> list = new ArrayList<>();
        System.out.println("    list.add(\"a\")          - 添加");
        System.out.println("    list.add(0, \"b\")       - 插入");
        System.out.println("    list.get(0)             - 获取");
        System.out.println("    list.set(0, \"c\")       - 修改");
        System.out.println("    list.remove(0)          - 删除");
        System.out.println("    list.size()             - 大小");
        System.out.println("    list.contains(\"a\")     - 包含");
        System.out.println("    list.indexOf(\"a\")      - 查找位置");

        System.out.println("\n  List 遍历:");
        System.out.println("    // 1. for 循环");
        System.out.println("    for (int i = 0; i < list.size(); i++) { }");
        System.out.println("    ");
        System.out.println("    // 2. 增强 for");
        System.out.println("    for (String s : list) { }");
        System.out.println("    ");
        System.out.println("    // 3. Iterator");
        System.out.println("    for (Iterator<String> it = list.iterator(); it.hasNext(); ) { }");
        System.out.println("    ");
        System.out.println("    // 4. forEach");
        System.out.println("    list.forEach(System.out::println);");
    }

    /**
     * Set 演示
     */
    private void setDemo() {
        System.out.println("\n--- Set 接口 ---");

        System.out.println("\n  HashSet (哈希表):");
        System.out.println("    - 基于 HashMap");
        System.out.println("    - 无序、不重复");
        System.out.println("    - 查找/插入 O(1)");
        System.out.println("    - 允许 null");

        System.out.println("\n  LinkedHashSet:");
        System.out.println("    - 继承 HashSet");
        System.out.println("    - 维护插入顺序");
        System.out.println("    - 性能略低于 HashSet");

        System.out.println("\n  TreeSet (红黑树):");
        System.out.println("    - 基于 TreeMap");
        System.out.println("    - 有序 (自然排序/Comparator)");
        System.out.println("    - 查找/插入 O(log n)");
        System.out.println("    - 不允许 null");

        System.out.println("\n  Set 基本操作:");
        Set<String> hashSet = new HashSet<>();
        System.out.println("    hashSet.add(\"a\")        - 添加");
        System.out.println("    hashSet.remove(\"a\")     - 删除");
        System.out.println("    hashSet.contains(\"a\")   - 包含");
        System.out.println("    hashSet.size()           - 大小");
        System.out.println("    hashSet.isEmpty()        - 空检查");

        System.out.println("\n  TreeSet 排序:");
        TreeSet<Integer> treeSet = new TreeSet<>(Comparator.reverseOrder());
        treeSet.add(5);
        treeSet.add(2);
        treeSet.add(8);
        System.out.println("    降序: " + treeSet);  // [8, 5, 2]
    }

    /**
     * Map 演示
     */
    private void mapDemo() {
        System.out.println("\n--- Map 接口 ---");

        System.out.println("\n  HashMap (哈希表):");
        System.out.println("    - 基于数组 + 链表/红黑树");
        System.out.println("    - 键无序、不重复");
        System.out.println("    - 查找/插入 O(1)");
        System.out.println("    - 允许 null 键和 null 值");

        System.out.println("\n  LinkedHashMap:");
        System.out.println("    - 维护插入顺序/访问顺序");
        System.out.println("    - 可用于 LRU 缓存");

        System.out.println("\n  TreeMap (红黑树):");
        System.out.println("    - 键有序");
        System.out.println("    - 支持范围查询");
        System.out.println("    - 查找/插入 O(log n)");
        System.out.println("    - 不允许 null 键");

        System.out.println("\n  Map 基本操作:");
        Map<String, Integer> map = new HashMap<>();
        System.out.println("    map.put(\"a\", 1)          - 添加/更新");
        System.out.println("    map.get(\"a\")             - 获取");
        System.out.println("    map.getOrDefault(\"b\", 0) - 获取默认值");
        System.out.println("    map.remove(\"a\")          - 删除");
        System.out.println("    map.containsKey(\"a\")    - 键存在");
        System.out.println("    map.containsValue(1)     - 值存在");
        System.out.println("    map.size()               - 大小");

        System.out.println("\n  Map 遍历:");
        System.out.println("    // 1. keySet");
        System.out.println("    for (String key : map.keySet()) { }");
        System.out.println("    ");
        System.out.println("    // 2. values");
        System.out.println("    for (Integer value : map.values()) { }");
        System.out.println("    ");
        System.out.println("    // 3. entrySet");
        System.out.println("    for (Map.Entry<String, Integer> entry : map.entrySet()) { }");
        System.out.println("    ");
        System.out.println("    // 4. forEach");
        System.out.println("    map.forEach((k, v) -> { });");
    }

    /**
     * Queue 演示
     */
    private void queueDemo() {
        System.out.println("\n--- Queue/Deque 接口 ---");

        System.out.println("\n  Queue 方法:");
        System.out.println("    add(e)       - 添加 (队列满时异常)");
        System.out.println("    offer(e)     - 添加 (队列满时返回 false)");
        System.out.println("    remove()     - 移除 (队列空时异常)");
        System.out.println("    poll()       - 移除 (队列空时返回 null)");
        System.out.println("    element()    - 查看 (队列空时异常)");
        System.out.println("    peek()       - 查看 (队列空时返回 null)");

        System.out.println("\n  Deque (双端队列):");
        System.out.println("    - 两端都可插入/删除");
        System.out.println("    - 可作为 Queue 或 Stack 使用");
        System.out.println("    实现: ArrayDeque, LinkedList");

        System.out.println("\n  PriorityQueue:");
        System.out.println("    - 基于堆结构");
        System.out.println("    - 元素按优先级排序");
        System.out.println("    - 不是 FIFO 队列");

        System.out.println("\n  ArrayDeque:");
        System.out.println("    - 数组实现的双端队列");
        System.out.println("    - 头尾插入 O(1)");
        System.out.println("    - 不允许 null");
        System.out.println("    - 作为栈使用比 Stack 快");

        System.out.println("\n  使用示例:");
        Deque<String> deque = new ArrayDeque<>();
        deque.addFirst("a");
        deque.addLast("b");
        deque.addFirst("c");
        System.out.println("    " + deque);  // [c, a, b]
    }

    /**
     * Collections 工具类
     */
    private void collectionsUtility() {
        System.out.println("\n--- Collections 工具类 ---");

        System.out.println("\n  排序:");
        System.out.println("    Collections.sort(list)                    - 自然排序");
        System.out.println("    Collections.sort(list, comparator)        - 自定义排序");
        System.out.println("    Collections.reverse(list)                 - 反转");
        System.out.println("    Collections.shuffle(list)                 - 随机打乱");
        System.out.println("    Collections.swap(list, i, j)              - 交换");

        System.out.println("\n  查找:");
        System.out.println("    Collections.binarySearch(list, key)       - 二分查找");
        System.out.println("    Collections.max(collection)               - 最大值");
        System.out.println("    Collections.min(collection)               - 最小值");

        System.out.println("\n(");
        System.out.println("    Collections.frequency(collection, obj)    - 出现次数");
        System.out.println("    Collections.disjoint(c1, c2)              - 是否无交集");

        System.out.println("\n  线程安全包装:");
        System.out.println("    Collections.synchronizedList(new ArrayList<>());");
        System.out.println("    Collections.synchronizedSet(new HashSet<>());");
        System.out.println("    Collections.synchronizedMap(new HashMap<>());");

        System.out.println("\n  不可变集合:");
        System.out.println("    Collections.unmodifiableList(list);");
        System.out.println("    Collections.unmodifiableSet(set);");
        System.out.println("    Collections.unmodifiableMap(map);");

        System.out.println("\n  List.of(...) (Java 9+):");
        System.out.println("    List<String> immutable = List.of(\"a\", \"b\", \"c\");");
        System.out.println("    Set<String> immutableSet = Set.of(\"a\", \"b\");");
        System.out.println("    Map<String, Integer> immutableMap = Map.of(\"a\", 1, \"b\", 2);");
    }
}
