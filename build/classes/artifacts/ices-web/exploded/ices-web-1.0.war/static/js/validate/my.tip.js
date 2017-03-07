$(function() {
	// 校验提示信息
	MY.TipValidation = function(options) {
		var id = options.id;
		var text = options.text;
		var target = options.target || element;
		var element = options.element;
		var position = options.position;
		var tipPosition = {
				my: "left center",
				at: "center right",
				target: target
			};
		
		if(position == "top") {
			tipPosition = {
				my: "bottom left",
				at: "top right",
				target: target
			};
		};
		
		element.qtip({
			id: id,									// 设置 ID，方便进行销毁
			show: true,								// 默认显示
			hide: false,							// 默认不隐藏
			overwrite: false,						// 手工销毁，无需自动删除上一次的提示
			position: tipPosition,					// 气泡位置
			style: {								// 气泡主题样式
				classes: "qtip-bootstrap"
			},
			content: text							// 提示内容
		});
	};
	
	MY.TipDestroy = function(element) {
		element.qtip('destroy');
	};
	
	MY.TipReposition = function(element) {
		element.qtip('reposition');
	};
	
});