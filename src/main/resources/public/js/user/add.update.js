layui.use(['form', 'layer','formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    // 引入 formSelects 模块
    var formSelects = layui.formSelects;

    /**
     * 监听表单的submit
     */
    form.on('submit(addOrUpdateUser)',function (data) {

        // 提交数据时的加载层 （https://layer.layui.com/）
        var index = layer.msg("数据提交中,请稍后...",{
            icon:16, // 图标
            time:false, // 不关闭
            shade:0.8 // 设置遮罩的透明度
        });
        // 得到请求的路径
        var url = ctx + "/user/add";

        // 得到请求的参数
        var paramData = data.field; // 得到表单的全部字段
        console.log(paramData);
        // 判断id是否为空 ，如果不为空，则为修改操作
        if(paramData.id != null && paramData.id.trim() != "") {
            url = ctx + "/user/update";
        }

        // 发送ajax请求
        $.post(url, paramData, function (result) {
            // 判断是否成功
            if (result.code == 200) {
                // 关闭加载层
                layer.close(index);
                // 提示用户成功
                layer.msg("操作成功！", {icon: 6});
                // 关闭所有的iframe层
                layer.closeAll("iframe");
                // 刷新父页面，重新渲染表格数据
                parent.location.reload();
            } else {
                layer.msg(result.msg, {icon: 5});
            }
        });

        // 阻止表单提交
        return false;
    });

    /**
     * 加载下拉框数据
     */
    var userId = $("input[name='id']").val();
    formSelects.config('selectId',{
        type:"post",
        searchUrl:ctx + "/role/queryAllRoles?userId="+userId,
        //自定义返回数据中name的key, 默认 name
        keyName: 'roleName',
        //自定义返回数据中value的key, 默认 value
        keyVal: 'id'
    },true);




    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });
    
});