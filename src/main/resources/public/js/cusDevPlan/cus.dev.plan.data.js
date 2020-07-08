layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;



    alert($("input[name='id']").val());
    /**
     * 计划项数据展示
     */
    var  tableIns = table.render({
        elem: '#cusDevPlanList',
        url : ctx+'/cus_dev_plan/list?sId='+$("input[name='id']").val(),
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "cusDevPlanListTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'planItem', title: '计划项',align:"center"},
            {field: 'exeAffect', title: '执行效果',align:"center"},
            {field: 'planDate', title: '执行时间',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,templet:"#cusDevPlanListBar"}
        ]]
    });




    /**
     * 工具栏监听事件
     */
    table.on('toolbar(cusDevPlans)', function (data) {
        var event = data.event;
        // 判断用户行为
        switch (event) {
            case "add":
                //  打开对话框
                openAddOrUpdateCusDevPlanDialog();
                break;
            case "success":
                //  开发成功
                updateSaleChanceDevResult(2);
                break;
            case "failed":
                //  开发失败
                updateSaleChanceDevResult(3);
                break;
        }
    });

    /*
    * 设置营销机会的开发状态
    * */
    function updateSaleChanceDevResult(devresult) {
        layer.confirm("确认执行当前操作",{icon:3,title:"计划项维护"},function (index) {
            //得到营销机会的ID，在隐藏域中
            var saleChanceId = $("input[name='id']").val();
            //发送ajax请求
            $.post(ctx+"/cus_dev_plan/updateSaleChanceDevResult",{saleChanceId:saleChanceId,devresult:devresult},function (result) {
                console.log("devresult"+devresult)
                if (result.code== 200){
                    layer.msg("操作成功！",{icon:6})
                    //关闭弹出层
                    layer.closeAll("iframe");
                    //刷新父页面
                    parent.location.reload();
                }else {
                    layer.msg(result.msg,{icon:5});
                }
            });
        });
    }


    /**
     * 行监听事件
     */
    table.on('tool(cusDevPlans)',function (data) {

        // 得到行事件
        var event = data.event;
        // 获取当前行的数据
        var rowData = data.data;
        console.log("rowdata:"+rowData);
        if (event == "edit") {
            //  打开对话框
            openAddOrUpdateCusDevPlanDialog(rowData.id);
        }else if(event=="del") {
            //删除计划项
            deleteCusDevPlan(rowData.id);
            
        }

    });
    /*
    * 删除计划项
    * */
    function deleteCusDevPlan(cId) {
    layer.confirm("确定删除当前数据？",{icon:3,title:"开发计划管理"},function (index) {
        //发送ajax请求
        $.post(ctx+"/cus_dev_plan/delete",{cId:cId},function (result) {
            if (result.code == 200){
                layer.msg("操作成功！",{icon:6})
                //重新渲染表格
                tableIns.reload();
            }else {
                layer.msg(result.msg,{icon:5});
            }
        });
    });
    }



    function openAddOrUpdateCusDevPlanDialog(cusDevPlanId) {
        console.log("cusDevPlanId: "+cusDevPlanId)
        var title = "<h2>计划项管理 - 添加数据</h2>";
        var url = ctx + "/cus_dev_plan/addOrUpdateCusDevPlanPage?sId=" + $("input[name='id']").val();
        console.log($("input[name='id']").val());
        // 判断Id是否为空 不为空则为修改
        if (cusDevPlanId != null) {
            title = "<h2>计划项管理 - 更新数据</h2>";
            url += "&cId=" + cusDevPlanId;
        }

        layui.layer.open({
            title:title, // 标题
            type:2, // ifream层
            content: url, // 加载路径
            area:["500px","300px"], // 弹出层的大小
            maxmin:true // 是否可以最大化
        });
    }


});
