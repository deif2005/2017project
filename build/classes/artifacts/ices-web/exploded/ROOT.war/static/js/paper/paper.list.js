/**
 * Created by cyj on 2016/7/8.
 */
var modelApp = angular.module('modelApp', []);
//过滤状态
modelApp.filter('paperStatus', function () {
    return function (input) {
        if (input == 1)
            input = "未完成";
        else if (input == 2)
            input = "待检查";
        else if (input == 3)
            input = "已检查";
        return input;
    }
});
//过滤类型
modelApp.filter('getPaperTypeStr', function () {
    return function (input) {
        if (input == 1) {
            input = "自动组卷";
        }else if (input == 2) {
            input = "手动组卷";
        }
        return input;
    }
});
//初始化分页插件
MY.page.render(modelApp);
modelApp.controller('modelListCtrl', function ($scope,$rootScope) {

    //预览试卷跳转
    $scope.preview = function(obj){
        var local = window.localStorage;
        local.clear();
        var data=JSON.stringify(obj);
        local.setItem("papers",data);
        if(obj.checkStatus == 1){
            MY.alert("该试卷未完成，请继续完成，否则无法进行预览！","error",function() {
                window.location.href = '/paper/updatePaper';
            });
        }else{
            window.location.href = '/paper/previewPaper';
        }
    }
    //修改试卷跳转
    $scope.update = function(obj){
        var local = window.localStorage;
        local.clear();
        var data=JSON.stringify(obj);
        local.setItem("papers",data);
        window.location.href = '/paper/updatePaper';
    }
    //新增试卷跳转
    $scope.addPaper = function(){
        window.location.href = '/paper/addPaper';
    }
    init();
    function init(){
        getSubject()
    }
    //查询科目列表
    function getSubject(){
        MY.ajax({
            url:'/subject/listSubject',
            type:'post'
        }).done(function(data) {
            $scope.$apply(function() {
                if(data.bizData) {
                    $scope.subjectList = data.bizData;
                }
            });
        });
    };
    //科目变动方法
    $('.subChange').change(function(){
        var subId = $('.subChange').val();
        subChange(subId);
    })
   function subChange(id) {
        MY.ajax({
            url:'/paper/pagePaper',
            type:'post',
            data:{subjectId:id,page:1}
        }).done(function(data) {
            if(data.rtnCode == '000000') {
                $scope.$apply(function() {
                    MY.page.init('modelEntrys',data);
                });
            }
        });
    }
    //删除试卷
    $scope.delPaper = function(obj){
        var json = {};
        json['id'] = obj.id;
        json['status'] = 2;
        MY.confirm('确定删除该试卷吗？','提示',function() {
        MY.ajax({
            url:'/paper/deletePaperById',
            type:'post',
            data:json
        }).done(function(data) {
            if(data.rtnCode == '000000') {
                window.location.href = '/paper/list';
            }
        });
        });
    }

    //复制试卷
    $scope.copyPaper = function(obj){
        //设置验证规则
        $('#copyForm').validate(CopyPaperOpt());
        var id=obj.id;
        var paperName = obj.name;
        $('#copyPaperModal').find('input[name="copyPaperId"]').val(id);
        $('#copyPaperModal').find('input[name="copyPaperName"]').val(paperName);
        $('#copyPaperModal').modal();
    }
    $scope.saveCopyPaper = function(){
        //提交表单
        $('#copyForm').submit();

    }
    function CopyPaperOpt() {
        var opt = {
            rules : {
                'copyPaperName' : {//复制试卷名称
                    required : true
                }
            },
            messages : {
                'copyPaperName' : {
                    required : "复制试卷名称不能为空"
                }
            },
            submitHandler : verifyCopyOk
        };
        return opt;
    };
    function verifyCopyOk(){
        var paperId = $('#copyPaperModal').find('input[name="copyPaperId"]').val();
        var paperName = $('#copyPaperModal').find('input[name="copyPaperName"]').val();
        MY.ajax({
            url: '/paper/copyPaper',
            type: 'post',
            data: {paperId: paperId,paperName:paperName}
        }).done(function (data) {
            if (data.rtnCode == '000000') {
                window.location.href = '/paper/list';
            }
        });
    };
    //修改试卷，根据ID查询试卷信息
   $scope.updatePaper = function(obj){
        var id=obj.id;
        MY.ajax({
            url:'/paper/getPaperById',
            type:'post',
            data:{id:id}
        }).done(function(data) {
            $scope.$apply(function() {

            });
        });
    };

});
