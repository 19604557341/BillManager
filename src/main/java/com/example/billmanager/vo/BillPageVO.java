package com.example.billmanager.vo;

import com.example.billmanager.enums.BillType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账单分页查询结果。
 *
 * <p>
 * 用于封装账单分页查询后返回给前端的数据。
 * </p>
 *
 * <p>
 * 由于账单表中的 {@code category_id} 只保存分类ID，
 * 查询时需要通过联表获取分类名称，
 * 因此本对象同时包含 {@code categoryId} 和 {@code categoryName}。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-14
 */
@Data
public class BillPageVO {

    /**
     * 账单ID。
     */
    private Long billId;

    /**
     * 账单金额。
     */
    private BigDecimal billAmount;

    /**
     * 账单类型。
     * <p>
     * 可选值：INCOME（收入）、EXPENSE（支出）。
     * </p>
     */
    private BillType billType;

    /**
     * 分类ID。
     */
    private Long categoryId;

    /**
     * 分类名称。
     * <p>
     * 由业务层根据分类ID批量查询分类表后组装得到，
     * 便于前端直接展示，无需再次查询分类。
     * </p>
     */
    private String categoryName;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 账单日期。
     */
    private LocalDate billDate;

    /**
     * 创建时间。
     */
    private LocalDateTime createdTime;

    /**
     * 修改时间。
     */
    private LocalDateTime updateTime;
}
