package com.example.billmanager.dto.amount;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 收支趋势查询结果。
 *
 * <p>
 * 承接 {@code BillMapper#selectTrendAmoubtDTOList} 聚合查询的一行结果：
 * 某个日期分组（日或月）内的收入合计与支出合计。
 * 仅用于 Mapper 到服务层的数据传递，不直接返回给前端。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-25
 */
@Data
public class TrendAmountDTO {

    /**
     * 日期分组键。
     * <p>
     * 按日分组时格式为 yyyy-MM-dd，按月分组时格式为 yyyy-MM，
     * 由 SQL 中的 DATE_FORMAT 根据 groupBy 参数决定。
     * </p>
     */
    private String date;

    /**
     * 该日期分组内的收入合计。
     * <p>
     * SQL 中已用 COALESCE 兜底为 0。
     * </p>
     */
    private BigDecimal income;

    /**
     * 该日期分组内的支出合计。
     * <p>
     * SQL 中已用 COALESCE 兜底为 0。
     * </p>
     */
    private BigDecimal expense;
}
