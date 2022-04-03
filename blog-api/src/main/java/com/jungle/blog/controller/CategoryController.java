package com.jungle.blog.controller;

import com.jungle.blog.service.CategoryService;
import com.jungle.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping("detail")
    public Result categorysDetail(){
        return categoryService.findAllDetail();
    }

    //
    @GetMapping("detail/{id}")
    public Result categoryDetailById(@PathVariable("id") Long id){
        return categoryService.categoryDetailById(id);
    }
}
