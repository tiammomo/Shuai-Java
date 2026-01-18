package com.shuai.database.redis.data;

import com.shuai.database.redis.data.constants.OrderConstants;
import com.shuai.database.redis.data.constants.UserConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 订单数据生成器
 * 生成真实的订单数据用于 Redis 测试
 */
public class OrderDataGenerator {

    private static final Random RANDOM = new Random();

    /**
     * 生成单个订单
     */
    public static Order generate() {
        String orderNo = generateOrderNo();
        Long userId = generateUserId();
        BigDecimal amount = generateAmount();
        String status = randomElement(OrderConstants.ORDER_STATUSES);
        String paymentMethod = randomElement(OrderConstants.PAYMENT_METHODS);
        String paymentStatus = randomElement(OrderConstants.PAYMENT_STATUSES);
        String shippingStatus = randomElement(OrderConstants.SHIPPING_STATUSES);
        String orderType = randomElement(OrderConstants.ORDER_TYPES);
        String city = randomElement(OrderConstants.CITIES);
        String address = generateAddress(city);
        LocalDateTime createdAt = generateCreatedAt();
        LocalDateTime paidAt = generatePaidAt(createdAt, status);
        LocalDateTime shippedAt = generateShippedAt(paidAt, status);
        LocalDateTime deliveredAt = generateDeliveredAt(shippedAt, status);

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAmount(amount);
        order.setDiscountAmount(calculateDiscount(amount));
        order.setTotalAmount(amount.subtract(calculateDiscount(amount)));
        order.setStatus(status);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(paymentStatus);
        order.setShippingStatus(shippingStatus);
        order.setOrderType(orderType);
        order.setReceiverName(generateReceiverName());
        order.setReceiverPhone(generatePhone());
        order.setReceiverAddress(address);
        order.setRemark(generateRemark());
        order.setCreatedAt(createdAt);
        order.setPaidAt(paidAt);
        order.setShippedAt(shippedAt);
        order.setDeliveredAt(deliveredAt);

        return order;
    }

    /**
     * 生成多个订单
     */
    public static List<Order> generateList(int count) {
        List<Order> orders = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            orders.add(generate());
        }
        return orders;
    }

    /**
     * 生成用户的订单列表
     */
    public static List<Order> generateListForUser(Long userId, int count) {
        List<Order> orders = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Order order = generate();
            order.setUserId(userId);
            order.setOrderNo("ORD" + System.currentTimeMillis() + String.format("%04d", i));
            orders.add(order);
        }
        return orders;
    }

    private static String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + String.format("%04d", RANDOM.nextInt(10000));
    }

    private static Long generateUserId() {
        return 1001L + RANDOM.nextInt(10000);
    }

    private static BigDecimal generateAmount() {
        return BigDecimal.valueOf(10 + RANDOM.nextDouble() * 9990)
            .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateDiscount(BigDecimal amount) {
        if (RANDOM.nextBoolean()) {
            return amount.multiply(BigDecimal.valueOf(0.05 + RANDOM.nextDouble() * 0.15))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private static String generateAddress(String city) {
        String prefix = randomElement(OrderConstants.ADDRESS_PREFIXES);
        String building = RANDOM.nextInt(50) + 1 + "号楼";
        String unit = "单元" + (RANDOM.nextInt(10) + 1);
        String room = (RANDOM.nextInt(100) + 1) + "室";
        return city + prefix + building + unit + room;
    }

    private static String generateReceiverName() {
        String surname = randomElement(UserConstants.SURNAMES);
        String name = randomElement(UserConstants.NAMES);
        return surname + name;
    }

    private static String generatePhone() {
        String prefix = randomElement(UserConstants.PHONE_PREFIXES);
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 8; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    private static String generateRemark() {
        if (RANDOM.nextBoolean()) {
            String[] remarks = {"请尽快发货", "周末配送", "工作日配送", "放门口就好", "电话联系"};
            return randomElement(remarks);
        }
        return "";
    }

    private static LocalDateTime generateCreatedAt() {
        int daysAgo = RANDOM.nextInt(90);
        return LocalDateTime.now().minusDays(daysAgo)
            .minusHours(RANDOM.nextInt(24))
            .minusMinutes(RANDOM.nextInt(60));
    }

    private static LocalDateTime generatePaidAt(LocalDateTime createdAt, String status) {
        if ("PAID".equals(status) || "PROCESSING".equals(status) || "SHIPPED".equals(status)
            || "DELIVERED".equals(status) || "COMPLETED".equals(status)) {
            return createdAt.plusMinutes(5 + RANDOM.nextInt(60));
        }
        return null;
    }

    private static LocalDateTime generateShippedAt(LocalDateTime paidAt, String status) {
        if (paidAt != null && ("SHIPPED".equals(status) || "DELIVERED".equals(status)
            || "COMPLETED".equals(status))) {
            return paidAt.plusHours(12 + RANDOM.nextInt(48));
        }
        return null;
    }

    private static LocalDateTime generateDeliveredAt(LocalDateTime shippedAt, String status) {
        if (shippedAt != null && ("DELIVERED".equals(status) || "COMPLETED".equals(status))) {
            return shippedAt.plusHours(24 + RANDOM.nextInt(48));
        }
        return null;
    }

    private static <T> T randomElement(T[] array) {
        return array[RANDOM.nextInt(array.length)];
    }

    /**
     * 订单内部类
     */
    public static class Order {
        private String orderNo;
        private Long userId;
        private BigDecimal amount;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private String status;
        private String paymentMethod;
        private String paymentStatus;
        private String shippingStatus;
        private String orderType;
        private String receiverName;
        private String receiverPhone;
        private String receiverAddress;
        private String remark;
        private LocalDateTime createdAt;
        private LocalDateTime paidAt;
        private LocalDateTime shippedAt;
        private LocalDateTime deliveredAt;

        // Getters and Setters
        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        public String getShippingStatus() { return shippingStatus; }
        public void setShippingStatus(String shippingStatus) { this.shippingStatus = shippingStatus; }
        public String getOrderType() { return orderType; }
        public void setOrderType(String orderType) { this.orderType = orderType; }
        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
        public String getReceiverPhone() { return receiverPhone; }
        public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
        public String getReceiverAddress() { return receiverAddress; }
        public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getPaidAt() { return paidAt; }
        public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
        public LocalDateTime getShippedAt() { return shippedAt; }
        public void setShippedAt(LocalDateTime shippedAt) { this.shippedAt = shippedAt; }
        public LocalDateTime getDeliveredAt() { return deliveredAt; }
        public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

        @Override
        public String toString() {
            return "Order{orderNo='" + orderNo + "', userId=" + userId + ", amount=" + amount + "}";
        }
    }
}
