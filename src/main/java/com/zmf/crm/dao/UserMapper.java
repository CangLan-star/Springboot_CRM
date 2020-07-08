package com.zmf.crm.dao;

import com.zmf.crm.base.BaseMapper;
import com.zmf.crm.query.UserQuery;
import com.zmf.crm.vo.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User,Integer>{
    //通过用户名查
    public User queryUserByName(String userName);
    //多条件查询用户列表
    List<User> queryUserByParams(UserQuery userQuery);
}