package com.example.billmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.billmanager.dto.category.CategoryCreateDTO;
import com.example.billmanager.dto.category.CategoryUpdateDTO;
import com.example.billmanager.entity.Category;
import com.example.billmanager.exception.BusinessException;
import com.example.billmanager.mapper.CategoryMapper;
import com.example.billmanager.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 分类业务实现类。
 *
 * <p>
 * 负责处理账单分类相关的业务逻辑。
 * 基础的数据库操作由 MyBatis-Plus 提供，
 * 具体业务规则（重名检查、逻辑删除等）在本类中进行统一处理。
 * </p>
 *
 * @author 白麝花生
 * @since 2026-09-13
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    /**
     * 查询分类列表。
     *
     * <p>
     * 只查询启用状态的分类；
     * 如果传入分类类型，则按照分类类型进行过滤；
     * 最后按照分类类型、排序字段升序排列。
     * </p>
     *
     * @param categoryType 分类类型，可选值：INCOME、EXPENSE；为空时查询全部类型
     * @return 分类列表
     */
    @Override
    public List<Category> getCategoryList(String categoryType) {

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Category::getStatus, 1);

        if (StringUtils.hasLength(categoryType)) {
            queryWrapper.eq(Category::getCategoryType, categoryType);
        }

        queryWrapper
                .orderByAsc(Category::getCategoryType)
                .orderByAsc(Category::getSort);

        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 新增分类。
     *
     * <p>
     * 新增前检查相同分类类型下是否已经存在同名分类，
     * 防止产生重复分类。
     * </p>
     *
     * <p>
     * 由于分类采用逻辑删除（status=0），且数据库存在唯一索引
     * {@code uk_category_name_type (category_name, category_type)}，
     * 已禁用的同名分类仍然占用唯一索引，直接插入会违反唯一约束。
     * 因此当同名同类型分类处于禁用状态时，恢复启用该分类并更新其排序值。
     * </p>
     *
     * @param categoryCreateDTO 分类新增请求参数
     * @return 新增（或恢复启用）后的分类信息
     */
    @Override
    public Category createCategory(CategoryCreateDTO categoryCreateDTO) {

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .eq(Category::getCategoryName, categoryCreateDTO.getCategoryName())
                .eq(Category::getCategoryType, categoryCreateDTO.getCategoryType());

        Category existCategory = baseMapper.selectOne(queryWrapper);

        if (existCategory != null) {
            if (Objects.equals(existCategory.getStatus(), 1)) {
                throw new BusinessException(409, "该分类已存在");
            }

            existCategory.setSort(categoryCreateDTO.getSort());
            existCategory.setStatus(1);

            baseMapper.updateById(existCategory);

            return existCategory;
        }

        Category category = new Category();

        category.setCategoryName(categoryCreateDTO.getCategoryName());

        category.setCategoryType(categoryCreateDTO.getCategoryType());

        category.setSort(categoryCreateDTO.getSort());

        category.setStatus(1);

        baseMapper.insert(category);
        return category;
    }

    /**
     * 修改分类。
     *
     * <p>
     *     根据分类ID查询原分类信息，确认分类存在后，
     *     检查修改后的分类名称和分类类型是否与其他分类重复，
     *     最后更新分类信息。
     * </p>
     *
     * @param categoryId 分类ID
     * @param categoryUpdateDTO 分类修改请求参数
     * @return 修改后的分类信息
     */
    @Override
    public Category updateCategory(Long categoryId, CategoryUpdateDTO categoryUpdateDTO) {

        Category category = baseMapper.selectById(categoryId);

        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .eq(Category::getCategoryName, categoryUpdateDTO.getCategoryName())
                .eq(Category::getCategoryType, categoryUpdateDTO.getCategoryType())
                .ne(Category::getCategoryId,categoryId);

        Category existCategory = baseMapper.selectOne(queryWrapper);

        if (existCategory != null) {
            throw new BusinessException(409, "该分类已存在");
        }

        category.setCategoryName(categoryUpdateDTO.getCategoryName());
        category.setCategoryType(categoryUpdateDTO.getCategoryType());
        category.setSort(categoryUpdateDTO.getSort());
        category.setStatus(categoryUpdateDTO.getStatus());

        baseMapper.updateById(category);

        return category;
    }

    /**
     * 删除分类。
     * <p>
     *     本方法采用逻辑删除方式，不直接删除数据库中的分类记录，
     *     而是将分类状态修改为禁用状态。
     * </p>
     *
     *  <p>
     *     删除前首先检查分类是否存在，并且只允许删除当前处于启用状态的分类。
     * </p>
     * @param categoryId 分类ID
     * @throws BusinessException 当分类不存在时抛出业务异常
     */
    @Override
    public void deleteCategory(Long categoryId) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .eq(Category::getCategoryId, categoryId)
                .eq(Category::getStatus, 1);

        Category category = baseMapper.selectOne(queryWrapper);

        if(category == null) {
            throw new BusinessException(404, "分类不存在");
        }

        Category updateCategory = new Category();

        updateCategory.setCategoryId(categoryId);
        updateCategory.setStatus(0);

        baseMapper.updateById(updateCategory);
    }
}
