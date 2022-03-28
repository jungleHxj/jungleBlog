package com.jungle.blog.service;

import com.jungle.blog.dao.pojo.SysUser;
import com.jungle.blog.vo.Result;

public interface SysUserService {

    SysUser findUserById(Long id);

    SysUser findUser(String account, String password);

    /**
     * 根据 token 查询用户信息
     * @param token
     * @return
     */
    Result findUserByToken(String token);
}
