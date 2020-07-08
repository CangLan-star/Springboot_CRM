package com.zmf.crm.dao;

import com.zmf.crm.base.BaseMapper;
import com.zmf.crm.base.BaseQuery;
import com.zmf.crm.query.RoleQuery;
import com.zmf.crm.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {

    List<Map<String,Object>> queryAllRoles(Integer userId);



}