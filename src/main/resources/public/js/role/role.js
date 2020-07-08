layui.use(['table','layer'],function(){
       var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    //角色列表展示
    var  tableIns = table.render({
        elem: '#roleList',
        url : ctx+'/role/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "roleListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'roleName', title: '角色名', minWidth:50, align:"center"},
            {field: 'roleRemark', title: '角色备注', minWidth:100, align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#roleListBar',fixed:"right",align:"center"}
        ]]
    });

    // 多条件搜索
    $(".search_btn").on("click",function () {
        table.reload("roleListTable",{
            page:{
                curr:1
            },
            where:{
                // 角色名
                roleName:$("input[name='roleName']").val()
            }
        })
    });


    /**
     * 数据表格的头部工具栏
     */
    table.on('toolbar(roles)', function (data) {
        // 获取数据表格选中的行
        var checkStatus = table.checkStatus(data.config.id);
        var event = data.event;
        // 判断用户行为
        switch (event) {
            case "add":
                // 打开用户添加或修改的对话框
                openAddOrUpdateRoleDialog();
                break;
            case "grant":
                // 打开角色授权的窗口
                openAddRoleGrantDialog(checkStatus.data);
                break;
        }
    });


    table.on('tool(roles)',function (data) {
        // console.log(data);
        // 得到行事件
        var event = data.event; // 编辑 edit  删除 del

        if (event == "edit") {
            // 打开角色添加或修改的对话框
            openAddOrUpdateRoleDialog(data.data.id);
        } else if (event == "del") {
            // 弹出提示框询问用户是否确认删除
            layer.confirm("您确定要删除当前记录吗？",{
                btn:["确认","取消"],
            },function (index) {
                // 关闭确认框
                layer.close(index);

                $.ajax({
                    type:"post",
                    url:ctx + "/role/delete",
                    data:{
                        roleId:data.data.id
                    },
                    success:function (result) {
                        if (result.code != 200) {
                            layer.msg(result.msg, {icon: 5});
                        } else {
                            layer.msg("角色删除成功！", {icon: 6});
                            // 加载表格
                            tableIns.reload();
                        }
                    }
                });

            });

        }

    });


    /**
     * 打开角色对话框
     */
    function  openAddOrUpdateRoleDialog(roleId) {

        var title = "<h3>角色管理-添加角色</h3>";
        var url = ctx + "/role/toRolePage";

        if (roleId != null && roleId != "") {
            title = "<h3>角色管理-更新角色</h3>";
            url += "?roleId=" + roleId;
        }

        layui.layer.open({
            title:title, // 标题
            type:2, // ifream层
            content: url, // 加载路径
            area:["450px","300px"], // 弹出层的大小
            maxmin:true // 是否可以最大化
        });
    }


    /**
     * 打开添加角色授权的页面
     */
    function openAddRoleGrantDialog(data) {

        console.log(data);
        // 判断是否选择了要授权的角色
        if (data == null || data.length == 0) {
            layer.msg("请选择要授权的角色！", {icon: 5});
            return;
        }
        if (data.length != 1) {
            layer.msg("只能选择一个角色授权！", {icon: 5});
            return;
        }

        layui.layer.open({
            title:"<h3>角色管理-角色授权</h3>", // 标题
            type:2, // ifream层
            content: ctx + "/role/toAddGrantPage?roleId=" + data[0].id, // 加载路径
            area:["450px","400px"], // 弹出层的大小
            maxmin:true // 是否可以最大化
        });
    }










});
