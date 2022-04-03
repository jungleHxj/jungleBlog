package com.jungle.blog.controller;

import com.jungle.blog.service.CategoryService;
import com.jungle.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("categorys")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping
    public Result categorys(){
        return categoryService.findAll();
    }

}
