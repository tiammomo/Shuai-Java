package com.shuai.database.redis.data;

import com.shuai.database.redis.data.constants.ProductConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 商品数据生成器
 * 生成真实的商品数据用于 Redis 测试
 */
public class ProductDataGenerator {

    private static final Random RANDOM = new Random();

    /**
     * 生成单个商品
     */
    public static Product generate() {
        Long id = generateId();
        String name = generateProductName();
        String category = randomElement(ProductConstants.CATEGORIES);
        String subCategory = generateSubCategory(category);
        String brand = randomElement(ProductConstants.BRANDS);
        BigDecimal price = generatePrice(category);
        BigDecimal originalPrice = generateOriginalPrice(price);
        Integer stock = generateStock();
        String color = randomElement(ProductConstants.COLORS);
        String size = randomElement(ProductConstants.SIZES);
        String description = generateDescription(name, category);
        String status = generateStatus();
        Integer salesCount = generateSalesCount();
        Integer viewCount = generateViewCount();
        Double rating = generateRating();
        Integer commentCount = generateCommentCount();
        LocalDateTime listedAt = generateListedAt();

        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCategory(category);
        product.setSubCategory(subCategory);
        product.setBrand(brand);
        product.setPrice(price);
        product.setOriginalPrice(originalPrice);
        product.setStock(stock);
        product.setColor(color);
        product.setSize(size);
        product.setDescription(description);
        product.setStatus(status);
        product.setSalesCount(salesCount);
        product.setViewCount(viewCount);
        product.setRating(rating);
        product.setCommentCount(commentCount);
        product.setListedAt(listedAt);

        return product;
    }

    /**
     * 生成多个商品
     */
    public static List<Product> generateList(int count) {
        List<Product> products = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            products.add(generate());
        }
        return products;
    }

    /**
     * 生成指定分类的商品
     */
    public static List<Product> generateListByCategory(String category, int count) {
        List<Product> products = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Product product = generate();
            product.setCategory(category);
            product.setSubCategory(generateSubCategory(category));
            products.add(product);
        }
        return products;
    }

    private static Long generateId() {
        return 10001L + RANDOM.nextInt(100000);
    }

    private static String generateProductName() {
        String prefix = randomElement(ProductConstants.PRODUCT_PREFIXES);
        String suffix = randomElement(ProductConstants.PRODUCT_SUFFIXES);
        String brand = randomElement(ProductConstants.BRANDS);
        return brand + " " + prefix + " " + suffix;
    }

    private static String generateSubCategory(String category) {
        switch (category) {
            case "电子产品":
                return randomElement(ProductConstants.ELECTRONIC_SUB_CATEGORIES);
            case "服装鞋帽":
                return randomElement(ProductConstants.CLOTHING_SUB_CATEGORIES);
            default:
                return "其他";
        }
    }

    private static BigDecimal generatePrice(String category) {
        double basePrice = switch (category) {
            case "电子产品" -> 500 + RANDOM.nextDouble() * 10000;
            case "家用电器" -> 300 + RANDOM.nextDouble() * 8000;
            case "美妆护肤" -> 50 + RANDOM.nextDouble() * 2000;
            case "服装鞋帽" -> 50 + RANDOM.nextDouble() * 2000;
            case "食品生鲜" -> 10 + RANDOM.nextDouble() * 500;
            default -> 20 + RANDOM.nextDouble() * 1000;
        };
        return BigDecimal.valueOf(basePrice).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private static BigDecimal generateOriginalPrice(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(1.1 + RANDOM.nextDouble() * 0.3))
            .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private static Integer generateStock() {
        return RANDOM.nextInt(1000) + 10;
    }

    private static String generateDescription(String name, String category) {
        return "【" + name + "】" + "高品质" + category + "，正品保证，假一赔十。";
    }

    private static String generateStatus() {
        String[] statuses = {"ON_SALE", "OUT_OF_STOCK", "DISCONTINUED"};
        return randomElement(statuses);
    }

    private static Integer generateSalesCount() {
        return RANDOM.nextInt(10000);
    }

    private static Integer generateViewCount() {
        return RANDOM.nextInt(100000);
    }

    private static Double generateRating() {
        return 3.5 + RANDOM.nextDouble() * 1.5;
    }

    private static Integer generateCommentCount() {
        return RANDOM.nextInt(5000);
    }

    private static LocalDateTime generateListedAt() {
        int daysAgo = RANDOM.nextInt(365);
        return LocalDateTime.now().minusDays(daysAgo)
            .minusHours(RANDOM.nextInt(24))
            .minusMinutes(RANDOM.nextInt(60));
    }

    private static <T> T randomElement(T[] array) {
        return array[RANDOM.nextInt(array.length)];
    }

    /**
     * 商品内部类
     */
    public static class Product {
        private Long id;
        private String name;
        private String category;
        private String subCategory;
        private String brand;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private Integer stock;
        private String color;
        private String size;
        private String description;
        private String status;
        private Integer salesCount;
        private Integer viewCount;
        private Double rating;
        private Integer commentCount;
        private LocalDateTime listedAt;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getSubCategory() { return subCategory; }
        public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public BigDecimal getOriginalPrice() { return originalPrice; }
        public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Integer getSalesCount() { return salesCount; }
        public void setSalesCount(Integer salesCount) { this.salesCount = salesCount; }
        public Integer getViewCount() { return viewCount; }
        public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        public Integer getCommentCount() { return commentCount; }
        public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
        public LocalDateTime getListedAt() { return listedAt; }
        public void setListedAt(LocalDateTime listedAt) { this.listedAt = listedAt; }

        @Override
        public String toString() {
            return "Product{id=" + id + ", name='" + name + "', price=" + price + "}";
        }
    }
}
