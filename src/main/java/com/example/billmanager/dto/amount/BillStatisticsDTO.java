package com.example.billmanager.dto.amount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 账单统计查询请求参数。
 *
 * <p>
 * 用于接收前端查询账单统计时提交的条件：
 * 统计日期范围（必填）、趋势分组方式（可选）。
 * </p>
 *
 * <p>
 * 注意：统计不再按账单类型（INCOME / EXPENSE）过滤——一次查询同时返回
 * 总收入、总支出、结余，以及收支两条趋势线、收入与支出的全部分类，
 * 前端按需取用即可，无需为"总结余"分别发送收入、支出两次请求。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-25
 */
@Data
@Schema (description = "账单统计查询请求参数")
public class BillStatisticsDTO {

    /**
     * 统计开始日期（含当天）。
     * <p>
     * 必填；对应 SQL 条件 {@code bill_date >= #{startDate}}。
     * </p>
     */
    @NotNull(message = "开始日期不可为空")
    @Schema (description = "统计开始日期（含当天）", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;

    /**
     * 统计结束日期（含当天）。
     * <p>
     * 必填；对应 SQL 条件 {@code bill_date <= #{endDate}}。
     * </p>
     */
    @NotNull(message = "结束日期不可为空")
    @Schema (description = "统计结束日期（含当天）", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate endDate;

    /**
     * 趋势分组方式。
     * <p>
     * 默认 "day" 按日分组；传 "month" 按月分组；
     * SQL 中仅识别 "month"，其余取值一律按日分组处理。
     * 按日分组时服务层会对无账单的日期补 0（见 BillStatisticsService）。
     * </p>
     */
    @Schema(description = "趋势分组方式，可选", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String groupBy = "day";
}
