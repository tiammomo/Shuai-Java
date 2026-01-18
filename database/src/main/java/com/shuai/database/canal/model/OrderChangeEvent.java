package com.shuai.database.canal.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单变更事件模型
 * 用于同步订单数据变更
 *
 * @author Shuai
 */
public class OrderChangeEvent {

    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String eventType;
    private String tableName;

    public OrderChangeEvent() {
    }

    public OrderChangeEvent(Long orderId, Long userId, BigDecimal amount, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
    }

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "OrderChangeEvent{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", eventType='" + eventType + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }

    /**
     * 转换为 Redis hash 格式
     */
    public String toRedisHashKey() {
        return "order:" + orderId;
    }

    /**
     * 获取变更摘要
     */
    public String getChangeSummary() {
        return String.format("订单[%d] %s: %s (金额: %s)",
            orderId, getEventTypeDisplay(), status, amount);
    }

    public String getEventTypeDisplay() {
        if (eventType == null) return "未知";
        return switch (eventType.toUpperCase()) {
            case "INSERT" -> "创建";
            case "UPDATE" -> "更新";
            case "DELETE" -> "删除";
            default -> eventType;
        };
    }
}
