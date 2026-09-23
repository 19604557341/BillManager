package com.example.billmanager.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分类修改请求参数对象。
 *
 * <p>
 * 用于接收前端修改分类时提交的请求参数。
 * </p>
 *
 * <p>
 * 分类ID不放在请求体中，而是通过请求路径中的 {@code categoryId} 进行传递。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-13
 */
@Data
public class CategoryUpdateDTO {

    /**
     * 分类名称。
     * <p>
     * 必填；与分类类型共同保证唯一性，不允许与其他分类重复。
     * </p>
     */
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    /**
     * 分类类型。
     * <p>
     * 必填；可选值：INCOME（收入）、EXPENSE（支出）。
     * </p>
     */
    @NotBlank(message = "分类类型不能为空")
    private String categoryType;

    /**
     * 排序值。
     * <p>
     * 必填；数值越小，排序越靠前。
     * </p>
     */
    @NotNull(message = "分类排序不能为空")
    private Integer sort;

    /**
     * 分类状态。
     * <p>
     * 必填；1：启用；0：禁用。
     * </p>
     */
    @NotNull(message = "分类状态不能为空")
    private Integer status;
}
