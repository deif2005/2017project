var modelApp = angular.module('modelApp', []);
//初始化分页插件
MY.page.render(modelApp);

modelApp.filter('getTypeStr', function() {
	return function(input) {
		var str = "";
		if(input == '1') {
			str = '主观题';
		}else if(input == '2') {
			str = '客观题';
		}
		return str;
	};
});
modelApp.controller('modelListCtrl', function ($scope) {
	var _this = {
		init: function() {
			this.bindEvents();
			this.initValidate();
		},
		bindEvents: function() {
			//科目改变事件
			$('#searchForm').on('change','.subject-select',_this.subjectChangeEvent);

			//添加题型点击事件
			$scope.addQuestion = function() {
				$('#addQTypeModal').find('.modal-title').text('新增题型');
				$('#addForm').data('url','addQuestionType');
				$('#addForm').find('input[name="id"]').val('');
				$('#addForm').find('select[name="subjectId"]').find('option:eq(0)').prop('selected',true);
				$('#addForm').find('select[name="code"]').find('option:eq(0)').prop('selected',true);
				$('#addForm').find('select[name="subjectId"]').prop('disabled',false);
				$('#addForm').find('select[name="code"]').prop('disabled',false);
				$('#addForm').find('input[name="name"]').val('');
				$('#addForm').find('textarea[name="remark"]').val('');
				$('#addQTypeModal').modal();
			}

			//保存题型点击事件
			$scope.saveQuestion = function() {
				$('#addForm').submit();
			}

			//修改题型事件
			$('#qtypeTable').on('click','.update-qtype',_this.updateQuestionType);

			//删除题型事件
			$('#qtypeTable').on('click','.delete-qtype',_this.deleteQuestionType);
		},
		subjectChangeEvent: function() {
			var subjectId = $(this).val();
			var ajax1 = MY.ajax({
				url:'/questiontype/queryQuestionType',
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
		initValidate: function() {
			var opt = {
				rules : {
					'subjectId' : {
						required : true
					},
					'code' : {
						required : true
					},
					'name' : {
						required : true
					}
				},
				messages : {
					'subjectId' : {
						required : "请选择科目"
					},
					'code' : {
						required : "请选择基础题型"
					},
					'name' : {
						required : "题型名称不能为空"
					}
				},
				submitHandler : _this.submitFunc
			};
			$('#addForm').validate(opt);
		},
		submitFunc: function() {
			var formJson = MY.formToJson($('#addForm'));
			var type = $('#questionTypeSelect').find('option:selected').data('type');
			var url = $('#addForm').data('url');
			formJson.type = type;
			MY.ajax({
				type:'POST',
				url:'/questiontype/'+url,
				data: formJson
			}).done(function(data) {
				if(data.rtnCode == '000000') {
					window.location.href = '/questiontype/list';
				}
			});
		},
		updateQuestionType: function() {
			$('#addQTypeModal').find('.modal-title').text('修改题型');
			var qtype = $(this).closest('tr').data('obj');
			$('#addForm').find('input[name="id"]').val(qtype.id);
			$('#addForm').find('select[name="subjectId"]').val(qtype.subjectId);
			$('#addForm').find('select[name="code"]').val(qtype.code);
			$('#addForm').find('select[name="subjectId"]').prop('disabled',true);
			$('#addForm').find('select[name="code"]').prop('disabled',true);
			$('#addForm').find('input[name="name"]').val(qtype.name);
			$('#addForm').find('textarea[name="remark"]').val(qtype.remark);
			$('#addForm').data('url','updateQuestionType');
			$('#addQTypeModal').modal();
		},
		deleteQuestionType: function() {
			var id = $(this).closest('tr').data('id');
			var name = $(this).closest('tr').data('name');
			MY.confirm('确定要删除"'+name+'"吗?','提示',function() {
				MY.ajax({
					type:'POST',
					url:'/questiontype/delQuestionTypeById',
					data: {questionTypeId:id}
				}).done(function(data) {
					if(data.rtnCode == '000000') {
						window.location.href = '/questiontype/list';
					}
				});
			});
		}
	};
	_this.init();
});