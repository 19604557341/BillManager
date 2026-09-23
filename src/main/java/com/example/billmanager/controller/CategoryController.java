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

@Slf4j
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 查询分类列表
     *
     * @param categoryType 分类类型，可选值：INCOME、EXPENSE
     * @return 统一返回结果
     */
    @GetMapping
    public Result<List<Category>> getCategories(@RequestParam(required = false) String categoryType) {

        List<Category> categoryList = categoryService.getCategoryList(categoryType);

        return Result.success("查询成功", categoryList);
    }

    /**
     * 新增分类。
     *
     * @param categoryCreateDTO 分类新增请求参数
     * @return 统一返回结果
     */
    @PostMapping
    public Result<Category> saveCategory(@Valid @RequestBody CategoryCreateDTO categoryCreateDTO) {

        Category category = categoryService.createCategory(categoryCreateDTO);

        return Result.success("新增成功", category);
    }

    /**
     * 修改分类。
     *
     * @param categoryId 分类ID
     * @param categoryUpdateDTO 分类修改请求参数
     * @return 修改后的分类信息
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
     *     本接口采用逻辑删除方式，不会直接删除数据库中的分类记录。
     * </p>
     * @param categoryId 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return Result.success("删除成功", null);
    }
}
