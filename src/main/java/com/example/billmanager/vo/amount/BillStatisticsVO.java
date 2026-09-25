package com.example.billmanager.vo.amount;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 账单统计结果（返回给前端）。
 *
 * <p>
 * 统计接口的顶层响应对象，一次性携带前端统计页面所需的全部数据：
 * 总览金额、统计区间回显、分类统计列表、收支趋势列表。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-25
 */
@Data
public class BillStatisticsVO {

    /**
     * 总收入。
     * <p>
     * 统计区间内指定账单类型的收入合计，无数据时为 0。
     * </p>
     */
    private BigDecimal totalIncome;

    /**
     * 总支出。
     * <p>
     * 统计区间内指定账单类型的支出合计，无数据时为 0。
     * </p>
     */
    private BigDecimal totalExpense;

    /**
     * 结余。
     * <p>
     * 结余 = 总收入 - 总支出，可能为负数（支出大于收入）。
     * </p>
     */
    private BigDecimal balance;

    /**
     * 统计开始日期（含当天）。
     * <p>
     * 回显查询条件，便于前端展示统计区间。
     * </p>
     */
    private LocalDate startDate;

    /**
     * 统计结束日期（含当天）。
     * <p>
     * 回显查询条件，便于前端展示统计区间。
     * </p>
     */
    private LocalDate endDate;

    /**
     * 分类统计列表。
     * <p>
     * 各分类的总金额与占比，按金额降序，用于饼图/环形图与分类排行。
     * </p>
     */
    private List<CategoryStatisticsVO> categoryStatisticsVOList;

    /**
     * 收支趋势列表。
     * <p>
     * 按日或按月分组的收入/支出/结余序列，用于折线图/柱状图；
     * 按日分组时已对无账单的日期补 0，数据连续。
     * </p>
     */
    private List<TrendStatisticsVO> trendStatisticsVOList;
}
