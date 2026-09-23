package com.example.billmanager.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.billmanager.dto.category.CategoryCreateDTO;
import com.example.billmanager.dto.category.CategoryUpdateDTO;
import com.example.billmanager.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<Category> getCategoryList(String categoryType);

    Category createCategory(CategoryCreateDTO categoryCreateDTO);

    Category updateCategory(Long categoryId, CategoryUpdateDTO categoryUpdateDTO);

    void deleteCategory(Long categoryId);
}
