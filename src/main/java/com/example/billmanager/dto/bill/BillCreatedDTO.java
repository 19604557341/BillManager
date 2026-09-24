package com.example.billmanager.dto.bill;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 账单新增请求参数。
 *
 * <p>
 * 用于接收前端新增账单时提交的表单数据，
 * 通过 JSR-303 注解在参数进入业务层之前完成基础校验。
 * </p>
 *
 * <p>
 * 注意：字段命名遵循 Java 驼峰规范（camelCase），
 * 与实体类 {@code Bill} 及数据库列保持一一对应，
 * 同时保证 JSON 序列化/反序列化的字段名一致。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-23
 */
@Data
public class BillCreatedDTO {

    /**
     * 账单金额。
     * <p>
     * 必填；最小值为 0.01，整数部分最多 13 位、小数部分最多 2 位，
     * 与数据库列 {@code bill_amount DECIMAL(15, 2)} 的精度保持一致。
     * </p>
     */
    @NotNull(message = "账单金额不能为空")
    @DecimalMin(value = "0.01", message = "账单金额必须大于0")
    @Digits(integer = 13, fraction = 2, message = "账单金额整数部分最多13位，小数部分最多2位")
    private BigDecimal billAmount;

    /**
     * 账单类型。
     * <p>
     * 必填；只允许 INCOME（收入）或 EXPENSE（支出），
     * 与数据库列 {@code bill_type VARCHAR(20) NOT NULL} 的取值约定一致。
     * </p>
     */
    @NotBlank(message = "账单类型不能为空")
    @Pattern(regexp = "INCOME|EXPENSE", message = "账单类型只能是INCOME或EXPENSE")
    private String billType;

    /**
     * 分类ID。
     * <p>
     * 必填；必须是分类表中已存在且处于启用状态的分类，
     * 具体校验在业务层完成。
     * </p>
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 备注。
     * <p>
     * 可选；最大长度 500 个字符，与数据库列 {@code remark VARCHAR(500)} 保持一致。
     * </p>
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    /**
     * 账单日期。
     * <p>
     * 必填；账单实际发生的日期，对应数据库列 {@code bill_date DATE NOT NULL}。
     * </p>
     */
    @NotNull(message = "账单日期不能为空")
    private LocalDate billDate;
}
