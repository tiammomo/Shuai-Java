package com.shuai.jvm;

import java.lang.management.*;

/**
 * JVM 内存区域演示
 *
 * @author Shuai
 * @version 1.0
 */
public class BasicsMemoryDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       JVM 内存区域");
        System.out.println("=".repeat(50));

        memoryStructure();
        heapDemo();
        nonHeapDemo();
        memoryMonitoring();
    }

    /**
     * JVM 内存结构
     */
    private void memoryStructure() {
        System.out.println("\n--- JVM 内存结构 ---");

        System.out.println("\n  运行时数据区:");
        System.out.println("    ┌─────────────────────────────────────────────────────┐");
        System.out.println("    │                    JVM Process                       │");
        System.out.println("    │  ┌─────────────────────────────────────────────────┐│");
        System.out.println("    │  │                  Heap (堆)                       ││");
        System.out.println("    │  │  ┌──────────────┐  ┌──────────────────────────┐ ││");
        System.out.println("    │  │  │   Young Gen  │  │      Old Gen             │ ││");
        System.out.println("    │  │  │  ┌────────┐  │  │                          │ ││");
        System.out.println("    │  │  │  │Eden    │  │  │                          │ ││");
        System.out.println("    │  │  │  ├────────┤  │  │                          │ ││");
        System.out.println("    │  │  │  │S0/S1   │  │  │                          │ ││");
        System.out.println("    │  │  │  └────────┘  │  │                          │ ││");
        System.out.println("    │  │  └──────────────┘  └──────────────────────────┘ ││");
        System.out.println("    │  └─────────────────────────────────────────────────┘│");
        System.out.println("    │  ┌─────────────────────────────────────────────────┐│");
        System.out.println("    │  │              Non-Heap (非堆)                    ││");
        System.out.println("    │  │  ┌─────────┐  ┌──────────┐  ┌────────────────┐ ││");
        System.out.println("    │  │  │ Metaspace│ │Code Cache │ │  Thread Stack  │ ││");
        System.out.println("    │  │  └─────────┘  └──────────┘  └────────────────┘ ││");
        System.out.println("    │  └─────────────────────────────────────────────────┘│");
        System.out.println("    └─────────────────────────────────────────────────────┘");

        System.out.println("\n  线程私有:");
        System.out.println("    - 程序计数器 (PC Register)");
        System.out.println("    - 虚拟机栈 (VM Stack)");
        System.out.println("    - 本地方法栈 (Native Method Stack)");

        System.out.println("\n  线程共享:");
        System.out.println("    - 堆 (Heap)");
        System.out.println("    - 元空间 (Metaspace)");
    }

    /**
     * 堆内存演示
     */
    private void heapDemo() {
        System.out.println("\n--- 堆内存 (Heap) ---");

        System.out.println("\n  年轻代 (Young Generation):");
        System.out.println("    - Eden      - 新对象分配区");
        System.out.println("    - S0/Survivor0 - 存活区 1");
        System.out.println("    - S1/Survivor1 - 存活区 2");

        System.out.println("\n  老年代 (Old Generation):");
        System.out.println("    - 长期存活的对象");
        System.out.println("    - 大对象直接分配");

        System.out.println("\n  堆参数:");
        System.out.println("    -Xms512m              - 初始堆大小");
        System.out.println("    -Xmx2g                - 最大堆大小");
        System.out.println("    -Xmn256m              - 年轻代大小");
        System.out.println("    -XX:NewRatio=2        - 老年代/年轻代比例");
        System.out.println("    -XX:SurvivorRatio=8   - Eden/Survivor 比例");

        System.out.println("\n  逃逸分析:");
        System.out.println("    - 栈上分配 (可能)");
        System.out.println("    - 标量替换");
        System.out.println("    - 减少同步块");

        System.out.println("\n  对象分配流程:");
        System.out.println("    1. 检查 TLAB (Thread Local Allocation Buffer)");
        System.out.println("    2. 尝试栈上分配");
        System.out.println("    3. Eden 区分配");
        System.out.println("    4. Minor GC");
        System.out.println("    5. 老年代分配");
    }

    /**
     * 非堆内存演示
     */
    private void nonHeapDemo() {
        System.out.println("\n--- 非堆内存 (Non-Heap) ---");

        System.out.println("\n  元空间 (Metaspace):");
        System.out.println("    - 存储类信息 (类名、访问修饰符)");
        System.out.println("    - 存储字段信息");
        System.out.println("    - 存储方法信息");
        System.out.println("    - 存储常量池");
        System.out.println("    - 存储 JIT 编译后代码");
        System.out.println("    ");
        System.out.println("    -XX:MetaspaceSize=256m   - 初始大小");
        System.out.println("    -XX:MaxMetaspaceSize=512m - 最大大小");

        System.out.println("\n  Code Cache:");
        System.out.println("    - JIT 编译器生成的本地代码");
        System.out.println("    - 启用分层编译: -XX:+TieredCompilation");
        System.out.println("    - 大小: -XX:ReservedCodeCacheSize=240m");

        System.out.println("\n(");
        System.out.println("    -XX:InitialCodeCacheSize=...  - 初始大小");
        System.out.println("    -XX:ReservedCodeCacheSize=... - 保留大小");

        System.out.println("\n  直接内存:");
        System.out.println("    - NIO Channel 使用的内存");
        System.out.println("    - -XX:MaxDirectMemorySize=1g");
        System.out.println("    - 通过 ByteBuffer.allocateDirect() 分配");
    }

    /**
     * 内存监控
     */
    private void memoryMonitoring() {
        System.out.println("\n--- 内存监控 ---");

        System.out.println("\n  MemoryMXBean:");
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();

        System.out.println("    堆内存:");
        System.out.println("      初始: " + (heapUsage.getInit() / 1024 / 1024) + " MB");
        System.out.println("      已用: " + (heapUsage.getUsed() / 1024 / 1024) + " MB");
        System.out.println("      最大: " + (heapUsage.getMax() / 1024 / 1024) + " MB");
        System.out.println("    ");
        System.out.println("    非堆内存:");
        System.out.println("      初始: " + (nonHeapUsage.getInit() / 1024 / 1024) + " MB");
        System.out.println("      已用: " + (nonHeapUsage.getUsed() / 1024 / 1024) + " MB");
        System.out.println("      最大: " + (nonHeapUsage.getMax() / 1024 / 1024) + " MB");

        System.out.println("\n  内存池:");
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            System.out.println("    " + pool.getName() + ": " +
                pool.getUsage().getUsed() / 1024 / 1024 + " MB");
        }

        System.out.println("\n  垃圾收集器:");
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println("    " + gc.getName());
            System.out.println("      收集次数: " + gc.getCollectionCount());
            System.out.println("      回收时间: " + gc.getCollectionTime() + " ms");
        }
    }
}
