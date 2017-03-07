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
    var url = window.location.href;
    if(url.search('#')!=-1){
        window.location.href = '/paper/previewPaper';
    }
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
                        for(var ckey in fList){
                            if(fList[ckey].questionId == qMap[key][vkey-1].id){
                                var code = JSON.parse(fList[ckey].questionType).code;
                                var num = parseInt(ckey)+1;
                            }
                        }
                        var aJson = {quesnum:num,id:qMap[key][vkey-1].id,code:code};
                        cList.push(aJson);
                    }
                    var kList = [];
                    for(var code in fList){
                        if(fList[code].sectionQuestion ==  key){
                            for(var k=0;k<qMap[key].length;k++){
                                if(fList[code].questionId == qMap[key][k].id){
                                    var quesId = qMap[key][k].id;
                                }
                            }
                            var name = fList[code].questionContent;
                            var quesItemList = JSON.parse(fList[code].questionItemStr);
                            var quesType = JSON.parse(fList[code].questionType);
                            var questionAnswerStr = JSON.parse(fList[code].questionAnswerStr);
                            var json = {num:parseInt(code)+1,name:name,quesItemList:quesItemList,quesId:quesId};
                            json.type = quesType.code;
                            json.answers = questionAnswerStr;
                            kList.push(json);
                        }
                    }
                    var json = {id:sList[obj].id,name:sList[obj].name,chapterId:sList[obj].sort,score:sList[obj].score,
                        count:sList[obj].count,remark:sList[obj].remark,questionNum:cList,questionContents:kList};
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
    });

    //点击小题号定位
    $('.preView').on('click','.child-key',function(){
        $('.orien-tap').removeClass('orien-tap');
        var id = $(this).attr('name');
        var code = $(this).attr('qcode')+id;
        $('.ques-lg').find('div[data-id="'+id+'"]').closest('.chd-tab').addClass('orien-tap');
        location.hash = code;
        var top =  parseFloat($('.ques-lg').find('div[data-id="'+id+'"]').closest('.chd-tab').offset().top)-120;
        $('html').scrollTop(top);
      $('body').scrollTop(top);//兼容谷歌
        setTimeout(function(){ $('.orien-tap').removeClass('orien-tap'); },3000);
    })
    //随屏幕大小切换div大小
    var height = $(window).height()-200;
    var height2 = $(window).height()-120;
    $('.qnum-div').attr('style','min-height:'+height+'px;')
    $('.ques-div').attr('style','height:'+height2+'px;overflow-y:scroll;')
});
