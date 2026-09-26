package com.example.billmanager.dto.bill;

import com.example.billmanager.enums.BillType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDate;

/**
 * 账单分页查询请求参数。
 *
 * <p>
 * 用于接收前端账单列表查询时提交的分页参数以及查询条件。
 * </p>
 *
 * <p>
 * 支持按照账单类型、分类、账单日期范围进行组合查询。
 * 所有查询条件均为可选条件，未传入的条件不会参与查询。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-14
 */
@Schema
@Data
public class BillQueryDTO {

    /**
     * 页码。
     * <p>
     * 从 1 开始，默认第 1 页。
     * </p>
     */
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer page = 1;

    /**
     * 每页数量。
     * <p>
     * 默认每页 10 条，最大不超过 100 条，
     * 与 {@code MyBatisPlusConfig} 中分页插件的单页上限保持一致。
     * </p>
     */
    @Min(value = 1, message = "每页数量必须大于等于1")
    @Max(value = 100, message = "每页数量不能大于100")
    private Integer size = 10;

    /**
     * 账单类型（可选查询条件）。
     * <p>
     * 可选值：INCOME（收入）、EXPENSE（支出）；为空时不按类型过滤。
     * 类型已由 String 改为 {@code BillType} 枚举：传入非法取值时在 Jackson
     * 反序列化阶段即失败，由全局异常处理器统一返回 400 提示，
     * 不会抛出系统异常（500），业务层也不再需要字符串转枚举的兜底逻辑。
     * </p>
     */
    @Schema(
            description = "账单类型，可选",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private BillType billType;

    /**
     * 分类ID（可选查询条件）。
     * <p>
     * 为空时不按分类过滤。
     * </p>
     */
    @Schema(
            description = "分类ID，可选",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Long categoryId;

    /**
     * 起始日期（可选查询条件）。
     * <p>
     * 账单日期范围的开始（含当天）；为空时不限制起始日期。
     * </p>
     */
    @Schema(
            description = "起始日期，可选",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private LocalDate startDate;

    /**
     * 截止日期（可选查询条件）。
     * <p>
     * 账单日期范围的结束（含当天）；为空时不限制截止日期。
     * </p>
     */
    @Schema(
            description = "截止日期，可选",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private LocalDate endDate;

}
