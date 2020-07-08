layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    /**
     * 监听表单的submit
     */
    form.on('submit(addOrUpdateSaleChance)',function (data) {

        // 提交数据时的加载层
        var index = layer.msg("数据提交中,请稍后...",{
            icon:16, // 图标
            time:false, // 不关闭
            shade:0.8 // 设置遮罩的透明度
        });
        // 得到请求的路径
        var url = ctx + "/sale_chance/add";
        // 得到请求的参数
        var paramData = data.field; // 得到表单的全部字段

        //判断id是否为空，如果为空则是添加操作如果不为空则是更新操作
        if(paramData.id !=null && paramData.id.trim() != ""){
            url = ctx + "/sale_chance/update";
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

    // 发送ajax请求，加载下拉框
    $.ajax({
        type:"get",
        url: ctx + "/sale_chance/queryAllSales",
        data:{},
        success:function (data) {
            // console.log(data);
            for (var i = 0; i < data.length; i++) {
                // 判断指派人是否为空
                var saleChanceName = $("#saleChanceName").val();
                var opt;
                // 如果指派人不为空，且与当前循环到的选项值相等
                if (saleChanceName != null && saleChanceName == data[i].userId) {
                    opt = '<option value="'+data[i].userId+'" selected>'+data[i].userName+'</option>';
                } else {
                    opt = '<option value="'+data[i].userId+'">'+data[i].userName+'</option>';
                }
                // 将下拉选项追加到下拉框中
                $("#assignMan").append(opt);
                console.log(data);
                console.log(saleChanceName);
            }
            // 重新渲染下拉框内容
            layui.form.render("select");
        }
    });





    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {

        // 关闭弹出层
        parent.layer.closeAll("iframe");
    });


});