package com.jungle.blog.service;

import com.jungle.blog.dao.pojo.SysUser;

public interface SysUserService {

    SysUser findUserById(Long id);
}
