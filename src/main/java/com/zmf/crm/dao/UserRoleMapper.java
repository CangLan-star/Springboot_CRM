package com.zmf.crm.dao;

import com.zmf.crm.base.BaseMapper;
import com.zmf.crm.vo.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {

    //查询用户角色表中是否存在指定用户的关联数据
    Integer countUserRoleByRoleId(Integer userId);
    //删除原来的用户角色关联关系
    void deleteUserRoleByUserId(Integer userId);

    void deleteUserRole(Integer[] ids);
}