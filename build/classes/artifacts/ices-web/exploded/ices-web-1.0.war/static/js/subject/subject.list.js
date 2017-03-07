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
				//跳转到知识点管理页面
				$scope.goKnowledgeList = function() {
					window.location.href = '/knowledge/list';
				};
				//题型管理按钮事件
				$scope.goQuestionTypeList = function() {
					window.location.href = '/questiontype/list';
				}
				//添加科目按钮事件
				$scope.addSubject = function() {
					$('#addSubjectModal').find('.modal-title').text('添加科目');
					$scope.subject = {};
					$('#addSubjectModal').modal();
				}
				//保存按钮事件
				$scope.saveSubject = function() {
					$('#addForm').submit();
				}
				//删除科目事件
				this.$panel.on('click','.delete-opt',this.deleteSubject);
				//修改科目事件
				this.$panel.on('click','.update-opt',this.updateSubject);
			},
			deleteSubject: function() {
				var $this = $(this);
				MY.confirm('确定删除该科目吗？','提示',function() {
					var id = $this.closest('tr').data('id');
					MY.ajax({
						url:'/subject/deleteSubjectById',
						type:'post',
						data:{id:id}
					}).done(function(data) {
						if(data.rtnCode == '000000') {
							window.location.href = '/subject/list';
						}
					});
				});
			},
			updateSubject: function() {
				var id = $(this).closest('tr').data('id');
				MY.ajax({
					url:'/subject/getSubjectById',
					type:'post',
					data:{id:id}
				}).done(function(data) {
					$scope.$apply(function() {
						if(data.rtnCode == '000000') {
							$('#addSubjectModal').find('.modal-title').text('修改科目');
							$scope.subject = data.bizData;
							$('#addSubjectModal').modal();
						}
					});
				});
			},
			//验证规则
			validateRule: function() {
				var opt = {
					rules : {
						'name' : {
							required : true
						}
					},
					messages : {
						'name' : {
							required : "科目名称不能为空"
						}
					},
					submitHandler : _this.submit
				};
				return opt;
			},
			submit: function() {
				var formJson = MY.formToJson($('#addForm'));
				var url = '/subject/insertSubject';
				if(formJson.id) {
					url = '/subject/updateSubjectById';
				}
				MY.ajax({
					url:url,
					type:'post',
					data:formJson
				}).done(function(data) {
					if(data.rtnCode == '000000') {
						window.location.href = '/subject/list';
					}
				});
			}
		};
		_this.init();
	});

});