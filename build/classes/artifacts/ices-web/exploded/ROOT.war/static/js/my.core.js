/**
 * 封装一些常用的公共方法
 * @author  周乾
 */

//注册命名空间
var MY = window.MY || {};

//封装jquery ajax
/**
 * MY.ajax
 *
 * 根据现有业务改写 ajax 方法：
 * 1、接收数据默认为 json，返回 html 时需要另外指定(便于后端确定返回 json 或者 html)
 * 2、禁用 ajax 的 success/error/complete 方法，提倡使用 derferred 的 done/fail/then
 */
MY.ajax = function(options) {
	// 默认设置
	var defaultOption = {
		dataType: 'json',			// 默认服务器返回的数据类型为 json，如果返回 html 则需要进行设置
		autoUnblockUI: true,		// 自动去遮罩(使用 $.when 的时候应设置为 false)
		errorHandler: true,			// 对返回 ResposeMessage 出现Error时，进行处理
		failHandler: true,			// 对请求异常时，进行处理
		cache: false,
		beforeSend: function () {
			MY.waitingUI();
		}
	};

	options = options || {};

	// 限制使用 success/error/complete
	if($.isFunction(options.success)) {
		throw new Error('Please use done of Deferred');
	}

	if($.isFunction(options.error)) {
		throw new Error('Please use fail of Deferred');
	}

	if($.isFunction(options.complete)) {
		throw new Error('Please use always of Deferred');
	}

	// 用户定义参数覆盖默认设置
	var opt = $.extend({}, defaultOption, options);

	// 进行方法的拦截处理
	var ajax = $.ajax(opt)
		.always(function() {
			//如果存在遮罩的方法，则去除遮罩
			if(MY.unblockUI) {
				MY.unblockUI();
			}
		}).done(function(data, textStatus, jqXHR) {
			try{
				if(data.rtnCode == '999998'){
					alert('您太久没有操作了，请重新登陆！');
					window.top.location.href='/';
				}
			}catch(e){
				console.log(e);
			}
		}).fail(function(jqXHR, textStatus, errorThrown) {
			// 异常时，统一提供系统异常的提示，如果是中断请求则不用进行提示(比如 ajax 刷新页面)
			//对于返回的消息格式解析不了不弹框提示，主要针对401，session过期
			if((jqXHR.readyState >= 2) && (opt.failHandler) && ('parsererror' !== textStatus) && (jqXHR.status !== 404)) {
				MY.alert('请求数据出错啦~');
			}
		}).done(function(data, textStatus, jqXHR) {
			// 异常时，统一提供系统异常的提示
			if(data.rtnCode == '999999') {
				MY.alert('系统错误','error');
			}else if(data.rtnCode == '000001') {
				MY.alert('请求参数错误','error');
			}else if(data.rtnCode == '030002') {
				MY.alert('存在关联数据，无法删除','error');
			}else if(data.rtnCode == '000005') {
				MY.alert('存在相同名称的数据，请修改后再保存','error');
			}
		});

	return ajax;
};

/**
 * CE.post/CE.get
 * 根据改写的 ajax 方法重写 post 和 get
 */
$.each([ "get", "post" ], function( i, method ) {
	MY[method] = function( url, data, callback, type ) {
		if ( $.isFunction( data ) ) {
			type = type || callback;
			callback = data;
			data = undefined;
		}

		return MY.ajax({
			url: url,
			type: method,
			dataType: type,
			data: data
		}).done(callback);
	};
});

/**
 * loadHTML 的改写
 * data 可以为空，也就是无参数时，直接调用 CE.loadHTML(url, callback)
 */
MY.loadHTML = function(url, data, callback) {
	if ( $.isFunction( data ) ) {
		callback = data;
		data = undefined;
	}
	return MY.ajax({
		'type': 'POST',
		'url': url,
		'data': data,
		'dataType': 'html'
	}).done(callback || $.noop());
};

