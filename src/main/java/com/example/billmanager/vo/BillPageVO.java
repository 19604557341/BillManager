package com.example.billmanager.vo;

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
    private Long billId;
    private BigDecimal billAmount;
    private String billType;
    private Long categoryId;
    private String categoryName;
    private String remark;
    private LocalDate billDate;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
}
