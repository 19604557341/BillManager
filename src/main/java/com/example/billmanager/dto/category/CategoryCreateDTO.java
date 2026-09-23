package com.example.billmanager.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分类新增请求参数对象。
 *
 * <p>
 * 用于接收前端新增分类时提交的请求参数。
 * </p>
 */
@Data
public class CategoryCreateDTO {

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    /**
     * 分类类型。
     *
     * <p>
     * 可选值：
     * INCOME：收入
     * EXPENSE：支出
     * </p>
     */
    @NotBlank(message = "分类类型不能为空")
    private String categoryType;

    /**
     * 排序值。
     *
     * <p>
     * 数值越小，排序越靠前。
     * </p>
     */
    @NotNull(message = "分类排序不能为空")
    private Integer sort;
}
