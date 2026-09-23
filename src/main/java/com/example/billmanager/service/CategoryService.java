package com.example.billmanager.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.billmanager.dto.category.CategoryCreateDTO;
import com.example.billmanager.dto.category.CategoryUpdateDTO;
import com.example.billmanager.entity.Category;

import java.util.List;

/**
 * 分类业务接口。
 *
 * <p>
 * 在 MyBatis-Plus 通用业务接口的基础上，
 * 对分类相关业务进行统一定义。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-13
 */
public interface CategoryService extends IService<Category> {

    /**
     * 查询分类列表。
     *
     * <p>
     * 只查询启用状态的分类；
     * 如果传入分类类型，则按照分类类型进行过滤。
     * </p>
     *
     * @param categoryType 分类类型，可选值：INCOME、EXPENSE；为空时查询全部类型
     * @return 分类列表
     */
    List<Category> getCategoryList(String categoryType);

    /**
     * 新增分类。
     *
     * <p>
     * 新增前检查相同分类类型下是否已经存在同名分类，
     * 防止产生重复分类；
     * 若同名同类型分类处于禁用状态，则恢复启用该分类。
     * </p>
     *
     * @param categoryCreateDTO 分类新增请求参数
     * @return 新增（或恢复启用）后的分类信息
     */
    Category createCategory(CategoryCreateDTO categoryCreateDTO);

    /**
     * 修改分类。
     *
     * <p>
     * 根据分类ID查询原分类信息，确认分类存在后，
     * 检查修改后的分类名称和分类类型是否与其他分类重复，
     * 最后更新分类信息。
     * </p>
     *
     * @param categoryId        分类ID
     * @param categoryUpdateDTO 分类修改请求参数
     * @return 修改后的分类信息
     */
    Category updateCategory(Long categoryId, CategoryUpdateDTO categoryUpdateDTO);

    /**
     * 删除分类。
     *
     * <p>
     * 采用逻辑删除方式，不直接删除数据库中的分类记录，
     * 而是将分类状态修改为禁用状态。
     * </p>
     *
     * @param categoryId 分类ID
     */
    void deleteCategory(Long categoryId);
}
