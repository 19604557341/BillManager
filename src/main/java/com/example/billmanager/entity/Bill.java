package com.example.billmanager.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.billmanager.enums.BillType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账单实体类。
 *
 * <p>
 * 与数据库表 {@code bill} 一一对应，
 * 用于承载账单的基础数据，
 * 由 MyBatis-Plus 完成实体与数据库记录之间的映射。
 * </p>
 *
 * <p>
 * 主键采用雪花算法自动生成；
 * 创建时间、修改时间由 {@code MyMetaObjectHandler} 自动填充；
 * 删除操作采用逻辑删除（{@code deleted} 字段标记），不会真正删除数据库记录。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-14
 */
@Data
@TableName("bill")
public class Bill {

    /**
     * 账单ID。
     * <p>
     * 主键，由 MyBatis-Plus 雪花算法自动生成（{@code IdType.ASSIGN_ID}）。
     * </p>
     */
    @TableId(value = "bill_id", type = IdType.ASSIGN_ID)
    private Long billId;

    /**
     * 账单金额。
     * <p>
     * 对应数据库列 {@code bill_amount DECIMAL(15, 2)}，
     * 使用 {@link BigDecimal} 保证金额计算精度。
     * </p>
     */
    private BigDecimal billAmount;

    /**
     * 账单类型。
     * <p>
     * 可选值：{@link BillType#INCOME}（收入）、{@link BillType#EXPENSE}（支出）。
     * 使用枚举类型后，数据库读写由 MyBatis-Plus 依据枚举上的
     * {@code @EnumValue} 注解自动完成与字符串（"INCOME" / "EXPENSE"）的转换，
     * 数据库列 {@code bill_type VARCHAR(20)} 无需任何改动。
     * </p>
     */
    private BillType billType;

    /**
     * 分类ID。
     * <p>
     * 关联分类表 {@code category} 的主键，
     * 数据库层通过外键 {@code fk_bill_category} 保证引用完整性。
     * </p>
     */
    private Long categoryId;

    /**
     * 备注。
     * <p>
     * 可选字段，最大长度 500 个字符。
     * </p>
     */
    private String remark;

    /**
     * 账单日期。
     * <p>
     * 账单实际发生的日期（非记录创建日期）。
     * </p>
     */
    private LocalDate billDate;

    /**
     * 创建时间。
     * <p>
     * 插入记录时由 {@code MyMetaObjectHandler} 自动填充。
     * </p>
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 修改时间。
     * <p>
     * 插入和更新记录时由 {@code MyMetaObjectHandler} 自动填充。
     * </p>
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记。
     * <p>
     * 0：正常；1：已删除。
     * 标注 {@code @TableLogic} 后，MyBatis-Plus 的查询会自动过滤已删除记录，
     * 删除操作会自动转换为更新该字段。
     * </p>
     */
    @TableLogic
    private Integer deleted;

}
