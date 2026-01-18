package com.shuai.database.redis.data.constants;

/**
 * 订单数据常量
 */
public final class OrderConstants {

    private OrderConstants() {}

    // 订单状态
    public static final String[] ORDER_STATUSES = {
        "PENDING", "PAID", "PROCESSING", "SHIPPED", "DELIVERED", "COMPLETED", "CANCELLED", "REFUNDED"
    };

    // 支付方式
    public static final String[] PAYMENT_METHODS = {
        "ALIPAY", "WECHAT", "CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER"
    };

    // 物流状态
    public static final String[] SHIPPING_STATUSES = {
        "PENDING", "PICKED_UP", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED"
    };

    // 支付状态
    public static final String[] PAYMENT_STATUSES = {
        "UNPAID", "PAID", "REFUNDING", "REFUNDED", "FAILED"
    };

    // 订单类型
    public static final String[] ORDER_TYPES = {
        "NORMAL", "FLASH_SALE", "GROUP_BUY", "PROMOTION"
    };

    // 收货城市
    public static final String[] CITIES = {
        "北京", "上海", "广州", "深圳", "杭州", "成都", "武汉", "南京", "西安", "重庆",
        "苏州", "天津", "长沙", "青岛", "大连", "宁波", "厦门", "郑州", "昆明", "沈阳"
    };

    // 详细地址前缀
    public static final String[] ADDRESS_PREFIXES = {
        "朝阳区建国路", "浦东新区陆家嘴", "天河区珠江新城", "南山区科技园",
        "西湖区文三路", "高新区天府软件园", "洪山区光谷", "鼓楼区中山路",
        "雁塔区小寨", "渝北区观音桥", "工业园区金鸡湖", "和平区南京路",
        "岳麓区麓谷", "市南区五四广场", "星海广场", "鄞州区南部商务区",
        "思明区中山路", "金水区花园路", "盘龙区翠湖", "沈河区中街"
    };
}
