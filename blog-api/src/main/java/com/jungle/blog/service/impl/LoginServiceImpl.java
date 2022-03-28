package com.jungle.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jungle.blog.dao.pojo.SysUser;
import com.jungle.blog.service.LoginService;
import com.jungle.blog.service.SysUserService;
import com.jungle.blog.util.JWTUtils;
import com.jungle.blog.vo.ErrorCode;
import com.jungle.blog.vo.Result;
import com.jungle.blog.vo.params.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    private static final String salt = "jungle!@#";

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result login(LoginParam loginParam) {
        /**
         * 1、检查参数是否合法
         * 2、根据用户名和密码去user表中查询  是否存在
         * 3、如果不存在  登录失败
         * 4、如果存在，使用jwt生成token  返回给前端
         * 5、token放入redis中，redis  token:user信息   设置过期时间
         *   （登录认证时，先认证token是否合法，去redis验证是否存在）
         */
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }

        password = DigestUtils.md5Hex(password + salt);
        SysUser sysUser = sysUserService.findUser(account, password);
        if (sysUser == null) {
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(), ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }

        String token = JWTUtils.createToken(sysUser.getId());

        // 过期时间可作为参数，动态传入
        redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(sysUser), 1, TimeUnit.DAYS);

        return Result.success(token);
    }

    @Override
    public SysUser checkToken(String token) {
        if(StringUtils.isBlank(token)){
            return null;
        }

        Map<String,Object> stringObjectMap = JWTUtils.checkToken(token);
        if(stringObjectMap == null){
            return null;
        }

        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if(StringUtils.isBlank(userJson)){
            return null;
        }

        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);

        return sysUser;
    }

    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_" + token);

        return Result.success(null);
    }


//    public static void main(String[] args) {
//        String pass = DigestUtils.md5Hex("admin" + salt);
//        System.out.println(pass);
//    }
}
