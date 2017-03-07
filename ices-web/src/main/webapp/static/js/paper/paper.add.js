/**
 * Created by cyj on 2016/7/8.
 */
var modelApp = angular.module('modelApp', []);
modelApp.filter(
    'to_trusted', ['$sce', function ($sce) {
        return function (text) {
            return $sce.trustAsHtml(text);
        }
    }]
);
var checkboxList = [];
//初始化分页插件
MY.page.render(modelApp,function(data) {
    var $panel = $('#viewModal .question-list');
    $panel.empty();
    if(data && data.bizData && data.bizData.rows
        && data.bizData.rows.length > 0) {
        var dataList = data.bizData.rows;
        var page = data.bizData.page;
        var pagesize = data.bizData.pagesize;
        $.each(dataList, function(i,obj) {
            var $temp = $('#questionTempPanel').clone();
            var num = ((page-1)*pagesize+(i+1));
            $temp.find('.question-num').text(num);
            $temp.find('.question-type').text(obj.questionTypeName);
            $temp.find('.question-dif').text(obj.difficultyStr);
            $temp.find('.question-keyword').text(obj.keyword);
            $temp.find('.question-res').text(obj.resource);
            $temp.find('.create-time').text(obj.createtime?obj.createtime.substring(0,obj.createtime.length-2):'');
            var $content = $('<div style="margin-left: 8px;">'+obj.name+'</div>');
            $temp.find('.question-content').append('<div>【题文】</div>');
            $temp.find('.question-content').append($content);
            $temp.find('.question-checkbox').attr('id',obj.id);
            $temp.find('.question-checkbox').attr('value',obj.name);
            $temp.data('id',obj.id);
            $temp.data('status',obj.checkStatus);
            $temp.data('subid',obj.subjectId);
            $temp.removeAttr('id');
            $temp.show();
            $panel.append($temp);
        });
        //根据对分页前后checkbox勾选状态存储的已勾选数据进行分页请求后对用户已选项进行勾选；
        var checkval = $('#viewModal .modal-body').find('.question-checkbox');
        for(var i=0;i<checkval.length;i++){
                for(var j=0;j<checkboxList.length;j++){
                    if(checkval[i].id == checkboxList[j].sid){
                        $('#viewModal .modal-body').find('.question-checkbox:eq('+i+')').attr('checked',true);
                    }
                }
        }
    }else {
        $panel.append('<div class="no-data">没有找到相关数据~</div>');
    }
});
modelApp.controller('modelListCtrl', function ($scope,$rootScope) {
    //返回
    $scope.goto = function(){
        window.location.href = "/paper/list";
    }
    //设置验证规则
    $('#stepFirst').validate(getOpt());
    sessionStorage.removeItem('questionType');
    $('.step-next').find('.next-elder .next-tree .third-list:eq(0)').toggle();
    //绑定滚动条监听事件 滚动时操作按钮固定到顶部
    MY.positionTop();

    //渲染select2
    $("#knowledgeId").select2();


    //选题列表选中试卷，针对目前分页每页请求保留页面跳转前选择的题目
    $('#viewModal').on('click','.btn-next',function(){//分页插件‘下一页’
        chenkque();
    })
    $('#viewModal').on('click','.btn-prev',function(){//分页插件‘上一页’
        chenkque();
    })
    $('#viewModal').on('click','.btn-first',function(){//分页插件‘第一页’
        chenkque();
    })
    $('#viewModal').on('click','.btn-go',function(){//分页插件‘指定页’
        chenkque();
    })
    $('#viewModal').on('click','.btn-last',function(){//分页插件‘最后一页’
        chenkque();
    })
    //动态更新分页前后checkbox勾选事件
    function chenkque(){
        var checkval = $('#viewModal .modal-body').find('.question-checkbox');
        for(var i=0;i<checkval.length;i++){
            if(checkval[i].checked){
                var has = true;
                for(var j=0;j<checkboxList.length;j++){
                    if(checkval[i].id == checkboxList[j].sid){
                        has = false;
                        continue;
                    }
                }
                if(has){
                    var json = {qname:checkval[i].value,sid:checkval[i].id};
                    checkboxList.push(json);
                }
            }else{
                for(var j=0;j<checkboxList.length;j++){
                    if(checkval[i].id == checkboxList[j].sid){
                        checkboxList.splice(j,1);
                    }
                }
            }
        }
    }



    //自动选题
    $('.step-first').on('click','.next-elder .que-self',function(){
        $('.newFather').removeClass('newFather');
        var typeId = $(this).closest('.next-tree').find('select').val();
        var quenum = $(this).closest('.next-tree').find('.queNum').val();
        var $panel = $(this).closest('.next-tree').find('.tree-pr');
        var obj = $('#knowledgeId').val();
        if(!typeId){
            MY.alert("请设置题型!(注:知识点,题型,题型数量设置好方可自动选题成功!),",'warning');
            return;
        }
        if(!quenum){
            MY.alert("请设置题型数量!(注:知识点,题型,题型数量设置好方可自动选题成功!)",'warning');
            return;
        }
        if(!obj){
            MY.alert("请设置知识点!注:知识点,题型,题型数量设置好方可自动选题成功!)",'warning');
            return;
        }
        var knowledgeString = obj.join(',');
        $(this).closest('.next-tree').addClass('newFather');
        if($('.newFather .tree-pr').find('.tree-sec').length!==0){
            MY.confirm("若进行自动选题将对当前题型存在题目进行替换，是否继续？)",'是否继续自动选题',function(index){
                $('.newFather').find('.tree-pr .tree-sec').remove();
                MY.ajax({
                    url:'/paper/listRandomQuestion',
                    type:'post',
                    data:{questionTypeId:typeId,number:quenum,knowIdList:knowledgeString}
                }).done(function(data) {
                    if (data.rtnCode == "000000") {
                        for(var key in data.bizData){
                            var data = data.bizData[key];
                            for(var i=0;i<data.length;i++){
                                var quesstr = JSON.parse(data[i].questionItemStr);
                                var hasStr = JSON.parse(data[i].questionType);
                                $panel.append('<div class="tree-sec" data-typeId="'+data[i].questionTypeId+'"><div class="sec-ft">'+(i+1)+'</div>' +
                                '<div class="sec-se" id="'+data[i].questionId+'"><em style="margin-left:0;">'+data[i].questionContent+'</em></div>' +
                                '<div class="sec-th"><img src="/static/images/del.png"></div><br style="clear:both;" /><div class="third-list newDiv">');
                                if(hasStr.code=='101'||hasStr.code=='103'){
                                    for(var j=0;j<quesstr.length;j++){
                                        $panel.find('.newDiv').append('<div class="third-se"><div class="question-cell"><input name="'+data[i].questionId+'"  type="radio" value="1"/></div><div class="question-cell">'
                                        +quesstr[j].content+'</div></div></div></div>');
                                    }
                                }
                                if(hasStr.code=='102'){
                                    for(var j=0;j<quesstr.length;j++){
                                        $panel.find('.newDiv').append('<div class="third-se"><div class="question-cell"><input name="'+data[i].questionId+'"  type="checkbox" value="1"/></div><div class="question-cell">'
                                        +quesstr[j].content+'</div></div></div></div>');
                                    }
                                }
                                if(hasStr.code == '104'){
                                    $('.newDiv').remove();
                                }
                                $('.newDiv').removeClass('newDiv');
                            }
                        }
                        updateQuesStat();
                    }else if(data.rtnCode == "030001"){
                        MY.alert('题目数量设置过大，请根据当前存在的试题数量进行合理自动选题！','error');
                    }
                });
            });
        }else{
            $('.newFather').find('.tree-pr .tree-sec').remove();
            MY.ajax({
                url:'/paper/listRandomQuestion',
                type:'post',
                data:{questionTypeId:typeId,number:quenum,knowIdList:knowledgeString}
            }).done(function(data) {
                if (data.rtnCode == "000000") {
                    for(var key in data.bizData){
                        var data = data.bizData[key];
                        for(var i=0;i<data.length;i++){
                            var quesstr = JSON.parse(data[i].questionItemStr);
                            var hasStr = JSON.parse(data[i].questionType);
                            $panel.append('<div class="tree-sec" data-typeId="'+data[i].questionTypeId+'"><div class="sec-ft">'+(i+1)+'</div>' +
                            '<div class="sec-se" id="'+data[i].questionId+'"><em style="margin-left:0;">'+data[i].questionContent+'</em></div>' +
                            '<div class="sec-th"><img src="/static/images/del.png"></div><br style="clear:both;" /><div class="third-list newDiv">');
                            if(hasStr.code=='101'||hasStr.code=='103'){
                                for(var j=0;j<quesstr.length;j++){
                                    $panel.find('.newDiv').append('<div class="third-se"><div class="question-cell"><input style="margin:0;" name="'+data[i].questionId+'"  type="radio" value="1"/></div><div class="question-cell">'
                                    +quesstr[j].content+'</div></div></div></div>');
                                }
                            }
                            if(hasStr.code=='102'){
                                for(var j=0;j<quesstr.length;j++){
                                    $panel.find('.newDiv').append('<div class="third-se"><div class="question-cell"><input style="margin:0;" name="'+data[i].questionId+'"  type="checkbox" value="1"/></div><div class="question-cell">'
                                    +quesstr[j].content+'</div></div></div></div>');
                                }
                            }
                            if(hasStr.code == '104'){
                                $('.newDiv').remove();
                            }
                            $('.newDiv').removeClass('newDiv');
                        }
                    }
                    updateQuesStat();
                }else if(data.rtnCode == "030001"){
                    MY.alert('题目数量设置过大，请根据当前存在的试题数量进行合理自动选题！','error');
                }
            });
        }

    });
    //手动添加试题实现；
    $scope.handGroup = function(){
        chenkque();
        var quesseclist = $('.next-elder').find('.sec-se');
        var count = checkboxList;
        var $quesNum = parseInt($('.addReapt').closest('.next-tree').find('input:eq(0)').val());
        var $nowQues = $('.addReapt').find('.tree-sec').length;
        var hasStatus = false;
        var thequesnum;
        for(var i=0;i<count.length;i++){
            for(var j=0;j<quesseclist.length;j++){
                var $quessec = $('.next-elder').find('.sec-se:eq('+j+')');
                if(count[i].sid == $quessec.attr('id')){
                    hasStatus = true;
                    thequesnum = count[i].qname;
                    continue;
                }
            }
            if(!hasStatus){
                continue;
            }
        }
        if(hasStatus){
            MY.alert('"'+thequesnum+'"此题目已存在不允许重复选择,请重新选题！','warning');
            return;
        }else if((count.length+$nowQues)>$quesNum){
            MY.alert("所选题目数量必需小于等于设置题目数量一致！",'warning');
            return;
        }else if(checkboxList.length!==0){
            $('#viewModal').modal('hide');
            newQuestionTree(checkboxList,$nowQues);
            $('.addReapt').removeClass('addReapt');
        }else{
            $('#alertModal').modal();
            setTimeout(function(){
                $('#alertModal').modal('hide');
            },2000);
        }

    }
    //点击添加题目后调用该方法再当前大题区域添加选中的小题信息；
    function newQuestionTree(data,length){
       for(var i=0;i<data.length;i++){
            $('.addReapt').append('<div class="tree-sec"><div class="sec-ft">'+(length+i+1)+'</div>' +
                                  '<div class="sec-se" id="'+data[i].sid+'"><em style="margin-left:0;">'+data[i].qname+'</em></div>' +
                                  '<div class="sec-th"><img src="/static/images/del.png"></div><br style="clear:both;" /></div>');
        }
        $('.addReapt').removeClass('addReapt');
        updateQuesStat();
    }
    //添加试题事件
    $('.step-first').on('click','.next-tree .add-ques',function(){
        checkboxList = [];
        $('.addReapt').removeClass('addReapt');
        var typeId = $(this).closest('.next-tree').find('select').val();
        var typeText = $(this).closest('.next-tree').find('select').find('option:selected').text();
        var subjectText = $('#subjectSelect').find('option:selected').text();
        $(this).closest('.next-tree').find('.tree-pr').addClass('addReapt');
        var quenum = $(this).closest('.next-tree').find('.queNum').val();
        var quescore = $(this).closest('.next-tree').find('.queScore').val();
        var eles = $('#knowledgeId').find('option:selected');
        var knowledges = [];
        $.each(eles, function() {
            knowledges.push($(this).data('obj'));
        });
        if(!knowledges || knowledges.length == 0){
            MY.alert("请设置知识点!注:知识点,题型,题型数量,每小题分数设置好方可自动选题成功!)",'warning');
            return;
        }
        if(!typeId){
            MY.alert("请设置题型!(注:知识点,题型,题型数量,每小题分数设置好方可自动选题成功!),",'warning');
            return;
        }
        if(!quenum){
            MY.alert("请设置题型数量!(注:知识点,题型,题型数量,每小题分数设置好方可自动选题成功!)",'warning');
            return;
        }
        if(!quescore){
            MY.alert("请设置每小题分数!注:知识点,题型,题型数量,每小题分数设置好方可自动选题成功!)",'warning');
            return;
        }
        MY.ajax({
            url:'/question/pageQuestion',
            type:'post',
            data:{page:1,subjectId:$scope.subjectId,questionTypeId:typeId,checkStatus:2,knowId:knowledges[0].id}
        }).done(function(data) {
            $scope.$apply(function() {
                if(data.rtnCode == '000000') {
                    MY.page.init('modelEntrys',data);
                    $scope.modalSubjectId = $scope.subjectId;
                    $scope.modalTypeId = typeId;
                    $scope.modalKnowledgeId = knowledges[0].id;
                    $scope.knowledges = knowledges;
                    $('#viewModal').find('.modal-title').text(subjectText+'('+typeText+')');
                    $('#viewModal').modal();
                }
            });
        });
    });

    //添加题型设置
    $('.step-first').on('click','.tree-body .addtypes .add-ty',function(){
        MY.waitingUI();
       if(!$scope.subjectId) {
           MY.unblockUI();
           MY.alert("请先选择科目",'warning');
           return;
       }
        var $secId = $('.next-elder').find('.next-tree').length+1;
        var $secIdName =  '第'+$secId+'题： ';
        var $div = $('.section-template-panel').clone().removeClass('section-template-panel');
        $('.tree-body .next-elder').append($div);
        $('.tree-body').find('.next-elder .next-tree:last').children('em').text($secIdName);
        var quetynum = parseInt($(this).prev().find('label:eq(0)').text())+1;
        $(this).prev().find('label:eq(0)').text(quetynum);
        $(this).prev().find('label:eq(1)');
        $(this).prev().find('label:eq(2)');
        setTimeout(function(){
            MY.unblockUI();
        },100);

    })

    //删除当前题型设置
    $('.step-first').on('click','.tree-body .next-tree .del-ty',function(){
        $(this).closest('.next-tree').remove();
        var nextList = $('.next-elder').find('.next-tree');
        for(var i=0;i<nextList.length;i++){
            $('.next-elder').find('.next-tree:eq('+(i)+')').children('em').text('第'+(i+1)+'题');
        }
        var $label = parseInt($('.addtypes').find('label:eq(1)').text())-1;
        $('.addtypes').find('label:eq(1)').text($label);
        updateQuesStat();
    });

    //清除题目
    $('.step-first').on('click','.tree-body .next-tree .clean-que',function(){
        $(this).closest('.next-tree').find('.tree-pr .tree-sec').remove();
        updateQuesStat();
    });

    //删除当前试题
    $('.step-first').on('click','.tree-pr .sec-th img',function(){
        $('.delsClass').removeClass('delsClass');
        var findList = $(this).closest('.tree-pr').find('.tree-sec').length-1;
        $(this).closest('.tree-pr').addClass('delsClass');
        $(this).closest('.tree-sec').remove();
        updateQuesStat();
        for(var i=0;i<findList;i++){
            $('.delsClass').find('.tree-sec:eq('+(i)+')').children('.sec-ft').text(i+1);
        }
    })

    //点击单选或多选题的题目进行展示;
    $('.step-first').on('click','.next-tree .sec-se',function(){
        var qtype = $(this).closest('.next-tree').find('select option:selected').text();
        if(qtype=='单选题'||qtype=='多选题'||qtype=='判断题'){
            var id = $(this).attr('id');
            var qhtml = $(this).html();
            var $div =  $(this).closest('.tree-sec');
            var hasClass = $(this).closest('.tree-sec').find('.third-list').length;
            if(hasClass==0){
                MY.ajax({
                    url:'/question/getQuestionDetail',
                    type:'post',
                    data:{id:id}
                }).done(function(data) {
                    if (data.rtnCode == "000000") {
                        $div.append('<div class="third-list"></div>');
                        var list = JSON.parse(data.bizData.questionContent.questionItemStr);
                        var type = JSON.parse(data.bizData.questionContent.questionType).code;
                        var id = data.bizData.id;
                        for(var i=0;i<list.length;i++){
                            if(type=='101'||type=='103'){
                                $div.find('.third-list').append('<div class="third-se"><div class="question-cell"><input name="'+id+'"  type="radio" style="margin:0;" value="1"/></div><div class="question-cell">'+list[i].content+'</div></div>');
                            }else if(type=='102'){
                                $div.find('.third-list').append('<div class="third-se"><div class="question-cell"><input name="'+id+'"  type="checkbox" style="margin:0;" value="1"/></div><div class="question-cell">'+list[i].content+'</div></div>');
                            }
                        }
                        $div.find('.third-list').toggle();
                    }
                });
            }else{
                $div.find('.third-list').toggle();
            }
        }
    });

    //当前添加题目数量及分数，实时更新
    $('.next-elder').change(function(){
        updateQuesStat();
    });
    function updateQuesStat(){
        var $obj = $('.step-first .next-elder').find('.next-tree');
        if($('.step-first .next-elder').find('.next-tree:eq(0)').find('.tree-sec').length!==0&&$('.step-first .next-elder').find('.next-tree:eq(0)').find('input:eq(1)').val()){
            var quesnumInt = $('.step-first .next-elder').find('.next-tree:eq(0)').find('.tree-sec').length;
            var quesscoreInt = parseFloat($('.step-first .next-elder').find('.next-tree:eq(0)').find('input:eq(1)').val())*parseFloat(quesnumInt);
        }else{
            var quesnumInt = 0;
            var quesscoreInt = 0;
        }
        for(var i=1;i<$obj.length;i++){
            var $ques = $('.step-first .next-elder').find('.next-tree:eq('+(i)+')');
            if($ques.find('input:eq(0)').val()!==''){
                quesnumInt = quesnumInt+$ques.find('.tree-sec').length;
            }else{
                quesnumInt = quesnumInt+0;
            }
            if($ques.find('input:eq(1)').val()!==''&&$ques.find('.tree-sec').length!==0) {
                quesscoreInt = quesscoreInt+(parseFloat($ques.find('input:eq(1)').val())*parseFloat($ques.find('.tree-sec').length));
            }else{
                quesscoreInt = quesscoreInt+0;
            }
        }
        $('.addtypes span:eq(0)').find('label:eq(1)').text(quesnumInt);
        $('.addtypes span:eq(0)').find('label:eq(2)').text(quesscoreInt.toFixed(2));
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
    //查询题型列表
    function getQuestionType(obj){
        MY.ajax({
            url:'/questiontype/listQuestionTypeBySubjectId',
            type:'post',
            data:{subjectId:obj}
        }).done(function(data) {
            $scope.$apply(function() {
                if(data.bizData) {
                    $scope.questionType = data.bizData;
                    //缓存题型信息，用于'添加题型设置'功能;
                    sessionStorage.setItem("questionType", JSON.stringify(data.bizData));
                }
            });
        });
    };
    //根据科目id获取知识点内容
   $scope.getSubjectVal = function(){
       if($('.next-elder').find('.next-tree').length!=0){
           MY.confirm("当前试卷已存在大题，若更改科目，将对试卷下存在的大题及小题进行清空，是否继续？)",'是否更改科目',function(index){
               $('.next-elder').find('.next-tree').remove();
               $('.addtypes').find('label:eq(1)').text(0);
               if ($scope.subjectId) {
                   getQuestionType($scope.subjectId);
                   MY.ajax({
                       url: '/knowledge/listKnowledgeBySubjectId',
                       type: 'post',
                       data: {subjectId: $scope.subjectId}
                   }).done(function (data) {
                       $scope.$apply(function () {
                           if (data.bizData) {
                               $scope.knowledgeList = data.bizData;
                           }
                       });
                   });
               }
           });
       }else{
           if ($scope.subjectId) {
               getQuestionType($scope.subjectId);
               MY.ajax({
                   url: '/knowledge/listKnowledgeBySubjectId',
                   type: 'post',
                   data: {subjectId: $scope.subjectId}
               }).done(function (data) {
                   $scope.$apply(function () {
                       if (data.bizData) {
                           $scope.knowledgeList = data.bizData;
                       }
                   });
               });
           }
       }

   }


    //保存新建试卷
    $scope.addPaper = function() {
        //提交表单
        $('#stepFirst').submit();

    }
    function getOpt() {
        var opt = {
            rules : {
                'paperName' : {//试卷名称
                    required : true
                },
                'subjectId' : {//所属科目
                    required : true
                },
                'paperType' : {//组卷方式
                    required : true
                },
                'scores' : {//卷面总分
                    required : true,
                    isZeroThan:function(){
                        return $scope.totalScore;
                    },
                    isChangeNum: function(){
                        var value = $scope.totalScore;
                        return value;
                    }
                },
                'passScroe' : {//及格分数
                    required : true,
                    isZeroThan:function(){
                        return $scope.passScore;
                    },
                    isChangeNum: function(){
                        var value = $scope.passScore;
                        return value;
                    }
                },
                'paperTimes' : {//考试时长
                    required : true,
                    isZeroThan:function(){
                        return $scope.paperTimes;
                    },
                    isChangeNum: function(){
                        var value = $scope.paperTimes;
                        return value;
                    }
                },
                'knowledgeId' : {//知识点分类
                    required : true
                },
                'questionType' : {//题型设置
                    required : true
                },
                'questionNum' : {//题目数量
                    required : true,
                    digits:true
                },
                'questionScore' : {//题目分数
                    required : true,
                    isChangeNum: function(){
                        var list = $('.step-first .next-elder').find('.next-tree');
                        for(var i=0;i<list.length;i++){
                            var value = $('.step-first .next-elder').find('.next-tree:eq('+i+')').find('.queScore').val;
                        }
                        return value;
                    }
                }
            },
            messages : {
                'paperName' : {
                    required : "试卷名称不能为空"
                },
                'subjectId' : {//所属科目
                    required : "请选择所属科目"
                },
                'paperType' : {//组卷方式
                    required :"请选择组卷方式"
                },
                'scores' : {
                    required : "卷面总分不能为空",
                    isChangeNum: "小数位数最多只能为两位",
                    isZeroThan:"卷面总分必需大于0"
                },
                'passScroe' : {
                    required : "及格分数不能为空",
                    isChangeNum: "小数位数最多只能为两位",
                    isZeroThan:"及格分数必需大于0"
                },
                'paperTimes' : {
                    required : "考试用时不能为空",
                    isChangeNum: "小数位数最多只能为两位",
                    isZeroThan:"考试用时必需大于0"
                },
                'knowledgeId' : {
                    required : "请选择知识点"
                },
                'questionType' : {
                    required : "请设置题型"
                },
                'questionNum' : {
                    required : "请设置该题型数量",
                    digits: "只能输入整数"
                },
                'questionScore' : {
                    required : "请设置该题型分数",
                    isChangeNum: "小数位数最多只能为两位"
                }
            },
            submitHandler : verifyOk
        };
        return opt;
    };
    function verifyOk(){
        addPaper();
    };
    //保存新建试卷
    function addPaper(){
        var quesNumLength = $('.next-elder .queNum').length;
        var quesNums=0;
        for(var i=0;i<quesNumLength;i++){
            quesNums = parseInt($('.next-elder .queNum:eq('+i+')').val())+parseInt(quesNums);
        }
        var seclength = $('.tree-sec').length;
        if(parseInt(seclength) != parseInt(quesNums)){
            MY.alert("设置的小题数量与实际添加的小题数量不一致！",'warning');
            return;
        }
        var quesscore = $('.step-first .addtypes').find('span:eq(0)').find('label:eq(2)').text();
        if(parseFloat(quesscore)!=parseFloat($scope.totalScore).toFixed(2)){
            MY.alert("题目分数合计与试卷总分不一致！",'warning');
            return;
        }
        var has = (parseFloat(quesscore).toFixed(2))-(parseFloat($scope.passScore).toFixed(2));
        if(has<0){
            MY.alert("合格分数不能大于试卷总分！",'warning');
            return;
        }
        var json = {}
        var obj = $('#knowledgeId').val();
        var knowledgeString = obj.join(',');
        json['name'] = $scope.paperName;
        json['subjectId'] = $scope.subjectId;
        json['type'] = 2;
        json['totalScore'] = $scope.totalScore;
        json['passScore'] = $scope.passScore;
        json['length'] = $('input[name="paperTimes"]').val();
        json['knowIdStr'] = knowledgeString;
        json['questionsJson'] = JSON.stringify(addCreeList());
        MY.ajax({
            url:'/paper/insertPaper',
            type:'post',
            data:json
        }).done(function(data) {
            if (data.rtnCode == "000000") {
                MY.alert("新建试卷成功！","success",function() {
                    window.location.href = "/paper/list";
                });
            }else if(data.rtnCode == "020001"){
                MY.alert("试卷名称过长！",'error');
            }
        });
    }
    //拼接提交新增试卷时的题目信息
    function addCreeList(){
        var list = [];
        var dadLen = $('.step-first').find('.next-elder .next-tree').length;
        for(var i=0;i<dadLen;i++) {
            var klist = [];
            var $tree = '.next-elder .next-tree:eq(' + i + ')';
            var quename = $($tree).find('select option:selected').text();
            var quenum = $($tree).find('.queNum').val();
            var quescore = $($tree).find('.queScore').val();
            var remark = $($tree).find('.text-que').val();
            var kdata = $($tree).find('.tree-pr .tree-sec');
            var typeId = $($tree).find('select').val();
            for(var j=0;j<kdata.length;j++){
                var id = $($tree).find('.tree-pr .tree-sec:eq('+j+')').children('.sec-se').attr('id');
                klist.push(id);
            }
            klist = klist.join(',');
            var json = {name:quename,questionTypeId:typeId,remark:remark,count:quenum,score:quescore,questionIdStr:klist};
            list.push(json);
        }
        return list;
    }
    //初始化方法
    function init(){
        getSubject();
    };
    init();
});