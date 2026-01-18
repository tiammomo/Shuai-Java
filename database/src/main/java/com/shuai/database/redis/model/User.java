package com.shuai.database.redis.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * 用于 Redis 示例中，可用于 Hash 存储或 JSON 序列化。
 *
 * @author Shuai
 */
public class User {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String status;
    private Integer age;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public User() {}

    public User(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', email='" + email + "'}";
    }
}
