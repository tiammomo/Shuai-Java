package com.shuai.database.redis.data;

import com.shuai.database.redis.data.OrderDataGenerator.Order;
import com.shuai.database.redis.data.ProductDataGenerator.Product;
import com.shuai.database.redis.data.UserDataGenerator.User;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * 数据生成器入口类
 * 用于生成并导入真实测试数据到 Redis
 */
public class DataGenerator {

    private static final String KEY_PREFIX = "real:";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Random RANDOM = new Random();

    private final Jedis jedis;

    public DataGenerator(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     * 生成所有测试数据
     */
    public void generateAll() {
        System.out.println("\n=== 生成真实测试数据 ===\n");

        generateUserData();
        generateOrderData();
        generateProductData();
        generateCategoryData();

        System.out.println("\n=== 数据生成完成 ===\n");
    }

    /**
     * 生成用户数据 (100条)
     */
    public void generateUserData() {
        System.out.println("[1/4] 生成用户数据 (100条)...");

        List<User> users = UserDataGenerator.generateList(100);

        for (User user : users) {
            String key = KEY_PREFIX + "user:" + user.getId();

            // Hash 存储用户信息
            Map<String, String> hash = new HashMap<>();
            hash.put("id", String.valueOf(user.getId()));
            hash.put("username", user.getUsername());
            hash.put("email", user.getEmail());
            hash.put("phone", user.getPhone());
            hash.put("status", user.getStatus());
            hash.put("age", String.valueOf(user.getAge()));
            hash.put("city", user.getCity());
            hash.put("occupation", user.getOccupation());
            hash.put("balance", user.getBalance().toString());
            hash.put("created_at", user.getCreatedAt().format(DATE_FORMAT));
            jedis.hset(key, hash);

            // 添加到用户列表
            jedis.lpush(KEY_PREFIX + "user:list", String.valueOf(user.getId()));
        }

        System.out.println("    已生成 " + users.size() + " 条用户数据");
    }

    /**
     * 生成订单数据 (100条)
     */
    public void generateOrderData() {
        System.out.println("[2/4] 生成订单数据 (100条)...");

        List<Order> orders = OrderDataGenerator.generateList(100);

        for (Order order : orders) {
            String key = KEY_PREFIX + "order:" + order.getOrderNo();

            // Hash 存储订单信息
            Map<String, String> hash = new HashMap<>();
            hash.put("order_no", order.getOrderNo());
            hash.put("user_id", String.valueOf(order.getUserId()));
            hash.put("amount", order.getAmount().toString());
            hash.put("discount_amount", order.getDiscountAmount().toString());
            hash.put("total_amount", order.getTotalAmount().toString());
            hash.put("status", order.getStatus());
            hash.put("payment_method", order.getPaymentMethod());
            hash.put("payment_status", order.getPaymentStatus());
            hash.put("order_type", order.getOrderType());
            hash.put("receiver_name", order.getReceiverName());
            hash.put("receiver_phone", order.getReceiverPhone());
            hash.put("receiver_address", order.getReceiverAddress());
            hash.put("created_at", order.getCreatedAt() != null ? order.getCreatedAt().format(DATE_FORMAT) : "");
            hash.put("paid_at", order.getPaidAt() != null ? order.getPaidAt().format(DATE_FORMAT) : "");
            jedis.hset(key, hash);

            // 添加到用户订单列表
            jedis.lpush(KEY_PREFIX + "user:" + order.getUserId() + ":orders", order.getOrderNo());
            // 添加到订单列表
            jedis.lpush(KEY_PREFIX + "order:list", order.getOrderNo());
        }

        System.out.println("    已生成 " + orders.size() + " 条订单数据");
    }

    /**
     * 生成商品数据 (50条)
     */
    public void generateProductData() {
        System.out.println("[3/4] 生成商品数据 (50条)...");

        List<Product> products = ProductDataGenerator.generateList(50);

        for (Product product : products) {
            String key = KEY_PREFIX + "product:" + product.getId();

            // Hash 存储商品信息
            Map<String, String> hash = new HashMap<>();
            hash.put("id", String.valueOf(product.getId()));
            hash.put("name", product.getName());
            hash.put("category", product.getCategory());
            hash.put("sub_category", product.getSubCategory());
            hash.put("brand", product.getBrand());
            hash.put("price", product.getPrice().toString());
            hash.put("original_price", product.getOriginalPrice().toString());
            hash.put("stock", String.valueOf(product.getStock()));
            hash.put("status", product.getStatus());
            hash.put("sales_count", String.valueOf(product.getSalesCount()));
            hash.put("view_count", String.valueOf(product.getViewCount()));
            hash.put("rating", String.valueOf(product.getRating()));
            hash.put("listed_at", product.getListedAt().format(DATE_FORMAT));
            jedis.hset(key, hash);

            // 添加到商品列表
            jedis.lpush(KEY_PREFIX + "product:list", String.valueOf(product.getId()));
            // 添加到分类商品列表
            jedis.lpush(KEY_PREFIX + "category:" + product.getCategory() + ":products", String.valueOf(product.getId()));
        }

        System.out.println("    已生成 " + products.size() + " 条商品数据");
    }

    /**
     * 生成分类数据 (10个)
     */
    public void generateCategoryData() {
        System.out.println("[4/4] 生成分类数据 (10个)...");

        String[] categories = {
            "电子产品", "服装鞋帽", "家用电器", "美妆护肤", "食品生鲜",
            "母婴玩具", "运动户外", "家居家装", "图书音像", "数码配件"
        };

        for (int i = 0; i < categories.length; i++) {
            String categoryId = String.format("%02d", i + 1);
            String key = KEY_PREFIX + "category:" + categoryId;

            Map<String, String> hash = new HashMap<>();
            hash.put("id", categoryId);
            hash.put("name", categories[i]);
            hash.put("parent_id", "0");
            hash.put("sort_order", String.valueOf(i + 1));
            hash.put("status", "ACTIVE");
            hash.put("product_count", String.valueOf(5 + RANDOM.nextInt(50)));
            jedis.hset(key, hash);

            // 添加到分类列表
            jedis.lpush(KEY_PREFIX + "category:list", categoryId);
        }

        System.out.println("    已生成 " + categories.length + " 个分类");
    }

    /**
     * 生成用户画像数据 (用于 ZSet 排行榜)
     */
    public void generateUserRankingData() {
        System.out.println("\n[额外] 生成用户排行榜数据...");

        List<User> users = UserDataGenerator.generateList(100);
        for (User user : users) {
            // 用户消费金额排行
            jedis.zadd(KEY_PREFIX + "user:spending:ranking", user.getBalance().doubleValue(), user.getId().toString());
            // 用户活跃度排行
            jedis.zadd(KEY_PREFIX + "user:activity:ranking", RANDOM.nextInt(10000), user.getId().toString());
        }

        System.out.println("    已生成用户排行榜数据");
    }

    /**
     * 生成商品销量排行
     */
    public void generateProductSalesRanking() {
        System.out.println("\n[额外] 生成商品销量排行...");

        List<Product> products = ProductDataGenerator.generateList(50);
        for (Product product : products) {
            jedis.zadd(KEY_PREFIX + "product:sales:ranking", product.getSalesCount(), product.getId().toString());
        }

        System.out.println("    已生成商品销量排行");
    }

    /**
     * 清理测试数据
     */
    public void cleanup() {
        System.out.println("\n[清理] 清理测试数据...");

        Set<String> keys = jedis.keys(KEY_PREFIX + "*");
        if (!keys.isEmpty()) {
            jedis.del(keys.toArray(new String[0]));
        }

        System.out.println("    已清理 " + keys.size() + " 个键");
    }

    /**
     * 获取数据统计
     */
    public Map<String, Object> getStats() {
        Long userCount = jedis.llen(KEY_PREFIX + "user:list");
        Long orderCount = jedis.llen(KEY_PREFIX + "order:list");
        Long productCount = jedis.llen(KEY_PREFIX + "product:list");
        Long categoryCount = jedis.llen(KEY_PREFIX + "category:list");

        return Map.of(
            "userCount", userCount != null ? userCount : 0,
            "orderCount", orderCount != null ? orderCount : 0,
            "productCount", productCount != null ? productCount : 0,
            "categoryCount", categoryCount != null ? categoryCount : 0
        );
    }
}
