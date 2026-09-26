package com.example.billmanager.dto.amount;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 分类金额汇总查询结果。
 *
 * <p>
 * 承接 {@code BillMapper#selectCategoryTotalAmounrList} 聚合查询的一行结果：
 * 统计日期范围内、指定账单类型下，按分类分组的总金额。
 * 仅用于 Mapper 到服务层的数据传递，不直接返回给前端。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-25
 */
@Data
public class CategoryAmountDTO {

    /**
     * 分类ID。
     */
    private Long categoryId;

    /**
     * 分类名称。
     * <p>
     * 通过 LEFT JOIN 分类表取得，且只关联启用状态（status=0，见 {@code CategoryStatus#ENABLED}）的分类；
     * 分类被禁用或已删除时为 null，但对应账单金额仍会计入统计。
     * </p>
     */
    private String categoryName;

    /**
     * 分类类型（INCOME / EXPENSE）。
     * <p>
     * SQL 中直接取账单表的 {@code bill_type} 列，
     * 与查询条件中的账单类型一致。
     * </p>
     */
    private String categoryType;

    /**
     * 该分类下的账单总金额。
     * <p>
     * SQL 中已用 COALESCE 兜底为 0。
     * </p>
     */
    private BigDecimal totalAmount;
}
