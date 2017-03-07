/**
 * Created by zhouqian on 2016/7/8.
 */
var modelApp = angular.module('modelApp', []);
modelApp.filter(
    'to_trusted', ['$sce', function ($sce) {
        return function (text) {
            return $sce.trustAsHtml(text);
        }
    }]
);

//缓存页面选中的checkbox的值
var checkList = [];
//初始化分页插件
MY.page.render(modelApp,function(data) {
    var cbArray = $('.table-div').find('input:checkbox');
    $('.table-div tbody').empty();
    if(data && data.bizData.rows && data.bizData.rows.length > 0) {
        var datarow = data.bizData.rows;
        var page = data.bizData.page;
        var pagesize = data.bizData.pagesize;
        var ids = checkList.join(',');
        $.each(datarow, function(i,obj) {
            var $tr = $('<tr></tr>');
            var checked = '';
            var typeStr = "";
            if(obj.type == '1') {
                typeStr = '自动组卷';
            }else if(obj.type == '2') {
                typeStr = '手动组卷';
            }
            $tr.append('<td>'+(pagesize*(page-1)+i+1)+'</td>');
            $tr.append('<td>'+obj.name+'</td>');
            $tr.append('<td>'+obj.length+'</td>');
            $tr.append('<td>'+typeStr+'</td>');
            $tr.append('<td>'+obj.createtime+'</td>');
            if(ids.indexOf(obj.id) > -1) {
                checked = 'checked';
            }
            $tr.append('<td><input type="checkbox" class="paper-cb" value="'+obj.id+'" '+checked+'></td>');
            $('.table-div tbody').append($tr);
        });
    }else{
        var $tr = $('<tr> <td colspan="6" style="text-align: center;height: 60px;vertical-align: middle;font-size: 16px;">没有找到相关数据</td></tr>');
        $('.table-div tbody').append($tr);
    }
});

modelApp.controller('modelListCtrl', function ($scope) {
    var _this = {
        init: function() {
            //缓存页面元素
            this.cacheElements();
            //绑定事件
            this.bindEvents();
            //设置表单验证规则
            this.$form.validate(_this.getValidateOpt());
        },
        cacheElements: function() {
            this.$form = $('#exportForm');
            this.$table = $('.table-div');
        },
        bindEvents: function() {
            //表格复选框点击事件
            this.$table.on('click','.paper-cb',this.checkboxEvent);
            //科目改变事件
            this.$form.on('change','.sub-select',this.subjectChange);
            //开始导出按钮点击事件
            this.$form.on('click','.export-btn',function() {
                _this.$form.submit();
            });
        },
        checkboxEvent: function() {
            var checked = $(this).is(':checked');
            var id = $(this).val();
            if(checked) {
                checkList.push(id);
            }else {
                checkList.splice($.inArray(id,checkList),1);
            }
        },
        subjectChange: function() {
            var subjectId = $(this).val();
            MY.ajax({
                url:'/paper/pagePaper',
                type:'post',
                data:{subjectId:subjectId,checkStatus:'3',page:1}
            }).done(function(data) {
                if(data.rtnCode == '000000') {
                    $scope.$apply(function() {
                        MY.page.init('modelEntrys',data);
                    });
                }
            });
        },
        getValidateOpt: function() {
            var opt = {
                rules : {
                    'paperName' : {
                        required : true
                    }
                },
                messages : {
                    'paperName' : {
                        required : "考试名称不能为空"
                    }
                },
                submitHandler : _this.submitFunc
            };
            return opt;
        },
        submitFunc: function() {
            var paperName = $('#paperName').val();
            if(!checkList || checkList.length == 0) {
                MY.alert('请选择要导出的试卷','warning');
                return;
            }
            var ids = '';
            $.each(checkList,function(i,str) {
                ids += "'" + str + "',";
            });
            MY.ajax({
                type:'POST',
                url:'/paper/exportPaper',
                data:{paperName:paperName,ids:ids.substring(0,ids.length-1)}
            }).done(function(data) {
                if(data.rtnCode == '000000') {
                    window.open(data.bizData.paperPkgPath)
                }
            });
        }
    };
    _this.init();
});