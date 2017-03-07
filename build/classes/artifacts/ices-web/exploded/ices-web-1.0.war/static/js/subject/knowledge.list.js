var modelApp = angular.module('modelApp', []);
//初始化分页插件
MY.page.render(modelApp);

modelApp.controller('modelListCtrl', function ($scope) {
	$(function() {
		var _this = {
			init: function() {
				//缓存页面元素
				this.cacheElements();
				//绑定事件
				this.bindEvents();
				//设置表单验证规则
				$('#addForm').validate(_this.validateRule());
			},
			cacheElements: function() {
				//缓存页面元素
				this.$panel = $('.table-div');
			},
			bindEvents: function() {
				//添加知识点按钮事件
				$scope.addKnowledge = function() {
					_this.getSubject(function() {
						$('#addKnowModal').find('.modal-title').text('添加知识点');
						$scope.knowledge = {};
						$('#addKnowModal').modal();
					});
				}
				//保存知识点按钮事件
				$scope.saveKnowledge = function() {
					$('#addForm').submit();
				}
				//删除知识点事件
				this.$panel.on('click','.delete-opt',this.deleteKnowledge);
				//修改知识点事件
				this.$panel.on('click','.update-opt',this.updateKnowledge);
				//科目改变事件
				$('#searchForm').on('change','.subject-select',this.subjectChange);
			},
			deleteKnowledge: function() {
				var $this = $(this);
				MY.confirm('确定删除该知识点吗？','提示',function() {
					var id = $this.closest('tr').data('id');
					MY.ajax({
						url:'/knowledge/deleteKnowledge',
						type:'post',
						data:{id:id}
					}).done(function(data) {
						if(data.rtnCode == '000000') {
							window.location.href = '/knowledge/list';
						}
					});
				});
			},
			updateKnowledge: function() {
				var id = $(this).closest('tr').data('id');
				MY.ajax({
					url:'/knowledge/getKnowledgeById',
					type:'post',
					data:{id:id}
				}).done(function(data) {
					if(data.rtnCode == '000000') {
						_this.getSubject(function() {
							$('#addKnowModal').find('.modal-title').text('修改知识点');
							$scope.knowledge = data.bizData;
							$('#addKnowModal').modal();
						});

					}
				});
			},
			//查询科目列表
			getSubject: function(_callback) {
				MY.ajax({
					url:'/subject/listSubject',
					type:'post'
				}).done(function(data) {
					$scope.$apply(function() {
						if(data.rtnCode == '000000') {
							$scope.subjectList = data.bizData;
							if(_callback) {
								_callback();
							}
						}
					});
				});
			},
			subjectChange: function() {
				var subjectId = $('.subject-select').val();
				var ajax1 = MY.ajax({
					url:'/knowledge/pageKnowledge',
					type:'post',
					data:{subjectId:subjectId,page:1}
				}).done(function(data) {
					if(data.rtnCode == '000000') {
						$scope.$apply(function() {
							MY.page.init('modelEntrys',data);
						});
					}
				});
			},
			//验证规则
			validateRule: function() {
				var opt = {
					rules : {
						'subjectId' : {
							required : true
						},
						'name' : {
							required : true
						}
					},
					messages : {
						'subjectId' : {
							required : "所属科目不能为空"
						},
						'name' : {
							required : "知识点不能为空"
						}
					},
					submitHandler : _this.submit
				};
				return opt;
			},
			submit: function() {
				var formJson = MY.formToJson($('#addForm'));
				var url = '/knowledge/insertKnowledge';
				if(formJson.id) {
					url = '/knowledge/updateKnowledge';
				}
				MY.ajax({
					url:url,
					type:'post',
					data:formJson
				}).done(function(data) {
					if(data.rtnCode == '000000') {
						//window.location.href = '/knowledge/list';
						$('#addKnowModal').modal('hide');
						_this.subjectChange();
					}
				});
			}
		};
		_this.init();
	});

});