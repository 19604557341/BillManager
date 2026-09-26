package com.example.billmanager.dto.amount;

import com.example.billmanager.enums.BillType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 账单统计查询请求参数。
 *
 * <p>
 * 用于接收前端查询账单统计时提交的条件：
 * 统计日期范围（必填）、账单类型（必填）、趋势分组方式（可选）。
 * 三个条件会同时作用于总额、分类、趋势三个聚合查询。
 * </p>
 *
 * <p>
 * 注意：{@code billType} 为枚举类型，非法取值在 Jackson
 * 反序列化阶段即失败，由全局异常处理器统一返回 400 提示，
 * 不会进入 JSR-303 校验与业务层。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-25
 */
@Data
public class BillStatisticsDTO {

    /**
     * 统计开始日期（含当天）。
     * <p>
     * 必填；对应 SQL 条件 {@code bill_date >= #{startDate}}。
     * </p>
     */
    @NotNull(message = "开始日期不可为空")
    private LocalDate startDate;

    /**
     * 统计结束日期（含当天）。
     * <p>
     * 必填；对应 SQL 条件 {@code bill_date <= #{endDate}}。
     * </p>
     */
    @NotNull(message = "结束日期不可为空")
    private LocalDate endDate;

    /**
     * 账单类型。
     * <p>
     * 必填；只允许 INCOME（收入）或 EXPENSE（支出），
     * 统计时只汇总该类型的账单。
     * </p>
     */
    @NotNull(message = "账单类型不能为空")
    private BillType billType;

    /**
     * 趋势分组方式。
     * <p>
     * 默认 "day" 按日分组；传 "month" 按月分组；
     * SQL 中仅识别 "month"，其余取值一律按日分组处理。
     * 按日分组时服务层会对无账单的日期补 0（见 BillStatisticsService）。
     * </p>
     */
    @Schema(
            description = "趋势分组方式，可选",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String groupBy = "day";
}
