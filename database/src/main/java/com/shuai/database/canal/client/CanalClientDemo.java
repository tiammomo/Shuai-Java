package com.shuai.database.canal.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.shuai.database.canal.handler.EntryHandler;
import com.shuai.database.canal.handler.RowDataHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Canal 客户端管理器
 * 负责连接管理、消息获取和事件分发
 *
 * 使用方式：
 * 1. 创建 CanalClientDemo 实例
 * 2. 调用 connect() 连接 Canal Server
 * 3. 调用 subscribe() 订阅数据变更
 * 4. 调用 run() 开始消费消息
 * 5. 调用 disconnect() 断开连接
 *
 * @author Shuai
 */
public class CanalClientDemo {

    private static final Logger logger = LoggerFactory.getLogger(CanalClientDemo.class);

    private final CanalConnector connector;
    private final EntryHandler entryHandler;
    private final RowDataHandler rowDataHandler;

    private volatile boolean running = false;
    private final AtomicLong messageCount = new AtomicLong(0);
    private final AtomicLong entryCount = new AtomicLong(0);

    private int batchSize = 1000;
    private long timeout = 60000L; // 毫秒

    /**
     * 构造函数
     */
    public CanalClientDemo(CanalConnector connector) {
        this(connector, null, null);
    }

    /**
     * 构造函数（带事件处理器）
     */
    public CanalClientDemo(CanalConnector connector, EntryHandler entryHandler, RowDataHandler rowDataHandler) {
        this.connector = connector;
        this.entryHandler = entryHandler;
        this.rowDataHandler = rowDataHandler;
    }

    /**
     * 设置批处理大小
     */
    public CanalClientDemo batchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    /**
     * 设置超时时间
     */
    public CanalClientDemo timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 连接 Canal Server
     */
    public void connect() {
        try {
            connector.connect();
            logger.info("Canal 客户端连接成功");
        } catch (Exception e) {
            logger.error("连接 Canal Server 失败", e);
            throw new RuntimeException("连接 Canal Server 失败", e);
        }
    }

    /**
     * 订阅数据变更
     */
    public void subscribe() {
        subscribe(".*\\..*");  // 订阅所有表
    }

    /**
     * 订阅指定表的数据变更
     * @param filter 正则表达式，如 "db\\.table" 订阅 db 下的所有表
     */
    public void subscribe(String filter) {
        connector.subscribe(filter);
        logger.info("已订阅: {}", filter);
    }

    /**
     * 开始消费消息（阻塞方式）
     */
    public void run() {
        running = true;
        logger.info("开始消费 Canal 消息...");

        while (running) {
            try {
                // 获取消息（阻塞等待）
                Message message = connector.getWithoutAck(batchSize, timeout, null);

                long batchId = message.getId();
                if (batchId != -1 && message.getEntries().size() > 0) {
                    messageCount.incrementAndGet();
                    entryCount.addAndGet(message.getEntries().size());

                    // 处理消息
                    processEntries(message.getEntries());

                    // 确认消息
                    connector.ack(batchId);
                    logger.debug("确认消息 batchId={}, entries={}", batchId, message.getEntries().size());
                } else {
                    // 没有消息时短暂休眠，避免空转
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                logger.error("消费消息失败", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * 处理 Entry 列表
     */
    private void processEntries(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            try {
                // 跳过事务开始和结束事件
                if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
                    || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                    continue;
                }

                // 调用 Entry 处理器
                if (entryHandler != null) {
                    entryHandler.handle(entry);
                }

                // 解析 RowData
                CanalEntry.RowChange rowChange;
                try {
                    rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                } catch (Exception e) {
                    logger.warn("解析 RowChange 失败: {}", e.getMessage());
                    continue;
                }

                // 调用 RowData 处理器
                if (rowDataHandler != null) {
                    CanalEntry.EventType eventType = rowChange.getEventType();
                    for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                        rowDataHandler.handle(eventType, rowData.getBeforeColumnsList(), rowData.getAfterColumnsList());
                    }
                }

            } catch (Exception e) {
                logger.error("处理 Entry 失败: {}", entry.getHeader().getLogfileName(), e);
            }
        }
    }

    /**
     * 停止消费
     */
    public void stop() {
        running = false;
        logger.info("停止消费消息");
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            stop();
            connector.disconnect();
            logger.info("断开 Canal 连接");
        } catch (Exception e) {
            logger.error("断开连接失败", e);
        }
    }

    /**
     * 获取消息统计
     */
    public long getMessageCount() {
        return messageCount.get();
    }

    public long getEntryCount() {
        return entryCount.get();
    }

    /**
     * 打印统计信息
     */
    public void printStats() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("          Canal 客户端统计");
        System.out.println("=".repeat(40));
        System.out.println("  消息批次数: " + messageCount.get());
        System.out.println("  Entry 总数: " + entryCount.get());
        System.out.println("  运行状态: " + (running ? "运行中" : "已停止"));
        System.out.println("=".repeat(40));
    }

    /**
     * 静默模式运行（不打印日志）
     */
    public static void silentRun(CanalConnector connector, EntryHandler handler) {
        CanalClientDemo client = new CanalClientDemo(connector, handler, null);
        client.connect();
        client.subscribe();
        try {
            client.run();
        } finally {
            client.disconnect();
        }
    }

    /**
     * 演示模式运行
     */
    public static void demoRun(CanalConnector connector) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         Canal 客户端演示");
        System.out.println("=".repeat(50));

        CanalClientDemo client = new CanalClientDemo(connector,
            new EntryHandler() {
                @Override
                public void handle(com.alibaba.otter.canal.protocol.CanalEntry.Entry entry) {
                    String tableName = entry.getHeader().getTableName();
                    String eventType = entry.getHeader().getEventType().toString();
                    String logFile = entry.getHeader().getLogfileName();
                    long logPos = entry.getHeader().getLogfileOffset();

                    System.out.printf("  [变更] %s.%s (log: %s:%d)%n",
                        tableName, eventType, logFile, logPos);
                }
            },
            null
        );

        client.connect();
        client.subscribe();
        client.run();
    }
}
