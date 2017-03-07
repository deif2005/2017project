//header 操作的js
$(function() {
	var _this = {
		init: function() {
			//绑定事件
			this.bindEvents();
			//设置菜单选中
			this.selectedMenu();
			//设置当前日期
			$('.header').find('.header-time .date').text(MY.getDay(0));
			//设置当前星期
			$('.header').find('.header-time .week').text(_this.getWeek());
		},
		bindEvents: function() {
			//菜单点击事件
			$('.menu').on('click','div',this.menuClick);
			//退出登录点击事件
			$('.header').on( 'click','.userMessage .logout img',this.loginOut);
			//修改密码点击事件
			$('.header').on( 'click','.header-user .update-pwd',function() {
				$('#updatePwdModal').modal();
				$("input[type=reset]").trigger("click");
			});
			//保存新密码
			$('#updatePwdModal').on('click','.save-pwd',this.savePwd);

		},
		getWeek: function() {
			var date = new Date();
			var weekNames = ["星期日","星期一","星期二","星期三","星期四","星期五","星期六"];
			return weekNames[date.getDay()];
		},
		menuClick: function() {
			var url = $(this).data('url');
			if(url) {
				var id = $(this).attr('id');
				$('.menu').find('div').removeClass('active');
				$(this).addClass('active');
				sessionStorage.setItem("menuId", id);
				window.location.href = url;
			}else{
				alert("此功能尚在开发中,请耐心等待0.0!");
			}
		},
		selectedMenu: function() {
			var menuId = sessionStorage.getItem("menuId");
			if(menuId) {
				$('.menu').find('#'+menuId).addClass('active');
			}else {
				$('.menu').find('div:first').addClass('active');
			}
		},
		loginOut: function() {
			MY.confirm("确定退出登录吗？","提示",function() {
				window.location.href = "/logout";
			});
		},
		savePwd: function() {
			//设置验证规则
			$('#editPwdForm').validate(_this.getOpt());
			//提交表单
			$('#editPwdForm').submit();
		},
		getOpt: function() {
			var opt = {
				rules : {
					'oldPwd' : {
						required : true
					},
					'newPwd' : {
						required : true
					},
					'doublePwd' : {
						required : true,
						isEqual:function() {
							var doublePwd = $('.doublePwd').val();
							var newPwd = $('.newPwd').val();
							return [newPwd,doublePwd];
						}
					}
				},
				messages : {
					'oldPwd' : {
						required : "原始密码不能为空"
					},
					'newPwd' : {
						required : "请设置新密码"
					},
					'doublePwd' : {
						required : "请再次确认新密码",
						isEqual:"两次输入密码不一致"
					}
				},
				submitHandler : _this.submit
			};
			return opt;
		},
		submit: function() {
			var json = {};
			var doublePwd = $('.doublePwd').val();
			json['password'] = $('.oldPwd').val();
			json['newPassword'] = $('.newPwd').val();
			MY.ajax({
				url: '/user/updateUserById',
				data: json,
				type: 'POST'
			}).done(function (data) {
				if (data.rtnCode == "000000") {
					MY.alert("密码修改成功！","success",function() {
						window.location.href = "/login";
					});
				}else{
					MY.alert("密码修改失败","error",function() {
						window.location.reload();
					});
				}
			});
		}
	};
	_this.init();
});

