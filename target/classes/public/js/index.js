layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    form.on('submit(login)', function(data){
        console.log(data);

        // console.log(data.elem) //被执行事件的元素DOM对象，一般为button对象
        // console.log(data.form) //被执行提交的form对象，一般在存在form标签时才会返回
        // console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}

        //获取表单字段
        var fieldData = data.field;
        console.log(fieldData);
        //判断是否为空
        if (null==fieldData.username || fieldData.username.trim()==""){
            layer.msg("用户名称不能为空");
            return false;
        }
        if (null==fieldData.password || fieldData.password.trim()==""){
            layer.msg("用户密码不能为空");
            return false;
        }
        // 发送ajax请求
        $.ajax({
            type:"post",
            url: ctx + "/user/login",
            data:{
                userName:fieldData.username,
                userPwd:fieldData.password
            },
            success:function(result){
                console.log(result);
                // 判断是否成功
                if (result.code != 200) {
                    // 提示信息
                    layer.msg(result.msg);
                } else {
                    layer.msg("登录成功！", function () {

                        // 将用户信息存到cookie中
                        var userInfo = result.result;
                        // 如果用户勾选了"记住我"的复选框，则设置cookie的有效期为7天
                        if ($("#rememberMe").prop("checked")) {
                            // 设置cookie对象7天失效
                            $.cookie("userIdStr", userInfo.userIdStr, {expires:7});
                            $.cookie("userName", userInfo.userName, {expires:7});
                            $.cookie("trueName", userInfo.trueName, {expires:7});
                        } else {
                            $.cookie("userIdStr", userInfo.userIdStr);
                            $.cookie("userName", userInfo.userName);
                            $.cookie("trueName", userInfo.trueName);
                        }


                        // 登录成功后，跳转到首页
                        window.location.href = ctx + "/main";
                    });
                }
            }
        });

        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });
    
    
});