//把表单序列化为 json 对象
MY.formToJson = function(form){
	var $form = form.jquery ? form : $(form);

	var array = $form.serializeArray();

	return formArrayToJson(array);
};

var formArrayToJson = function(arr){
	var listReg = /\[\d{1,3}\]$/ig;
	var bracketReg = /\[\d{1,3}\]/i;
	var realObj = {};

	$.each(arr, function(index,ele) {
		var pName = '';
		var value = '';
		if(typeof ele == "string"){
			pName = ele;
			value = ele;
		}
		if(typeof ele == "object"){
			pName = ele.name;
			value = ele.value;
		}

		//名称是否包含"."
		var pointPos = pName.indexOf(".");
		if (pointPos >= 0 && !pName.match(/\.$/)){
			var objsArray = pName.split(".");
			iterateObj(realObj,objsArray,value);
		} else {
			//如果最后元素也是已[1]结尾，则认为是string数组。
			if (this.name.match(listReg)){
				//如果匹配含有favor[1]类似名称，则表示下级是数组
				var thisName = this.name.replace(bracketReg,"");

				if (!realObj[thisName]) {
					realObj[thisName] = [];
				}
				realObj[thisName].push(this.value || '');
			} else {
				//如果是拆分的名称数组中最后一个元素，则认为是对象的属性，不再拆分。
				// modify:huanggh 2013-03-27 对应 checkbox，存在同一个 name，需要自动封装成数组
				if(realObj[this.name]) {
					if($.isArray(realObj[this.name])){
						realObj[this.name].push(this.value);
					} else {
						realObj[this.name] = [realObj[this.name],this.value];
					}
				} else {
					realObj[this.name] = this.value || '';
				}
			}
		}
	});
	return realObj;
};

var iterateObj = function(obj,objsArray,pageValue){
	//寻找使用[0].abc模式的名称，这种属性需要组成list形式
	var listReg = /\[\d{1,3}\]$/ig;;
	var bracketReg = /\[\d{1,3}\]/i;

	var arrLen = objsArray.length;
	$.each(objsArray, function( i, v ){
		if(i < arrLen-1){
			var nextEleName = objsArray[i+1];
			var ele = {};
			if (nextEleName.match(listReg)){
				nextEleName = nextEleName.replace(bracketReg,"");
			}
			ele[nextEleName] = {};

			if(v.match(listReg)){
				var idxStr = v.match(listReg)[0].replace(/\[/ig,"").replace(/\]/ig,"");
				var idx = parseInt(idxStr);
				//如果匹配含有favor[1]类似名称，则表示下级是数组
				v = v.replace(bracketReg,"");
				if (obj[v]){
					if(obj[v][idx]){
						ele[nextEleName] = obj[v][idx];
					} else {
						obj[v][idx] = ele[nextEleName];
					}
				} else {
					obj[v] = [];
					obj[v][idx] = ele[nextEleName];
				}
			} else {
				if (!obj[v]){
					obj[v] = ele[nextEleName];
				} else {
					ele[nextEleName] = obj[v];
				}
			}
			objsArray.shift();
			ele[nextEleName] = iterateObj(ele[nextEleName],objsArray,pageValue);
			return false;
		} else {
			//如果最后元素也是已[1]结尾，则认为是string数组。
			if (v.match(listReg)){
				//如果匹配含有favor[1]类似名称，则表示下级是数组
				v = v.replace(bracketReg,"");
				if (!obj[v]){
					obj[v] = [];
				}
				obj[v].push(pageValue || '');
			} else {
				//如果是拆分的名称数组中最后一个元素，则认为是对象的属性，不再拆分。
				// modify:huanggh 2013-03-27 对应 checkbox，存在同一个 name，需要自动封装成数组
				if(obj[v]) {
					if($.isArray(obj[v])){
						obj[v].push(pageValue);
					}else{
						obj[v]=[obj[v], pageValue];
					}
				} else {
					obj[v] = pageValue || '';
				}
			}
			return false;
		}
	});
	return obj;
};

