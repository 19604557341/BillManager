package com.example.billmanager.dto.bill;

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
@Data
public class BillQueryDTO {

    @Min(value = 1, message = "页码必须大于等于1")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量必须大于等于1")
    @Max(value = 100, message = "每页数量不能大于100")
    private Integer size = 10;

    private String billType;

    private Long categoryId;

    private LocalDate startDate;

    private LocalDate endDate;

}
