package com.shuai.datatype;

/**
 * Java 运算符演示类
 *
 * 涵盖内容：
 * - 算术运算符：+, -, *, /, %, ++, --
 * - 关系运算符：==, !=, >, <, >=, <=
 * - 逻辑运算符：&&, ||, !, &, |
 * - 位运算符：&, |, ^, ~, <<, >>, >>>
 * - 赋值运算符：=, +=, -=, *=, /=, %=
 * - 三元运算符：condition ? expr1 : expr2
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsOperatorsDemo {

    /**
     * 执行所有运算符演示
     */
    public void runAllDemos() {
        arithmeticOperators();
        relationalOperators();
        logicalOperators();
        bitwiseOperators();
        assignmentOperators();
        operatorPrecedence();
    }

    /**
     * 演示算术运算符
     *
     * 注意：
     * - 整数除法会截断小数部分
     * - ++ 和 -- 有前缀/后缀之分：前缀先增/减，后缀后增/减
     * - 除以零会抛出 ArithmeticException
     */
    private void arithmeticOperators() {
        int a = 10, b = 3;

        // + 加法 / 字符串拼接
        int sum = a + b;  // 13

        // - 减法
        int diff = a - b;  // 7

        // * 乘法
        int product = a * b;  // 30

        // / 除法（整数除法截断小数）
        int quotient = a / b;  // 3

        // % 取余（取模）
        int remainder = a % b;  // 1

        // ++ 自增
        int x = 5;
        x++;  // 后增：先返回当前值，再增加
        ++x;  // 前增：先增加，再返回新值

        // -- 自减
        x--;  // 后减
        --x;  // 前减
    }

    /**
     * 演示关系运算符
     *
     * 关系运算符返回 boolean 类型：
     * - 用于比较两个值的大小关系
     * - 可以比较数值、字符等基本类型
     * - 对象比较用 equals() 方法而非 ==
     */
    private void relationalOperators() {
        // == 等于, != 不等于, > 大于, < 小于, >= 大于等于, <= 小于等于
        boolean isEqual = 5 == 5;      // true
        boolean isNotEqual = 5 != 3;   // true
        boolean isGreater = 5 > 3;      // true
        boolean isLess = 5 < 10;        // true
        boolean isGreaterOrEqual = 5 >= 5;  // true
        boolean isLessOrEqual = 3 <= 5;     // true
    }

    /**
     * 演示逻辑运算符
     *
     * 短路特性：
     * - &&：左边 false 则右边不执行，直接返回 false
     * - ||：左边 true 则右边不执行，直接返回 true
     * - & 和 | 会执行两边再计算（用于需要副作用的场景）
     */
    private void logicalOperators() {
        // && 短路与：左边 false 则右边不执行
        // || 短路或：左边 true 则右边不执行
        // ! 非
        // & 与（非短路），| 或（非短路）

        boolean andResult = true && false;  // false
        boolean orResult = true || false;   // true
        boolean notResult = !true;           // false

        // 非短路运算符（两边都执行）
        boolean andNonShort = true & false;  // false
        boolean orNonShort = true | false;   // true
    }

    /**
     * 演示位运算符
     *
     * 位运算符直接操作整数的二进制位：
     * - &：对应位都为1才为1
     * - |：对应位有一个为1就为1
     * - ^：对应位不同为1，相同为0
     * - ~：按位取反
     * - <<：左移（乘2的n次方）
     * - >>：右移（除2的n次方，保持符号位）
     * - >>>：无符号右移（高位补0）
     */
    private void bitwiseOperators() {
        int andResult = 5 & 3;   // 1 (0101 & 0011 = 0001)
        int orResult = 5 | 3;    // 7 (0101 | 0011 = 0111)
        int xorResult = 5 ^ 3;   // 6 (0101 ^ 0011 = 0110)
        int notResult = ~5;      // -6 (按位取反)
        int leftShift = 5 << 1;  // 10 (左移1位 = 5 * 2^1)
        int rightShift = 8 >> 1; // 4 (右移1位 = 8 / 2^1)
    }

    /**
     * 演示赋值运算符
     *
     * 复合赋值运算符会自动进行类型转换：
     * - x += y 等价于 x = (x的类型)(x + y)
     * - 可以避免类型溢出（如 x *= 2 在 byte 上）
     */
    private void assignmentOperators() {
        int x = 5;
        x += 3;  // x = x + 3 = 8
        x -= 3;  // x = x - 3 = 5
        x *= 3;  // x = x * 3 = 15
        x /= 3;  // x = x / 3 = 5
        x %= 3;  // x = x % 3 = 2
        x &= 3;  // x = x & 3
        x |= 3;  // x = x | 3
    }

    /**
     * 演示运算符优先级
     *
     * 优先级从高到低（建议使用括号明确意图）：
     * - () [] .
     * - ++ -- ! ~ (类型)
     * - * / %
     * - + -
     * - << >> >>>
     * - < <= > >= instanceof
     * - == !=
     * - &
     * - ^
     * - |
     * - &&
     * - ||
     * - ?:
     * - = += -= 等
     */
    private void operatorPrecedence() {
        // 三元运算符：条件 ? 表达式1 : 表达式2
        int score = 85;
        String grade = score >= 90 ? "A" : (score >= 80 ? "B" : "C");
    }
}
