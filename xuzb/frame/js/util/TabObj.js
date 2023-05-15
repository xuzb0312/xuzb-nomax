/**
 * TabObj布局对象-Obj
 * <p>
 * 对于动态添加，关闭的tabs不支持使用name进行业务操作
 * 
 * </p>
 * 
 * @author yjc
 * @return
 */
function TabObj(obj) {
	this.obj = obj;
}

/**
 * 根据name获取title
 * 
 * @param name
 * @return
 */
TabObj.prototype.getTitleByName = function(name) {
	var tagname = this.obj.attr("id");
	return eval(tagname + "_tabPageKeyIndexMap.get(\"" + name + "\")");
}

/**
 * 新增一个tabpage;需要与后台相关连<br>
 * 传入了uniqueId的该标签只允许被打开一次，第二次的时候进行激活
 * 
 * @param title
 * @param iconCls
 * @param url
 *            连接字符串，或者URL对象
 * @return
 */
TabObj.prototype.addTabPage = function(title, iconCls, url, uniqueId, p_closable) {
	var result
	var frameid = randomString(16);
	var map = new HashMap();
	map.put("title", title);
	map.put("requrl", url);// 将url放到参数中
	map.put("frameid", frameid);// 将frameid也放到参数中
	if (null != iconCls && "" != iconCls) {
		map.put("iconCls", iconCls);
	}
	var closable = true;
	if (!chkObjNull(p_closable) && false == p_closable) {
		closable = false;
	}
	map.put("closable", closable);
	var uniqueIdStr = "";
	if (!chkObjNull(uniqueId)) {
		// 检查该tabpage是否已经存在
		if ($("#sys_tab_unique_" + uniqueId).length > 0) {
			this.selectByTitle(title);// tab重名的情况，会存在问题，暂时不管了
			return;
		}
		uniqueIdStr = "<div id=\"sys_tab_unique_" + uniqueId
				+ "\" style=\"display:none;\"></div>";
	}
	if ("string" == typeof url) {
		map.put("content", uniqueIdStr + "<iframe id='" + frameid
				+ "' scrolling=\"auto\" frameborder=\"0\" " + "src=\"" + url
				+ "\" " + "style=\"width:100%;height:100%;\"></iframe>");
		result = this.obj.tabs("add", map.values);
	} else {
		// 默认处理URL-增加业务信息jbjgid,jbjgfw
		var parasMap = url.paras;
		if (!parasMap.containsKey("__jbjgid")) {
			url.addPara("__jbjgid", getJbjgid("00000000"));
		}
		if (!parasMap.containsKey("__jbjgqxfw")) {
			url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		}
		if (!parasMap.containsKey("__yhid")) {
			url.addPara("__yhid", getPageYhid());
		}
		map.put("content", uniqueIdStr + "<iframe id='" + frameid
				+ "' scrolling=\"auto\" frameborder=\"0\" "
				+ "src=\"taglib.do?method=fwdPageFrame&iframeid=" + frameid
				+ "\" " + "style=\"width:100%;height:100%;\"></iframe>");
		result = this.obj.tabs("add", map.values);
		$("#" + frameid)[0].contentWindow.__requrl = url;// 传递参数，-可以支持传递大数据量数据
		$("#" + frameid)[0].contentWindow.__opener_type = "tabpage";// 打开类型为tab
		$("#" + frameid)[0].contentWindow.__con_id = this.obj.attr("id");// tab
		$("#" + frameid)[0].contentWindow.__con_tabpage_title = title;// tabpage的标题，必须唯一
	}

	return result;
}

/**
 * 根据title关闭一个标签
 * 
 * @param title
 * @return
 */
TabObj.prototype.closeByTitle = function(title) {
	return this.obj.tabs("close", title);
}

/**
 * 根据name关闭一个标签
 * 
 * @param title
 * @return
 */
TabObj.prototype.close = function(name) {
	var title = this.getTitleByName(name);
	return this.closeByTitle(title);
}

/**
 * 根据title选择一个标签
 * 
 * @param title
 * @return
 */
TabObj.prototype.selectByTitle = function(title) {
	return this.obj.tabs("select", title);
}

/**
 * 根据name选择一个标签
 * 
 * @param title
 * @return
 */
TabObj.prototype.select = function(name) {
	var title = this.getTitleByName(name);
	return this.selectByTitle(title);
}

/**
 * 根据title判断是否存在一个标签
 * 
 * @param title
 * @return
 */
TabObj.prototype.existsByTitle = function(title) {
	return this.obj.tabs("exists", title);
}

/**
 * 根据name判断是否存在一个标签
 * 
 * @param title
 * @return
 */
TabObj.prototype.exists = function(name) {
	var title = this.getTitleByName(name);
	return this.existsByTitle(title);
}