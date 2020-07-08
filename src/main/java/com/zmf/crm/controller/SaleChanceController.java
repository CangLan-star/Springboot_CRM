package com.zmf.crm.controller;

import com.zmf.crm.base.BaseController;
import com.zmf.crm.base.ResultInfo;
import com.zmf.crm.query.SaleChanceQuery;
import com.zmf.crm.service.SaleChanceService;
import com.zmf.crm.service.UserService;
import com.zmf.crm.utils.LoginUserUtil;
import com.zmf.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;
    @Resource
    private UserService userService;
    /**
     * 多条件分⻚查询营销机会
     * @param query
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery query,Integer flag,HttpServletRequest request){
        if (flag!=null && flag==1){
            //查询客户开发计划数据（查询已分配当前登录用户为指派人的营销机会数据）
           Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
           //设定指派人
            query.setAssignMan(userId);
        }
        return saleChanceService.querySaleChanceByParams(query);
    }
    /**
     * 进⼊营销机会⻚⾯
     * @return
     */
    @RequestMapping("index")
    public String index () {
        return "saleChance/sale_chance";
    }

    /*
    * 添加营销机会数据
    * */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addSaleChance(SaleChance saleChance, HttpServletRequest request){
        //1.获取用户ID
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //2.获取用户的真实姓名
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        //3.设置营销机会的创建人
        saleChance.setCreateMan(trueName);
        //4.添加营销机会的数据
        saleChanceService.addSaleChance(saleChance);
        return success("营销机会数据添加成功");
    }
    /**
     * 机会数据添加与更新⻚⾯视图转发
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateSaleChancePage")
    public String addOrUpdateeSaleChancePage(Integer id, Model model){
        //判断id是否为空不为空查询指定id数据
        if(id!=null && id!=0){
            //通过id查询营销机会对象
            SaleChance saleChance =saleChanceService.selectByPrimaryKey(id);
            //设置数据到请求域中返回给前台
            model.addAttribute("saleChance",saleChance);
        }

        return "saleChance/add_update";
    }
    /*
     * 添加营销机会数据
     * */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(SaleChance saleChance, HttpServletRequest request){
        //1.获取用户ID
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //2.获取用户的真实姓名
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        //3.设置营销机会的创建人
        saleChance.setCreateMan(trueName);
        //4.添加营销机会的数据
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据更新成功");
    }
    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String,Object>> queryAllSales(){
        return saleChanceService.queryAllSales();
    }

    /**
     * 营销机会数据删除
     * @param ids
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        saleChanceService.deleteSaleChance(ids);
        return success("success");
    }
}
