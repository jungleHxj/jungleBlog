package com.jungle.blog.controller;

import com.jungle.blog.dao.pojo.SysUser;
import com.jungle.blog.util.UserThreadLocal;
import com.jungle.blog.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @RequestMapping
    public Result test(){
        SysUser sysUser = UserThreadLocal.get();
        System.out.println(sysUser);
        return Result.success(null);
    }

}
