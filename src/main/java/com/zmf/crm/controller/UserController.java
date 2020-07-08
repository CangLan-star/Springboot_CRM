package com.zmf.crm.controller;

import com.zmf.crm.base.BaseController;
import com.zmf.crm.base.ResultInfo;
import com.zmf.crm.exceptions.ParamsException;
import com.zmf.crm.model.UserModel;
import com.zmf.crm.query.UserQuery;
import com.zmf.crm.service.UserService;
import com.zmf.crm.utils.LoginUserUtil;
import com.zmf.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class UserController extends BaseController {
    @Resource
    private UserService userService;
    /**
     * ⽤户登录
     * @param userName
     * @param userPwd
     * @return
     */
    @PostMapping ("user/login")
    @ResponseBody
    public ResultInfo userLogin(String userName,String userPwd){
        ResultInfo resultInfo = new ResultInfo();
//        try {
//
//            UserModel userModel = userService.userLogin(userName,userPwd);
//            resultInfo.setResult(userModel);
//        }catch (ParamsException p){
//            resultInfo.setCode(p.getCode());
//            resultInfo.setMsg(p.getMsg());
//            p.printStackTrace();
//        }catch (Exception e){
//            resultInfo.setCode(500);
//            resultInfo.setMsg("操作失败");
//            e.printStackTrace();
//        }
        UserModel userModel = userService.userLogin(userName,userPwd);
        resultInfo.setResult(userModel);
        return resultInfo;
    }

    @PostMapping("user/updatePassword")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request, String oldPassword, String newPassword, String confirmPassword){
        ResultInfo resultInfo = new ResultInfo();
//        try {
//            //获取userId
//            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
//            //调用Service层的密码修改方法
//            userService.updateUserPassword(userId,oldPassword,newPassword,confirmPassword);
//        }catch (ParamsException p){
//            resultInfo.setCode(p.getCode());
//            resultInfo.setMsg(p.getMsg());
//            p.printStackTrace();
//        }catch (Exception e){
//            resultInfo.setCode(500);
//            resultInfo.setMsg("更新失败");
//            e.printStackTrace();
//        }
        // 获取userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 调⽤Service层的密码修改⽅法
        userService.updateUserPassword(userId, oldPassword, newPassword, confirmPassword);
        return resultInfo;
    }
    @RequestMapping("user/toPasswordPage")
    public String toPasswordPage(){
        return "user/password"; }


        /*
        * 多条件分页查用户列表
        * */
        @RequestMapping("user/list")
        @ResponseBody
        public Map<String,Object> queryUserByParams(UserQuery userQuery){
        return  userService.queryUserByParams(userQuery);
        }

        @RequestMapping("user/index")
        public String index(){
            return "user/user";

        }
        @RequestMapping("user/toUserPage")
        public String toUserPage(Integer userId,HttpServletRequest request){
            if (userId!=null){
                User user =userService.selectByPrimaryKey(userId);
                request.setAttribute("user",user);
            }
            return "user/add_update";
        }
    /*
     *用户添加
     * */
    @PostMapping("user/add")
    @ResponseBody
    public ResultInfo addUser(User user){
        userService.addUser(user);
        return success("用户添加成功！");
    }

    /*
     * 用户更新
     * */
    @PostMapping("user/update")
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("用户更新成功！");
    }
    /*
     * 用户删除
     * */
    @PostMapping("user/delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        userService.deleteUser(ids);
        return success("用户删除成功！");
    }

}
