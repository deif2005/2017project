/**
 * 表格分页插件。
 * @author  周乾
 */
var _page = {
	init: function(app,_callback) {
		this.pageHTMl = '<div class="page-num">' +
							'<button class="btn btn-primary btn-sm btn-first" ng-click="first()"  title="第一页" style="margin:3px;">' +
							'<span class="glyphicon glyphicon-step-backward"></span></button>' +
							'<button class="btn btn-primary btn-sm btn-prev" ng-click="prev()" title="上一页" style="margin:3px;">' +
							'<span class="glyphicon glyphicon-chevron-left"></span></button>' +
							'<input class="form-control input-sm page-current" type="text" onkeypress="return MY.onlyNum(event)" style="margin:3px;"> / 共<span class="page-total"></span>页'+
							'<button class="btn btn-default btn-sm btn-go" title="前往" ng-click="go()" style="margin:3px;">GO</button>'+
							'<button class="btn btn-primary btn-sm btn-next" ng-click="next()" title="下一页" style="margin:3px;">'+
							'<span class="glyphicon glyphicon-chevron-right"></span></button>' +
							'<button class="btn btn-primary btn-sm btn-last" ng-click="last()" title="最后一页" style="margin:3px;">'+
							'<span class="glyphicon glyphicon-step-forward"></span></button>每页<span class="page-size"></span>条，共<span class="page-records"></span>条' +
						'</div>';
		this.bindEvents(app,_callback);
		this.$rootScope;
		this._callback;
	},
	bindEvents: function(app,_callback) {
		//声明angularjs的自定义标签
		app.directive("page", function() {
			return {
				restrict:'E',
				priority:5,
				scope:{
					model:'@model',
					formid:'@formid',
					ajaxurl:'@ajaxurl',
					method:'@method',
					autoinit:'@autoinit',
					createbody:'@createbody'
				},
				template : _page.pageHTMl,
				controller : function($scope,$rootScope, $element) {
					var modelName = $scope.model;
					var url = $scope.ajaxurl;
					var formId = $scope.formid;
					var method = $scope.method?$scope.method:'POST';
					var autoinit = $scope.autoinit;
					var createbody = $scope.createbody;
					_page.$rootScope = $rootScope;
					//初始化表格数据
					var initData = function() {
						var formJson = MY.formToJson('#'+formId);
						formJson.page = 1;
						MY.ajax({
							url:url,
							data: formJson,
							type:method
						}).done(function(data) {
							$rootScope.$apply(function () {
								$rootScope[modelName] = data;
							});
							$('page[model="'+modelName+'"]').find('.page-total').text(data.bizData.total);
							$('page[model="'+modelName+'"]').find('.page-size').text(data.bizData.pagesize);
							$('page[model="'+modelName+'"]').find('.page-records').text(data.bizData.records);
							$('page[model="'+modelName+'"]').find('.page-current').val(data.bizData.page);
							$('page[model="'+modelName+'"]').find('.btn-first,.btn-prev').addClass('disabled');
							if(data.bizData.total == 1) {
								$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').addClass('disabled');
							}
							if(data.bizData.total == 0) {
								$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').addClass('disabled');
								$('page[model="'+modelName+'"]').find('.page-current').val(0);
							}
							if(_callback && $.isFunction(_callback) ) {
								_callback(data);
							}
						});
					};
					//跳转到首页事件
					$scope.first = function() {
						window.scrollTo(0,0);
						var formJson = MY.formToJson('#'+formId);
						var page = $rootScope[modelName].bizData.page;
						if(page == 1) {
							return;
						}
						formJson.page = 1;
						MY.ajax({
							url:url,
							data: formJson,
							type:method
						}).done(function(data) {
							if(_callback && $.isFunction(_callback)) {
								_callback(data);
							}
							$rootScope.$apply(function () {
								$rootScope[modelName] = data;
							});
							$('page[model="'+modelName+'"]').find('.page-total').text(data.bizData.total);
							$('page[model="'+modelName+'"]').find('.page-size').text(data.bizData.pagesize);
							$('page[model="'+modelName+'"]').find('.page-records').text(data.bizData.records);
							$('page[model="'+modelName+'"]').find('.page-current').val(data.bizData.page);
							$('page[model="'+modelName+'"]').find('.btn-first,.btn-prev').addClass('disabled');
							$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').removeClass('disabled');
						});
					};
					//上一页事件
					$scope.prev = function() {
						window.scrollTo(0,0);
						var pageObj = $rootScope[modelName].bizData;
						var page = (pageObj.page-1);
						if(page < 1) {
							return false;
						}
						var formJson = MY.formToJson('#'+formId);
						formJson.page = page;
						MY.ajax({
							url:url,
							data: formJson,
							type:method
						}).done(function(data) {
							$rootScope.$apply(function () {
								$rootScope[modelName] = data;
							});
							$('page[model="'+modelName+'"]').find('.page-total').text(data.bizData.total);
							$('page[model="'+modelName+'"]').find('.page-size').text(data.bizData.pagesize);
							$('page[model="'+modelName+'"]').find('.page-records').text(data.bizData.records);
							$('page[model="'+modelName+'"]').find('.page-current').val(data.bizData.page);
							if(page == 1) {
								$('page[model="'+modelName+'"]').find('.btn-first,.btn-prev').addClass('disabled');
								$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').removeClass('disabled');
							}else {
								$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').removeClass('disabled');
								$('page[model="'+modelName+'"]').find('.btn-first,.btn-prev').removeClass('disabled');
							}
							if(_callback && $.isFunction(_callback)) {
								_callback(data);
							}
						});
					};
					//下一页事件
					$scope.next = function() {
						window.scrollTo(0,0);
						var pageObj = $rootScope[modelName].bizData;
						var page = (pageObj.page+1);
						if(page > pageObj.total) {
							return false;
						}
						var formJson = MY.formToJson('#'+formId);
						formJson.page = page;
						MY.ajax({
							url:url,
							data:formJson,
							type:method
						}).done(function(data) {
							$rootScope.$apply(function () {
								$rootScope[modelName] = data;
							});
							$('page[model="'+modelName+'"]').find('.page-total').text(data.bizData.total);
							$('page[model="'+modelName+'"]').find('.page-size').text(data.bizData.pagesize);
							$('page[model="'+modelName+'"]').find('.page-records').text(data.bizData.records);
							$('page[model="'+modelName+'"]').find('.page-current').val(data.bizData.page);
							if(page == pageObj.total) {
								$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').addClass('disabled');
								$('page[model="'+modelName+'"]').find('.btn-first,.btn-prev').removeClass('disabled');
							}else {
								$('page[model="'+modelName+'"]').find('.btn-first,.btn-prev').removeClass('disabled');
								$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').removeClass('disabled');
							}
							if(_callback && $.isFunction(_callback)) {
								_callback(data);
							}
						});
					};
					//跳转到指定页事件
					$scope.go = function() {
						window.scrollTo(0,0);
						var pageObj = $rootScope[modelName].bizData;
						var page = $('page[model="'+modelName+'"]').find('.page-current').val();
						if(pageObj.total == 0 || parseInt(page) == pageObj.page || pageObj.total <= 1) {
							return false;
						}

						if(parseInt(page) >= pageObj.total) {
							page = pageObj.total;
							$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').addClass('disabled');
							$('page[model="'+modelName+'"]').find('.btn-first,.btn-prev').removeClass('disabled');
						}
						if(parseInt(page) <= 1) {
							page = 1;
							$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').removeClass('disabled');
							$('page[model="'+modelName+'"]').find('.btn-first,.btn-prev').addClass('disabled');
						}
						var formJson = MY.formToJson('#'+formId);
						formJson.page = page;
						MY.ajax({
							url:url,
							data:formJson,
							type:method
						}).done(function(data) {
							$rootScope.$apply(function () {
								$rootScope[modelName] = data;
							});
							$('page[model="'+modelName+'"]').find('.page-total').text(data.bizData.total);
							$('page[model="'+modelName+'"]').find('.page-size').text(data.bizData.pagesize);
							$('page[model="'+modelName+'"]').find('.page-records').text(data.bizData.records);
							$('page[model="'+modelName+'"]').find('.page-current').val(data.bizData.page);
							if(_callback && $.isFunction(_callback)) {
								_callback(data);
							}
						});
					};
					//跳转到最后一页事件
					$scope.last = function() {
						window.scrollTo(0,0);
						var pageObj = $rootScope[modelName].bizData;
						var page = pageObj.total;
						if(page == pageObj.page) {
							return;
						}
						var formJson = MY.formToJson('#'+formId);
						formJson.page = page;
						MY.ajax({
							url:url,
							data: formJson,
							type:method
						}).done(function(data) {
							$rootScope.$apply(function () {
								$rootScope[modelName] = data;
							});
							$('page[model="'+modelName+'"]').find('.page-total').text(data.bizData.total);
							$('page[model="'+modelName+'"]').find('.page-size').text(data.bizData.pagesize);
							$('page[model="'+modelName+'"]').find('.page-records').text(data.bizData.records);
							$('page[model="'+modelName+'"]').find('.page-current').val(data.bizData.page);
							$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').addClass('disabled');
							$('page[model="'+modelName+'"]').find('.btn-first,.btn-prev').removeClass('disabled');
							if(_callback && $.isFunction(_callback)) {
								_callback(data);
							}
						});
					};

					$('#'+formId).on('click','.search-btn', function () {
						var formJson = MY.formToJson('#'+formId);
						formJson.page = 1;
						MY.ajax({
							url:url,
							data: formJson,
							type:method
						}).done(function(data) {
							$rootScope.$apply(function () {
								$rootScope[modelName] = data;
							});
							$('page[model="'+modelName+'"]').find('.page-total').text(data.bizData.total);
							$('page[model="'+modelName+'"]').find('.page-size').text(data.bizData.pagesize);
							$('page[model="'+modelName+'"]').find('.page-records').text(data.bizData.records);
							$('page[model="'+modelName+'"]').find('.page-current').val(data.bizData.page);
							$('page[model="'+modelName+'"]').find('.btn-first,.btn-prev').addClass('disabled');
							if(data.bizData.total <= 1) {
								$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').addClass('disabled');
								$('page[model="'+modelName+'"]').find('.page-current').val(data.bizData.total);
							}else {
								$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').removeClass('disabled');
							}
							if(_callback && $.isFunction(_callback)) {
								_callback(data);
							}
						});
					});
					if(autoinit == 'true' || !autoinit) {
						initData();
					}
				}

			};
		});
	},
	methods: {
		render: function(app,_callback) {
			_page._callback = _callback;
			_page.init(app,_callback);
		},
		init: function(modelName,data) {
			_page.$rootScope[modelName] = data;
			$('page[model="'+modelName+'"]').find('.page-total').text(data.bizData.total);
			$('page[model="'+modelName+'"]').find('.page-size').text(data.bizData.pagesize);
			$('page[model="'+modelName+'"]').find('.page-records').text(data.bizData.records);
			$('page[model="'+modelName+'"]').find('.page-current').val(data.bizData.page);
			$('page[model="'+modelName+'"]').find('.btn-first,.btn-prev').addClass('disabled');
			if(data.bizData.total == 1) {
				$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').addClass('disabled');
			}else{
				$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').removeClass('disabled');
			}
			if(data.bizData.total == 0) {
				$('page[model="'+modelName+'"]').find('.btn-next,.btn-last').addClass('disabled');
				$('page[model="'+modelName+'"]').find('.page-current').val(0);
			}
			if(_page._callback && $.isFunction(_page._callback)) {
				_page._callback(data);
			}
		}
	}
};
MY.page = _page.methods;
