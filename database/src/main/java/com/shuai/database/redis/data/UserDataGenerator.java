package com.shuai.database.redis.data;

import com.shuai.database.redis.data.constants.UserConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 用户数据生成器
 * 生成真实的用户数据用于 Redis 测试
 */
public class UserDataGenerator {

    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 生成单个用户
     */
    public static User generate() {
        long id = generateId();
        String username = generateUsername(id);
        String email = generateEmail(username);
        String phone = generatePhone();
        String status = randomElement(UserConstants.STATUSES);
        int age = 18 + RANDOM.nextInt(50);
        String city = randomElement(UserConstants.CITIES);
        String occupation = randomElement(UserConstants.OCCUPATIONS);
        BigDecimal balance = generateBalance();
        LocalDateTime createdAt = generateCreatedAt();

        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setStatus(status);
        user.setAge(age);
        user.setCity(city);
        user.setOccupation(occupation);
        user.setBalance(balance);
        user.setCreatedAt(createdAt);

        return user;
    }

    /**
     * 生成多个用户
     */
    public static List<User> generateList(int count) {
        List<User> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            users.add(generate());
        }
        return users;
    }

    private static long generateId() {
        return 1001 + RANDOM.nextInt(10000);
    }

    private static String generateUsername(Long id) {
        String surname = randomElement(UserConstants.SURNAMES);
        String name = randomElement(UserConstants.NAMES);
        return surname + name + "_" + id;
    }

    private static String generateEmail(String username) {
        String domain = randomElement(UserConstants.EMAIL_DOMAINS);
        return username.toLowerCase() + "@" + domain;
    }

    private static String generatePhone() {
        String prefix = randomElement(UserConstants.PHONE_PREFIXES);
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 8; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    private static BigDecimal generateBalance() {
        return BigDecimal.valueOf(100 + RANDOM.nextInt(100000)).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private static LocalDateTime generateCreatedAt() {
        int daysAgo = RANDOM.nextInt(365 * 3);
        return LocalDateTime.now().minusDays(daysAgo)
            .minusHours(RANDOM.nextInt(24))
            .minusMinutes(RANDOM.nextInt(60));
    }

    private static <T> T randomElement(T[] array) {
        return array[RANDOM.nextInt(array.length)];
    }

    /**
     * 用户内部类
     */
    public static class User {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String status;
        private Integer age;
        private String city;
        private String occupation;
        private BigDecimal balance;
        private LocalDateTime createdAt;

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
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getOccupation() { return occupation; }
        public void setOccupation(String occupation) { this.occupation = occupation; }
        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        @Override
        public String toString() {
            return "User{id=" + id + ", username='" + username + "', email='" + email + "'}";
        }
    }
}
