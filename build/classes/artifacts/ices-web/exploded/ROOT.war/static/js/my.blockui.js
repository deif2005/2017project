
  //注册命名空间
  var MY = window.MY || {};

  var msgHtml = $('<div style="width:100%; text-align: center;"><img src="/static/images/loading1.gif" style="width:30px;height: 30px;"></div>');
	
	// 默认的等待图层展示
	MY.waitingUI = function() {
		$.blockUI({
	        message: msgHtml,
	        baseZ: 2000,
	        css: {
				left:		'50%',
				width:		'auto',
				border:		'0',
				backgroundColor:'none'
	        },
	        overlayCSS:  {
				opacity:			0.4
			},
			ignoreIfBlocked: true
		});
	};
	
	// 取消遮罩层展示
	MY.unblockUI = function() {
		$.unblockUI();
	}
