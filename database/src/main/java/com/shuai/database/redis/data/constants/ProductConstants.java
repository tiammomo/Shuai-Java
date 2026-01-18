package com.shuai.database.redis.data.constants;

/**
 * 商品数据常量
 */
public final class ProductConstants {

    private ProductConstants() {}

    // 商品分类
    public static final String[] CATEGORIES = {
        "电子产品", "服装鞋帽", "家用电器", "美妆护肤", "食品生鲜",
        "母婴玩具", "运动户外", "家居家装", "图书音像", "数码配件"
    };

    // 电子产品子分类
    public static final String[] ELECTRONIC_SUB_CATEGORIES = {
        "手机", "电脑", "平板", "耳机", "相机", "智能手表", "游戏机", "音箱"
    };

    // 服装鞋帽子分类
    public static final String[] CLOTHING_SUB_CATEGORIES = {
        "男装", "女装", "童装", "男鞋", "女鞋", "箱包", "配饰"
    };

    // 品牌
    public static final String[] BRANDS = {
        "Apple", "华为", "小米", "OPPO", "vivo", "三星", "联想", "戴尔",
        "耐克", "阿迪达斯", "优衣库", "ZARA", "海澜之家", "七匹狼",
        "美的", "格力", "海尔", "松下", "飞利浦",
        "欧莱雅", "雅诗兰黛", "兰蔻", "完美日记", "花西子"
    };

    // 商品名称前缀
    public static final String[] PRODUCT_PREFIXES = {
        "旗舰版", "Pro版", "标准版", "青春版", "尊享版", "豪华版", "经典版", "运动版"
    };

    // 商品名称后缀
    public static final String[] PRODUCT_SUFFIXES = {
        "新款", "限量版", "爆款", "热销", "推荐", "新品上市", "促销", "特价"
    };

    // 颜色
    public static final String[] COLORS = {
        "黑色", "白色", "灰色", "银色", "金色", "玫瑰金", "蓝色", "绿色", "红色", "粉色"
    };

    // 尺寸
    public static final String[] SIZES = {
        "XS", "S", "M", "L", "XL", "XXL",
        "36", "37", "38", "39", "40", "41", "42", "43", "44"
    };

    // 单位
    public static final String[] UNITS = {"件", "个", "台", "套", "盒", "包", "斤", "kg"};
}
