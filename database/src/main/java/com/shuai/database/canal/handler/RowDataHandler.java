package com.shuai.database.canal.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.util.List;

/**
 * RowData 事件处理器接口
 * 用于处理 Row 级别的数据变更（INSERT/UPDATE/DELETE）
 *
 * 事件类型：
 * - INSERT：插入事件
 * - UPDATE：更新事件
 * - DELETE：删除事件
 * - CREATE：建表事件
 * - ALTER：改表事件
 * - ERASE：删表事件
 * - QUERY：TRUNCATE 事件
 *
 * @author Shuai
 */
@FunctionalInterface
public interface RowDataHandler {

    /**
     * 处理 RowData 事件
     *
     * @param eventType    事件类型
     * @param beforeColumns 更新前的列数据（DELETE/UPDATE 有值）
     * @param afterColumns  更新后的列数据（INSERT/UPDATE 有值）
     */
    void handle(CanalEntry.EventType eventType,
                List<CanalEntry.Column> beforeColumns,
                List<CanalEntry.Column> afterColumns);
}
