/**
 * 对于后台的请求封装的操作工具类，依赖与jquery
 * 
 * @author yjc
 */
var AjaxUtil = {
	// 同步发送请求
	syncRequest : function(url) {
		var responseText = false;
		$.ajax( {
			type : "post",
			async : false,
			url : url.getURLString(),
			data : url.getParas(),
			dataType : "text",
			success : function(data) {
				responseText = data;
			},
			complete : function(xhr, status) {
				if ("success" != status) {
					MsgBox.alert("请求出错：[" + status + "]" + xhr.status + ":"
							+ xhr.statusText);
				}
			}
		});
		return responseText;
	},

	// 异步发送请求
	asyncRequest : function(url, callback) {
		$.ajax( {
			type : "post",
			async : true,
			url : url.getURLString(),
			data : url.getParas(),
			dataType : "text",
			success : function(responseText) {
				if ("undefined" != typeof callback && null != callback
						&& "" != callback) {
					callback(responseText);
				}
			},
			complete : function(xhr, status) {
				if ("success" != status) {
					MsgBox.alert("请求出错：[" + status + "]" + xhr.status + ":"
							+ xhr.statusText);
				}
			}
		});
	},

	// 同步发送请求-业务请求，默认加载上数据jbjgid和jbjgqxfw-自动反馈结果的验证
	syncBizRequest : function(url, callback) {
		url.addPara("__jbjgid", getJbjgid("00000000"));
		url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		url.addPara("__yhid", getPageYhid());
		var data = AjaxUtil.syncRequest(url);
		if (AjaxUtil.checkException(data)) {
			AjaxUtil.showException(data);
			return false;
		}

		if ("undefined" != typeof callback && null != callback
				&& "" != callback) {
			callback(data);
		}

		// 如果返回的数据为空，但是没有异常的则返回true
		if (chkObjNull(data)) {
			return true;
		}
		return data;
	},

	// 在syncBizRequest调用后执行，判断是否发生异常，是否继续运行。
	checkIsGoOn : function(data) {
		if (typeof (data) == "boolean") {
			if (data) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	},

	// 异步发送请求-业务请求，默认加载上数据jbjgid和jbjgqxfw-自动反馈结果的验证
	asyncBizRequest : function(url, callback) {
		url.addPara("__jbjgid", getJbjgid("00000000"));
		url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		url.addPara("__yhid", getPageYhid());
		AjaxUtil.asyncRequest(url, function(data) {
			if (AjaxUtil.checkException(data)) {
				AjaxUtil.showException(data);
				return false;
			}
			if ("undefined" != typeof callback && null != callback
					&& "" != callback) {
				callback(data);
			}
		});
	},

	// 检查请求结果是发生了异常
	checkException : function(responseText) {
		if ("string" == typeof responseText) {// 解决火狐浏览器下的相关问题。-yjc.2015年8月28日
			if (responseText
					.indexOf("<!--\r\n//errmsgsign_20150603_grace.easyFrame\r\n-->") >= 0) {
				return true;// 存在异常
			} else {
				return false;// 无异常
			}
		} else {
			return false;
		}
	},

	// 处理异常信息
	showException : function(reponseText) {
		if ($("#sys_errmsg_con_div").length <= 0) {
			$("body").append("<div id=\"sys_errmsg_con_div\"></div>");
		}
		$("#sys_errmsg_con_div").html(reponseText);
	},

	// 刷新form和grid的方法，操作为同步的方式,可以同时刷新多个form或者grid;
	// options参数格式例如：formPerInfo:dsperinfo,gridPerList:dsperlist
	syncRefreshData : function(url, options) {
		var data = AjaxUtil.syncRequest(url);
		if (AjaxUtil.checkException(data)) {
			AjaxUtil.showException(data);
			return;
		}

		// 刷新组件
		AjaxUtil.refrshObjByDataStr(data, options);
		return true;
	},

	// 刷新form和grid的方法，操作为异步的方式,可以同时刷新多个form或者grid;
	// options参数格式例如：formPerInfo:dsperinfo,gridPerList:dsperlist
	asyncRefreshData : function(url, options, callback, isloading) {
		if (chkObjNull(isloading) || isloading == true) {
			// 整个页面增加缓冲标志
			showLoading();
		}

		// 异步请求
		AjaxUtil.asyncRequest(url, function(data) {
			hideLoading();// 隐藏缓冲标志
				// 处理数据
				if (AjaxUtil.checkException(data)) {
					AjaxUtil.showException(data);
					return;
				}

				// 刷新组件
				AjaxUtil.refrshObjByDataStr(data, options);

				// 数据刷新完成，则调用回调函数
				if ("undefined" != typeof callback && null != callback
						&& "" != callback) {
					callback(data);
				}
			});
	},

	// 业务刷新:刷新form和grid的方法，操作为同步的方式,可以同时刷新多个form或者grid;
	// options参数格式例如：formPerInfo:dsperinfo,gridPerList:dsperlist
	syncRefreshBizData : function(url, options) {
		url.addPara("__jbjgid", getJbjgid("00000000"));
		url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		url.addPara("__yhid", getPageYhid());
		AjaxUtil.syncRefreshData(url, options);
	},

	// 业务刷新：刷新form和grid的方法，操作为异步的方式,可以同时刷新多个form或者grid;
	// options参数格式例如：formPerInfo:dsperinfo,gridPerList:dsperlist
	asyncRefreshBizData : function(url, options, callback, isloading) {
		url.addPara("__jbjgid", getJbjgid("00000000"));
		url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		url.addPara("__yhid", getPageYhid());
		AjaxUtil.asyncRefreshData(url, options, callback, isloading);
	},

	// 刷新整个页面
	asyncRefreshPage : function(url) {
		if (chkObjNull(url)) {// 未传url,刷新原有页面
			if (typeof (_loadPageContent) != "undefined") {
				_loadPageContent();// 刷新页面
			} else {
				alert("该页面不支持，使用asyncRefreshPage方式进行页面的刷新");
			}
		} else {// 传入url
			if (typeof (_initFramePage) != "undefined") {
				_initFramePage(url);// 刷新页面
			} else {
				// 不包含_initFramePage表示页面不支持刷新
				alert("该页面不支持，使用asyncRefreshPage方式进行页面的刷新");
			}
		}
	},

	// 异步发送请求-业务请求，默认加载上数据jbjgid和jbjgqxfw-自动反馈结果的验证-通过进度条的方式请求。
	asyncBizRequestViaPgBar : function(url, bartitle, callback) {
		// 请求前首先创建进度条对象,并将ID放置到URL中
		var pgBar = new ProgressBar(bartitle, "确定取消该业务操作吗？");
		var pgBarId = pgBar.id;
		pgBar.setMsg("操作正在进行...");
		url.addPara("__pgbarid", pgBarId);
		// url增加jbjx信息
		url.addPara("__jbjgid", getJbjgid("00000000"));
		url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		url.addPara("__yhid", getPageYhid());
		AjaxUtil.asyncRequest(url, function(data) {
			/**
			 * 异步bar直接返回
			 */
			if (AjaxUtil.isAsynBar(data)) {
				return false;
			}
			/**
			 * 异步请求已经返回，需要销毁进度条
			 */
			if (!ProgressBarUtil.isDestroy(pgBarId)) {
				pgBar.destroy();
			}
			if (AjaxUtil.checkException(data)) {
				AjaxUtil.showException(data);
				return false;
			}
			if ("undefined" != typeof callback && null != callback
					&& "" != callback) {
				callback(data);
			}
		});

		// 3秒钟请求后台一次进度
		setTimeout(setProgressBarInfo, 1000);

		function setProgressBarInfo() {
			var jdUrl = new URL("taglib.do", "getProgressBarInfo");
			jdUrl.addPara("pbid", pgBarId);
			var data = AjaxUtil.syncRequest(jdUrl);
			try {
				var dataMap = new HashMap(data);
				var pgmsg = dataMap.get("msg");
				var pgPercent = dataMap.get("percent");

				// 更该进度条信息
				if (!ProgressBarUtil.isDestroy(pgBarId)) {
					var pgBarObj = ProgressBarUtil.getBar(pgBarId);
					var finish = dataMap.get("finish");
					if (finish) {// 如果业务逻辑已经结束，则关闭进度条
						pgBarObj.destroy();
						var enableasynbar = dataMap.get("enableasynbar");
						if (enableasynbar) {
							var errmsg = dataMap.get("errmsg");
							if (!chkObjNull(errmsg)) {
								alert(errmsg);
							} else {
								var return_data = dataMap.get("return_data");
								if ("undefined" != typeof callback
										&& null != callback && "" != callback) {
									callback(return_data);
								}
							}
						}
						return;
					}
					pgBarObj.setMsg(pgmsg);
					pgBarObj.setPercent(pgPercent);
					setTimeout(setProgressBarInfo, 2000);
				}
			} catch (e) {
				// 发生了异常不予处理
			}
		}
	},

	// 刷新数据的进度条方法
	asyncRefreshBizDataViaPgBar : function(url, bartitle, options, callback) {
		// 请求前首先创建进度条对象,并将ID放置到URL中
		var pgBar = new ProgressBar(bartitle, "确定取消该业务操作吗？");
		var pgBarId = pgBar.id;
		pgBar.setMsg("操作正在进行...");
		url.addPara("__pgbarid", pgBarId);
		// url增加jbjx信息
		url.addPara("__jbjgid", getJbjgid("00000000"));
		url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		url.addPara("__yhid", getPageYhid());
		// 业务请求信息-刷新数据的JS方法
		AjaxUtil.asyncRequest(url, function(data) {
			/**
			 * 异步bar直接返回
			 */
			if (AjaxUtil.isAsynBar(data)) {
				return false;
			}
			/**
			 * 异步请求已经返回，需要销毁进度条
			 */
			if (!ProgressBarUtil.isDestroy(pgBarId)) {
				pgBar.destroy();
			}
			if (AjaxUtil.checkException(data)) {
				AjaxUtil.showException(data);
				return false;
			}

			AjaxUtil.refrshObjByDataStr(data, options);

			if ("undefined" != typeof callback && null != callback
					&& "" != callback) {
				callback(data);
			}
		}, false);

		// 3秒钟请求后台一次进度
		setTimeout(setProgressBarInfo, 1000);

		function setProgressBarInfo() {
			var jdUrl = new URL("taglib.do", "getProgressBarInfo");
			jdUrl.addPara("pbid", pgBarId);
			var data = AjaxUtil.syncRequest(jdUrl);
			try {
				var dataMap = new HashMap(data);
				var pgmsg = dataMap.get("msg");
				var pgPercent = dataMap.get("percent");

				// 更该进度条信息
				if (!ProgressBarUtil.isDestroy(pgBarId)) {
					var pgBarObj = ProgressBarUtil.getBar(pgBarId);
					var finish = dataMap.get("finish");
					if (finish) {// 如果业务逻辑已经结束，则关闭进度条
						pgBarObj.destroy();
						var enableasynbar = dataMap.get("enableasynbar");
						if (enableasynbar) {
							var errmsg = dataMap.get("errmsg");
							if (!chkObjNull(errmsg)) {
								alert(errmsg);
							} else {
								var return_data = dataMap.get("return_data");
								AjaxUtil.refrshObjByDataStr(return_data,
										options);
								if ("undefined" != typeof callback
										&& null != callback && "" != callback) {
									callback(return_data);
								}
							}
						}
						return;
					}
					pgBarObj.setMsg(pgmsg);
					pgBarObj.setPercent(pgPercent);
					setTimeout(setProgressBarInfo, 2000);
				}
			} catch (e) {
				// 发生了异常不予处理
			}
		}
	},

	// 检查是否异步bar
	isAsynBar : function(responseText) {
		if ("string" == typeof responseText) {// 解决火狐浏览器下的相关问题。-yjc.2015年8月28日
			if (responseText
					.indexOf("<!--\r\n//enable_asynbar_0602_grace.easyFrame\r\n-->") >= 0) {
				return true;// 是
			} else {
				return false;// 不是
			}
		} else {
			return false;
		}
	},

	// 根据数据字符串刷新页面组件的方法优化
	refrshObjByDataStr : function(data, options) {
		// 将数据转换为map
		var map = new HashMap(data);
		var arrObj = options.split(",");
		for ( var i = 0, n = arrObj.length; i < n; i++) {
			var objone = arrObj[i];
			var arrobjone = objone.split(":");
			var objkey = arrobjone[0];// 业务对象的name，id
			var datakey = arrobjone[1];// 数据的key;可以通过map.get(datakey);获取数据
			var objData = map.get(datakey);// 数据

			// 查看Obj为何种对象，是否form还是grid;
			var refreshObj = getObject(objkey);
			if (refreshObj.type == "form") {
				refreshObj.setData(objData);// form刷新数据
			} else if (refreshObj.type == "querygrid") {
				refreshObj.loadData(objData);// grid刷新数据
			} else if (refreshObj.type == "printer") {// 打印对象
				refreshObj.setPrintData(objData);
			} else if (refreshObj.type == "echarts") {// echarts数据刷新
				refreshObj.clear();
				try {
					refreshObj.setOption(objData);
				} catch (e) {
					console.log(e);
				}
			} else {
				alert("该业务对象【" + objkey + "】无法进行刷新");
				return;
			}
		}
	}
};
