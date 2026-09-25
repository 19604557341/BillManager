package com.example.billmanager.vo.amount;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 收支趋势统计结果（返回给前端）。
 *
 * <p>
 * 单个日期分组（日或月）内的收入、支出与结余，
 * 作为 {@link BillStatisticsVO} 趋势统计列表的元素，
 * 用于折线图/柱状图展示。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-25
 */
@Data
public class TrendStatisticsVO {

    /**
     * 日期分组键。
     * <p>
     * 按日分组时格式为 yyyy-MM-dd，按月分组时格式为 yyyy-MM。
     * </p>
     */
    private String date;

    /**
     * 该日期分组内的收入合计。
     * <p>
     * 按日分组时，无账单的日期已补 0。
     * </p>
     */
    private BigDecimal income;

    /**
     * 该日期分组内的支出合计。
     * <p>
     * 按日分组时，无账单的日期已补 0。
     * </p>
     */
    private BigDecimal expense;

    /**
     * 该日期分组内的结余。
     * <p>
     * 结余 = 收入 - 支出，可能为负数。
     * </p>
     */
    private BigDecimal balance;
}
