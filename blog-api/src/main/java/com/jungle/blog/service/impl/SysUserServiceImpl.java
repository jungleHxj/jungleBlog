package com.jungle.blog.service.impl;

import com.jungle.blog.dao.mapper.SysUserMapper;
import com.jungle.blog.dao.pojo.SysUser;
import com.jungle.blog.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

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
}
