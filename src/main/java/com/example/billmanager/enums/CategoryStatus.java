package com.example.billmanager.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 分类状态枚举。
 *
 * <p>
 * 将项目中散落的数字魔法值 {@code 0} / {@code 1} 收敛为枚举，
 * 对应数据库列 {@code category.status TINYINT}。
 * 改造后业务代码中的状态判断从 {@code Objects.equals(status, 1)}
 * 变为 {@code status == CategoryStatus.ENABLED}，语义一目了然。
 * </p>
 *
 * <p>
 * 分类采用"逻辑删除"：删除分类时仅将状态置为 {@link #DISABLED}（禁用），
 * 已记账的历史账单仍可正常关联到该分类。
 * </p>
 *
 * <p>
 * 注解说明：
 * <ul>
 *     <li>{@code @EnumValue}：MyBatis-Plus 注解，只能标注在<b>字段</b>上，
 *     读写数据库时使用 {@code code} 字段的值（0 / 1），与现有 TINYINT 列兼容；</li>
 *     <li>{@code @JsonValue}：Jackson 注解，JSON 序列化时输出 {@code code} 值（0 / 1），
 *     反序列化时也按 0 / 1 匹配，保证前端报文格式与改造前一致。</li>
 * </ul>
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-24
 */
@Getter
public enum CategoryStatus {

    /** 禁用（分类的逻辑删除状态，禁用后不可再被新账单选择） */
    DISABLED(0, "禁用"),

    /** 启用（分类的正常可用状态） */
    ENABLED(1, "启用");

    /**
     * 状态码。
     * <p>
     * 0：禁用；1：启用。
     * 作为数据库存储值（{@code @EnumValue}）与 JSON 序列化值（{@code @JsonValue}）。
     * </p>
     */
    @EnumValue
    @JsonValue
    private final Integer code;

    /**
     * 状态中文描述。
     * <p>
     * 用于日志输出与前端展示，不参与数据库存储。
     * </p>
     */
    private final String desc;

    /**
     * 构造方法。
     *
     * @param code 状态码（0：禁用；1：启用）
     * @param desc 状态中文描述
     */
    CategoryStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
