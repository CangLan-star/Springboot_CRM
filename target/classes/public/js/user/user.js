layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 用户列表展示
     */
    var  tableIns = table.render({
        elem: '#userList',
        url : ctx + '/user/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "userListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'userName', title: '用户名', minWidth:50, align:"center"},
            {field: 'email', title: '用户邮箱', minWidth:100, align:'center'},
            {field: 'phone', title: '用户电话', minWidth:100, align:'center'},
            {field: 'trueName', title: '真实姓名', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#userListBar',fixed:"right",align:"center"}
        ]]
    });


    /**
     * 多条件查询
     */
    $(".search_btn").click(function () {
        tableIns.reload({
            where: {
                //设定异步数据接口的额外参数，任意设
                userName:$("[name='userName']").val(),
                email:$("[name='email']").val(),
                phone:$("[name='phone']").val()
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });




    /**
     * 数据表格的头部工具栏
     */
    table.on('toolbar(users)', function (data) {
        //选中当前行
        var checkStatus = table.checkStatus(data.config.id);
        var event = data.event;
        // 判断用户行为
        switch (event) {
            case "add":
                // 用户添加或修改
                openAddOrUpdateUserDialog();
                break;
            case "del":
                //删除用户记录
                deleteUser(checkStatus.data);
                break;

        }

    });

    //删除用户方法
    function deleteUser(data) {
            //判断是否选择要删除的记录
            //console.log(data);
            if (data.length==0){
                layer.msg("请选择要删除的记录",{icon:5});
                return;
            }
            //弹出提示框询问用户是否要删除
            layer.confirm("您确认要删除选中的记录吗？",{
                    btn:["确认","取消"]},
                function (index) {
                    //alert(1);
                    //关闭确认框
                    layer.close(index);

                    //获取要删除记录的id  ids=1&ids=2&ids=3
                    var ids ="ids=";
                    for (var i = 0;i < data.length;i++){
                        var id = data[i].id;
                        // console.log(id);
                        if (i==data.length-1){
                            ids += id;
                        }else {
                            ids += id+"&ids=";
                        }
                    }
                    console.log(ids);
                    //发送ajax请求后台
                    $.ajax({
                        type:"post",
                        url:ctx + "/user/delete",
                        data:ids, // 传递的参数是数组
                        success:function (result) {
                            if (result.code!=200){
                                layer.msg(result.msg,{icon:6});
                            }else {
                                layer.msg("用户数据删除成功！", {icon: 6});
                                // 加载表格
                                tableIns.reload();
                            }
                        }
                    });
                });
        }

    //工具栏监听事件
    function openAddOrUpdateUserDialog(userId) {
        var title ="<h3>用户管理-添加用户</h3>";
        var url = ctx+"/user/toUserPage";
        if (userId!=null && userId!=""){
           title ="<h3>用户管理-更新用户</h3>";
           url +="?userId="+userId;
        }
            layui.layer.open({
                title: title, // 标题
                type: 2, // ifream层
                content: url, // 加载路径
                area: ["650px", "400px"], // 弹出层的大小
                maxmin: true // 是否可以最大化
            });
    }
    /*
    * 表格行监听事件
    * saleChances为table标签的lay-filter 属性值
    * */
    table.on('tool(users)',function (data) {
        var event =data.event;//编辑 edit；删除 del
        event.title="<h2>用户管理-更新用户</h2>"
        //判断事件类型
        switch (event) {
            case "edit":
                //打开添加或修改的弹出框
                openAddOrUpdateUserDialog(data.data.id);
                break;
            case "del":
                console.log("删除营销机会数据...");
                layer.confirm("您确认要删除此条记录吗",{
                    btn:["确认","取消"],
                },function (index) {
                    // 关闭确认框
                    layer.close(index);

                    $.ajax({
                        type:"post",
                        url:ctx+"/user/delete",
                        data:{
                            ids:data.data.id
                        },
                        success:function (result) {
                            console.log(result.id);
                            if (result.code!=200){
                                layer.msg(result.msg,{icon:5});
                            }else {
                                layer.msg("营销机会数据删除成功！",{icon:6});
                                //加载表格
                                tableIns.reload();
                            }

                        }
                    });

                });
                break;

        }

    });

});