/**
 * Created by cyj on 2016/7/19.
 */
var modelApp = angular.module('modelApp', []);
modelApp.filter(
    'to_trusted', ['$sce', function ($sce) {
        return function (text) {
            return $sce.trustAsHtml(text);
        }
    }]
);
//过滤类型
modelApp.filter('getChar', function () {
    return function (input) {

        return String.fromCharCode((65+input));
    }
});
//初始化分页插件
MY.page.render(modelApp);
modelApp.controller('modelListCtrl', function ($scope,$rootScope) {
    //绑定滚动条监听事件 滚动时操作按钮固定到顶部
    MY.positionTop();
    //初始化试卷信息
    var local = window.localStorage;
    var sesData = local.getItem('papers');
    sesData = JSON.parse(sesData);
    //根据缓存的id进行试卷信息请求
    function getPaper(obj){
        MY.ajax({
            url:'/paper/previewPaper',
            type:'post',
            data:{id:obj.id}
        }).done(function(data) {
            if (data.rtnCode == "000000") {
                $scope.$apply(function () {
                    $('.length').text(data.bizData.paper.length+' 分钟');
                    $('.totalScore').text(data.bizData.paper.totalScore);
                    $('.ses-d').attr('data-id',data.bizData.paper.id);
                    var quesnumlist = quesNum(data.bizData);
                    $scope.quesNum = quesnumlist;
                    console.log(quesnumlist);
                    if(data.bizData.paper.checkStatus == '3'){
                        $('.check-btn').val('审核取消');
                    }
                });
            }
        });
    }
    //将请求得到的试卷信息进行封装：将试卷信息，大题信息，大题信息与小题关联进行封装在一个json串；
    function quesNum(data){
        var datalist=[];
        var qMap = data.questionMap;
        var sList = data.sectionList;
        var fList = data.questionContents;
        for(var key in qMap){
            for(var obj in sList){
                if(sList[obj].sort == key){
                    var cList = [];
                    for(vkey in qMap[key]){
                        vkey = parseInt(vkey)+1;
                        var aJson = {quesnum:vkey};
                        cList.push(aJson);
                    }
                    var kList = [];
                    for(var code in fList){
                        if(fList[code].sectionQuestion ==  key){
                            for(var k=0;k<qMap[key].length;k++){
                                if(fList[code].questionContent == qMap[key][k].name){
                                    var quesId = qMap[key][k].id;
                                }
                            }
                            var name = fList[code].questionContent;
                            var quesItemList = JSON.parse(fList[code].questionItemStr);
                            var quesType = JSON.parse(fList[code].questionType);
                            var questionAnswerStr = JSON.parse(fList[code].questionAnswerStr);
                            var json = {name:name,quesItemList:quesItemList,quesId:quesId};
                            json.type = quesType.code;
                            json.answers = questionAnswerStr;
                            kList.push(json);
                        }
                    }
                    var json = {id:sList[obj].id,name:sList[obj].name,chapterId:sList[obj].sort,score:sList[obj].score,count:sList[obj].count,questionNum:cList,questionContents:kList};
                    datalist.push(json);
                }
            }
        }
        return datalist;
    }
    //初始化方法
   function init(){
       getPaper(sesData)
   }
    init()
    //审核确认功能
    $scope.check = function(){
        var id = $('.ses-d').attr('data-id');
        if($('.check-btn').val() == '审核取消'){
            var status = 2;
            var alerttext = '是否取消该试卷审核通过状态，重新审核？'
        }else{
            var status = 3;
            var alerttext = '确认该试卷审核通过吗？'
        }
        MY.confirm(alerttext,'提示',function(index) {
            layer.close(index);
            MY.ajax({
                url:'/paper/updatePaperCheckStatus',
                type:'post',
                data:{id:id,checkStatus:status}
            }).done(function(data) {
                if (data.rtnCode == "000000") {
                        window.location.href = "/paper/list";
                }
        })
        });
    }
    //返回
    $scope.getBack = function(){
        window.location.href = "/paper/list";
    }
    //题目显示隐藏
    $('.preView').on('click','.tree-list .fud-list .handle',function(){
        var hasClass = $(this).hasClass('extend');
        if(hasClass){
            $(this).removeClass('extend').addClass('empty');
            $(this).closest('.fud-list').next('.question-list').toggle();
        }else{
            $(this).removeClass('empty').addClass('extend');
            $(this).closest('.fud-list').next('.question-list').toggle();
        }
    })
});
