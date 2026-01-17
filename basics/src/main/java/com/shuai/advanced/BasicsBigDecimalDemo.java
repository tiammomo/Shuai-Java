package com.shuai.advanced;

import java.math.*;
import java.util.*;
import java.util.function.*;

/**
 * Java BigDecimal 演示类
 *
 * 涵盖内容：
 * - 浮点数精度问题：double 的二进制精度损失
 * - BigDecimal 基础：创建方式、String vs double 构造
 * - 算术运算：add, subtract, multiply, divide, pow
 * - 舍入模式：HALF_UP, HALF_DOWN, HALF_EVEN 等
 * - 比较方式：equals vs compareTo
 * - 实际应用：价格计算、金额运算
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsBigDecimalDemo {

    /**
     * 执行所有 BigDecimal 演示
     */
    public void runAllDemos() {
        floatingPointProblem();
        bigDecimalBasics();
        arithmeticOperations();
        roundingModes();
        comparison();
        practicalUsage();
    }

    /**
     * 演示浮点数精度问题
     *
     * double 是二进制浮点数，存在精度问题：
     * - 0.1 + 0.2 != 0.3
     * - 0.3 != 0.1 + 0.1 + 0.1
     * - 这是二进制表示精度损失导致的
     */
    private void floatingPointProblem() {
        // double 精度问题
        double a = 0.1;
        double b = 0.2;
        double sum = a + b;
        // sum != 0.3 (二进制浮点数精度问题)

        double c = 0.3;
        double d = 0.1 + 0.1 + 0.1;
        // c != d (0.30000000000000004)
    }

    /**
     * 演示 BigDecimal 基础
     *
     * 创建方式：
     * - 使用 String 构造：避免精度问题
     * - 使用 valueOf：推荐方式
     * - 避免使用 double 构造：会有精度问题
     */
    private void bigDecimalBasics() {
        // 使用 String 构造避免精度问题
        BigDecimal bd1 = new BigDecimal("0.1");
        BigDecimal bd2 = new BigDecimal("0.2");
        BigDecimal sum = bd1.add(bd2);  // 0.3

        // 使用 valueOf 构造
        BigDecimal bd3 = BigDecimal.valueOf(0.1);
        BigDecimal bd4 = BigDecimal.valueOf(0.2);

        // 不要使用 double 构造，会有精度问题
        BigDecimal bd5 = new BigDecimal(0.1);
        BigDecimal bd6 = new BigDecimal(0.2);

        Supplier<BigDecimal> sumSupplier = () -> new BigDecimal("0.1").add(new BigDecimal("0.2"));
    }

    /**
     * 演示算术运算
     *
     * BigDecimal 提供精确算术运算：
     * - add：加法
     * - subtract：减法
     * - multiply：乘法
     * - divide：除法（需要指定舍入模式）
     * - pow：幂运算
     * - negate：取反
     */
    private void arithmeticOperations() {
        BigDecimal x = new BigDecimal("10");
        BigDecimal y = new BigDecimal("3");

        // 算术运算
        BigDecimal addResult = x.add(y);
        BigDecimal subtractResult = x.subtract(y);
        BigDecimal multiplyResult = x.multiply(y);
        BigDecimal divideResult = x.divide(y, RoundingMode.HALF_UP);
        BigDecimal powResult = x.pow(2);
        BigDecimal negateResult = x.negate();

        // 常量
        BigDecimal ONE = BigDecimal.ONE;
        BigDecimal TEN = BigDecimal.TEN;
        BigDecimal ZERO = BigDecimal.ZERO;
    }

    /**
     * 演示舍入模式
     *
     * java.math.RoundingMode 枚举：
     * - HALF_UP：四舍五入（常用）
     * - HALF_DOWN：五舍六入
     * - HALF_EVEN：银行家舍入
     * - UP：向上舍入
     * - DOWN：向下舍入
     * - CEILING：向正无穷舍入
     * - FLOOR：向负无穷舍入
     */
    private void roundingModes() {
        BigDecimal num = new BigDecimal("3.456");

        // 舍入模式
        BigDecimal halfUp = num.setScale(2, RoundingMode.HALF_UP);
        BigDecimal halfDown = num.setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal halfEven = num.setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal up = num.setScale(2, RoundingMode.UP);
        BigDecimal down = num.setScale(2, RoundingMode.DOWN);
        BigDecimal ceiling = num.setScale(0, RoundingMode.CEILING);
        BigDecimal floor = num.setScale(0, RoundingMode.FLOOR);

        BigDecimal half = new BigDecimal("2.5");
        BigDecimal halfUp2 = half.setScale(0, RoundingMode.HALF_UP);
        BigDecimal halfDown2 = half.setScale(0, RoundingMode.HALF_DOWN);
        BigDecimal halfEven2 = half.setScale(0, RoundingMode.HALF_EVEN);
    }

    /**
     * 演示比较方式
     *
     * 比较方法区别：
     * - equals：比较值和精度（1.0 != 1.00）
     * - compareTo：仅比较数值（1.0 == 1.00）
     * - 金额比较建议使用 compareTo
     */
    private void comparison() {
        BigDecimal bd1 = new BigDecimal("1.0");
        BigDecimal bd2 = new BigDecimal("1.00");
        BigDecimal bd3 = new BigDecimal("2.0");

        // equals 比较精度
        boolean isEqual = bd1.equals(bd2);  // false (精度不同)

        // compareTo 比较数值
        int result1 = bd1.compareTo(bd2);  // 0 (相等)
        int result2 = bd1.compareTo(bd3);  // -1 (bd1 < bd3)

        // compareTo 判断
        int cmp = bd1.compareTo(bd3);
        if (cmp < 0) {
            // bd1 < bd3
        } else if (cmp > 0) {
            // bd1 > bd3
        } else {
            // bd1 == bd3
        }

        Comparator<BigDecimal> reverseOrder = Comparator.reverseOrder();
    }

    /**
     * 演示实际应用场景
     *
     * 典型场景：
     * - 价格计算：原价、折扣、最终价格
     * - 税费计算：税率应用
     * - 总金额汇总：Stream reduce
     * - 四舍五入到分：setScale(2, RoundingMode.HALF_UP)
     */
    private void practicalUsage() {
        BigDecimal price = new BigDecimal("99.99");
        BigDecimal discount = new BigDecimal("0.15");

        BigDecimal discountAmount = price.multiply(discount);
        BigDecimal finalPrice = price.subtract(discountAmount);

        BigDecimal taxRate = new BigDecimal("0.08");
        BigDecimal tax = finalPrice.multiply(taxRate);
        BigDecimal total = finalPrice.add(tax);

        Function<BigDecimal, BigDecimal> calculateTax = amount ->
            amount.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);

        List<BigDecimal> prices = Arrays.asList(
            new BigDecimal("10.00"),
            new BigDecimal("20.50"),
            new BigDecimal("30.75")
        );

        BigDecimal totalAmount = prices.stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
