package com.example.billmanager.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类实体类。
 *
 * <p>
 * 与数据库表 {@code category} 一一对应，
 * 用于承载账单分类的基础数据，
 * 由 MyBatis-Plus 完成实体与数据库记录之间的映射。
 * </p>
 *
 * <p>
 * 分类通过 {@code status} 字段实现"逻辑删除"：
 * 删除分类时仅将其状态置为禁用（status=0），
 * 已记账的历史账单仍可正常关联到该分类。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-13
 */
@Data
@TableName("category")
public class Category {

    /**
     * 分类ID。
     * <p>
     * 主键，由 MyBatis-Plus 雪花算法自动生成（{@code IdType.ASSIGN_ID}）。
     * </p>
     */
    @TableId(value = "category_id", type = IdType.ASSIGN_ID)
    private Long categoryId;

    /**
     * 分类名称。
     * <p>
     * 与分类类型共同构成唯一索引 {@code uk_category_name_type}，
     * 即同一类型下分类名称不允许重复。
     * </p>
     */
    private String categoryName;

    /**
     * 分类类型。
     * <p>
     * 可选值：INCOME（收入）、EXPENSE（支出）。
     * </p>
     */
    private String categoryType;

    /**
     * 排序值。
     * <p>
     * 数值越小，排序越靠前。
     * </p>
     */
    private Integer sort;

    /**
     * 分类状态。
     * <p>
     * 1：启用；0：禁用（逻辑删除）。
     * </p>
     */
    private Integer status;

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

}
