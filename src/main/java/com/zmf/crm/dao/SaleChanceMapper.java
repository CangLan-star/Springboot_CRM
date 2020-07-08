package com.zmf.crm.dao;

import com.zmf.crm.base.BaseMapper;

import java.util.List;
import java.util.Map;

public interface SaleChanceMapper extends BaseMapper {
    //查询所有销售人员
    public List<Map<String,Object>> queryAllSales();
}
