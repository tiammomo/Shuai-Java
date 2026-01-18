package com.shuai.elasticsearch.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.config.IndexConfig;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.model.LocationDocument;
import com.shuai.elasticsearch.model.LocationDocument.GeoPoint;
import com.shuai.elasticsearch.model.ProductDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 测试数据初始化工具类
 *
 * 功能说明
 * ----------
 * 提供演示用的测试数据批量初始化功能，
 * 方便快速填充索引数据进行演示和测试。
 *
 * 数据说明
 * ----------
 *   - blog: 30 篇博客文档
 *   - locations: 20 个地理位置点（北京、上海著名地点）
 *
 * @author Shuai
 * @version 1.0
 */
public class DataInitUtil {

    private static final String BLOG_INDEX = "blog";
    private static final String LOCATIONS_INDEX = "locations";

    /**
     * 初始化所有测试数据
     *
     * @param client ES 客户端
     * @throws IOException IO异常
     */
    public static void initAllData(ElasticsearchClient client) throws IOException {
        ResponsePrinter.printMethodInfo("initAllData", "初始化所有测试数据");

        // 先创建索引
        IndexConfig.createBlogIndex(client);
        IndexConfig.createLocationsIndex(client);
        IndexConfig.createProductIndex(client);

        // 删除旧数据
        deleteAllData(client);

        // 插入测试数据
        initSampleBlogs(client);
        initSampleLocations(client);
        initSampleProducts(client);

        // 刷新索引
        client.indices().refresh(r -> r.index(Arrays.asList(BLOG_INDEX, LOCATIONS_INDEX, "product")));

        ResponsePrinter.printResponse("数据初始化", "测试数据初始化完成");
    }

    /**
     * 删除所有演示数据
     *
     * @param client ES 客户端
     * @throws IOException IO异常
     */
    public static void deleteAllData(ElasticsearchClient client) throws IOException {
        ResponsePrinter.printMethodInfo("deleteAllData", "删除所有演示数据");

        client.deleteByQuery(d -> d
            .index(BLOG_INDEX)
            .query(q -> q.matchAll(m -> m))
        );

        client.deleteByQuery(d -> d
            .index(LOCATIONS_INDEX)
            .query(q -> q.matchAll(m -> m))
        );

        ResponsePrinter.printResponse("删除数据", "演示数据已删除");
    }

    /**
     * 初始化示例博客数据
     *
     * 生成 30 篇博客，涵盖不同主题和作者
     *
     * @param client ES 客户端
     * @throws IOException IO异常
     */
    public static void initSampleBlogs(ElasticsearchClient client) throws IOException {
        ResponsePrinter.printMethodInfo("initSampleBlogs", "初始化示例博客数据 (30篇)");

        List<BlogDocument> blogs = generateSampleBlogs();

        int count = 0;
        for (BlogDocument doc : blogs) {
            client.index(i -> i
                .index(BLOG_INDEX)
                .id(doc.getId())
                .document(doc)
            );
            count++;
        }

        ResponsePrinter.printResponse("插入博客", count + " 篇博客已插入");
    }

