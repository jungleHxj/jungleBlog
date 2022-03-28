package com.jungle.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jungle.blog.dao.mapper.SysUserMapper;
import com.jungle.blog.dao.pojo.SysUser;
import com.jungle.blog.service.LoginService;
import com.jungle.blog.service.SysUserService;
import com.jungle.blog.vo.ErrorCode;
import com.jungle.blog.vo.LoginUserVo;
import com.jungle.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private LoginService loginService;

    // 两种方法
    //private final SysUserMapper sysUserMapper;
    //public SysUserServiceImpl(SysUserMapper sysUserMapper){
    //    this.sysUserMapper = sysUserMapper;
    //}

    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);

        if(Objects.isNull(sysUser)){
            sysUser = new SysUser();
            sysUser.setNickname("jungle");
        }
        return sysUser;
    }

    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.eq(SysUser::getPassword,password);
        queryWrapper.select(SysUser::getAccount,SysUser::getId,SysUser::getAvatar,SysUser::getNickname);
        queryWrapper.last("limit 1");

        return sysUserMapper.selectOne(queryWrapper);

    }

    @Override
    public Result findUserByToken(String token) {
        /**
         * 1、token合法性校验
         *    1.1 是否为空
         *    1.2 解析是否成功
         *    1.3 redis是否存在
         * 2、如果校验失败，返回错误
         * 3、如果成功，返回对应的结果  LoginUserVo
         */
        SysUser sysUser = loginService.checkToken(token);
        if(sysUser == null){
            Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }

        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(sysUser.getId());
        loginUserVo.setNickname(sysUser.getNickname());
        loginUserVo.setAvatar(sysUser.getAvatar());
        loginUserVo.setAccount(sysUser.getAccount());

        return Result.success(loginUserVo);
    }
}
