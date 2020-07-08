package com.zmf.crm.controller;

import com.zmf.crm.base.BaseController;
import com.zmf.crm.base.ResultInfo;
import com.zmf.crm.dao.RoleMapper;
import com.zmf.crm.query.RoleQuery;
import com.zmf.crm.service.RoleService;
import com.zmf.crm.vo.Role;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {
    @Resource
    private RoleService roleService;
    @Resource
    private RoleMapper roleMapper;

    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){

        return roleService.queryAllRoles(userId);

    }

    @RequestMapping("index")
    public String index(){
        return "role/role";
    }

    /**
     * 多条件分页查询角色
     * @param roleQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryRoleByParams(RoleQuery roleQuery) {
        return roleService.queryByParamsForTable(roleQuery);
    }

}
