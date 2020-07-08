package com.zmf.crm.controller;

import com.zmf.crm.base.BaseController;
import com.zmf.crm.service.UserService;
import com.zmf.crm.utils.LoginUserUtil;
import com.zmf.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController extends BaseController {
    @Resource
    private UserService userService;
    /**
     * 系统登录⻚
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "index";
    }

    // 系统界⾯欢迎⻚
    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }
    /**
     * 后端管理主⻚⾯
     * @return
     */
    @RequestMapping("main")
    public String main(HttpServletRequest request){
        // 通过⼯具类，从cookie中获取userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 调⽤对应Service层的⽅法，通过userId主键查询⽤户对象
        User user = userService.selectByPrimaryKey(userId);
        // 将⽤户对象设置到request作⽤域中
        request.setAttribute("user", user);
        return "main";
    }
}
