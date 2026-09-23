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
 * 分类ID不放在请求体中，而是通过请求路径中的 {@code categoryId} * 进行传递。
 * </p>
 */
@Data
public class CategoryUpdateDTO {

    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    @NotBlank(message = "分类类型不能为空")
    private String categoryType;

    @NotNull(message = "分类排序不能为空")
    private Integer sort;

    @NotNull(message = "分类状态不能为空")
    private Integer status;
}
