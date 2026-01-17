package com.shuai.threads;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Java 多线程演示类
 *
 * 涵盖内容：
 * - 线程创建：Thread、Runnable、Callable
 * - 线程状态：NEW、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING、TERMINATED
 * - 线程方法：sleep、join、yield、interrupt
 * - 线程同步：synchronized、volatile、Atomic 类
 * - 线程池：ExecutorService、Executors、ThreadPoolExecutor
 * - 并发工具：CountDownLatch、CyclicBarrier、Semaphore
 * - CompletableFuture：异步编程
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsThreadsDemo {

    /**
     * 执行所有多线程演示
     */
    public void runAllDemos() {
        threadCreation();
        threadLifecycle();
        threadMethods();
        threadSynchronization();
        threadPoolDemo();
        concurrentTools();
        completableFutureDemo();
    }

    /**
     * 演示线程创建
     *
     * 三种方式：
     * - 继承 Thread 类
     * - 实现 Runnable 接口
     * - 实现 Callable 接口（有返回值）
     */
    private void threadCreation() {
        // 方式1：继承 Thread
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                // 线程执行逻辑
            }
        };

        // 方式2：实现 Runnable
        Runnable task1 = () -> {
            // 任务逻辑
        };
        Thread thread2 = new Thread(task1);

        // 方式3：实现 Callable（有返回值）
        Callable<Integer> callableTask = () -> {
            Thread.sleep(100);
            return 42;
        };
        Future<Integer> future = Executors.newSingleThreadExecutor().submit(callableTask);
    }

    /**
     * 演示线程状态
     *
     * 6 种状态：
     * - NEW：新建，未 start
     * - RUNNABLE：可运行（就绪或运行中）
     * - BLOCKED：阻塞，等待锁
     * - WAITING：无限等待
     * - TIMED_WAITING：限时等待
     * - TERMINATED：终止
     */
    private void threadLifecycle() {
        Thread.State[] states = Thread.State.values();
        for (Thread.State state : states) {
            // 打印所有线程状态
        }

        // 获取当前线程状态
        Thread.State currentState = Thread.currentThread().getState();
    }

    /**
     * 演示线程方法
     *
     * 主要方法：
     * - sleep：休眠，不释放锁
     * - join：等待线程结束
     * - yield：让出 CPU
     * - interrupt：中断线程
     */
    private void threadMethods() {
        // sleep：线程休眠
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // join：等待线程结束
        Thread workerThread = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        workerThread.start();
        try {
            workerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // yield：让出 CPU
        Thread.yield();

        // interrupt：中断
        Thread anotherThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                // 循环执行
            }
        });
        anotherThread.start();
        anotherThread.interrupt();
    }

    /**
     * 演示线程同步
     *
     * 同步方式：
     * - synchronized：内置锁
     * - volatile：可见性
     * - Atomic 类：原子操作
     */
    private void threadSynchronization() {
        // synchronized 同步块
        Object lock = new Object();
        synchronized (lock) {
            // 同步代码
        }

        // synchronized 方法
        class Counter {
            private int count = 0;
            public synchronized void increment() {
                count++;
            }
            public synchronized int getCount() {
                return count;
            }
        }

        // volatile 可见性
        class SharedData {
            volatile boolean running = true;
        }

        // Atomic 类原子操作
        AtomicInteger atomicInt = new AtomicInteger(0);
        atomicInt.incrementAndGet();
        atomicInt.addAndGet(5);
        atomicInt.getAndIncrement();
    }

    /**
     * 演示线程池
     *
     * 线程池类型：
     * - newFixedThreadPool：固定大小
     * - newCachedThreadPool：缓存线程池
     * - newSingleThreadExecutor：单线程
     * - newScheduledThreadPool：定时任务
     */
    private void threadPoolDemo() {
        ExecutorService executor;

        // 固定大小线程池
        executor = Executors.newFixedThreadPool(4);

        // 缓存线程池（自动回收）
        executor = Executors.newCachedThreadPool();

        // 单线程线程池
        executor = Executors.newSingleThreadExecutor();

        // 定时任务线程池
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);
        scheduledExecutor.scheduleAtFixedRate(
            () -> { /* 任务逻辑 */ },
            0, 1, TimeUnit.SECONDS
        );

        // 提交任务
        executor.submit(() -> {
            // 任务逻辑
        });

        // 有返回值的任务
        Future<String> result = executor.submit(() -> "完成");

        // 关闭线程池
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        // 自定义线程池配置
        ThreadPoolExecutor customPool = new ThreadPoolExecutor(
            2,  // 核心线程数
            4,  // 最大线程数
            60L, TimeUnit.SECONDS,  // 空闲线程存活时间
            new LinkedBlockingQueue<>(100),  // 队列容量
            Executors.defaultThreadFactory(),  // 线程工厂
            new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
        );
        customPool.shutdown();
    }

    /**
     * 演示并发工具类
     *
     * 工具类：
     * - CountDownLatch：倒数计数器
     * - CyclicBarrier：循环栅栏
     * - Semaphore：信号量
     * - Exchanger：线程间交换数据
     */
    private void concurrentTools() {
        // CountDownLatch：等待多个任务完成
        CountDownLatch latch = new CountDownLatch(3);
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                latch.countDown();
            }).start();
        }
        try {
            latch.await();  // 等待所有任务完成
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // CyclicBarrier：多线程等待后继续
        CyclicBarrier barrier = new CyclicBarrier(3, () -> {
            // 所有线程到达后执行
        });
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    barrier.await();  // 等待其他线程
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        barrier.reset();  // 重置后可复用

        // Semaphore：控制并发访问数量
        Semaphore semaphore = new Semaphore(2);
        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();  // 获取许可证
                    // 访问共享资源
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    semaphore.release();  // 释放许可证
                }
            }).start();
        }
    }

    /**
     * 演示 CompletableFuture 异步编程
     *
     * 功能：
     * - 异步执行任务
     * - 链式调用
     * - 组合多个 Future
     * - 异常处理
     */
    private void completableFutureDemo() {
        // 创建异步任务
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "结果";
        });

        // 链式处理结果
        CompletableFuture<String> processed = future
            .thenApply(result -> result + "处理")
            .thenApply(String::toUpperCase);

        // 组合两个 Future
        CompletableFuture<String> combined = CompletableFuture.supplyAsync(() -> "A")
            .thenCombine(CompletableFuture.supplyAsync(() -> "B"), (a, b) -> a + b);

        // 异步任务完成时回调
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                // 处理异常
            } else {
                // 处理结果
            }
        });

        // 异常处理
        CompletableFuture<String> exceptional = CompletableFuture.<String>supplyAsync(() -> {
            throw new RuntimeException("错误");
        }).exceptionally(ex -> "默认值");

        // 获取结果（阻塞）
        try {
            String result = future.get();
            String resultWithTimeout = future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // 处理异常
        }

        // 关闭 ForkJoinPool（全局）
        ForkJoinPool.commonPool().shutdown();
    }
}
