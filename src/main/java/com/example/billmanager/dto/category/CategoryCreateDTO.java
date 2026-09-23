package com.example.billmanager.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分类新增请求参数对象。
 *
 * <p>
 * 用于接收前端新增分类时提交的请求参数，
 * 通过 JSR-303 注解在参数进入业务层之前完成基础校验。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-13
 */
@Data
public class CategoryCreateDTO {

    /**
     * 分类名称。
     * <p>
     * 必填；与分类类型共同保证唯一性，
     * 同一类型下不允许出现同名分类。
     * </p>
     */
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    /**
     * 分类类型。
     *
     * <p>
     * 必填；可选值：
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
     * 必填；数值越小，排序越靠前。
     * </p>
     */
    @NotNull(message = "分类排序不能为空")
    private Integer sort;
}