/**
 * 获取指定的日期
 * @param  intervalDay为-1时返回昨天的日期，0返回当前日期，1为明天的日期
 */
MY.getDay =function(intervalDay) {
	if (isNaN(intervalDay))
		intervalDay = 0;

	var local = new Date();
	var localYear = local.getFullYear();
	var localMonth = local.getMonth();
	var localDate = local.getDate();

	var result = new Date(localYear, localMonth, localDate + intervalDay);
	var tempMonth = result.getMonth() + 1;
	var resultMonth = tempMonth >= 10 ? tempMonth : ('0' + tempMonth);
	var tempDate = result.getDate();
	var resultDate = tempDate >= 10 ? tempDate : ('0' + tempDate);

	return result.getFullYear() + "-" + resultMonth + "-" + resultDate;
};

//跳转到页面并选中菜单
MY.goto = function(url,menuId) {
	$.cookie('menuId', menuId, {path: '/'});
	window.location.href = url;
}

//验证邮箱格式
MY.verifyEmail = function(email) {
	var reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
	if(!reg.test(email)) {
		return false;
	}
	return true;
}

//验证手机号
MY.verifyMobile = function(mobile) {
	var reg = /^(1[0-9])\d{9}$/;
	if(!reg.test(mobile)) {
		return false;
	}
	return true;
}


//layer 提示框
//MY.alert  参数说明：1.提示内容（必填）   2.类型(成功提示:"success",失败提示:"error"，警告:"warning")（必填） 3.确定的 回调函数（选填）
MY.alert = function(content,type,_callback) {
	var opt = {};
	switch(type){
		case "success":
			opt.icon = 1;
			break;
		case "error":
			opt.icon = 2;
			break;
		case "warning":
			opt.icon = 0;
			break;
		default:
			break;
	}
	var okFunc = function(index) {
		if(_callback) {
			_callback();
		}
		layer.close(index);
	}
	layer.alert(content,opt,okFunc);
}

//layer 确认框
//MY.confirm  参数说明：1.提示内容（必填）   2.提示框标题（必填） 3.确定的 回调函数（选填） 4.取消的回调函数（选填）
MY.confirm = function(content,title,_ok,_cancel) {
	var opt = {};
	opt.icon = 3;
	opt.title=title;
	var okFun = function(index) {
		if(_ok) {
			_ok();
		}
		layer.close(index);
	}
	layer.confirm(content,opt,okFun,_cancel);
}

//MY.positionTop 页面滚动时操作区域固定在顶部
MY.positionTop = function() {
	//设置最外层div距离顶部间距
	$('.top-panel').parents('div:last').css({'margin-top':'60px'});
	//监听滚动事件 如果滚动距离超过60px 按钮操作区域定位到顶部
	window.onscroll = function(e){
		//获取滚动距离
		var top = window.pageYOffset;
		//如果滚动距离超过60px，设置定位到顶部
		if(top > 60) {
			$('.top-panel').addClass('position-top').css({
				'top':'0px',
				'right':'0px'
			});
		}else {
			//如果滚动距离不超过60px，设置定位距离顶部60-top的距离
			$('.top-panel').removeClass('position-top').css({
				'top':(60-top)+'px',
				'right':'0px'
			});
		}
	};
}

//限制只能输入数字
MY.onlyNum = function (e) {
	var k = window.event ? e.keyCode : e.which;
	if (((k >= 48) && (k <= 57)) || k == 8 || k == 0) {
	} else {
		if (window.event) {
			window.event.returnValue = false;
		}
		else {
			e.preventDefault(); //for firefox
		}
	}
}

//获取url中的参数
function getUrlParam(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	var r = window.location.search.substr(1).match(reg);  //匹配目标参数
	if (r != null) return unescape(r[2]); return null; //返回参数值
}