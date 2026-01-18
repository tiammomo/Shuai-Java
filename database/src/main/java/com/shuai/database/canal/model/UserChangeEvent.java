package com.shuai.database.canal.model;

import java.time.LocalDateTime;

/**
 * 用户变更事件模型
 * 用于同步用户数据变更
 *
 * @author Shuai
 */
public class UserChangeEvent {

    private Long userId;
    private String username;
    private String email;
    private String phone;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String eventType;
    private String tableName;

    public UserChangeEvent() {
    }

    public UserChangeEvent(Long userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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
        return "UserChangeEvent{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", eventType='" + eventType + '\'' +
                '}';
    }

    /**
     * 转换为 Redis hash 格式
     */
    public String toRedisHashKey() {
        return "user:" + userId;
    }

    /**
     * 获取变更摘要
     */
    public String getChangeSummary() {
        String action = getEventTypeDisplay();
        if ("创建".equals(action)) {
            return String.format("用户[%d] %s: %s (%s)", userId, action, username, email);
        } else if ("更新".equals(action)) {
            return String.format("用户[%d] %s", userId, action);
        } else if ("删除".equals(action)) {
            return String.format("用户[%d] %s: %s", userId, action, username);
        }
        return String.format("用户[%d] %s", userId, action);
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
