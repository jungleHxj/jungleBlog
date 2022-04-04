package com.jungle.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jungle.admin.mapper.AdminMapper;
import com.jungle.admin.mapper.PermissionMapper;
import com.jungle.admin.pojo.Admin;
import com.jungle.admin.pojo.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;


    public Admin findAdminByUserName(String username){
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, username).last("limit 1");
        Admin adminUser = adminMapper.selectOne(queryWrapper);
        return adminUser;
    }

    public List<Permission> findPermissionsByAdminId(Long adminId){
        return adminMapper.findPermissionsByAdminId(adminId);
    }
}
