package com.zmf.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmf.crm.base.BaseService;
import com.zmf.crm.dao.UserMapper;
import com.zmf.crm.dao.UserRoleMapper;
import com.zmf.crm.model.UserModel;
import com.zmf.crm.query.SaleChanceQuery;
import com.zmf.crm.query.UserQuery;
import com.zmf.crm.utils.AssertUtil;
import com.zmf.crm.utils.Md5Util;
import com.zmf.crm.utils.PhoneUtil;
import com.zmf.crm.utils.UserIDBase64;
import com.zmf.crm.vo.SaleChance;
import com.zmf.crm.vo.User;
import com.zmf.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    public UserModel userLogin(String userName, String userPwd){
        //1.参数校验
        checkParams(userName,userPwd);
        //2.查询用户对象，返回的是user对象
        User user = userMapper.queryUserByName(userName);
        //3.判断用户对象在数据库中是否为空
        AssertUtil.isTrue(null==user,"用户不存在");
        //4.校验密码
        checkPwd(userPwd,user.getUserPwd());
        //5.构建返回的用户模型
        return buildUserModel(user);
    }

    /*
    * 构建用户模型
    * */
    private UserModel buildUserModel(User user) {
        UserModel userModel = new UserModel();
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    private void checkPwd(String userPwd, String pwd) {
        //1.将前台传递的密码加密
        userPwd = Md5Util.encode(userPwd);
        //比较前台的密码和数据库的密码是否一致
        AssertUtil.isTrue(!(userPwd.equals(pwd)),"密码不正确");

    }

    private void checkParams(String userName, String userPwd) {
        //1.用户名是否为空
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空!");
        //2.用户密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空!");
    }


    /**
     * ⽤户密码修改
     * 1. 参数校验
     * ⽤户ID：userId ⾮空 ⽤户对象必须存在
     * 原始密码：oldPassword ⾮空 与数据库中密⽂密码保持⼀致
     * 新密码：newPassword ⾮空 与原始密码不能相同
     * 确认密码：confirmPassword ⾮空 与新密码保持⼀致
     * 2. 设置⽤户新密码
     * 新密码进⾏加密处理
     * 3. 执⾏更新操作
     * 受影响的⾏数⼩于1，则表示修改失败
     *
     * 注：在对应的更新⽅法上，添加事务控制
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPassword(Integer userId, String oldPassword, String newPassword, String confirmPassword){
        //1.通过userId获取用户对象
        User user = userMapper.selectByPrimaryKey(userId);
        //2.数据校验
        checkPasswordParams(user,oldPassword,newPassword,confirmPassword);
        // 3. 设置⽤户新密码
        user.setUserPwd(Md5Util.encode(newPassword));
        // 4. 执⾏更新操作
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "⽤户密码更新失败！");


    }

    private void checkPasswordParams(User user, String oldPassword, String newPassword, String confirmPassword) {
        //1.user对象非空校验
        AssertUtil.isTrue(null==user,"用户未登录！");
        //2.原始密码非空校验
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"请输入原始密码！");
        //3.原始密码和数据库中的加密密码一致
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))),"原始密码不正确！");
        //4.新密码非空校验
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"请输入新密码！");
        //5.新密码与原始密码不能相同
        AssertUtil.isTrue(newPassword.equals(oldPassword),"新密码不能与原始密码相同！");
        //6.确认密码的非空校验
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword),"请输入确认密码");
        //7.新密码要与确认密码一致
        AssertUtil.isTrue(!(newPassword.equals(confirmPassword)),"新密码与确认密码不一致");
    }

    /**
     * 多条件分⻚查询营销机会
     * @param query
     * @return
     */
    public Map<String,Object> queryUserByParams(UserQuery query){
        Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(query.getPage(),query.getLimit());
        PageInfo<User> pageInfo = new PageInfo<>(userMapper.queryUserByParams(query));
        System.out.println(pageInfo.toString());
        map.put("code",0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加用户
     *  1. 参数校验
     *      用户名 非空 唯一性
     *      邮箱   非空
     *      手机号 非空  格式合法
     *  2. 设置默认参数
     *      isValid 1
     *      creteDate   当前时间
     *      updateDate  当前时间
     *      userPwd 123456 -> md5加密
     *  3. 执行添加，判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user) {
    checkUserParams(user.getUserName(),user.getEmail(),user.getPhone());
        // 判断数据库中是否有用户记录
        User temp = userMapper.queryUserByName(user.getUserName());
        AssertUtil.isTrue(temp != null, "用户名已存在，请重新输入！");
        // 设置参数的默认值
        user.setUpdateDate(new Date());
        user.setCreateDate(new Date());
        user.setIsValid(1);
        user.setUserPwd(Md5Util.encode("123456"));

        // 执行添加
       AssertUtil.isTrue(userMapper.insertHasKey(user) < 1, "用户添加失败！");
        Integer userId = user.getId();
       // AssertUtil.isTrue(userId<1, "用户添加失败！");
        /*
         * 用户角色关联
         */
        userRoleRelation(userId, user.getRoleIds());

    }



    /**
     * 参数校验
     * @param userName
     * @param email
     * @param phone
     */
    private void checkUserParams(String userName, String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空！");

        AssertUtil.isTrue(StringUtils.isBlank(email), "用户邮箱不能为空！");
        // TODO 判断邮箱格式正确
        AssertUtil.isTrue(StringUtils.isBlank(phone), "手机号码不能为空！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "手机号码格式不正确！");
    }

    /**
     * 更新用户
     *  判断用户ID非空 且 用户存在
     *  1. 参数校验
     *      用户名 非空 唯一性
     *      邮箱   非空
     *      手机号 非空  格式合法
     *  2. 设置默认参数
     *      isValid 1
     *      creteDate   当前时间
     *      updateDate  当前时间
     *      userPwd 123456 -> md5加密
     *  3. 执行更新，判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user) {
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(user.getId() == null || temp == null, "待更新记录不存在！");
        // 参数校验
        checkUserParams(user.getUserName(), user.getEmail(), user.getPhone());



        // 判断用户名是否可用
        // AssertUtil.isTrue(userMapper.queryUserByName(user.getUserName()) != null && !temp.getUserName().equals(user.getUserName()), "用户名已存在，不可使用！");
        User u = userMapper.queryUserByName(user.getUserName());
        AssertUtil.isTrue( u != null &&  !u.getId().equals(temp.getId()), "用户名已存在，不可使用！");


        // 设置默认值
        user.setUpdateDate(new Date());

        // 执行更新
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "用户更新失败！");

        userRoleRelation(user.getId(),user.getRoleIds());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Integer[] ids) {
        AssertUtil.isTrue(ids==null,"待删除记录不存在");
        AssertUtil.isTrue(userMapper.deleteBatch(ids)!=ids.length,"删除记录失败");
        // 删除用户角色关联的数据
        userRoleMapper.deleteUserRole(ids);

    }
    /**
     * 用户角色关联
     *  用户ID    角色ID
     *  添加操作
     *      无角色      无角色  （用户角色表不需要操作）
     *      无角色      有角色  （需要在用户角色表中添加对应的关联数据）
     *   修改操作
     *      无角色      无角色  （用户角色表不需要操作）
     *      无角色      有角色  （需要在用户角色表中添加对应的关联数据）
     *      有角色      无角色  （需要删除指定用户的关联数据）
     *      有角色      有角色  （将原来的关联数据删除，添加新的关联数据）
     *
     *  当需要设置用户角色时，先删除原来的用户角色关联数据，再添加新的用户角色关联数据
     */

    private void userRoleRelation(Integer userId, String roleIds) {
        // 通过用户ID查询用户角色数据
        Integer count = userRoleMapper.countUserRoleByRoleId(userId);
        if (count > 0) {
            // 删除原来的用户角色关联数据
            userRoleMapper.deleteUserRoleByUserId(userId);
        }
            // 如果用户绑定角色
            if (StringUtils.isBlank(roleIds)) {
                return;
            }
            // 设置需要添加的角色数据
            List<UserRole> userRoleList = new ArrayList<>();
            // 将字符串通过指定符号分割，转换成数组
            for (String roleId: roleIds.split(",") ) {
                UserRole userRole = new UserRole();
                userRole.setCreateDate(new Date());
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setUpdateDate(new Date());
                userRole.setUserId(userId);
                userRoleList.add(userRole);
            }
            // 批量添加
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList) < userRoleList.size(), "用户角色关联失败！");
        }
}
