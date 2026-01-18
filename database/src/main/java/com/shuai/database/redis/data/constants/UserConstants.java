package com.shuai.database.redis.data.constants;

/**
 * 用户数据常量
 */
public final class UserConstants {

    private UserConstants() {}

    // 姓氏
    public static final String[] SURNAMES = {
        "张", "王", "李", "赵", "刘", "陈", "杨", "黄", "周", "吴",
        "徐", "孙", "马", "朱", "胡", "郭", "林", "何", "高", "罗"
    };

    // 名字
    public static final String[] NAMES = {
        "伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋",
        "勇", "艳", "杰", "娟", "涛", "明", "超", "秀英", "霞", "平"
    };

    // 邮箱域名
    public static final String[] EMAIL_DOMAINS = {
        "gmail.com", "163.com", "qq.com", "outlook.com", "yahoo.com",
        "sina.com", "hotmail.com", "foxmail.com", "icloud.com", "mail.com"
    };

    // 手机号前缀
    public static final String[] PHONE_PREFIXES = {
        "130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
        "150", "151", "152", "153", "155", "156", "157", "158", "159",
        "180", "181", "182", "183", "185", "186", "187", "188", "189"
    };

    // 状态
    public static final String[] STATUSES = {"ACTIVE", "INACTIVE", "PENDING", "SUSPENDED"};

    // 城市
    public static final String[] CITIES = {
        "北京", "上海", "广州", "深圳", "杭州", "成都", "武汉", "南京", "西安", "重庆",
        "苏州", "天津", "长沙", "青岛", "大连", "宁波", "厦门", "郑州", "昆明", "沈阳"
    };

    // 职业
    public static final String[] OCCUPATIONS = {
        "软件工程师", "产品经理", "设计师", "销售经理", "财务分析师",
        "市场专员", "HR", "教师", "医生", "律师",
        "会计", "项目经理", "数据分析师", "UI设计师", "测试工程师"
    };
}