    /**
     * 生成示例博客数据
     */
    private static List<BlogDocument> generateSampleBlogs() {
        List<BlogDocument> blogs = new ArrayList<>();
        String[] authors = {"张三", "李四", "王五", "赵六", "钱七"};
        String[] statuses = {"published", "draft"};
        String[][] tagsArray = {
            {"Java", "教程", "编程"},
            {"Python", "AI", "机器学习"},
            {"Spring", "Java", "框架"},
            {"Docker", "容器", "DevOps"},
            {"大数据", "Hadoop", "Spark"},
            {"MySQL", "数据库", "SQL"},
            {"Redis", "缓存", "NoSQL"},
            {"Elasticsearch", "搜索", "大数据"},
            {"算法", "数据结构", "面试"},
            {"前端", "React", "Vue"}
        };

        String[] titles = {
            "Java 基础教程，从入门到精通",
            "Python 机器学习实战指南",
            "Spring Boot 快速开发企业应用",
            "Docker 容器化部署完全指南",
            "Hadoop 大数据处理架构详解",
            "MySQL 性能优化最佳实践",
            "Redis 缓存架构设计与实现",
            "Elasticsearch 全文检索实战",
            "算法与数据结构面试题精讲",
            "前端工程化与性能优化"
        };

        String[] contents = {
            "本文详细介绍 Java 编程语言的基础知识，包括面向对象、集合框架、多线程等核心概念。",
            "本教程涵盖 Python 在机器学习领域的应用，包括 NumPy、Pandas、Scikit-learn 等库的使用。",
            "Spring Boot 让 Java 企业级开发变得更简单，本文介绍其核心特性和最佳实践。",
            "Docker 是现代软件部署的核心技术，本文讲解容器化原理和实际应用场景。",
            "Hadoop 是大数据处理的基础框架，本文深入分析其 HDFS 和 MapReduce 架构。",
            "数据库性能优化是每个开发者必须掌握的技能，本文分享 MySQL 优化经验和技巧。",
            "Redis 作为高性能缓存数据库，在互联网应用中广泛使用，本文探讨其架构设计。",
            "Elasticsearch 是强大的全文检索引擎，本文通过实际案例展示其强大功能。",
            "算法是程序员的核心竞争力，本文精选高频面试算法题进行详细讲解。",
            "前端工程化是现代前端开发的必备技能，本文介绍模块化、构建和优化方法。"
        };

        Random random = new Random();
        int id = 1;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 3; j++) {
                blogs.add(BlogDocument.builder()
                    .id(String.valueOf(id++))
                    .title(titles[i])
                    .content(contents[i] + " 详细内容请参考本系列文章。")
                    .author(authors[random.nextInt(authors.length)])
                    .tags(Arrays.asList(tagsArray[i]))
                    .views(50 + random.nextInt(500))
                    .status(statuses[random.nextInt(2)])
                    .createTime("2024-0" + (1 + random.nextInt(9)) + "-" + (10 + random.nextInt(20)))
                    .build());
            }
        }

        return blogs;
    }

    /**
     * 初始化示例地理位置数据
     *
     * 生成北京、上海著名地点的地理位置信息
     *
     * @param client ES 客户端
     * @throws IOException IO异常
     */
    public static void initSampleLocations(ElasticsearchClient client) throws IOException {
        ResponsePrinter.printMethodInfo("initSampleLocations", "初始化示例地理位置数据 (20个)");

        List<LocationDocument> locations = generateSampleLocations();

        int count = 0;
        for (LocationDocument doc : locations) {
            client.index(i -> i
                .index(LOCATIONS_INDEX)
                .id(doc.getId())
                .document(doc)
            );
            count++;
        }

        ResponsePrinter.printResponse("插入位置", count + " 个地理位置已插入");
    }

    /**
     * 生成示例地理位置数据
     */
    private static List<LocationDocument> generateSampleLocations() {
        List<LocationDocument> locations = new ArrayList<>();

        // 北京著名地点
        locations.addAll(Arrays.asList(
            LocationDocument.builder()
                .id("1")
                .name("天安门广场")
                .location(GeoPoint.builder().lat(39.9055).lon(116.3975).build())
                .type("广场")
                .build(),
            LocationDocument.builder()
                .id("2")
                .name("故宫")
                .location(GeoPoint.builder().lat(39.9163).lon(116.3972).build())
                .type("景点")
                .build(),
            LocationDocument.builder()
                .id("3")
                .name("天坛")
                .location(GeoPoint.builder().lat(39.8822).lon(116.4066).build())
                .type("景点")
                .build(),
            LocationDocument.builder()
                .id("4")
                .name("颐和园")
                .location(GeoPoint.builder().lat(39.9996).lon(116.2755).build())
                .type("公园")
                .build(),
            LocationDocument.builder()
                .id("5")
                .name("长城（八达岭）")
                .location(GeoPoint.builder().lat(40.4319).lon(117.2157).build())
                .type("景点")
                .build(),
            LocationDocument.builder()
                .id("6")
                .name("鸟巢")
                .location(GeoPoint.builder().lat(39.9903).lon(116.3912).build())
                .type("体育场馆")
                .build(),
            LocationDocument.builder()
                .id("7")
                .name("水立方")
                .location(GeoPoint.builder().lat(39.9892).lon(116.3887).build())
                .type("体育场馆")
                .build(),
            LocationDocument.builder()
                .id("8")
                .name("三里屯")
                .location(GeoPoint.builder().lat(39.9377).lon(116.4546).build())
                .type("商业区")
                .build(),
            LocationDocument.builder()
                .id("9")
                .name("西单")
                .location(GeoPoint.builder().lat(39.9127).lon(116.3692).build())
                .type("商业区")
                .build(),
            LocationDocument.builder()
                .id("10")
                .name("中关村")
                .location(GeoPoint.builder().lat(39.9561).lon(116.3099).build())
                .type("科技园区")
                .build()
        ));

        // 上海著名地点
        locations.addAll(Arrays.asList(
            LocationDocument.builder()
                .id("11")
                .name("外滩")
                .location(GeoPoint.builder().lat(31.2397).lon(121.4990).build())
                .type("景点")
                .build(),
            LocationDocument.builder()
                .id("12")
                .name("东方明珠")
                .location(GeoPoint.builder().lat(31.2397).lon(121.4998).build())
                .type("地标")
                .build(),
            LocationDocument.builder()
                .id("13")
                .name("豫园")
                .location(GeoPoint.builder().lat(31.2277).lon(121.4819).build())
                .type("景点")
                .build(),
            LocationDocument.builder()
                .id("14")
                .name("南京路步行街")
                .location(GeoPoint.builder().lat(31.2304).lon(121.4747).build())
                .type("商业区")
                .build(),
            LocationDocument.builder()
                .id("15")
                .name("静安寺")
                .location(GeoPoint.builder().lat(31.2294).lon(121.4485).build())
                .type("宗教场所")
                .build(),
            LocationDocument.builder()
                .id("16")
                .name("陆家嘴")
                .location(GeoPoint.builder().lat(31.2397).lon(121.5001).build())
                .type("金融区")
                .build(),
            LocationDocument.builder()
                .id("17")
                .name("世博园")
                .location(GeoPoint.builder().lat(31.1851).lon(121.4748).build())
                .type("公园")
                .build(),
            LocationDocument.builder()
                .id("18")
                .name("田子坊")
                .location(GeoPoint.builder().lat(31.2185).lon(121.4736).build())
                .type("文化区")
                .build(),
            LocationDocument.builder()
                .id("19")
                .name("新天地")
                .location(GeoPoint.builder().lat(31.2196).lon(121.4813).build())
                .type("商业区")
                .build(),
            LocationDocument.builder()
                .id("20")
                .name("迪士尼乐园")
                .location(GeoPoint.builder().lat(31.1443).lon(121.6590).build())
                .type("游乐场")
                .build()
        ));

        return locations;
    }

    /**
     * 初始化示例产品数据
     *
     * 生成 15 个产品，涵盖不同类别和属性
     *
     * @param client ES 客户端
     * @throws IOException IO异常
     */
    public static void initSampleProducts(ElasticsearchClient client) throws IOException {
        ResponsePrinter.printMethodInfo("initSampleProducts", "初始化示例产品数据 (15个)");

        List<ProductDocument> products = generateSampleProducts();

        int count = 0;
        for (ProductDocument doc : products) {
            client.index(i -> i
                .index("product")
                .id(doc.getId())
                .document(doc)
            );
            count++;
        }

        ResponsePrinter.printResponse("插入产品", count + " 个产品已插入");
    }

    /**
     * 生成示例产品数据
     */
    private static List<ProductDocument> generateSampleProducts() {
        List<ProductDocument> products = new ArrayList<>();
        Random random = new Random();

        // 笔记本电脑
        products.add(ProductDocument.builder()
            .id("P001")
            .name("MacBook Pro 14英寸 M3 Pro")
            .category("笔记本电脑")
            .price(14999.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("内存", "18GB", "string"),
                new ProductDocument.ProductProperty("硬盘", "512GB", "string"),
                new ProductDocument.ProductProperty("CPU", "M3 Pro", "string"),
                new ProductDocument.ProductProperty("屏幕", "14.2英寸", "string")
            ))
            .tags(Arrays.asList("Apple", "笔记本", "高端"))
            .status("on_sale")
            .stock(50)
            .rating(4.9)
            .build());

        products.add(ProductDocument.builder()
            .id("P002")
            .name("联想ThinkPad X1 Carbon")
            .category("笔记本电脑")
            .price(9999.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("内存", "16GB", "string"),
                new ProductDocument.ProductProperty("硬盘", "1TB", "string"),
                new ProductDocument.ProductProperty("CPU", "i7-1365U", "string"),
                new ProductDocument.ProductProperty("重量", "1.12kg", "string")
            ))
            .tags(Arrays.asList("联想", "商务本", "轻薄"))
            .status("on_sale")
            .stock(100)
            .rating(4.7)
            .build());

        products.add(ProductDocument.builder()
            .id("P003")
            .name("戴尔 XPS 15")
            .category("笔记本电脑")
            .price(12999.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("内存", "32GB", "string"),
                new ProductDocument.ProductProperty("硬盘", "1TB", "string"),
                new ProductDocument.ProductProperty("CPU", "i7-13700H", "string"),
                new ProductDocument.ProductProperty("显卡", "RTX 4050", "string")
            ))
            .tags(Arrays.asList("戴尔", "创作本", "高性能"))
            .status("on_sale")
            .stock(30)
            .rating(4.8)
            .build());

        // 手机
        products.add(ProductDocument.builder()
            .id("P004")
            .name("iPhone 15 Pro Max")
            .category("手机")
            .price(9999.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("内存", "8GB", "string"),
                new ProductDocument.ProductProperty("存储", "256GB", "string"),
                new ProductDocument.ProductProperty("屏幕", "6.7英寸", "string"),
                new ProductDocument.ProductProperty("摄像头", "4800万像素", "string")
            ))
            .tags(Arrays.asList("Apple", "手机", "旗舰"))
            .status("on_sale")
            .stock(200)
            .rating(4.9)
            .build());

        products.add(ProductDocument.builder()
            .id("P005")
            .name("华为 Mate 60 Pro")
            .category("手机")
            .price(6999.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("内存", "12GB", "string"),
                new ProductDocument.ProductProperty("存储", "512GB", "string"),
                new ProductDocument.ProductProperty("屏幕", "6.82英寸", "string"),
                new ProductDocument.ProductProperty("卫星通信", "支持", "string")
            ))
            .tags(Arrays.asList("华为", "手机", "卫星通信"))
            .status("on_sale")
            .stock(150)
            .rating(4.8)
            .build());

        products.add(ProductDocument.builder()
            .id("P006")
            .name("小米 14 Ultra")
            .category("手机")
            .price(6499.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("内存", "16GB", "string"),
                new ProductDocument.ProductProperty("存储", "512GB", "string"),
                new ProductDocument.ProductProperty("屏幕", "6.73英寸", "string"),
                new ProductDocument.ProductProperty("徕卡四摄", "是", "string")
            ))
            .tags(Arrays.asList("小米", "手机", "拍照旗舰"))
            .status("on_sale")
            .stock(100)
            .rating(4.7)
            .build());

        // 平板
        products.add(ProductDocument.builder()
            .id("P007")
            .name("iPad Pro 12.9英寸 M2")
            .category("平板")
            .price(9299.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("内存", "8GB", "string"),
                new ProductDocument.ProductProperty("存储", "256GB", "string"),
                new ProductDocument.ProductProperty("屏幕", "12.9英寸", "string"),
                new ProductDocument.ProductProperty("M2芯片", "是", "string")
            ))
            .tags(Arrays.asList("Apple", "平板", "生产力"))
            .status("on_sale")
            .stock(80)
            .rating(4.8)
            .build());

        products.add(ProductDocument.builder()
            .id("P008")
            .name("华为 MatePad Pro 13.2")
            .category("平板")
            .price(5699.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("内存", "12GB", "string"),
                new ProductDocument.ProductProperty("存储", "256GB", "string"),
                new ProductDocument.ProductProperty("屏幕", "13.2英寸", "string"),
                new ProductDocument.ProductProperty("星闪技术", "支持", "string")
            ))
            .tags(Arrays.asList("华为", "平板", "大屏"))
            .status("on_sale")
            .stock(60)
            .rating(4.6)
            .build());

        // 耳机
        products.add(ProductDocument.builder()
            .id("P009")
            .name("索尼 WH-1000XM5")
            .category("耳机")
            .price(2999.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("降噪", "支持", "string"),
                new ProductDocument.ProductProperty("续航", "30小时", "string"),
                new ProductDocument.ProductProperty("连接", "蓝牙5.2", "string"),
                new ProductDocument.ProductProperty("驱动单元", "30mm", "string")
            ))
            .tags(Arrays.asList("索尼", "降噪耳机", "旗舰"))
            .status("on_sale")
            .stock(200)
            .rating(4.8)
            .build());

        products.add(ProductDocument.builder()
            .id("P010")
            .name("AirPods Pro 第二代")
            .category("耳机")
            .price(1899.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("降噪", "支持", "string"),
                new ProductDocument.ProductProperty("续航", "6小时", "string"),
                new ProductDocument.ProductProperty("连接", "蓝牙5.3", "string"),
                new ProductDocument.ProductProperty("自适应音频", "支持", "string")
            ))
            .tags(Arrays.asList("Apple", "耳机", "TWS"))
            .status("on_sale")
            .stock(300)
            .rating(4.7)
            .build());

        // 智能手表
        products.add(ProductDocument.builder()
            .id("P011")
            .name("Apple Watch Ultra 2")
            .category("智能手表")
            .price(6499.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("屏幕", "49mm", "string"),
                new ProductDocument.ProductProperty("续航", "36小时", "string"),
                new ProductDocument.ProductProperty("防水", "100米", "string"),
                new ProductDocument.ProductProperty("S9芯片", "是", "string")
            ))
            .tags(Arrays.asList("Apple", "智能手表", "运动"))
            .status("on_sale")
            .stock(40)
            .rating(4.9)
            .build());

        products.add(ProductDocument.builder()
            .id("P012")
            .name("华为 Watch GT 4")
            .category("智能手表")
            .price(1588.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("屏幕", "1.43英寸", "string"),
                new ProductDocument.ProductProperty("续航", "14天", "string"),
                new ProductDocument.ProductProperty("防水", "50米", "string"),
                new ProductDocument.ProductProperty("运动模式", "100+", "string")
            ))
            .tags(Arrays.asList("华为", "智能手表", "长续航"))
            .status("on_sale")
            .stock(150)
            .rating(4.6)
            .build());

        // 台式机
        products.add(ProductDocument.builder()
            .id("P013")
            .name("Mac Studio M2 Max")
            .category("台式机")
            .price(16499.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("内存", "32GB", "string"),
                new ProductDocument.ProductProperty("存储", "512GB", "string"),
                new ProductDocument.ProductProperty("CPU", "M2 Max", "string"),
                new ProductDocument.ProductProperty("显卡", "38核", "string")
            ))
            .tags(Arrays.asList("Apple", "台式机", "专业"))
            .status("on_sale")
            .stock(20)
            .rating(4.9)
            .build());

        products.add(ProductDocument.builder()
            .id("P014")
            .name("联想拯救者刃7000K")
            .category("台式机")
            .price(8999.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("内存", "16GB", "string"),
                new ProductDocument.ProductProperty("存储", "1TB", "string"),
                new ProductDocument.ProductProperty("CPU", "i7-13700K", "string"),
                new ProductDocument.ProductProperty("显卡", "RTX 4060 Ti", "string")
            ))
            .tags(Arrays.asList("联想", "游戏台式机", "高性能"))
            .status("on_sale")
            .stock(35)
            .rating(4.7)
            .build());

        // 显示器
        products.add(ProductDocument.builder()
            .id("P015")
            .name("戴尔 U2723QE 27英寸 4K")
            .category("显示器")
            .price(3999.0)
            .properties(Arrays.asList(
                new ProductDocument.ProductProperty("分辨率", "4K", "string"),
                new ProductDocument.ProductProperty("屏幕", "27英寸", "string"),
                new ProductDocument.ProductProperty("接口", "全功能Type-C", "string"),
                new ProductDocument.ProductProperty("色彩", "98% DCI-P3", "string")
            ))
            .tags(Arrays.asList("戴尔", "显示器", "专业"))
            .status("on_sale")
            .stock(60)
            .rating(4.8)
            .build());

        return products;
    }
}
