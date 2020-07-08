package com.zmf.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmf.crm.base.BaseService;
import com.zmf.crm.dao.SaleChanceMapper;
import com.zmf.crm.enums.DevResult;
import com.zmf.crm.enums.StateStatus;
import com.zmf.crm.query.SaleChanceQuery;
import com.zmf.crm.utils.AssertUtil;
import com.zmf.crm.utils.PhoneUtil;
import com.zmf.crm.vo.SaleChance;
import freemarker.template.utility.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService <SaleChance,Integer>{
    @Resource
    private SaleChanceMapper saleChanceMapper;
    /**
     * 多条件分⻚查询营销机会
     * @param query
     * @return
     */
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery query){
        Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(query.getPage(),query.getLimit());
        PageInfo<SaleChance> pageInfo = new PageInfo<>(saleChanceMapper.selectByParams(query));
        System.out.println(pageInfo.toString());
        map.put("code",0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }


    /**
     * 营销机会数据添加
     * 1.参数校验
     * customerName:⾮空
     * linkMan:⾮空
     * linkPhone:⾮空 11位⼿机号
     * 2.设置相关参数默认值
     * state:默认未分配 如果选择分配⼈ state 为已分配
     * assignTime:如果 如果选择分配⼈ 时间为当前系统时间
     * devResult:默认未开发 如果选择分配⼈devResult为开发中 0-未开发 1-开发中 2-开发成
     功 3-开发失败
     * isValid:默认有效数据(1-有效 0-⽆效)
     * createDate updateDate:默认当前系统时间
     * 3.执⾏添加 判断结果
     */

    /*
    * 营销机会数据添加
    * */
    public void addSaleChance(SaleChance saleChance){
        //1.参数校验
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //2.设置相关参数默认值
        //未选择分配人
        saleChance.setState(StateStatus.UNSTATE.getType());
        saleChance.setDevResult(DevResult.UNDEV.getStatus());
        //选择分配人
        if (StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
            saleChance.setAssignTime(new Date());
        }
        saleChance.setIsValid(1);
        saleChance.setUpdateDate(new Date());
        saleChance.setCreateDate(new Date());
        //3.执行添加，判断结果
        AssertUtil.isTrue(insertSelective(saleChance)<1,"营销数据添加失败");
    }

    /*
    * 更新营销机会数据
    *   1.判断id是否为空，且要修改的数据存在
    *   2.参数校验：
    *           客户名 非空
    *           联系人 非空
    *           手机号码 非空 格式正确与否
    *   3.设置默认值
    *           updataDate 当前时间
    *           指派人 ： 未指派——>已指派
    *                      已指派——>未指派
    *           分配人：    无值——>有值
    *                       有值——>无值
    *           分配状态： 未分配——>已分配
    *                      已分配——>未分配
    *           开发状态： 未开发——>开发中
    *                      开发中——>未开发
    *           分配时间： 无时间——>当前时间
    *                      当前时间——>无时间
    *   4.执行更新操作
    * */
    public void updateSaleChance(SaleChance saleChance){
        //1.判断id是否为空，数据是否存在
        AssertUtil.isTrue(saleChance.getId()==null,"更新失败");
        //通过id查询营销机会对象
        SaleChance temp = (SaleChance) saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(temp==null,"待更新记录不存在");
        //2.参数校验
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        // 3. 设置默认值
        // 创建时间在做修改操作不作处理，默认是数据中对应的创建时间
        saleChance.setCreateDate(temp.getCreateDate());
        // 更新时间是当前时间
        saleChance.setUpdateDate(new Date());
        // 设置分配时间
        saleChance.setAssignTime(temp.getAssignTime());
        // 未指派 ————> 已指派
        if (StringUtils.isBlank(temp.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())) {
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
            saleChance.setAssignTime(new Date());
        } else if (StringUtils.isNotBlank(temp.getAssignMan()) && StringUtils.isBlank(saleChance.getAssignMan())) {
            // 已指派 ————> 未指派
            saleChance.setState(StateStatus.UNSTATE.getType());
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
            saleChance.setAssignTime(null);
            saleChance.setAssignMan("");
        } else if (StringUtils.isNotBlank(saleChance.getAssignMan())
                && StringUtils.isNotBlank(temp.getAssignMan())
                && !(temp.getAssignMan()).equals(saleChance.getAssignMan())) {
            // 已指派 ————> 已指派 （指派前后不是同一个人）
            // 更新指派时间
            saleChance.setAssignTime(new Date());
        }

        // 4. 执行更新操作
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) < 1, "营销机会数据更新失败！");

    }

    /*
    * 查询所有销售人员
    *
    * */
        public List<Map<String,Object>> queryAllSales(){
        return saleChanceMapper.queryAllSales();
        }



        /*
        * 基本参数校验
        * */
    private void checkParams(String customerName, String linkMan, String linkPhone) {
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"客户名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"联系人不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"手机号不能为空！");
        AssertUtil.isTrue(!(PhoneUtil.isMobile(linkPhone)),"手机号格式不正确！");

    }

    /**
     * 营销机会数据删除
     *      1. 判断参数是否为空
     *      2. 执行删除操作
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids) {
        AssertUtil.isTrue(ids == null, "待删除的记录不存在！");
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids) < 1, "营销机会数据删除失败！");
    }
}
