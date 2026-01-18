package com.shuai.database.canal.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;

/**
 * Entry 事件处理器接口
 * 用于处理 Canal Entry 事件
 *
 * Canal Entry 包含：
 * - EntryHeader：事务头信息（表名、操作类型、日志位置等）
 * - StoreValue：具体的数据变更
 *
 * @author Shuai
 */
@FunctionalInterface
public interface EntryHandler {

    /**
     * 处理 Entry 事件
     *
     * @param entry Canal Entry
     */
    void handle(CanalEntry.Entry entry);
}
