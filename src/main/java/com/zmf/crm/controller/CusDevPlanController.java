package com.zmf.crm.controller;

import com.zmf.crm.base.BaseController;
import com.zmf.crm.base.ResultInfo;
import com.zmf.crm.query.CusDevQuery;
import com.zmf.crm.query.UserQuery;
import com.zmf.crm.service.CusDevPlanService;
import com.zmf.crm.service.SaleChanceService;
import com.zmf.crm.service.UserService;
import com.zmf.crm.vo.CusDevPlan;
import com.zmf.crm.vo.SaleChance;
import com.zmf.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("cus_dev_plan")
public class CusDevPlanController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;
    @Resource
    private CusDevPlanService cusDevPlanService;

    /*
    * 进入客户开发计划页面
    * */

    @RequestMapping("index")
    public String index(){
        return "cusDevPlan/cus_dev_plan";
    }
    /*
    * 加载计划项数据页面
    * */
    @RequestMapping("toCusDevPlanDataPage")
    public String toCusDevPlanDataPage(Integer sId, Model model){

        //通过id查询营销机会数据
        if (sId!=null){
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(sId);
            //设置请求域
            model.addAttribute("saleChance",saleChance);
        }
        return "cusDevPlan/cus_dev_plan_data";
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCusDevPlan(CusDevQuery query){
        System.out.println(query);
        return cusDevPlanService.queryByParamsForTable(query);
    }

    /*
    * 进入添加或修改计划项页面
    * */
    @RequestMapping("addOrUpdateCusDevPlanPage")
    public String addOrUpdataCusDevPlanPage(Integer cId,Model model,Integer sId ){
        //设置请求域
        model.addAttribute("sId",sId);
        System.out.println(cId);

            if (cId!=null){
                //通过id查询计划项数据
                CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(cId);
                //设置请求域
                model.addAttribute("cusDevPlan",cusDevPlan);
            }
            return "cusDevPlan/add_update";
    }

    /*
    * 添加计划项
    * */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.addCusDevPlan(cusDevPlan);
        return success("success");
    }
    /*
    * 更新计划项
    * */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("success");
    }
    /*
    * 删除计划项
    * */
    @PostMapping("delete")
    @ResponseBody
    private ResultInfo deleteCusDevPlan(Integer cId){
        cusDevPlanService.deleteCusDevPlan(cId);
        return success("success");

    }
    /*
     * 更新营销机会的开发状态
     * */
    @PostMapping("updateSaleChanceDevResult")
    @ResponseBody
    private ResultInfo updateSaleChanceDevResult(Integer saleChanceId,Integer devresult){
        cusDevPlanService.updateSaleChanceDevResult(saleChanceId,devresult);
        return success("success");
    }


}
