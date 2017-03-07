var modelApp = angular.module('modelApp', []);

modelApp.controller('modelListCtrl', function ($scope) {
		var _this = {
			init: function() {
				//缓存变量
				this.cacheElements();
				//绑定事件
				this.bindEvents();
				//绑定滚动条监听事件 滚动时操作按钮固定到顶部
				MY.positionTop();
				//渲染select2
				$("#knowledgeId").select2();
				//初始化题型和知识点
				this.subjectChange();
				//设置表单验证规则
				$('#questionForm').validate(_this.getOpt());
				//单选 复选框临时name
				this.name = "temp-name";
				//初始化富文本编辑器
				this.contentUE = UE.getEditor('quesContent');
				//富文本编辑器家加载完成后初始化选项区域
				_this.contentUE.addListener("ready", function () {
					//显示题目选项区域
					_this.initOptionsPanel();
				});
				this.remarkUE = UE.getEditor('quesRemark');
			},
			cacheElements: function() {
				this.name = "temp-name";

				this.$answerPanel = $('.answer-panel');

				//缓存单选选项区域模板
				this.$radioPanel = $('#radioTempPanel') ;

				//缓存多选选项区域模板
				this.$checkboxPanel = $('#checkboxTempPanel');

				//缓存判断题选项区域模板
				this.$judgmentPanel = $('#judgmentTempPanel');

				//缓存填空题区域模板
				this.$insertPanel = $('#insertTempPanel');

				//缓存页面富文本编辑器实例的对象
				this.editors = {};
			},
			bindEvents: function() {
				//添加题目类型改变事件
				$('#type').on('change',this.typeChange);
				//添加选项区域事件
				$('.add-options').on('click',this.addOptions);
				//删除选项区域事件
				this.$answerPanel.on('click','.del-icon',this.delOption);
				//预览事件
				$('.preview-btn').on('click',this.previewQuestion);
				//科目改变事件
				$('#subject').on('change',this.subjectChange);
				//保存事件
				$('.save-btn').on('click',this.saveQuestion);
				//取消事件
				$scope.cancelEvent = function() {
					window.location.href = '/question/list';
				}
			},
			initOptionsPanel: function() {
				//获取题目对象json String
				var contentJson = $('#questionContentPanel').text();
				var type = $('#type').find('option:selected').data('code');
				if(contentJson) {
					//转成json对象
					contentJson = $.parseJSON(contentJson);
					//获取题目选项和标答的json String(选择题)
					if(type == '101' || type == '102') {
						if(contentJson.questionItemStr && contentJson.questionAnswerStr) {
							//选项转成数组对象
							var itemArray = $.parseJSON(contentJson.questionItemStr);
							//标答转成数组对象
							var answers = $.parseJSON(contentJson.questionAnswerStr);
							if(itemArray && itemArray.length > 0) {
								$.each(itemArray, function(i,content) {
									//设置标答
									$.each(answers, function(k,answer) {
										if(content.code == answer) {
											content.answer = answer;
											return false;
										}
									});
									$('.add-options').trigger('click',content);
								});
							}
						}
					}
					//设置判断题标答
					else if(type == '103') {
						if(contentJson.questionAnswerStr) {
							//填空题标答转成数组对象
							var answers = $.parseJSON(contentJson.questionAnswerStr);
							if (answers && answers.length > 0) {
								//设置标答
								$('#type').trigger('change', answers[0]);
							}
						}
					}
					//设置填空题标答
					else if(type == '104') {
						if(contentJson.questionItemStr) {
							//填空题标答转成数组对象
							var answers = $.parseJSON(contentJson.questionAnswerStr);
							if (answers && answers.length > 0) {
								$.each(answers, function (i, answer) {
									//设置标答
									$('.add-options').trigger('click', answer);
								});
							}
						}
					}
				}

			},
			typeChange: function(e,answer) {
				var type = $(this).find('option:selected').data('code');
				//清空选项面板
				_this.$answerPanel.empty();
				//先销毁编辑器实例
				$.each(_this.editors,function(key,editor) {
					editor.destroy();
					//清除ueditor生成的文本域
					$('textarea[id="'+key+'"]').remove();
				});
				//重置变量
				_this.editors = {};
				if(type == '103') {
					_this.createJudgmentPanel(answer);
				}else{
					//显示添加图标
					$('.add-options').show();
				}
			},
			addOptions: function(e,content) {
				var type = $('#type').find('option:selected').data('code');
				switch (type) {
					//单选区域
					case 101:
						_this.createSelectPanel('radio',content);
						break;
					//多选区域
					case 102:
						_this.createSelectPanel('checkbox',content);
						break;
					//填空题区域
					case 104:
						_this.createInsertPanel(content);
						break;
					default :
						break;
				}
			},
			delOption: function() {
				//先清除当前元素
				_this.destroyEditor($(this).closest('.answer-panel-child'));
				var code = $('#type').find('option:selected').data('code');
				//获取选项区域元素
				var $options = _this.$answerPanel.find('.answer-panel-child');
				//获取选项区域的值
				var options = _this.getQuestionOptionsStr(code);
				//获取选项区域标答的值
				var answers = _this.getQuestionAnswerStr(code);
				//销毁剩余选项区域
				$.each($options, function(i,ele) {
					_this.destroyEditor($(ele));
				});
				_this.editors = {};
				//选项元素重新生成选项区域
				_this.reloadOptions(options,answers);
			},
			removeCache: function(key) {
				var obj = {};
				//移除缓存对象的值
				$.each(_this.editors, function(name,value) {
					if(name != key) {
						obj[name] = value;
					}
				});
				_this.editors = obj;
			},
			destroyEditor: function($panel) {
				if(!$panel.is('.answer-panel-child')) {
					return;
				}
				var id = $panel.data('editor');
				$panel.remove();
				var editorObj = _this.editors[id];
				//销毁ueditor
				editorObj.destroy();
				//清除缓存中的对象
				_this.removeCache(id);
				//清除ueditor生成的文本域
				$('textarea[id="'+id+'"]').remove();
			},
			//删除元素后重新排序生成
			reloadOptions: function(options,answers) {
				var code = $('#type').find('option:selected').data('code');
				if(code == '101' || code == '102') {
					$.each(options, function(i,content) {
						//设置标答
						$.each(answers, function(k,answer) {
							if(content.code == answer) {
								content.answer = answer;
								return false;
							}
						});
						$('.add-options').trigger('click',content);
					});
				}
				//设置填空题标答
				else if(code == '104') {
					if (answers && answers.length > 0) {
						$.each(answers, function (i, answer) {
							//设置标答
							$('.add-options').trigger('click', answer);
						});
					}
				}
			},
			previewQuestion: function() {
				var type = $('#type').find('option:selected').data('code');
				//获得题目内容
				var content = _this.contentUE.getContent();
				var remark = _this.remarkUE.getContent();
				var options = _this.getQuestionOptionsStr(type);
				var answers = _this.getQuestionAnswerStr(type);
				var answerStr = [];
				if(!content) {
					MY.alert("请先填写试题内容.",'warining');
					return;
				}
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
					//收集标答
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
			createJudgmentPanel: function(answer) {
				//克隆选项模板
				var $panel = _this.$judgmentPanel.clone();
				var index = 0;
				if(answer) {
					index = answer.substring(answer.length-1,answer.length);
				}
				$panel.removeAttr('id');
				//选项区域追加到页面上
				_this.$answerPanel.append($panel);
				//给单选框添加name属性
				$panel.find('input:radio').attr('name',_this.name);
				if(index) {
					$panel.find('input:radio').eq(index-1).prop('checked',true);
				}

				//隐藏添加图标
				$('.add-options').hide();

			},
			createSelectPanel: function(type,content) {
				//统计页面已添加选项数量
				var count = _this.$answerPanel.find('.answer-panel-child').length;
				if(count > 25) {
					MY.alert("最多只能输入26个选项",'warining');
					return;
				}
				//计算选项字母
				var letter = String.fromCharCode((65+count));
				//克隆选项模板
				var $panel = null;
				var checked = false;
				if(content && content.answer == content.code) {
					checked = true;
				}
				if(type == 'radio') {
					$panel = _this.$radioPanel.clone();
					//给单选框添加name属性
					$panel.find('input:radio').attr('name',_this.name).prop('checked',checked);

				}else if(type == 'checkbox') {
					$panel = _this.$checkboxPanel.clone();
					//给复选框添加name属性
					$panel.find('input:checkbox').attr('name',_this.name).prop('checked',checked);
				}
				$panel.find('.sort').text(letter);
				$panel.find('.editor').attr('id','editor'+letter);
				$panel.data('editor','editor'+letter);
				$panel.removeAttr('id');
				//选项区域追加到页面上
				_this.$answerPanel.append($panel);
				//实例化编辑器
				var editor = UE.getEditor('editor'+letter);
				editor.addListener("ready", function () {
					// editor准备好之后才可以使用
					if(content) {
						editor.setContent(content.content);
					}
					//缓存富文本编辑器实例
					_this.editors['editor'+letter] = editor;
				});
			},
			createInsertPanel: function(answer) {
				//统计页面已添加选项数量
				var count = _this.$answerPanel.find('.answer-panel-child').length;
				var insertCount =$(_this.contentUE.getContent()).find('.tk-area').length;
				if(insertCount == count) {
					MY.alert("该填空题总共只有"+insertCount+"个空",'warining');
					return;
				}
				//计算总数
				var index = (1+count);
				//克隆选项模板
				var $panel = _this.$insertPanel.clone();
				$panel.find('.sort').text(index);
				$panel.find('.editor').attr('id','editor'+index);
				$panel.data('editor','editor'+index);
				$panel.removeAttr('id');
				//选项区域追加到页面上
				_this.$answerPanel.append($panel);
				//实例化富文本编辑器
				var ue = UE.getEditor('editor'+index);
				ue.addListener("ready", function () {
					// editor准备好之后才可以使用
					if(answer) {
						ue.setContent(answer);
					}
					//缓存富文本编辑器实例
					_this.editors['editor'+index] = ue;
				});
			},
			subjectChange: function() {
				var data = {subjectId:$('#subject').val()};
				var url1 = "/question/listQuestionKnow",
				url2 = "/questiontype/listQuestionTypeBySubjectId",
				ajax1 = MY.ajax({
					type:'POST',
					url:url1,
					data:data
				}),
				ajax2 = MY.ajax({
					type:'POST',
					url:url2,
					data:data
				});
				//同时发送两个ajax请求 减少请求时间
				$.when(ajax1,ajax2).done(function(data1,data2) {
					$scope.$apply(function() {
						$scope.qknowList = data1[0].bizData;
						$scope.qtypeList = data2[0].bizData;
					});
				});
			},
			saveQuestion: function() {
				$('#questionForm').submit();
			},
			getOpt: function() {
				var opt = {
					rules : {
						'subjectId' : {
							required : true
						},
						'difficulty' : {
							required : true
						},
						'questionTypeId' : {
							required : true
						},
						'knowledgeIds' : {
							required : true
						},
						'keyword' : {
							required : true
						},
						'resource' : {
							required : true
						}
					},
					messages : {
						'subjectId' : {
							required : "请选择科目"
						},
						'difficulty' : {
							required : "请选择试题难度"
						},
						'questionTypeId' : {
							required : "请选择题型"
						},
						'knowledgeIds' : {
							required : "请选择知识点"
						},
						'keyword' : {
							required : "关键字不能为空"
						},
						'resource' : {
							required : "试题来源不能为空"
						}
					},
					submitHandler : _this.submitFunc
				};
				return opt;
			},
			submitFunc: function() {
				var dataJson = MY.formToJson($('#questionForm'));
				var qtypeCode = $('#type').find('option:selected').data('code');
				var qtypeName = $('#type').find('option:selected').text();
				if(!dataJson.questionContent ||
					!dataJson.questionContent.questionContent) {
					MY.alert("试题内容不能为空",'error');
					return;
				}
				if(dataJson.questionContent && !dataJson.questionContent.questionanAlysis) {
					dataJson.questionContent.questionanAlysis = "";
				}
				dataJson.questionContent.questionType = JSON.stringify({code:qtypeCode,name:qtypeName});
				var content = dataJson.questionContent.questionContent;
				dataJson.name = content;
				//收集选项值json
				var options = _this.getQuestionOptionsStr(qtypeCode,dataJson);
				if(qtypeCode != '104' && options.length == 0) {
					MY.alert("请先添加试题选项",'error');
					return;
				}
				var pass = true;
				$.each(options, function(i,obj) {
					if(!obj.content) {
						pass = false;
						return false;
					}
				});
				if(!pass) {
					MY.alert("请填写选项内容",'error');
					return;
				}
				dataJson.questionContent.questionItemStr = JSON.stringify(options);
				//收集表达json
				var answers = _this.getQuestionAnswerStr(qtypeCode,dataJson);
				dataJson.questionContent.questionAnswerStr = JSON.stringify(answers);
				if(qtypeCode != '104') {
					if(answers.length == 0) {
						MY.alert("请先设置标准答案",'error');
						return;
					}
				}else {
					var tks = $(content).find('.tk-area');
					if(tks.length != answers.length) {
						MY.alert("填空数跟选项区个数不一致",'error');
						return;
					}
				}
				//收集知识点
				_this.getQuestionKnowList(dataJson);
				//题目资源(图片，视频，音频等)
				_this.getQuestionResourceList(dataJson);
				//删除多余的属性
				delete dataJson.editorValue;
				delete dataJson.knowledgeIds;
				delete dataJson[this.name];
				MY.ajax({
					type:'POST',
					url:'/question/updateQuestionDetail',
					data: JSON.stringify(dataJson),
					contentType:'application/json; charset=utf-8'
				}).done(function(data) {
					if(data.rtnCode == '000000') {
						window.location.href = '/question/list';
					}
				});
			},
			getQuestionOptionsStr: function(code) {
				var $child = _this.$answerPanel.find('.answer-panel-child');
				var array = [];
				if(code == '101' || code == '102') {
					$.each($child,function(index,ele) {
						var id = $(this).data('editor');
						var editor = _this.editors[id];
						var content = editor.getContent();
						var qcode = (code+'0'+(index+1));
						array.push({code:qcode,content:content});
					});
				}else if(code == '103')  {
					array.push({code:code+'01',content:'对'});
					array.push({code:code+'02',content:'错'});
				}
				return array;
			},
			getQuestionAnswerStr: function(code) {
				var array = [];
				if(code == '101' || code == '102' || code == '103') {
					var $child = _this.$answerPanel.find('input[name="'+_this.name+'"]');
					$.each($child , function(index) {
						var checked = $(this).is(':checked');
						//获取标答
						if(checked) {
							array.push(code+'0'+(index+1));
						}
					});
				}else if(code == '104')  {
					var $child = _this.$answerPanel.find('.answer-panel-child');
					$.each($child, function() {
						var id = $(this).data('editor');
						var editor = _this.editors[id];
						var content = $(editor.getContent()).html();
						if(!content) {
							content = '';
						}
						array.push(content);
					});
				}
				return array;
			},
			getQuestionKnowList: function(dataJson) {
				var array = [];
				if(dataJson.knowledgeIds) {
					if(!$.isArray(dataJson.knowledgeIds)) {
						dataJson.knowledgeIds = [dataJson.knowledgeIds]
					}
					$.each(dataJson.knowledgeIds, function(index,id) {
						var obj = {};
						obj.knowId = id;
						array.push(obj);
					});
				}
				dataJson.questionKnowList = array;
			},
			getQuestionResourceList: function(dataJson) {
				var array = [];
				if(dataJson.questionContent) {
					//收集题干资源
					if(dataJson.questionContent.questionContent) {
						var $content = $('<div>'+dataJson.questionContent.questionContent+'</div>');
						var $imgs = $content.find('img.sys-upload');
						$.each($imgs, function(index,img) {
							var src = $(this).attr('src');
							var index = src.lastIndexOf('\/');
							var name = $(this).attr('title');
							array.push({
								type:1,
								name:name,
								path:src
							});
						});
					}
					//收集选项资源
					if(dataJson.questionContent.questionItemStr) {
						var options = JSON.parse(dataJson.questionContent.questionItemStr);
						$.each(options, function(index,obj) {
							var $content = $('<div>'+obj.content+'</div>');
							var $imgs = $content.find('img.sys-upload');
							$.each($imgs, function(i,img) {
								var src = $(this).attr('src');
								var index = src.lastIndexOf('\/');
								var name = $(this).attr('title');
								array.push({
									type:1,
									name:name,
									path:src
								});
							});
						});
					}
					//收集解析资源
					if(dataJson.questionContent.questionanAlysis) {
						var $content = $('<div>'+dataJson.questionContent.questionanAlysis+'</div>');
						var $imgs = $content.find('img.sys-upload');

						$.each($imgs, function(index,img) {
							var src = $(this).attr('src');
							var index = src.lastIndexOf('\/');
							var name = $(this).attr('title');
							array.push({
								type:1,
								name:name,
								path:src
							});
						});
					}
					//收集标答资源
					if(dataJson.questionContent.questionAnswerStr) {
						var answers = JSON.parse(dataJson.questionContent.questionAnswerStr);
						$.each(answers, function(index,content) {
							var $content = $('<div>'+content+'</div>');
							var $imgs = $content.find('img.sys-upload');
							$.each($imgs, function(i,img) {
								var src = $(this).attr('src');
								var index = src.lastIndexOf('\/');
								var name = $(this).attr('title');
								array.push({
									type:1,
									name:name,
									path:src
								});
							});
						});
					}
				}
				dataJson.questionResourceList = array;
			}
		};
		_this.init();
});
