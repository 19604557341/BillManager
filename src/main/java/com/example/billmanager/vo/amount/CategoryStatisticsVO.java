package com.example.billmanager.vo.amount;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 分类统计结果（返回给前端）。
 *
 * <p>
 * 单个分类在统计区间内的金额汇总与占比，
 * 作为 {@link BillStatisticsVO} 分类统计列表的元素，
 * 用于饼图/环形图与分类金额排行展示。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-25
 */
@Data
public class CategoryStatisticsVO {

    /**
     * 分类ID。
     */
    private Long categoryId;

    /**
     * 分类名称。
     * <p>
     * 分类被禁用或已删除时为 null，前端需做空值兜底展示。
     * </p>
     */
    private String categoryName;

    /**
     * 分类类型（INCOME / EXPENSE）。
     * <p>
     * 与统计查询条件中的账单类型一致。
     * </p>
     */
    private String categoryType;

    /**
     * 该分类在统计区间内的账单总金额。
     */
    private BigDecimal totalAmount;

    /**
     * 金额占比（数值形式，保留 2 位小数）。
     * <p>
     * 占比 = 该分类金额 ÷ 所有分类金额合计 × 100，如 12.34 表示 12.34%；
     * 供前端画图（如饼图角度计算）使用。
     * 各分类分别四舍五入，合计可能不严格等于 100。
     * </p>
     */
    private BigDecimal percentage;

    /**
     * 金额占比（文本形式，如 "12.34%"）。
     * <p>
     * 已在服务端格式化完成，前端可直接展示。
     * </p>
     */
    private String percentageText;
}
