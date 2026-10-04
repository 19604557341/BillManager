package com.example.billmanager.dto.amount;

import lombok.Data;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 账单总额统计查询结果。
 *
 * <p>
 * 承接 {@code BillMapper#selectTotalAmount} 聚合查询的一行结果：
 * 统计日期范围内的总收入与总支出（不按账单类型过滤，收支一并汇总）。
 * 仅用于 Mapper 到服务层的数据传递，不直接返回给前端。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-25
 */
@Data
@Schema(description = "账单总额统计查询结果")
public class BillTotalAmountDTO {

    /**
     * 总收入。
     * <p>
     * SQL 中已用 COALESCE 兜底为 0；服务层取值时仍会做空值兜底。
     * </p>
     */
    @Schema(description = "总收入")
    private BigDecimal totalIncome;

    /**
     * 总支出。
     * <p>
     * SQL 中已用 COALESCE 兜底为 0；服务层取值时仍会做空值兜底。
     * </p>
     */
    @Schema(description = "总支出")
    private BigDecimal totalExpense;
}
