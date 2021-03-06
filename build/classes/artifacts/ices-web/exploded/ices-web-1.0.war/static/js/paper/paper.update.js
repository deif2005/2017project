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
            $temp.find('.question-res').text(obj.resource);
            $temp.find('.create-time').text(obj.createtime?obj.createtime.substring(0,obj.createtime.length-2):'');
            $temp.find('.question-content').html('【题文】'+obj.name);
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
modelApp.controller('modelListCtrl', function ($scope) {
    //返回
    $scope.goto = function(){
        window.location.href = "/paper/list";
    }
    //设置验证规则
    $('#stepFirst').validate(getOpt());
    sessionStorage.removeItem('questionType');
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
                        $div.append('<div class="third-list"><div class="third-ft">'+qhtml+'</div></div>');
                        var list = JSON.parse(data.bizData.questionContent.questionItemStr);
                        for(var i=0;i<list.length;i++){
                         $div.find('.third-list').append('<div class="third-se"><div class="question-cell"><input name="radio"  type="radio" style="margin:0;" value="1"/></div><div class="question-cell">'+list[i].content+'</div></div>');
                        }
                        $div.find('.third-list').toggle();
                    }
                });
            }else{
                $div.find('.third-list').toggle();
            }
        }
    });

    //点击选择添加题目后调用该方法再当前大题区域添加选中的小题信息；
    function newQuestionTree(data,length){
       for(var i=0;i<data.length;i++){
            $('.addReapt').append('<div class="tree-sec"><div class="sec-ft">'+(length+i+1)+'</div>' +
                                  '<div class="sec-se" id="'+data[i].sid+'"><em>'+data[i].qname+'</em></div>' +
                                  '<div class="sec-th"><img src="/static/images/del.png"></div><br style="clear:both;" /></div>');
        }
        $('.addReapt').removeClass('addReapt');
        updateQuesStat();
    }

    //当前添加题目数量及分数，实时更新
    $('.next-elder').change(function(){
        updateQuesStat();
    });
    function updateQuesStat(){
        var $obj = $('.step-first .next-elder').find('.next-tree');
        if($('.step-first .next-elder').find('.next-tree:eq(0)').find('.tree-sec').length!==0&&$('.step-first .next-elder').find('.next-tree:eq(0)').find('input:eq(1)').val()){
            var quesnumInt = $('.step-first .next-elder').find('.next-tree:eq(0)').find('.tree-sec').length;
            var quesscoreInt = parseInt($('.step-first .next-elder').find('.next-tree:eq(0)').find('input:eq(1)').val())*quesnumInt;
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
                quesscoreInt = quesscoreInt+(parseInt($ques.find('input:eq(1)').val())*$ques.find('.tree-sec').length);
            }else{
                quesscoreInt = quesscoreInt+0;
            }
        }
        $('.addtypes span:eq(0)').find('label:eq(1)').text(quesnumInt);
        $('.addtypes span:eq(0)').find('label:eq(2)').text(quesscoreInt);
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
                        layer.close(index);
                        for(var key in data.bizData){
                            var data = data.bizData[key];
                            for(var i=0;i<data.length;i++){
                                var quesstr = JSON.parse(data[i].questionItemStr);
                                var hasStr = JSON.parse(data[i].questionType);
                                $panel.append('<div class="tree-sec" data-typeId="'+data[i].questionTypeId+'"><div class="sec-ft">'+(i+1)+'</div>' +
                                '<div class="sec-se" id="'+data[i].questionId+'"><em style="margin-left:0;">'+data[i].questionContent+'</em></div>' +
                                '<div class="sec-th"><img src="/static/images/del.png"></div><br style="clear:both;" /><div class="third-list newDiv">' +
                                '<div class="third-ft">'+data[i].questionContent+'</div>');
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
                            '<div class="sec-th"><img src="/static/images/del.png"></div><br style="clear:both;" /><div class="third-list newDiv">' +
                            '<div class="third-ft">'+data[i].questionContent+'</div>');
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
        }
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
    //添加试题事件
    $('.step-first').on('click','.next-tree .add-ques',function(){
        checkboxList = [];
        $('.addReapt').removeClass('addReapt');
        var typeId = $(this).closest('.next-tree').find('select').val();
        $(this).closest('.next-tree').find('.tree-pr').addClass('addReapt');
        var quenum = $(this).closest('.next-tree').find('.queNum').val();
        var quescore = $(this).closest('.next-tree').find('.queScore').val();
        var obj = $('#knowledgeId').val();
        if(!obj){
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
            data:{page:1,subjectId:$scope.paper.subjectId,questionTypeId:typeId}
        }).done(function(data) {
            $scope.$apply(function() {
                if(data.rtnCode == '000000') {
                    MY.page.init('modelEntrys',data);
                    $scope.modalSubjectId = $scope.paper.subjectId;
                    $scope.modalTypeId = typeId;
                    $('#viewModal').modal();
                }
            });
        });
    });
    //添加题型设置
    $('.step-first').on('click','.tree-body .addtypes .add-ty',function(){
        if(!$scope.paper.subjectId) {
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

    })
    //清除题目
    $('.step-first').on('click','.tree-body .next-tree .clean-que',function(){
        $(this).closest('.next-tree').find('.tree-pr .tree-sec').remove();
        updateQuesStat();
    });
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


    //查询科目列表
    function getSubject(id){
        MY.ajax({
            url:'/subject/getSubjectById',
            type:'post',
            data:{id:id}
        }).done(function(data) {
            if(data.bizData) {
                var $option = '<option value="' + data.bizData.id + '" selected>' + data.bizData.name + '</option>'
                $('.subjectId').append($option);
                $('.subjectIds').append($option);
            }
        });
    };
    //根据科目id 获取题型信息
    $('#modalSubject').on('change',function(){
        var subjectId = $(this).val();
        getQuestionType(subjectId);
    });
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
                    $scope.modalQuestionType = data.bizData;
                    //缓存题型信息，用于'添加题型设置'功能;
                    sessionStorage.setItem("questionType", JSON.stringify(data.bizData));
                }
            });
        });
    };
    //根据科目id获取知识点内容
    function getknowdgeVal(id){
           MY.ajax({
               url:'/knowledge/listKnowledgeBySubjectId',
               type:'post',
               data:{subjectId:id}
           }).done(function(data) {
               $scope.$apply(function() {
                   if(data.bizData) {
                    $scope.knowledgeList = data.bizData;
                   }
               });
           });
       }

    //保存
    $scope.updatePaper = function() {
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
                'scores' : {//卷面总分
                    required : true,
                    isChangeNum: function(){
                        var value = $scope.paper.totalScore;
                        return value;
                    }
                },
                'passScroe' : {//及格分数
                    required : true,
                    isChangeNum: function(){
                        var value = $scope.paper.passScore;
                        return value;
                    }
                },
                'paperTimes' : {//考试时长
                    required : true,
                    isChangeNum: function(){
                        var value = $scope.paper.length;
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
                    isChangeNum: function(){
                        var list = $('.step-first .next-elder').find('.next-tree');
                        for(var i=0;i<list.length;i++){
                            var value = $('.step-first .next-elder').find('.next-tree:eq('+i+')').find('.queNum').val;
                        }
                        return value;
                    }
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
                'scores' : {
                    required : "卷面总分不能为空",
                    isChangeNum: "小数位数最多只能为两位"
                },
                'passScroe' : {
                    required : "及格分数不能为空",
                    isChangeNum: "小数位数最多只能为两位"
                },
                'paperTimes' : {
                    required : "考试用时不能为空"
                },
                'knowledgeId' : {
                    required : "请选择知识点"
                },
                'questionType' : {
                    required : "请设置题型"
                },
                'questionNum' : {
                    required : "请设置该题型数量",
                    isChangeNum: "小数位数最多只能为两位"
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
    //保存修改试卷
    function verifyOk(){
        var json = {}
        var obj = $('#knowledgeId').val();
        var knowledgeString = obj.join(',');
        json['id'] = $('.paperName label').attr('data-pid');
        json['name'] = $scope.paper.name;
        json['subjectId'] = $scope.paper.subjectId;
        json['type'] = 2;
        json['totalScore'] = $scope.paper.totalScore;
        json['passScore'] = $scope.paper.passScore;
        json['length'] = $scope.paper.length;
        json['knowIdStr'] = knowledgeString;
        json['questionsJson'] = JSON.stringify(updateCreeList());
        MY.ajax({
            url:'/paper/updatePaper',
            type:'post',
            data:json
        }).done(function(data) {
            if (data.rtnCode == "000000") {
                MY.alert("试卷修改成功！","success",function() {
                    window.location.href = '/paper/list';
                });
            }else{
                MY.alert("试卷修改失败！","error",function(index) {
                    layer.close(index)
                });
            }
        });

    };
    //拼接提交修改试卷时的题目信息
    //拼接提交新增试卷时的题目信息
    function updateCreeList(){
        var list = [];
        var dadLen = $('.step-first').find('.next-elder .next-tree').length;
        for(var i=0;i<dadLen;i++) {
            var klist = [];
            var $tree = '.next-elder .next-tree:eq(' + i + ')';
            var quename = $($tree).find('select option:selected').text();
            var quenum = $($tree).find('.queNum').val();
            var quescore = $($tree).find('.queScore').val();
            var remark = $($tree).find('.tree-first textarea').text();
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
    //初始化试卷信息
    var local = window.localStorage;
    var sesData = local.getItem('papers');
    sesData = JSON.parse(sesData);
    function read(obj){
        $scope.paper = obj;
        getknowdgeVal(obj.subjectId);
        MY.ajax({
            url:'/paper/getPaperById',
            type:'post',
            data:{id:obj.id}
        }).done(function(data) {
            if (data.rtnCode == "000000") {
                $scope.$apply(function() {
                    $scope.questionTypes = data.bizData.sectionList;
                    var knowledgeList = data.bizData.knowledgeList;
                    for (var i = 0; i < knowledgeList.length; i++) {
                        var val = knowledgeList[i].id
                        $('.knowledgeSel').val(val).trigger("change");
                    }
                    var datalist=[];
                    var qMap = data.bizData.questionMap;
                    var sList = data.bizData.sectionList;
                    var selectList = [];
                    var count = 0;
                    var scorelength = 0;
                for(var key in qMap){
                    for(var obj in sList){
                        if(sList[obj].sort == key){
                        var cList = [];
                            for(var i=0;i<qMap[key].length;i++){
                                var aJson = {questionId:qMap[key][i].id,questionContent:qMap[key][i].name};
                                cList.push(aJson);
                            }

                        var json = {questionTypeId:sList[obj].questionTypeId,id:sList[obj].id,name:sList[obj].name,chapterId:sList[obj].sort,score:sList[obj].score,count:qMap[key].length,questionMap:cList,remark:sList[obj].remark};
                        datalist.push(json);
                            count=count+cList.length;
                            scorelength = scorelength+(qMap[key].length*parseFloat(sList[obj].score));
                        }
                    }
                }
                    var jsons = {typeNum:sList.length,score:scorelength,quesNum:count};
                    selectList.push(jsons);
                    $scope.paperData = datalist;
                    $scope.questionCount = selectList;
                });
            }
        });

    }
    //初始化方法
    function init(){
        console.log(sesData);
        getSubject(sesData.subjectId);
        getQuestionType(sesData.subjectId);
        read(sesData);
        $('#modalSubject').val(sesData.subjectId);
    };
    init();
});