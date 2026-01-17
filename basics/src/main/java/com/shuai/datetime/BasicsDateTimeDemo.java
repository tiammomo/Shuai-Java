package com.shuai.datetime;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;

/**
 * Java 日期时间 API 演示类
 *
 * 涵盖内容：
 * - LocalDate：日期（年-月-日）
 * - LocalTime：时间（时-分-秒）
 * - LocalDateTime：日期时间
 * - ZonedDateTime：带时区的日期时间
 * - Instant：时间戳
 * - Duration：时间段（基于时间）
 * - Period：时间段（基于日期）
 * - DateTimeFormatter：格式化
 * - 时区转换与格式化
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsDateTimeDemo {

    /**
     * 执行所有日期时间演示
     */
    public void runAllDemos() {
        localDateDemo();
        localTimeDemo();
        localDateTimeDemo();
        zonedDateTimeDemo();
        instantDemo();
        durationAndPeriod();
        dateTimeFormatter();
        temporalAdjusters();
    }

    /**
     * 演示 LocalDate（日期）
     *
     * 只包含日期信息：年、月、日
     */
    private void localDateDemo() {
        // 获取当前日期
        LocalDate today = LocalDate.now();

        // 通过指定日期创建
        LocalDate date1 = LocalDate.of(2024, 1, 15);
        LocalDate date2 = LocalDate.of(2024, Month.JANUARY, 15);

        // 使用 epoch day 创建
        LocalDate dateFromEpoch = LocalDate.ofEpochDay(0);

        // 使用字符串创建
        LocalDate dateFromString = LocalDate.parse("2024-01-15");

        // 获取日期分量
        int year = today.getYear();
        int month = today.getMonthValue();
        Month monthEnum = today.getMonth();
        int day = today.getDayOfMonth();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int dayOfYear = today.getDayOfYear();

        // 日期计算
        LocalDate tomorrow = today.plusDays(1);
        LocalDate nextMonth = today.plusMonths(1);
        LocalDate nextYear = today.plusYears(1);
        LocalDate yesterday = today.minusDays(1);

        // 使用 Period 计算
        LocalDate birthday = LocalDate.of(2000, 1, 1);
        Period age = Period.between(birthday, today);

        // 比较日期
        boolean isBefore = today.isBefore(date1);
        boolean isAfter = today.isAfter(date1);
        boolean isEqual = today.isEqual(date1);

        // 检查闰年
        boolean isLeapYear = today.isLeapYear();

        // 获取日期范围
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate firstDayOfYear = today.with(TemporalAdjusters.firstDayOfYear());
    }

    /**
     * 演示 LocalTime（时间）
     *
     * 只包含时间信息：时、分、秒、纳秒
     */
    private void localTimeDemo() {
        // 获取当前时间
        LocalTime now = LocalTime.now();

        // 通过指定时间创建
        LocalTime time1 = LocalTime.of(14, 30);
        LocalTime time2 = LocalTime.of(14, 30, 45);
        LocalTime time3 = LocalTime.of(14, 30, 45, 123456789);

        // 使用字符串创建
        LocalTime timeFromString = LocalTime.parse("14:30:45");

        // 获取时间分量
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        int nano = now.getNano();

        // 时间计算
        LocalTime later = now.plusHours(1);
        LocalTime earlier = now.minusMinutes(30);

        // 比较时间
        boolean isBefore = now.isBefore(time1);
        boolean isAfter = now.isAfter(time1);

        // 获取特殊时间
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalTime noon = LocalTime.NOON;
        LocalTime maxTime = LocalTime.MAX;
        LocalTime minTime = LocalTime.MIN;
    }

    /**
     * 演示 LocalDateTime（日期时间）
     *
     * 包含日期和时间信息
     */
    private void localDateTimeDemo() {
        // 获取当前日期时间
        LocalDateTime now = LocalDateTime.now();

        // 通过指定日期时间创建
        LocalDateTime dt1 = LocalDateTime.of(2024, 1, 15, 14, 30);
        LocalDateTime dt2 = LocalDateTime.of(2024, Month.JANUARY, 15, 14, 30, 45);
        LocalDateTime dt3 = LocalDateTime.of(LocalDate.of(2024, 1, 15), LocalTime.of(14, 30));

        // 使用字符串创建
        LocalDateTime dtFromString = LocalDateTime.parse("2024-01-15T14:30:45");

        // LocalDate + LocalTime = LocalDateTime
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        LocalDateTime dateTime = date.atTime(time);
        LocalDateTime dateTime2 = time.atDate(date);

        // 分离为日期和时间
        LocalDate separatedDate = dateTime.toLocalDate();
        LocalTime separatedTime = dateTime.toLocalTime();

        // 日期时间计算
        LocalDateTime later = now.plusDays(1).plusHours(2);

        // 格式化
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatted = now.format(formatter);
    }

    /**
     * 演示 ZonedDateTime（带时区的日期时间）
     *
     * 包含日期、时间和时区信息
     */
    private void zonedDateTimeDemo() {
        // 获取当前带时区的日期时间
        ZonedDateTime now = ZonedDateTime.now();

        // 指定时区创建
        ZonedDateTime nowInTokyo = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        ZonedDateTime nowInNewYork = ZonedDateTime.now(ZoneId.of("America/New_York"));

        // 使用指定日期时间和时区创建
        ZonedDateTime zoned1 = LocalDateTime.of(2024, 1, 15, 14, 30)
                .atZone(ZoneId.of("Asia/Shanghai"));

        // 使用字符串创建（需指定格式化器）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss VV");
        ZonedDateTime zonedFromString = ZonedDateTime.parse("2024-01-15 14:30:45 Asia/Shanghai", formatter);

        // 获取时区信息
        ZoneId zone = now.getZone();
        String zoneId = zone.getId();

        // 常用时区
        Set<String> allZones = ZoneId.getAvailableZoneIds();

        // 时区转换
        ZonedDateTime tokyoTime = nowInNewYork.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));

        // 与 UTC 转换
        Instant instant = now.toInstant();
        ZonedDateTime fromInstant = instant.atZone(ZoneId.of("UTC"));
    }

    /**
     * 演示 Instant（时间戳）
     *
     * 表示 UTC 时间线上的一个点（纳秒精度）
     */
    private void instantDemo() {
        // 获取当前时间戳
        Instant now = Instant.now();

        // 从 epoch 秒创建
        Instant fromEpochSecond = Instant.ofEpochSecond(0);

        // 从 epoch 毫秒创建
        Instant fromEpochMilli = Instant.ofEpochMilli(System.currentTimeMillis());

        // 从 Date 创建
        Date date = new Date();
        Instant fromDate = date.toInstant();

        // 转换为 Date
        Date toDate = Date.from(now);

        // 获取 epoch 值
        long epochSecond = now.getEpochSecond();
        long epochMilli = now.toEpochMilli();
        int nano = now.getNano();

        // 时间戳计算
        Instant later = now.plusSeconds(3600);
        Instant earlier = now.minusMillis(1000);

        // 时间戳比较
        boolean isBefore = now.isBefore(later);
        boolean isAfter = now.isAfter(earlier);

        // Duration 转换
        Duration duration = Duration.between(Instant.ofEpochSecond(0), now);
        long seconds = duration.getSeconds();
    }

    /**
     * 演示 Duration 和 Period
     *
     * Duration：基于时间的测量（秒、纳秒）
     * Period：基于日期的测量（年、月、日）
     */
    private void durationAndPeriod() {
        // Duration：时间段（基于时间）
        Duration duration1 = Duration.ofHours(2);
        Duration duration2 = Duration.ofMinutes(30);
        Duration duration3 = Duration.ofSeconds(60);

        // Duration 计算
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 30);
        Duration workDuration = Duration.between(startTime, endTime);

        long hours = workDuration.toHours();
        long minutes = workDuration.toMinutes();
        long totalMinutes = workDuration.toMinutes();

        // Period：时间段（基于日期）
        Period period1 = Period.of(1, 6, 0);  // 1年6个月
        Period period2 = Period.ofMonths(6);
        Period period3 = Period.ofWeeks(2);

        // Period 计算
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        Period yearPeriod = Period.between(startDate, endDate);

        int years = yearPeriod.getYears();
        int months = yearPeriod.getMonths();
        int days = yearPeriod.getDays();

        // 日期时间 + Duration/Period
        LocalDateTime dt = LocalDateTime.of(2024, 1, 15, 9, 0);
        LocalDateTime laterDt = dt.plus(duration1);
        LocalDate laterDate = startDate.plus(period1);

        // 创建日期时间的 Duration
        Duration dateTimeDuration = Duration.between(
                LocalDateTime.of(2024, 1, 15, 9, 0),
                LocalDateTime.of(2024, 1, 15, 11, 30)
        );
    }

    /**
     * 演示 DateTimeFormatter（格式化器）
     *
     * 用于格式化和解析日期时间
     */
    private void dateTimeFormatter() {
        LocalDateTime now = LocalDateTime.now();

        // 预定义格式化器
        String format1 = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String format2 = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String format3 = now.format(DateTimeFormatter.ISO_INSTANT);

        // 自定义格式化器
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern("EEEE");

        String formatted1 = now.format(formatter1);
        String formatted2 = now.format(formatter2);
        String formatted3 = now.format(formatter3);
        String formatted4 = now.format(formatter4);  // 星期几

        // 解析字符串
        LocalDate parsedDate = LocalDate.parse("2024-01-15", formatter1);
        LocalDateTime parsedDateTime = LocalDateTime.parse("2024-01-15 14:30:45",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 带时区的格式化
        ZonedDateTime zoned = ZonedDateTime.now();
        DateTimeFormatter zonedFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss VV");
        String zonedFormatted = zoned.format(zonedFormatter);

        // 格式化选项
        DateTimeFormatter localizedFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        String localized = now.format(localizedFormatter);

        // 解析时的本地化
        LocalDate chineseDate = LocalDate.parse("2024年01月15日",
                DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
    }

    /**
     * 演示 TemporalAdjusters（日期调整器）
     *
     * 用于计算特殊的日期
     */
    private void temporalAdjusters() {
        LocalDate today = LocalDate.now();

        // 常用调整器
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate firstDayOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastDayOfYear = today.with(TemporalAdjusters.lastDayOfYear());

        // 下一个/上一个
        LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate previousFriday = today.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));

        // 下一个工作日（跳过周末）
        LocalDate nextWorkday = today.with(temporal -> {
            LocalDate date = LocalDate.from(temporal);
            DayOfWeek dow = date.getDayOfWeek();
            int addDays = 1;
            if (dow == DayOfWeek.FRIDAY) addDays = 3;
            else if (dow == DayOfWeek.SATURDAY) addDays = 2;
            return date.plusDays(addDays);
        });

        // 月份第一天和最后一天
        LocalDate firstDayOfNextMonth = today.with(TemporalAdjusters.firstDayOfNextMonth());
        LocalDate lastDayOfNextMonth = today.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());

        // 自定义调整器
        LocalDate middleOfMonth = today.with(temporal -> {
            LocalDate date = LocalDate.from(temporal);
            return date.withDayOfMonth(date.lengthOfMonth() / 2);
        });

        // 使用查询
        TemporalUnit precision = today.query(TemporalQueries.precision());
    }
}
