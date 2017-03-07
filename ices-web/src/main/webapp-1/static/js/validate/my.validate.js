	// 错误信息缓存常量名
	var FIELDMSG = 'ce-field-msg';
	
	// 有错误则显示错误内容
	var showErrorTip = function(element) {
		var $e = element.jquery ? element : $(element);
		var $target = $e;
		var errorMsg = $e.data(FIELDMSG);
		// 调整选择框错误显示的位置
		if($e.is('select.select2-render')) {
			$target = $e.next('span.select2');
			if($e.hasClass('form-valid-error') && !!errorMsg) {
				$e.next('span.select2').addClass('select2-valid-error');
			}else {
				$e.next('span.select2').removeClass('select2-valid-error');
			}
		}
		// 重设表单校验，使用 validation.resetForm 后，无法销毁 FIELDMSG，故如果没有错误样式也不进行提示
		if($e.hasClass('form-valid-error') && !!errorMsg) {
			MY.TipValidation({
				text: errorMsg,
				element: $e,
				position: "top",
				target: $target
			});
		}
	};
	// 对 select2 进行校验
	$('body').on('select2:open', 'select.select2-render.form-valid-error', function(e) {
		// 获得焦点时，有错误则显示错误提示
		showErrorTip(this);
	}).on('select2:close', 'select.select2-render.form-valid-error', function(e) {
		var $e = $(this);
		// 销毁气泡
		MY.TipDestroy($e);
	}).on('change', 'select.select2-render', function(e) {
		var $this = $(this);
		$this.valid();
		// 获得焦点时，有错误则显示错误提示
		showErrorTip($this);
	});


	$.validator.setDefaults({
		/**
		 *  使用 qtip2 气泡提示
		 *  有错误的，输入框变成红色，获得焦点时，气泡提示；失去焦点时，气泡销毁
		 */
		errorPlacement: function(error, element) {
			var $e = $(element);
			//if($e.is('select.select2-render')) {
			//	$e .next('span.select2').addClass('select2-valid-error');
			//}
			var errorMsg = error.html();

			$e.data(FIELDMSG, errorMsg);
		},
		errorClass: 'form-valid-error',
		success: function(label, element){
			var $e = $(element);
			
			// 移除错误内容
			$e.removeData(FIELDMSG);
			
			// 销毁气泡
			MY.TipDestroy($e);
		},
		onfocusin: function(element, event) {
			// 获得焦点时，有错误则显示错误提示
			showErrorTip(element);
		},
		onfocusout: function(element, event) {
			var $e = $(element);
			
			// 失去焦点时，进行校验，错误或者正确有相应的方法更改边框
			var validSuccess = $e.valid();
			
			// 销毁气泡
			MY.TipDestroy($e);
		},
		onkeyup: function(element, event) {
			// 键盘按起时，有错误则显示错误提示(如果超过0.5秒没有输入内容，则可能不再输入，进行校验)
			setTimeout(function() {
				var $e = $(element);
				$e.valid();
				showErrorTip(element);
			}, 500);
		}
	});
	
	/**
	 * 验证
	 */
	$.validator.addMethod('isPhoneNum', function(value, element, arg) {
		if(!value) {
			return true;
		}
		
		// var reg = /^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[7,8])|(18[0-9]))\d{8}$/;
		var reg = /^(([1-9]))\d{10}$/;
		
		return reg.test(value);
	}, "请输入正确的手机号码");
	

	
	/**
	 * 验证
	 */
	$.validator.addMethod('isTime', function(value, element, arg) {
		if(!value) {
			return true;
		}
		if(arg == '5') {
			var reg = /^(([0-1][0-9])|(2[0-3])):[0-5][0-9]$/;
			
			return reg.test(value);
		}
		return true;
	}, "请输入正确的时间格式(如：15:00)");
	
	/**
	 * 验证不相等
	 */
	$.validator.addMethod('isNotEqual', function(value, element, arg) {
		if(arg && $.isArray(arg) && arg.length ==2) {
			if(arg[0] == arg[1]) {
				return false;
			}
		}
		return true;
	}, "");

	/**
	 * 验证相等
	 */
	$.validator.addMethod('isEqual', function(value, element, arg) {
		if(arg && $.isArray(arg) && arg.length ==2) {
			if(arg[0] != arg[1]) {
				return false;
			}
		}
		return true;
	}, "");
	
	/**
	 * 验证是否大于
	 */
	$.validator.addMethod('isGreaterThan', function(value, element, arg) {
		if(arg && $.isArray(arg) && arg.length ==2) {
			if(arg[0] >= arg[1]) {
				return false;
			}
		}
		return true;
	}, "");

	/**
	 * 只能输入数字类型字符，包括小数
	 * 限制两位小数
	 */
	$.validator.addMethod('isChangeNum', function(value) {
		var _value = value;
		if(!_value){
			return true;
		}
		var reg = new RegExp("^[0-9]+(.[0-9]{0,2})?$", "g");
		if (!reg.test(_value)) {
			return false;
		}
		return true;
	});

	/**
	 * 验证是否大于0
	 */
	$.validator.addMethod('isZeroThan', function(value, element, arg) {
		if(value) {
			if(value< 0 || value==0) {
				return false;
			}
		}
		return true;
	}, "");