package com.jungle.blog.service;

import com.jungle.blog.dao.pojo.SysUser;
import com.jungle.blog.vo.Result;
import com.jungle.blog.vo.params.LoginParam;


public interface LoginService {

    /**
     * 登录功能
     * @param loginParam
     * @return
     */
    Result login(LoginParam loginParam);

    SysUser checkToken(String token);

    /**
     * 退出功能
     * @param token
     * @return
     */
    Result logout(String token);
}
