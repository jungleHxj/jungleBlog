package com.jungle.blog.service;

import com.jungle.blog.vo.CategoryVo;
import com.jungle.blog.vo.Result;

public interface CategoryService {
    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result findAllDetail();

    Result categoryDetailById(Long id);
}
