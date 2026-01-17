package com.shuai.concurrent;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 线程与线程池演示
 *
 * @author Shuai
 * @version 1.0
 */
public class BasicsThreadDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       线程与线程池");
        System.out.println("=".repeat(50));

        threadCreation();
        threadPoolDemo();
        executorsDemo();
        callableFuture();
        virtualThreadDemo();
    }

    /**
     * 线程创建方式
     */
    private void threadCreation() {
        System.out.println("\n--- 线程创建方式 ---");

        System.out.println("\n  1. 继承 Thread:");
        System.out.println("    class MyThread extends Thread {");
        System.out.println("        @Override");
        System.out.println("        public void run() {");
        System.out.println("            System.out.println(\"线程执行\");");
        System.out.println("        }");
        System.out.println("    }");
        System.out.println("    new MyThread().start();");

        System.out.println("\n  2. 实现 Runnable:");
        System.out.println("    Runnable task = () -> System.out.println(\"线程执行\");");
        System.out.println("    new Thread(task).start();");
        System.out.println("    ");
        System.out.println("    Thread t = new Thread(() -> {");
        System.out.println("        for (int i = 0; i < 5; i++) {");
        System.out.println("                System.out.println(i);");
        System.out.println("        }");
        System.out.println("    });");
        System.out.println("    t.start();");

        System.out.println("\n  3. 实现 Callable:");
        System.out.println("    Callable<Integer> task = () -> {");
        System.out.println("        Thread.sleep(1000);");
        System.out.println("        return 42;");
        System.out.println("    };");

        System.out.println("\n  4. 线程常用方法:");
        System.out.println("    thread.start()           - 启动线程");
        System.out.println("    thread.run()             - 执行 Runnable");
        System.out.println("    thread.join()            - 等待线程结束");
        System.out.println("    thread.sleep(millis)     - 休眠");
        System.out.println("    thread.interrupt()       - 中断");
        System.out.println("    thread.isAlive()         - 是否存活");
        System.out.println("    thread.setPriority()     - 设置优先级");
        System.out.println("    thread.setDaemon(true)   - 设置守护线程");
    }

    /**
     * 线程池演示
     */
    private void threadPoolDemo() {
        System.out.println("\n--- 线程池 ---");

        System.out.println("\n  为什么用线程池?");
        System.out.println("    - 复用线程，减少创建/销毁开销");
        System.out.println("    - 控制并发数量，防止资源耗尽");
        System.out.println("    - 统一管理线程生命周期");

        System.out.println("\n  线程池参数:");
        System.out.println("    corePoolSize      - 核心线程数");
        System.out.println("    maximumPoolSize   - 最大线程数");
        System.out.println("    keepAliveTime     - 空闲线程存活时间");
        System.out.println("    unit              - 时间单位");
        System.out.println("    workQueue         - 工作队列");
        System.out.println("    threadFactory     - 线程工厂");
        System.out.println("    handler           - 拒绝策略");

        System.out.println("\n  拒绝策略:");
        System.out.println("    AbortPolicy       - 抛出异常");
        System.out.println("    CallerRunsPolicy  - 调用者执行");
        System.out.println("    DiscardOldestPolicy - 丢弃最老的");
        System.out.println("    DiscardPolicy     - 丢弃当前");
    }

    /**
     * Executors 工具类
     */
    private void executorsDemo() {
        System.out.println("\n--- Executors 工厂方法 ---");

        System.out.println("\n  固定线程池:");
        System.out.println("    ExecutorService executor = Executors.newFixedThreadPool(4);");
        System.out.println("    executor.execute(() -> System.out.println(\"任务执行\"));");

        System.out.println("\n  单线程池:");
        System.out.println("    ExecutorService executor = Executors.newSingleThreadExecutor();");

        System.out.println("\n  缓存线程池:");
        System.out.println("    ExecutorService executor = Executors.newCachedThreadPool();");

        System.out.println("\n  调度线程池:");
        System.out.println("    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);");
        System.out.println("    scheduler.schedule(() -> System.out.println(\"延迟执行\"), 5, TimeUnit.SECONDS);");
        System.out.println("    scheduler.scheduleAtFixedRate(() -> {}, 0, 1, TimeUnit.SECONDS);  // 周期执行");
        System.out.println("    scheduler.scheduleWithFixedDelay(() -> {}, 0, 1, TimeUnit.SECONDS);");

        System.out.println("\n  虚拟线程池 (Java 21+):");
        System.out.println("    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();");

        System.out.println("\n  线程池使用:");
        System.out.println("    ExecutorService executor = Executors.newFixedThreadPool(4);");
        System.out.println("    try {");
        System.out.println("        executor.execute(() -> System.out.println(\"任务1\"));");
        System.out.println("        executor.execute(() -> System.out.println(\"任务2\"));");
        System.out.println("    } finally {");
        System.out.println("        executor.shutdown();  // 平缓关闭");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    // 强制关闭");
        System.out.println("    executor.shutdownNow();");
    }

    /**
     * Callable 与 Future
     */
    private void callableFuture() {
        System.out.println("\n--- Callable 与 Future ---");

        System.out.println("\n  Callable vs Runnable:");
        System.out.println("    - Runnable.run() 返回 void");
        System.out.println("    - Callable.call() 返回 V，支持泛型");
        System.out.println("    - Callable 可以抛出受检异常");

        System.out.println("\n  Future 常用方法:");
        System.out.println("    future.get()              - 获取结果 (阻塞)");
        System.out.println("    future.get(timeout, unit) - 限时获取");
        System.out.println("    future.isDone()           - 是否完成");
        System.out.println("    future.cancel(mayInterrupt) - 取消");
        System.out.println("    future.isCancelled()      - 是否取消");

        System.out.println("\n  示例:");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(1000);
            return 42;
        });

        System.out.println("    Future<Integer> future = executor.submit(() -> 42);");
        System.out.println("    Integer result = future.get();  // 阻塞等待");
        System.out.println("    executor.shutdown();");

        System.out.println("\n  CompletableFuture (Java 8+):");
        System.out.println("    CompletableFuture.supplyAsync(() -> {");
        System.out.println("        return \"异步结果\";");
        System.out.println("    }).thenAccept(System.out::println);");
    }

    /**
     * 虚拟线程演示 (Java 21+)
     */
    private void virtualThreadDemo() {
        System.out.println("\n--- 虚拟线程 (Java 21+) ---");

        System.out.println("\n  虚拟线程特点:");
        System.out.println("    - 轻量级线程，JVM 管理");
        System.out.println("    - 创建成本极低 (几千/秒)");
        System.out.println("    - 阻塞不会阻塞 OS 线程");
        System.out.println("    - 不需要线程池");

        System.out.println("\n  创建虚拟线程:");
        System.out.println("    Thread vt = Thread.startVirtualThread(() -> {");
        System.out.println("        System.out.println(\"虚拟线程执行\");");
        System.out.println("    });");
        System.out.println("    ");
        System.out.println("    ThreadFactory factory = Thread.ofVirtual().factory();");
        System.out.println("    Thread vt = factory.newThread(() -> {});");
        System.out.println("    vt.start();");

        System.out.println("\n(");
        System.out.println("    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {");
        System.out.println("        List<Future<String>> futures = new ArrayList<>();");
        System.out.println("        for (int i = 0; i < 10000; i++) {");
        System.out.println("            int taskId = i;");
        System.out.println("            futures.add(executor.submit(() -> \"任务\" + taskId));");
        System.out.println("        }");
        System.out.println("        for (Future<String> future : futures) {");
        System.out.println("            future.get();");
        System.out.println("        }");
        System.out.println("    }");

        System.out.println("\n  虚拟线程限制:");
        System.out.println("    - 不支持 stop()、suspend()、resume()");
        System.out.println("    - ThreadGroup 有限支持");
        System.out.println("    - 继承上下文 (ClassLoader、InheritableThreadLocal)");
    }
}
