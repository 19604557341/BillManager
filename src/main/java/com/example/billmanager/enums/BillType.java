package com.example.billmanager.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 账单类型枚举。
 *
 * <p>
 * 将项目中散落的字符串魔法值 {@code "INCOME"} / {@code "EXPENSE"} 收敛为枚举，
 * 同时供账单类型（{@code bill.bill_type}）与分类类型（{@code category.category_type}）使用：
 * 二者取值域完全相同，且业务规则要求"分类类型必须与账单类型一致"，
 * 共用同一枚举后可以直接使用 {@code ==} / {@code !=} 比较，既安全又直观。
 * </p>
 *
 * <p>
 * 两个关键注解的作用（注意它们的标注位置不同）：
 * <ul>
 *     <li>{@code @EnumValue}：MyBatis-Plus 注解，<b>只能标注在字段上</b>，
 *     标注在方法上会报"注解类型不适用于该声明类型"的编译错误。
 *     标注后 MyBatis-Plus 读写数据库时使用 {@code code} 字段的值
 *     （"INCOME" / "EXPENSE" 字符串），与现有 {@code VARCHAR(20)} 列完全兼容，无需改表；</li>
 *     <li>{@code @JsonValue}：Jackson 注解（可标注在字段或方法上），
 *     JSON 序列化时输出 {@code code} 值、反序列化时按 {@code code} 值匹配，
 *     保证改造前后接口的 JSON 报文格式完全不变，前端无感知。</li>
 * </ul>
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-24
 */
@Getter
public enum BillType {

    /** 收入类型（账单类型与分类类型共用） */
    INCOME("收入"),

    /** 支出类型（账单类型与分类类型共用） */
    EXPENSE("支出");

    /**
     * 类型编码。
     * <p>
     * 取值与枚举名一致（"INCOME" / "EXPENSE"），
     * 作为数据库存储值（{@code @EnumValue}）与 JSON 序列化值（{@code @JsonValue}）。
     * </p>
     */
    @EnumValue
    @JsonValue
    private final String code;

    /**
     * 类型中文描述。
     * <p>
     * 用于日志输出与前端展示，不参与数据库存储。
     * </p>
     */
    private final String desc;

    /**
     * 构造方法。
     * <p>
     * {@code code} 直接取枚举名（{@code name()}），
     * 避免在每个枚举常量上重复书写一遍编码字符串。
     * </p>
     *
     * @param desc 类型中文描述
     */
    BillType(String desc) {
        this.code = name();
        this.desc = desc;
    }
}
