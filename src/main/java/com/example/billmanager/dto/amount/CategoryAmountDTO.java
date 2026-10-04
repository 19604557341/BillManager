package com.example.billmanager.dto.amount;

import lombok.Data;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 分类金额汇总查询结果。
 *
 * <p>
 * 承接 {@code BillMapper#selectCategoryTotalAmounrList} 聚合查询的一行结果：
 * 统计日期范围内，按分类与账单类型分组的总金额（收入、支出分类混排在同一次结果中）。
 * 仅用于 Mapper 到服务层的数据传递，不直接返回给前端。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-25
 */
@Data
@Schema(description = "分类金额汇总查询结果")
public class CategoryAmountDTO {

    /**
     * 分类ID。
     */
    @Schema(description = "分类ID")
    private Long categoryId;

    /**
     * 分类名称。
     * <p>
     * 通过 LEFT JOIN 分类表取得，且只关联启用状态（status=0，见 {@code CategoryStatus#ENABLED}）的分类；
     * 分类被禁用或已删除时为 null，但对应账单金额仍会计入统计。
     * </p>
     */
    @Schema(description = "分类名称")
    private String categoryName;

    /**
     * 分类类型（INCOME / EXPENSE）。
     * <p>
     * SQL 中直接取账单表的 {@code bill_type} 列；
     * 服务层按此字段分组，分别计算收入、支出各自的分类占比。
     * </p>
     */
    @Schema(description = "分类类型")
    private String categoryType;

    /**
     * 该分类下的账单总金额。
     * <p>
     * SQL 中已用 COALESCE 兜底为 0。
     * </p>
     */
    @Schema(description = "账单总金额")
    private BigDecimal totalAmount;
}
