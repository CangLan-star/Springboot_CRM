layui.use(['table','layer'],function() {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 营销机会列表展示
     */
    var tableIns = table.render({
        elem: '#saleChanceList', // 表格绑定的ID
        url: ctx + '/sale_chance/list', // 访问数据的地址
        cellMinWidth: 95, // 单元格最小的宽度
        page: true, // 开启分页
        height: "full-125", // 高度
        limits: [10, 15, 20, 25], // 每页显示的数量
        limit: 10, // 默认每页显示10条
        toolbar: "#toolbarDemo", // 绑定工具栏，id绑定
        id: "saleChanceListTable", // 数据表格的ID值
        // 数据  type：列的类型  field：值要与数据的字段名保持一致  title：显示的表头名
        cols: [[
            {type: "checkbox", fixed: "center"},
            {field: "id", title: '编号', fixed: "true"},
            {field: 'chanceSource', title: '机会来源', align: "center"},
            {field: 'customerName', title: '客户名称', align: 'center'},
            {field: 'cgjl', title: '成功几率', align: 'center'},
            {field: 'overview', title: '概要', align: 'center'},
            {field: 'linkMan', title: '联系人', align: 'center'},
            {field: 'linkPhone', title: '联系电话', align: 'center'},
            {field: 'description', title: '描述', align: 'center'},
            {field: 'createMan', title: '创建人', align: 'center'},
            {field: 'createDate', title: '创建时间', align: 'center'},
            {field: 'uname', title: '指派人', align: 'center'},
            {field: 'assignTime', title: '分配时间', align: 'center'},
            // templet 格式化数据 需要将格式化之后的值返回
            {
                field: 'state', title: '分配状态', align: 'center', templet: function (d) {
                    return formatterState(d.state);
                }
            },
            {
                field: 'devResult', title: '开发状态', align: 'center', templet: function (d) {
                    return formatterDevResult(d.devResult);
                }
            },
            {title: '操作', templet: '#saleChanceListBar', fixed: "right", align: "center", minWidth: 150}
        ]]
    });

    /**
     * 格式化分配状态
     *  0 - 未分配
     *  1 - 已分配
     *  其他 - 未知
     * @param state
     * @returns {string}
     */
    function formatterState(state) {
        if (state == 0) {
            return "<div style='color: yellow'>未分配</div>";
        } else if (state == 1) {
            return "<div style='color: green'>已分配</div>";
        } else {
            return "<div style='color: red'>未知</div>";
        }
    }

    /**
     * 格式化开发状态
     *  0 - 未开发
     *  1 - 开发中
     *  2 - 开发成功
     *  3 - 开发失败
     * @param value
     * @returns {string}
     */
    function formatterDevResult(value) {
        if (value == 0) {
            return "<div style='color: yellow'>未开发</div>";
        } else if (value == 1) {
            return "<div style='color: #00FF00;'>开发中</div>";
        } else if (value == 2) {
            return "<div style='color: #00B83F'>开发成功</div>";
        } else if (value == 3) {
            return "<div style='color: red'>开发失败</div>";
        } else {
            return "<div style='color: #af0000'>未知</div>"
        }

    }

    /**
     * 多条件查询
     */
    $(".search_btn").click(function () {
        tableIns.reload({
            where: {
                //设定异步数据接口的额外参数，任意设
                customerName: $("[name='customerName']").val(),

                createMan: $("[name='createMan']").val(),

                state: $("#state").val(),
            }
            , page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });


    /**
     * 数据表格的头部工具栏
     */
    table.on('toolbar(saleChances)', function (data) {
        // 获取数据表格选中的行
        var checkStatus = table.checkStatus(data.config.id);
        var event = data.event;
        // 判断用户行为
        switch (event) {
            case "add":
                // 营销机会添加操作 打开对话框
                openAddOrUpdateSaleChanceDialog();
                break;
            case "del":
                deleteSaleChance(checkStatus.data);
                break;
        }

    });

    /*
    * 表格行监听事件
    * saleChances为table标签的lay-filter 属性值
    * */
    table.on('tool(saleChances)',function (data) {
        //console.log(data);//event: "edit"
        var event =data.event;//编辑 edit；删除 del
        //获取当前行的数据
        var rowData = data.data;
        console.log(rowData);
        //获取当前行的数据的id和添加页面区别
        var saleChanceId = rowData.id;
        console.log(saleChanceId);
        //判断事件类型
        switch (event) {
            case "edit":
                console.log("编辑营销机会数据...");

                //打开添加或修改的弹出框
                openAddOrUpdateSaleChanceDialog(saleChanceId);
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
                        url:ctx+"/sale_chance/delete",
                        data:{
                            ids:saleChanceId
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


    /**
     * 打开营销机会添加/修改对话框
     */
    function openAddOrUpdateSaleChanceDialog(saleChanceId) {
        var title = "<h2>营销机会管理 - 机会添加</h2>";
        var url = ctx + "/sale_chance/addOrUpdateSaleChancePage";

        //判断传进的id是否为空，如果为空则标题为添加页面不用修改，如果非空则将标题改为修改页面
        if(saleChanceId){
            title="<h2>营销机会管理 - 机会更新</h2>"
            url += "?id="+saleChanceId;

        }
        layui.layer.open({
            title: title, // 标题
            type: 2, // ifream层
            content: url, // 加载路径
            area: ["500px", "620px"], // 弹出层的大小
            maxmin: true // 是否可以最大化
        });
    }
    /*
    * 删除被选中的营销机会记录
    * */
    function deleteSaleChance(data) {
        //判断是否选择要删除的记录
        //console.log(data);
        if (data==null || data.length<1){
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
                    url:ctx + "/sale_chance/delete",
                    data:ids, // 传递的参数是数组
                    success:function (result) {
                        if (result.code!=200){
                            layer.msg(result.msg,{icon:6});
                        }else {
                            layer.msg("营销机会数据删除成功！", {icon: 6});
                            // 加载表格
                            tableIns.reload();
                        }
                    }
                });
        });
    }



});
