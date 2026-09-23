package com.example.billmanager.controller;

import com.example.billmanager.vo.Result;
import com.example.billmanager.dto.category.CategoryCreateDTO;
import com.example.billmanager.dto.category.CategoryUpdateDTO;
import com.example.billmanager.entity.Category;
import com.example.billmanager.service.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器。
 * <p>
 *     负责处理账单分类相关的 HTTP 请求，
 *     接收并校验前端请求参数，然后调用分类业务层完成具体业务处理。
 * </p>
 *
 * <p>
 *     Controller 层不直接处理数据库操作和具体业务逻辑，
 *     以保证控制层职责单一。
 * </p>
 * @author 白麝花生
 * @since 2026-09-13
 */
@Slf4j
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    /**
     * 分类业务接口。
     */
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 查询分类列表。
     * <p>
     *     只返回启用状态的分类；
     *     可通过分类类型参数过滤，不传时返回全部类型。
     * </p>
     *
     * @param categoryType 分类类型，可选值：INCOME、EXPENSE；可选参数
     * @return 统一返回结果，数据为分类列表
     */
    @GetMapping
    public Result<List<Category>> getCategories(@RequestParam(required = false) String categoryType) {

        List<Category> categoryList = categoryService.getCategoryList(categoryType);

        return Result.success("查询成功", categoryList);
    }

    /**
     * 新增分类。
     * <p>
     *     接收前端提交的分类表单数据，
     *     通过 {@code @Valid} 触发 DTO 上的参数校验，
     *     校验通过后调用业务层完成分类创建。
     * </p>
     *
     * @param categoryCreateDTO 分类新增请求参数
     * @return 统一返回结果，数据为新增成功后的分类信息
     */
    @PostMapping
    public Result<Category> createCategory(@Valid @RequestBody CategoryCreateDTO categoryCreateDTO) {

        Category category = categoryService.createCategory(categoryCreateDTO);

        return Result.success("新增成功", category);
    }

    /**
     * 修改分类。
     * <p>
     *     根据路径中的分类ID定位待修改的分类，
     *     使用请求体中的参数更新分类信息。
     * </p>
     *
     * @param categoryId        分类ID
     * @param categoryUpdateDTO 分类修改请求参数
     * @return 统一返回结果，数据为修改后的分类信息
     */
    @PutMapping("/{categoryId}")
    public Result<Category> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryUpdateDTO categoryUpdateDTO
    ) {
        Category category = categoryService.updateCategory(categoryId, categoryUpdateDTO);

        return Result.success("修改成功", category);
    }

    /**
     * 删除分类。
     * <p>
     *     根据分类ID删除指定分类。
     *     本接口采用逻辑删除方式，不会直接删除数据库中的分类记录，
     *     而是将分类状态置为禁用。
     * </p>
     * @param categoryId 分类ID
     * @return 删除结果，无业务数据
     */
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return Result.success("删除成功", null);
    }
}
