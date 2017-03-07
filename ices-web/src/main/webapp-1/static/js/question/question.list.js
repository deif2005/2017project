var modelApp = angular.module('modelApp', []);
modelApp.filter(
	'to_trusted', ['$sce', function ($sce) {
		return function (text) {
			return $sce.trustAsHtml(text);
		}
	}]
);
modelApp.filter('checkStatusStr', function() {
	return function(input) {
		var str = "";
		if(input == '1') {
			str = '未检查';
		}else if(input == '2') {
			str = '已检查';
		}
		return str;
	};
});
//初始化分页插件
MY.page.render(modelApp,function(data) {
	var $panel = $('.question-list');
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
			$temp.data('id',obj.id);
			$temp.data('status',obj.checkStatus);
			$temp.data('subid',obj.subjectId);
			$temp.removeAttr('id');
			if(obj.checkStatus == '1') {
				$temp.find('.preview-opt').text('审核');
			}else if(obj.checkStatus == '2') {
				$temp.find('.preview-opt').text('预览');
			}
			$temp.show();
			$panel.append($temp);
		});
	}else {
		$panel.append('<div class="no-data">没有找到相关数据~</div>');
	}

});

modelApp.controller('modelListCtrl', function ($scope) {
	$(function() {
		var _this = {
			init: function() {
				//缓存页面元素
				this.cacheElements();
				//绑定元素事件
				this.bindEvents();
			},
			cacheElements: function() {
				this.$table = $('.table-div');
				this.$modal = $('#uploadResourceModal');
				this.$form = $('#searchForm');
			},
			bindEvents: function() {
				this.name = "temp-name";
				//新增试题按钮事件
				$scope.addQuestion = function() {
					var subjectId = $('#searchForm').find('.subject-select').val();
					var questionTypeId = $('#searchForm').find('.type-select').val();
					window.location.href = '/question/addQuestion?subjectId='+subjectId+'&questionTypeId='+questionTypeId;
				}
				//导入试题资源按钮事件
				$scope.uploadResource = this.showUploadModal;

				//导入资源文件
				$scope.uploadFile = this.uploadFile;

				//文件改变事件
				this.$modal.on('change','#fileId',this.fileChange);

				//删除试题事件
				this.$table.on('click','.delete-opt',this.deleteQuestion);

				//预览试题事件
				this.$table.on('click','.preview-opt',this.previewQuestion);

				//预览试题事件
				this.$table.on('click','.copy-opt',this.copyQuestion);

				//预览试题事件
				this.$table.on('click','.modify-opt',this.modifyQuestion);

				//科目改变事件
				this.$form.on('change','.subject-select',this.subjectChange);

				//题型改变事件
				this.$form.on('change','.type-select',this.typeChange);

				//审核状态改变事件
				this.$form.on('change','.status-select',this.statusChange);

				//新增试题按钮事件
				$scope.updateCheckStatus = this.updateCheckStatus;

			},
			showUploadModal: function() {
				MY.ajax({
					url:'/subject/listSubject',
					type:'post'
				}).done(function(data) {
					$scope.$apply(function() {
						if(data.rtnCode == '000000') {
							$('.file-error').hide();
							$scope.subjectList = data.bizData;
							_this.$modal.modal();
						}
					});
				});
			},
			uploadFile: function() {
				$('#uploadForm').ajaxSubmit({
					url:'/question/uploadResource',
					cache:false,
					dataType:'json',
					beforeSubmit: function() {
						MY.waitingUI();
					},
					success: function(data) {
						MY.unblockUI();
						if(data.rtnCode == '000000') {
							MY.alert('上传成功','success');
						}else if(data.rtnCode == '000002') {
							MY.alert('上传文件格式错误','error');
						}else if(data.rtnCode == '000003') {
							MY.alert('上传文件为空','error');
						}else if(data.rtnCode == '000004') {
							MY.alert('文件格式有误，仅支持一下格式:'+data.msg,'error');
						}else if(data.rtnCode == '999999') {
							MY.alert('上传试题资源失败，请检查资源包是否正确.','error');
						}
					}
				});
			},
			fileChange: function() {
				var file =this.files[0];
				if (file) {
					var fileSize = 0;
					if (file.size > 1024 * 1024) {
						fileSize = (Math.round(file.size * 100 / (1024 * 1024)) / 100).toString() + 'MB';
					}else {
						fileSize = (Math.round(file.size * 100 / 1024) / 100).toString() + 'KB';
					}
					console.log(file.name);
					console.log(fileSize);
					console.log(file);
					if(file.name.substring(file.name.lastIndexOf('.')+1,file.name.length) != 'zip') {
						$('.file-error').show();
						$('#uploadFileBtn').prop('disabled',true);
					}else {
						$('.file-error').hide();
						$('#uploadFileBtn').prop('disabled',false);
					}
				}
			},
			//删除题目函数
			deleteQuestion: function() {
				var $this = $(this);
				MY.confirm('确定删除该题目吗？','提示',function() {
					var id = $this.closest('.question-unit').data('id');
					MY.ajax({
						url:'/question/delQuestionDetailByQuestionId',
						type:'post',
						data:{id:id}
					}).done(function(data) {
						if(data.rtnCode == '000000') {
							window.location.href = '/question/list';
						}
					});
				});
			},
			subjectChange: function() {
				var subjectId = $(this).val();
				var checkStatus = $('.status-select').val();
				var ajax1 = MY.ajax({
					url:'/question/pageQuestion',
					type:'post',
					data:{subjectId:subjectId,checkStatus:checkStatus,page:1}
				});
				var ajax2 = MY.ajax({
					url:'/questiontype/listQuestionTypeBySubjectId',
					type:'post',
					data:{subjectId:subjectId}
				});

				$.when(ajax1,ajax2).done(function(data1,data2) {
					if(data1[0].rtnCode == '000000' && data2[0].rtnCode == '000000') {
						$scope.$apply(function() {
							MY.page.init('modelEntrys',data1[0]);
							$scope.questionType = data2[0].bizData;
						});
					}
				});
			},
			typeChange: function() {
				var typeId = $(this).val();
				var subjectId = $('.subject-select').val();
				var checkStatus = $('.status-select').val();
				MY.ajax({
					url:'/question/pageQuestion',
					type:'post',
					data:{subjectId:subjectId,questionTypeId:typeId,checkStatus:checkStatus,page:1}
				}).done(function(data) {
					MY.page.init('modelEntrys',data);
				});
			},
			statusChange: function() {
				var typeId = $('.type-select').val();
				var subjectId = $('.subject-select').val();
				var checkStatus = $(this).val();
				MY.ajax({
					url:'/question/pageQuestion',
					type:'post',
					data:{subjectId:subjectId,questionTypeId:typeId,checkStatus:checkStatus,page:1}
				}).done(function(data) {
					MY.page.init('modelEntrys',data);
				});
			},
			updateCheckStatus: function(type) {
				var questionId = $('#questionId').val();
				MY.ajax({
					url:'/question/updateQuestionCheckStatus',
					type:'post',
					data:{id:questionId,type:type}
				}).done(function(data) {
					if(data.rtnCode == '000000') {
						$('#viewModal').modal('hide');
						window.location.reload();
					}
				});
			},
			previewQuestion: function() {
				var id = $(this).closest('.question-unit').data('id');
				var checkStatus = $(this).closest('.question-unit').data('status');
				MY.ajax({
					url:'/question/getQuestionDetail',
					type:'post',
					data:{id:id}
				}).done(function(data) {
					if(data.rtnCode == '000000') {
						$scope.$apply(function() {
							$scope.checkStatus = checkStatus;
							$scope.questionId = id;
						});
						_this.showPreviewModal(data.bizData);
					}
				});
			},
			showPreviewModal: function(data) {
				var contentObj = data.questionContent;
				var answers = $.parseJSON(contentObj.questionAnswerStr);
				var options = $.parseJSON(contentObj.questionItemStr);
				var type = $.parseJSON(contentObj.questiontype).code;
				var content = data.name;
				var remark = contentObj.questionanAlysis;
				var answerStr = [];
				var $body = $('#viewModal').find('.modal-body');
				$body.empty();
				//设置题目内容到预览面板
				$body.append('<div class="qes-content">'+content+'</div>');
				var char = 65;
				$.each(options, function(i,obj) {
					char = String.fromCharCode((65+i));
					var itype = '';
					if(type == '101' || type == '103') {
						itype = 'radio';
					}else if(type == '102') {
						itype = 'checkbox';
					}
					//选择题收集标答
					$.each(answers, function(i,answer) {
						if(type == '101' || type == '102' || type == '103') {
							if(obj.code == answer) {
								answerStr.push(char);
							}
						}
					});
					//拼接试题选项（因为选项中带有<p></p>标签所以要特殊的样式处理）
					var $option = $('<div class="question-row" style="margin-left: 6px;"></div>');
					var $rb = $('<div class="question-cell"><input type="'+itype+'" name="'+_this.name+'"></div>');
					var $char = $('<div class="question-cell"><label>'+char+'.</label></div>');
					var $content = $('<div class="question-cell">'+obj.content+'</div>');
					$option.append($rb);
					$option.append($char);
					$option.append($content);
					$body.append($option);
				});
				//填空题收集标答
				$.each(answers, function(i,answer) {
					 if(type == '104'){
						answerStr.push(answer);
					}
				});
				$body.append('<div class="qes-answer">标准答案：'+answerStr.join(",")+'</div>');
				//拼接解析（因为解析中带有<p></p>标签所以要特殊的样式处理）
				var $remark = $('<div class="question-row" style="margin-top: 20px;"></div>');
				var $label = $('<div class="question-cell" style="width: 73px;">试题解析：</div>');
				var $content = $('<div class="question-cell">'+remark+'</div>');
				$remark.append($label);
				$remark.append($content);
				$body.append($remark);
				$('#viewModal').modal();
			},
			copyQuestion: function() {
				var id = $(this).closest('.question-unit').data('id');
				var subjectId = $(this).closest('.question-unit').data('subid');
				MY.ajax({
					url:'/question/copyQuestionDetail',
					type:'post',
					data:{id:id,subjectId:subjectId}
				}).done(function(data) {
					if(data.rtnCode == '000000') {
						MY.alert('复制试题成功','success');
						window.location.reload();
					}
				});
			},
			modifyQuestion: function() {
				var id = $(this).closest('.question-unit').data('id');
				window.location.href = "/question/editQuestion?id="+id;
			}
		};
		_this.init();
	});
});