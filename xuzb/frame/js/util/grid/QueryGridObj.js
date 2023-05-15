/**
 * 对于数据展示窗口使用
 */
function QueryGridObj(obj) {
	this.obj = obj; // 数据表格对象
	this.type = "querygrid";
	this.id = this.obj.attr("id");
	this.tableobj = $("#" + this.id + "_table");
};

/**
 * 业务加载完成后需要的动态调整高度进行自适应
 * 
 * @return
 */
QueryGridObj.prototype.selfAdaptHeight = function() {
	var parent = this.obj.parent("div"); // 获取到父容器
	var parentHeight = parent.height(); // 容器高度
	var lastObj = parent.children("*").last(); // 获取最后一个元素
	var conHeight = lastObj.offset().top + lastObj.height(); // 内容高度
	var adjustHeigh = conHeight - parentHeight; // 需要修正高度
	// 窗口当前渲染高度
	var gridHeight = this.obj.height();
	gridHeight = gridHeight - adjustHeigh - QueryGridUtil.adjustHeight;
	var sumArr = this.getSumArr();
	if (sumArr.length > 0) { // 对于存在合计的需要修正合计列占用的高度
		gridHeight = gridHeight - 23; // 合计列高度
	}
	// grid的最小高度
	// 由于不同的电脑，分辨率不一致，所以通过cookies进行微调数据窗口的展示行数，适应不同分辨率。
	// ctrl+alt+F2打开cookie设置窗口
	var rowdiff = CookieUtil.get("sys_grid_row_diff");
	if (chkObjNull(rowdiff)) {
		rowdiff = 0;
	} else {
		var rowdiff = Number(rowdiff);
		if (isNaN(rowdiff)) {
			rowdiff = 0;
		}
	}
	this.initData = this.obj.data("initData"); // 初始化数据
	this.height = this.initData.get("height") + 1 + rowdiff;
	this.height = 20 * this.height; // 像素高度
	if (this.height > gridHeight) { // 如果最小高度大于计算高度，则不进行调整
		gridHeight = this.height;
	}
	// 动态调整高度
	this.tableobj.setGridHeight(gridHeight);
};

/**
 * 对于tab页的情况，切换到该页时，grid需要重新刷新一遍数据防止未知错误
 * 
 * @return
 */
QueryGridObj.prototype.refreshData4TabSelect = function() {
	if (!this.obj.data("hasRefreshData4TabSelect")) {
		this.tableobj.trigger("reloadGrid");
		this.obj.data("hasRefreshData4TabSelect", true)
	}
};

/**
 * 初始化数据表格
 */
QueryGridObj.prototype.initGrid = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	// 从初始化数据中，获取数据
	this.title = this.initData.get("title");
	this.dataSource = this.initData.get("datasource");
	this.data_uiid = this.initData.get("data_uiid");
	this.columnds = this.initData.get("columnds");
	this.columngroupds = this.initData.get("columngroupds"); // 分组信息
	this.multi = this.initData.get("multi"); // 是否多选
	this.page = this.initData.get("page"); // 是否分页
	this.edit = this.initData.get("edit"); // 是否支持编辑
	this.edittype = this.initData.get("edittype"); // 编辑模式
	this.onselectall = this.initData.get("onselectall"); // 全选触发的事件
	this.onbeforeselectrow = this.initData.get("onbeforeselectrow");
	this.ondblclickrow = this.initData.get("ondblclickrow");
	this.onrightclickrow = this.initData.get("onrightclickrow");
	this.onkeydown = this.initData.get("onkeydown");
	this.onselectrow = this.initData.get("onselectrow");
	this.groupcolumnname = this.initData.get("groupcolumnname");
	this.exportfile = this.initData.get("exportfile");
	this.sortable = this.initData.get("sortable"); // 是否允许排序
	this.sortname = this.initData.get("sortname");
	this.sortorder = this.initData.get("sortorder");
	this.frozenindex = this.initData.get("frozenindex"); // 冻结列
	this.iconcls = this.initData.get("iconcls"); // 图标样式
	// 事件的初始化操作
	if (chkObjNull(this.onbeforeselectrow)) {
		this.onbeforeselectrow = function(rowid, e) {
			return true;
		};
	} else {
		this.onbeforeselectrow = new Function("rowid", "e", "return " + this.onbeforeselectrow + "(rowid,e);");
	}
	if (chkObjNull(this.ondblclickrow)) {
		this.ondblclickrow = function(rowid, iRow, iCol, e) {
			return true;
		};
	} else {
		this.ondblclickrow = new Function("rowid", "iRow", "iCol", "e", "return " + this.ondblclickrow + "(rowid,iRow,iCol,e);");
	}
	if (chkObjNull(this.onrightclickrow)) {
		this.onrightclickrow = function(rowid, iRow, iCol, e) {
			return true;
		};
	} else {
		this.onrightclickrow = new Function("rowid", "iRow", "iCol", "e", "return " + this.onrightclickrow + "(rowid,iRow,iCol,e);");
	}
	if (chkObjNull(this.onselectrow)) {
		this.onselectrow = function(rowid, status) {
			return true;
		};
	} else {
		this.onselectrow = new Function("rowid", "status", "return " + this.onselectrow + "(rowid,status);");
	}
	if (chkObjNull(this.onselectall)) {
		this.onselectall = function(aRowids, status) {
			return true;
		};
	} else {
		this.onselectall = new Function("aRowids", "status", "return " + this.onselectall + "(aRowids, status);");
	}

	// 获取数据
	var data = [];
	if (!chkObjNull(this.dataSource)) {
		var url = new URL("taglib.do", "requestData4Grid");
		url.addPara("__uiid", this.data_uiid);
		data = AjaxUtil.syncRequest(url);
		if (AjaxUtil.checkException(data)) {
			AjaxUtil.showException(data);
			return;
		}
		data = new List(data);
		data = data.values; // 对字符串类型的数据进行转换
	}
	// 自动标识唯一id
	var multiSelMap = new HashMap(); // 多选的时候使用的
	for (var i = 0,
	n = data.length; i < n; i++) {
		var rowdata = data[i];
		rowdata.rowid = "rowid_" + i;
		if (this.multi && !this.page) { // 如果多选，将所有的全部记录起来-默认不选-且不分页
			multiSelMap.put("rowid_" + i, false);
		}
	}
	this.obj.data("maxrowid", data.length - 1);
	this.obj.data("multiSelMap", multiSelMap);
	this.obj.data("currentEditRowid", ""); // 当前编辑的行ID
	// 解析得到列
	var colsName = this.getColNames();

	// 解析列模式
	var colModel = this.getColModel();

	// 如果为多选则分页
	var multiselect = false;
	var scroll = 1; // 设置为滚动一页
	if (this.page) { // 如果分页，则滚动为false
		scroll = false;
	}
	if (this.multi) {
		multiselect = true;
	}

	// 标题
	if (!chkObjNull(this.title)) {
		if (chkObjNull(this.iconcls)) {
			this.iconcls = "icon-table";
		}
		this.title = "<div class=\"panel-icon " + this.iconcls + "\"></div><span style=\"padding-left:25px;\">" + this.title + "</span>";
	}

	// 子函数使用的数据；
	var idTmp = this.id;
	var callbackBSRFunc = this.onbeforeselectrow;
	var dealMultiSelect = function() {};
	var selecAllFunc = this.onselectall;
	var dealSelectAll = function(aRowids, status) {
		return selecAllFunc(aRowids, status);
	};
	var dealSelectRow = function(rowid, e) {
		return callbackBSRFunc(rowid, e);
	};
	if (this.multi && !this.page) {
		dealMultiSelect = function() {
			var multiSelMap = $("#" + idTmp).data("multiSelMap");
			var nowids = $("#" + idTmp + "_table").getDataIDs(); // 获取当前的所有行
			var selectrowIds = [];
			if (!chkObjNull(nowids)) {
				for (var i = 0,
				n = nowids.length; i < n; i++) {
					if (multiSelMap.get(nowids[i])) {
						selectrowIds.push(nowids[i]);
					}
				}
			}
			$("#" + idTmp + "_table").resetSelection(); // 重置选择信息
			// 执行选择
			for (var i = 0,
			n = selectrowIds.length; i < n; i++) {
				$("#" + idTmp + "_table").setSelection(selectrowIds[i], true);
			}
		};
		dealSelectAll = function(aRowids, status) {
			// 首先更改为全部不选择
			var multiSelMap = $("#" + idTmp).data("multiSelMap");
			var arrKey = multiSelMap.keySet();
			if (!chkObjNull(arrKey)) {
				for (var i = 0,
				n = arrKey.length; i < n; i++) {
					multiSelMap.put(arrKey[i], false);
				}
			}
			// 如果全选的情况
			if (status) {
				// 只处理当前数据窗口的数据为全选
				var cdids = getObject(idTmp).getCurrentDataIds();
				if (!chkObjNull(cdids)) {
					for (var i = 0,
					n = cdids.length; i < n; i++) {
						multiSelMap.put(cdids[i], true);
					}
				}
			}

			$("#" + idTmp).data("multiSelMap", multiSelMap);

			return selecAllFunc(aRowids, status);
		};
		dealSelectRow = function dealSelectRow(rowid, e) {
			var result = callbackBSRFunc(rowid, e);
			if (result) {
				var multiSelMap = $("#" + idTmp).data("multiSelMap");
				// 首先判断该行是否已经被选中
				if (multiSelMap.get(rowid)) {
					multiSelMap.put(rowid, false);
				} else {
					multiSelMap.put(rowid, true);
				}
			}
			return result;
		};
	}

	// 是否展示合计列
	var isshowfooterrow = true;
	if (this.getSumArr().length <= 0) {
		isshowfooterrow = false;
	}
	var oncellselect = function(rowid, iCol, cellcontent, e) {
		return true;
	};
	var onelRow = this.onselectrow; // 参数用于向下传递
	var onselectrowTmp = function(rowid, status) {
		onelRow(rowid, status);
		return true;
	};
	if (this.edit && ("smart" == this.edittype || "onlyedit" == this.edittype)) {
		onselectrowTmp = function(rowid, status) {
			var gridTemp = getObject(idTmp);
			var lastsel = gridTemp.obj.data("currentEditRowid");
			if (rowid && rowid !== lastsel) {
				if (!chkObjNull(lastsel)) {
					gridTemp.tableobj.jqGrid("saveRow", lastsel, {
						url: "clientArray"
					});
				}
				gridTemp.tableobj.jqGrid("editRow", rowid);
				gridTemp.obj.data("currentEditRowid", rowid)
			}

			onelRow(rowid, status);
		};
		oncellselect = function(rowid, iCol, cellcontent, e) {
			var gridTemp = getObject(idTmp);
			var multi = gridTemp.obj.data("initData").get("multi"); // 初始化数据
			var iColIndex = iCol - 1;
			if (multi) {
				iColIndex = iCol - 2;
			}
			var iColName = gridTemp.getColNameList()[iColIndex];
			setTimeout(function() {
				$("#" + rowid + "_" + iColName).focus(); // 焦点
			},
			20);
		};
	}
	var gridOptions = {
		datatype: "local",
		data: data,
		multiselect: multiselect,
		colNames: colsName,
		colModel: colModel,
		pager: "#" + this.id + "_pager",
		rownumbers: true,
		rownumWidth: 35,
		rowNum: 50,
		gridview: true,
		caption: this.title,
		scroll: scroll,
		shrinkToFit: false,
		footerrow: isshowfooterrow,
		viewrecords: true,
		autowidth: true,
		height: 100,
		beforeSelectRow: function(rowid, e) {
			return dealSelectRow(rowid, e);
		},
		ondblClickRow: this.ondblclickrow,
		onRightClickRow: this.onrightclickrow,
		gridComplete: function() {
			dealMultiSelect();
		},
		onSelectAll: function(aRowids, status) {
			dealSelectAll(aRowids, status);
		},
		onSelectRow: onselectrowTmp,
		onCellSelect: oncellselect
	};

	// 排序
	if (!chkObjNull(this.sortname)) {
		gridOptions.sortname = this.sortname;
	}
	if (!chkObjNull(this.sortorder)) {
		gridOptions.sortorder = this.sortorder;
	}

	if (!chkObjNull(this.groupcolumnname)) {
		gridOptions.grouping = true;
		gridOptions.groupingView = {
			groupField: [this.groupcolumnname],
			groupDataSorted: true,
			groupText: ['<b>{0} - {1}条记录</b>']
		};
	}

	// 开始创建grid
	this.tableobj.jqGrid(gridOptions);

	// 增加高级查询的功能
	this.tableobj.jqGrid("navGrid", "#" + this.id + "_pager", {
		edit: false,
		add: false,
		del: false
	},
	{},
	{},
	{},
	{
		multipleSearch: true,
		multipleGroup: true
	});

	// 处理合计列-需要把数据也一并传入
	this.dealSumCol(data);

	// 放到子函数使用的数据
	var tableObjTmp = this.tableobj;

	// 数据导出等自定义按钮
	if (this.exportfile) {
		this.tableobj.navButtonAdd("#" + this.id + "_pager", {
			caption: "导出文件",
			buttonicon: "ui-icon-document",
			onClickButton: function() {
				getObject(idTmp).exportData();
			},
			position: "last"
		});
	}

	if (this.multi && !this.page) {
		this.tableobj.navButtonAdd("#" + this.id + "_pager", {
			caption: "部分选择",
			buttonicon: "ui-icon-check",
			onClickButton: function() {
				getObject(idTmp).selectPartRow();
			},
			position: "last"
		});
	}

	if (this.edit && "normal" == this.edittype) {
		// 支持编辑的情况
		this.tableobj.navButtonAdd("#" + this.id + "_pager", {
			caption: "新增",
			buttonicon: "ui-icon-plus",
			onClickButton: function() {
				getObject(idTmp).addOneRow4Edit();
			},
			position: "last"
		});
		this.tableobj.navButtonAdd("#" + this.id + "_pager", {
			caption: "编辑",
			buttonicon: "ui-icon-pencil",
			onClickButton: function() {
				getObject(idTmp).moidfyOneRow4Edit();
			},
			position: "last"
		});
		this.tableobj.navButtonAdd("#" + this.id + "_pager", {
			caption: "删除",
			buttonicon: "ui-icon-trash",
			onClickButton: function() {
				getObject(idTmp).deleteRows4Edit();
			},
			position: "last"
		});
	}
	// 对于编辑情况
	if (this.edit && "smart" == this.edittype) {
		this.tableobj.navButtonAdd("#" + this.id + "_pager", {
			caption: "新增",
			buttonicon: "ui-icon-plus",
			onClickButton: function() {
				getObject(idTmp).addBlankRow();
			},
			position: "last"
		});
		this.tableobj.navButtonAdd("#" + this.id + "_pager", {
			caption: "删除",
			buttonicon: "ui-icon-trash",
			onClickButton: function() {
				getObject(idTmp).deleteRows4Edit();
			},
			position: "last"
		});
	}

	// 单选启用上下移动键
	if (!this.multi) {
		this.tableobj.jqGrid("bindKeys");
	}

	// 列分组
	var groupHeadersArr = [];
	for (var i = 0,
	n = this.columngroupds.length; i < n; i++) {
		var columngroupdsRow = this.columngroupds[i];
		var cg_startname = columngroupdsRow.startname;
		var cg_num = columngroupdsRow.num;
		var cg_title = columngroupdsRow.title;
		var cg_helptip = columngroupdsRow.helptip;
		if (!chkObjNull(cg_helptip)) {
			cg_title = cg_title + "<i _tipmsg=\"" + cg_helptip + "\" class=\"input-tip-msg-icon\"><svg viewBox=\"64 64 896 896\" class=\"\" data-icon=\"info-circle\" width=\"1em\" height=\"1em\" fill=\"currentColor\" aria-hidden=\"true\" focusable=\"false\"><path d=\"M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z\"></path><path d=\"M464 336a48 48 0 1 0 96 0 48 48 0 1 0-96 0zm72 112h-48c-4.4 0-8 3.6-8 8v272c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V456c0-4.4-3.6-8-8-8z\"></path></svg></i>";
		}
		if (cg_num > 0) {
			var ghJson = {
				startColumnName: cg_startname,
				numberOfColumns: cg_num,
				titleText: cg_title
			};
			groupHeadersArr.push(ghJson);
		}
	}
	if (groupHeadersArr.length > 0) {
		this.tableobj.jqGrid("setGroupHeaders", {
			useColSpanStyle: true,
			groupHeaders: groupHeadersArr
		});
	}

	// 在创建完成5毫秒之后，自动调整宽度到合适位置
	setTimeout(function() {
		tableObjTmp.setGridWidth($("#" + idTmp + "_width_use").width() - 5);
	},
	500);

	// 窗口大小变更时，此grid的也跟着变更大小-绑定事件
	$(window).resizeEnd({
		delay: 500
	},
	function() {
		getObject(idTmp).selfAdaptHeight();
		tableObjTmp.setGridWidth($("#" + idTmp + "_width_use").width() - 5);
	});

	// 绑定onkeydown事件
	if (!chkObjNull(this.onkeydown)) {
		this.onkeydown = new Function("event", "return " + this.onkeydown + "(event);");
		this.tableobj.keydown(this.onkeydown);
	}

	// 处理冻结列-分页的情况
	if (this.page && this.frozenindex >= 0) {
		this.tableobj.jqGrid("setFrozenColumns");
		if (data.length > 0) { // 初始数据大于0行，调整列高度
			this.hackFrozenColmHeight();
		}
	}
};

/**
 * 部分选择
 */
QueryGridObj.prototype.selectPartRow = function() {
	var data = this.getData();
	if (chkObjNull(data) || data.length <= 0) {
		alert("没有数据，无法【部分选择】");
		return;
	}
	var idTmp = this.id;
	var urlS = new URL("taglib.do", "fwdQueryGridPartSelect");
	urlS.addPara("size", data.length);
	openWindow("部分选择（按照加载数据行的顺序进行部分数据选择）", "icon-ok", urlS, 410, 180,
	function(para) {
		if (chkObjNull(para)) {
			return;
		}
		var qshh = para.qshh;
		var zzhh = para.zzhh;
		var gridObj = getObject(idTmp);
		gridObj.unSelectAll();
		for (; qshh <= zzhh; qshh++) {
			var rowid = "rowid_" + qshh;
			gridObj.obj.data("multiSelMap").put(rowid, true);
			gridObj.tableobj.setSelection(rowid, true);
		}

		// 选择后的事件
		var onselectpartrow = gridObj.obj.data("initData").get("onselectpartrow");
		if (!chkObjNull(onselectpartrow)) {
			onselectpartrow = new Function("qshh", "zzhh", "return " + onselectpartrow + "(qshh, zzhh);");
			onselectpartrow(qshh, zzhh);
		}
		return true;
	});
};

/**
 * 新增一行数据-弹出窗口-不进行服务端的操作，只进行前台的数据操作
 * 
 * @param data
 * @return
 */
QueryGridObj.prototype.addOneRow4Edit = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	var columnds = this.initData.get("columnds"); // 初始化数据
	var idTmp = this.id;
	var url = new URL("taglib.do", "fwdQueryGridDataEdit4Add");
	url.addPara("columnds", JSON.stringify(columnds));
	openWindow("数据新增", null, url, 550, 350,
	function(data) {
		if (chkObjNull(data)) {
			return;
		}
		var gridObj = getObject(idTmp);
		gridObj.addRowMapData(data);
	});
};

/**
 * 处理可编辑数据到后台内存中-最后提交数据前的操作
 */
QueryGridObj.prototype.dealEditableData4Edit = function() {
	var lastsel = this.obj.data("currentEditRowid");
	if (!chkObjNull(lastsel)) {
		this.tableobj.jqGrid("saveRow", lastsel, {
			url: "clientArray"
		});
		this.obj.data("currentEditRowid", "")
	}
};

/**
 * 增加一行空行
 * 
 * @return
 */
QueryGridObj.prototype.addBlankRow = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	var columnds = this.initData.get("columnds"); // 初始化数据
	var nullMap = new HashMap();
	for (var i = 0,
	n = columnds.length; i < n; i++) {
		var name = columnds[i].columnpara.name;
		var datatype = columnds[i].columnpara.datatype;
		if ("number" == datatype) {
			nullMap.put(name, 0);
		} else {
			nullMap.put(name, "");
		}
	}
	var rowid = this.addRowMapData(nullMap);
	this.selectOneRow(rowid);
};

/**
 * 编辑一行数据-弹出窗口-不进行服务端的操作，只进行前台的数据操作
 * 
 * @param data
 * @return
 */
QueryGridObj.prototype.moidfyOneRow4Edit = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	var idTmp = this.id;
	var rowid = "";
	if (this.multi) {
		var rowids = this.getSelectedRows();
		if (rowids) {
			if (rowids.length == 1) {
				rowid = rowids[0];
			}
		}
	} else {
		var rowidt = this.getSelectedRow();
		if (rowidt) {
			rowid = rowidt;
		}
	}
	if (chkObjNull(rowid)) {
		alert("请选择一行数据进行此操作。");
		return;
	}
	var columnds = this.initData.get("columnds"); // 初始化数据
	var rowdata = this.getRowData(rowid);
	var url = new URL("taglib.do", "fwdQueryGridDataEdit4Modify");
	url.addPara("data", JSON.stringify(rowdata));
	url.addPara("columnds", JSON.stringify(columnds));
	openWindow("数据修改", null, url, 550, 350,
	function(data) {
		if (chkObjNull(data)) {
			return;
		}
		var gridObj = getObject(idTmp);
		gridObj.updateRowMapData(rowid, data);
	});
}
/**
 * 删除选中的数据数据-弹出窗口-不进行服务端的操作，只进行前台的数据操作
 * 
 * @param data
 * @return
 */
QueryGridObj.prototype.deleteRows4Edit = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	// 删除前触发的事件
	this.onbeforedeleterow = this.initData.get("onbeforedeleterow");
	if (chkObjNull(this.onbeforedeleterow)) {
		this.onbeforedeleterow = function(rowid) {
			return true;
		};
	} else {
		this.onbeforedeleterow = new Function("rowid", "return " + this.onbeforedeleterow + "(rowid);");
	}
	var rowid = this.multi ? this.getSelectedRows() : this.getSelectedRow();
	if (!this.onbeforedeleterow(rowid)) {
		return true;
	}

	if (this.multi) {
		var rowids = this.getSelectedRows();
		if (rowids) {
			for (var i = 0,
			n = rowids.length; i < n; i++) {
				this.delRowData(rowids[i]);
			}
		}
	} else {
		var rowid = this.getSelectedRow();
		if (rowid) {
			this.delRowData(rowid);
		}
	}
}

/**
 * 处理合计列
 * 
 * @return
 */
QueryGridObj.prototype.dealSumCol = function(data) {
	var sumArr = this.getSumArr();
	if (sumArr.length <= 0) {
		return;
	}
	var sumMap = new HashMap();
	sumMap.put("rn", "合计");
	for (var i = 0,
	n = sumArr.length; i < n; i++) {
		var name = sumArr[i].name;
		var sumValue = 0.00;
		// 获取合计
		if (!chkObjNull(data)) {
			for (var j = 0,
			m = data.length; j < m; j++) {
				var rowData = data[j];
				sumValue = sumValue + rowData[name];
			}
		}
		sumMap.put(name, sumValue);
	}
	// 设置合计
	this.tableobj.footerData("set", sumMap.values);
	return sumMap;
};

/**
 * 计算选择行的合计列合计信息
 */
QueryGridObj.prototype.calcSelectedSumCol = function() {
	var sumMap = this.dealSumCol(this.getSelectData());
	return sumMap;
};

/**
 * 获取合计列的列表
 */
QueryGridObj.prototype.getSumArr = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	this.columnds = this.initData.get("columnds");
	var sumArr = [];
	for (var i = 0,
	n = this.columnds.length; i < n; i++) {
		var type = this.columnds[i].type;
		var para = this.columnds[i].columnpara;
		if ("columntext" == type.toLowerCase()) {
			var dataType = para.datatype;
			if ("number" == dataType.toLowerCase()) {
				if (para.sum) {
					sumArr.push({
						name: para.name,
						mask: para.mask
					});
				}
			}
		}
	}
	return sumArr;
};

/**
 * 获取列名称
 * 
 * @return
 */
QueryGridObj.prototype.getColNames = function() {
	var arrCols = [];
	for (var i = 0,
	n = this.columnds.length; i < n; i++) {
		var labelName = this.columnds[i].columnpara.label;
		var helpTip = this.columnds[i].columnpara.helptip;
		if (!chkObjNull(helpTip)) {
			labelName = labelName + "<i _tipmsg=\"" + helpTip + "\" class=\"input-tip-msg-icon\"><svg viewBox=\"64 64 896 896\" class=\"\" data-icon=\"info-circle\" width=\"1em\" height=\"1em\" fill=\"currentColor\" aria-hidden=\"true\" focusable=\"false\"><path d=\"M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z\"></path><path d=\"M464 336a48 48 0 1 0 96 0 48 48 0 1 0-96 0zm72 112h-48c-4.4 0-8 3.6-8 8v272c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V456c0-4.4-3.6-8-8-8z\"></path></svg></i>";
		}
		arrCols.push(labelName);
	}
	arrCols.push("ROWID");
	return arrCols;
};

/**
 * 工具方法，获取列明的数组
 * 
 * @return
 */
QueryGridObj.prototype.getColNameList = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	this.columnds = this.initData.get("columnds"); // 初始化数据
	var arrCols = [];
	for (var i = 0,
	n = this.columnds.length; i < n; i++) {
		var name = this.columnds[i].columnpara.name;
		arrCols.push(name);
	}
	return arrCols;
};

/**
 * 获取列参数信息
 * 
 * @return
 */
QueryGridObj.prototype.getColModel = function() {
	var arrCols = [];
	var isrowedit = this.edit && ("smart" == this.edittype || "onlyedit" == this.edittype);
	for (var i = 0,
	n = this.columnds.length; i < n; i++) {
		var type = this.columnds[i].type;
		var para = this.columnds[i].columnpara;

		// 放置参数数据
		var jqColPara = {};

		// 是否能进行排序-默认是可排序的
		if (!this.sortable) {
			jqColPara.sortable = false;
		}

		// 冻结列处理
		if (this.page && i <= this.frozenindex) {
			jqColPara.frozen = true;
		}
		// 对于搜索情况全部展示
		jqColPara.searchoptions = {
			sopt: ['eq', 'ne', 'lt', 'le', 'gt', 'ge', 'bw', 'bn', 'in', 'ni', 'ew', 'en', 'cn', 'nc']
		};
		if ("columntext" == type.toLowerCase()) {
			// 获取参数
			var name = para.name; // 唯一id
			var align = para.align; // 文本方向-默认：文本左对齐，日期居中；数字右对齐
			var width = para.width; // 宽度-以文字的个数为准
			var hidden = para.hidden; // 是否隐藏
			var mask = para.mask; // 标签的掩码值
			var dataType = para.datatype; // 标签文本框中输入值的类别
			var sourceMask = para.sourcemask; // 当时间类型是String时，source的mask。
			var sum = para.sum; // 是否合计
			var readonly = para.readonly; // 是否只读
			var onsearchclick = para.onsearchclick; // 编辑模式下的事件-lov
			// 通用
			jqColPara.name = name;
			jqColPara.index = name;
			if (0 != width) {
				jqColPara.width = (width * 15) + "px";
			}
			jqColPara.align = align;
			jqColPara.hidden = hidden;
			if ("string" == dataType.toLowerCase()) {
				// 解决汉字需要按照字母排序的问题-只获取前2个字字符排序
				jqColPara.sorttype = function(cell, obj) {
					if (chkObjNull(cell)) {
						return "";
					}
					cell = cell.trim();
					if (cell.length <= 2) {
						return PinyinUtil.getSortPy(cell) + cell;
					} else {
						var preSortStr = PinyinUtil.getSortPy(cell.substring(0, 2));
						preSortStr = preSortStr + cell;
						return preSortStr;
					}
				};
				// 原样输出，没有特殊定制
				if (isrowedit && !readonly) {
					jqColPara.editable = true;
					if (!chkObjNull(onsearchclick)) {
						jqColPara.edittype = "custom";
						jqColPara.editoptions = {
							onsearchclick: onsearchclick,
							custom_element: function(value, options) {
								var rowidTemp = options.id.replace("_" + options.name, "");
								var eleStr = "<input title=\"双击选择...\" style=\"width:98%;\" class=\"gridTxtSearchBtn\" " + "placeholder=\"双击选择...\" type=\"text\" ondblclick=\"" + options.onsearchclick + "('" + rowidTemp + "','" + options.name + "');\"/>";
								var jqObj = $(eleStr);
								jqObj.val(value);
								return jqObj.get(0);
							},
							custom_value: function(elem) {
								return $(elem).val();
							}
						};
					}
				}
			} else if ("date" == dataType.toLowerCase()) {
				jqColPara.sorttype = function(cell, obj) {
					if (chkObjNull(cell)) {
						return "";
					} else if ("string" == typeof cell) {
						return cell;
					} else {
						try {
							return cell.time; // 后台传回的是日期格式--直接使用time判断
						} catch(e) {
							return "";
						}
					}
				};

				jqColPara.formatoptions = {
					mask: mask,
					sourceMask: sourceMask
				};
				jqColPara.formatter = function(cellvalue, options, rowObject) {
					var maskT = options.colModel.formatoptions.mask;
					var sourceMaskT = options.colModel.formatoptions.sourceMask;
					if (chkObjNull(cellvalue)) {
						cellvalue = "";
					} else {
						if (chkObjNull(maskT)) {
							maskT = "yyyy-MM-dd";
						}
						if (typeof(cellvalue) == "object") { // 如果为日期类型
							var celldate = new Date(cellvalue.time);
							cellvalue = DateUtil.getDateString(celldate, maskT);
						} else {
							if (chkObjNull(sourceMaskT)) {
								sourceMaskT = "yyyyMMdd";
							}
							cellvalue = DateUtil.changeFormat(cellvalue, sourceMaskT, maskT);
						}
					}
					return cellvalue;
				};
				if (isrowedit && !readonly) {
					jqColPara.editable = true;
					jqColPara.edittype = "custom";
					jqColPara.editoptions = {
						mask: mask,
						sourceMask: sourceMask,
						custom_element: function(value, options) {
							var element = document.createElement("input");
							element.type = "text";
							var elementObj = $(element);
							elementObj.attr("id", options.id);
							elementObj.attr("name", options.name);
							elementObj.attr("role", "textbox");
							elementObj.css("text-align", "center");
							elementObj.css("width", "98%");
							elementObj.attr("dataType", "date");
							elementObj.attr("mask", options.mask);
							elementObj.attr("sourceMask", options.sourceMask);

							new CalendarObj(elementObj);
							new DateTimeMask(elementObj);

							elementObj.val(value);
							return element;
						},
						custom_value: function(elem) {
							var elementObj = $(elem);
							var eleValue = elementObj.val();
							if (chkObjNull(eleValue)) {
								eleValue = "";
							} else {
								var mask = elementObj.attr("mask");
								if (chkObjNull(mask)) {
									mask = "yyyy-MM-dd";
								}
								var sourcemask = elementObj.attr("sourceMask");
								if (chkObjNull(sourcemask)) {
									sourcemask = "yyyyMMdd";
								}
								eleValue = DateUtil.changeFormat(eleValue, mask, sourcemask);
							}
							return eleValue;
						}
					};
				}
			} else if ("number" == dataType.toLowerCase()) {
				jqColPara.sorttype = "number";
				jqColPara.formatoptions = {
					mask: mask
				};
				jqColPara.formatter = function(cellvalue, options, rowObject) {
					var maskT = options.colModel.formatoptions.mask;
					if (chkObjNull(cellvalue)) {
						cellvalue = 0;
					}
					if (chkObjNull(maskT)) {
						maskT = "###########################0.00";
					}
					var valueTmpNm = Number(cellvalue);
					if (isNaN(valueTmpNm)) {
						return "NaN";
					}
					cellvalue = NumberUtil.getShowValueByMask(maskT, valueTmpNm.toString());
					return cellvalue;
				};
				if (isrowedit && !readonly) {
					jqColPara.editable = true;
					jqColPara.edittype = "custom";
					jqColPara.editoptions = {
						mask: mask,
						gridObj_id: this.id,
						isSum: sum,
						custom_element: function(value, options) {
							var element = document.createElement("input");
							element.type = "text";
							var elementObj = $(element);
							elementObj.attr("id", options.id);
							elementObj.attr("name", options.name);
							elementObj.attr("gridObj_id", options.gridObj_id);
							elementObj.attr("role", "textbox");
							elementObj.css("text-align", "right");
							elementObj.css("width", "98%");
							elementObj.attr("dataType", "number");
							elementObj.attr("mask", options.mask);
							new NumberMask(elementObj);
							elementObj.val(value);
							elementObj.attr("_value", value);

							// 处理合计
							if (options.isSum) {
								elementObj.change(function(e) {
									var elementObj = $(e.target);
									var id = elementObj.attr("id");
									var name = elementObj.attr("name");
									var rowid = id.replace("_" + name, "");
									var eleValue = elementObj.val();
									if (chkObjNull(eleValue)) {
										eleValue = 0;
									} else {
										var mask = elementObj.attr("mask");
										if (chkObjNull(mask)) {
											mask = "###########################0.00";
										}
										eleValue = NumberUtil.getRealValue(eleValue, mask);
									}
									var eleValue = Number(eleValue);
									if (isNaN(eleValue)) {
										eleValue = 0;
									}
									var eleSValue = elementObj.attr("_value");
									elementObj.attr("_value", eleValue);

									var gridObj = getObject(elementObj.attr("gridObj_id"));
									var sumArr = gridObj.getSumArr();
									var ysumMap = new HashMap(gridObj.tableobj.footerData("get"));
									var xsumMap = new HashMap();
									xsumMap.put("rn", "合计");
									for (var i = 0,
									n = sumArr.length; i < n; i++) {
										var nameT = sumArr[i].name;
										var maskT = sumArr[i].mask;
										if (chkObjNull(maskT)) {
											maskT = "###########################0.00";
										}
										var sumValue = NumberUtil.getRealValue(ysumMap.get(nameT), maskT);
										if (nameT == name) {
											sumValue = sumValue - eleSValue + eleValue;
										} else {
											sumValue = sumValue;
										}
										xsumMap.put(nameT, sumValue);
									}
									// 设置合计
									gridObj.tableobj.footerData("set", xsumMap.values);
								});
							}
							return element;
						},
						custom_value: function(elem) {
							var elementObj = $(elem);
							var eleValue = elementObj.val();
							if (chkObjNull(eleValue)) {
								eleValue = 0;
							} else {
								var mask = elementObj.attr("mask");
								if (chkObjNull(mask)) {
									mask = "###########################0.00";
								}
								eleValue = NumberUtil.getRealValue(eleValue, mask);
							}
							var eleValue = Number(eleValue);
							if (isNaN(eleValue)) {
								eleValue = 0;
							}
							return eleValue;
						}
					};
				}
			} else {
				throw new Error("传入的子标签数据类型不合法");
			}
		} else if ("columndropdown" == type.toLowerCase()) {
			var name = para.name;
			var align = para.align;
			var width = para.width;
			var hidden = para.hidden;
			var readonly = para.readonly; // 是否只读
			var dsCode = para.dscode; // 后台进行数据组装好后，此处可以获取的得到
			var codeMap = new HashMap();
			if (isrowedit && !readonly) {
				jqColPara.editable = true;
				jqColPara.edittype = "select";
				codeMap.put("", "--请选择--");
			}
			for (var j = 0,
			m = dsCode.length; j < m; j++) {
				var code = dsCode[j].code;
				var content = dsCode[j].content;
				if ((!isrowedit || readonly) && !chkObjNull(dsCode[j].color)) {
					codeMap.put(code, "<span class='grid-tag' style='box-shadow: 0px 0px 2px 0px " + dsCode[j].color + ";color:" + dsCode[j].color + ";'>" + content + "</span>");
				} else {
					codeMap.put(code, content);
				}
			}

			jqColPara.name = name;
			jqColPara.index = name;
			if (0 != width) {
				jqColPara.width = (width * 15) + "px";
			}
			jqColPara.align = align;
			jqColPara.hidden = hidden;
			// 下拉框
			jqColPara.formatter = "select";
			jqColPara.editoptions = {
				value: codeMap.values
			};

			// 对于下拉菜单-搜索列重置
			jqColPara.stype = "select";
			jqColPara.searchoptions = {
				sopt: ['eq', 'ne', 'lt', 'le', 'gt', 'ge'],
				value: codeMap.values
			};
		} else if ("columncheckbox" == type.toLowerCase()) {
			var name = para.name; // 唯一id
			var width = para.width; // 宽度-以文字的个数为准
			var hidden = para.hidden; // 是否隐藏
			var readonly = para.readonly; // 是否只读
			jqColPara.name = name;
			jqColPara.index = name;
			if (isrowedit && !readonly) {
				jqColPara.editable = true;
				jqColPara.edittype = "checkbox";
				jqColPara.editoptions = {
					value: "1:0"
				};
			}
			if (0 != width) {
				jqColPara.width = (width * 15) + "px";
			}
			jqColPara.align = "center";
			jqColPara.hidden = hidden;
			jqColPara.formatter = "checkbox";

			// 搜索列重置
			jqColPara.stype = "select";
			jqColPara.searchoptions = {
				sopt: ['eq', 'ne', 'lt', 'le', 'gt', 'ge'],
				value: {
					"1": "是",
					"0": "否"
				}
			};
		} else if ("columnmultidropdown" == type.toLowerCase()) {
			var name = para.name;
			var align = para.align;
			var width = para.width;
			var hidden = para.hidden;
			var readonly = para.readonly; // 是否只读
			var dsCode = para.dscode; // 后台进行数据组装好后，此处可以获取的得到
			// 事件
			var onclick = para.onclick;
			var onchange = para.onchange;
			var ondblclick = para.ondblclick;
			var onblur = para.onblur;
			var onfocus = para.onfocus;

			// 数据构建
			jqColPara.name = name;
			jqColPara.index = name;
			if (0 != width) {
				jqColPara.width = (width * 15) + "px";
			}
			jqColPara.align = align;
			jqColPara.hidden = hidden;

			// 是否可渲染标签
			var canParseTag = false;
			if (!isrowedit || readonly) {
				canParseTag = true;
			}
			// 其他的操作
			jqColPara.formatoptions = {
				dsCode: dsCode,
				canParseTag: canParseTag
			};
			jqColPara.formatter = function(cellvalue, options, rowObject) {
				if (chkObjNull(cellvalue)) {
					return "";
				} else {
					var dsCodeT = options.colModel.formatoptions.dsCode;
					var canParseTagT = options.colModel.formatoptions.canParseTag;
					return QueryGridUtil.getContentStrByCodeStr(cellvalue, dsCodeT, canParseTagT);
				}
			};

			// 编辑
			if (isrowedit && !readonly) {
				jqColPara.editable = true;
				jqColPara.edittype = "custom";
				jqColPara.editoptions = {
					dsCode: dsCode,
					onclick: onclick,
					onchange: onchange,
					ondblclick: ondblclick,
					onfocus: onfocus,
					onblur: onblur,
					custom_element: function(value, options) {
						var rowidTemp = options.id.replace("_" + options.name, "");
						var realvalue = QueryGridUtil.getCodeStrByContentStr(value, options.dsCode);
						var dataOptStr = JSON.stringify(QueryGridUtil.chageCode2Data(options.dsCode));
						dataOptStr = dataOptStr.replace(/\"/g, "'");
						var eleStr = "";
						eleStr = eleStr + " <input obj_type=\"multidropdownlist\" class=\"mulitdropdownlist\" id=\"";
						eleStr = eleStr + options.id + "\" name=\"";
						eleStr = eleStr + options.id + " \" _required=\"false\" ";
						if ("v2" == GlobalVars.VIEW_TYPE) {
							eleStr = eleStr + " style=\"background:#FFFFFF;height:20px;width:100%;padding:4px 3px;\" ";
						} else {
							eleStr = eleStr + " style=\"background:#FFFFFF;height:20px;width:100%;padding:1px 3px;\" ";
						}
						eleStr = eleStr + " _value=\"" + realvalue + "\" ";
						eleStr = eleStr + " value=\"" + value + "\" ";
						eleStr = eleStr + " data-opt=\"" + dataOptStr + "\" ";
						if (!chkObjNull(options.onclick)) {
							eleStr = eleStr + " onclick=\"" + options.onclick + "('" + rowidTemp + "','" + options.name + "');\" ";
						}
						if (!chkObjNull(options.onchange)) {
							eleStr = eleStr + " onchange=\"" + options.onchange + "('" + rowidTemp + "','" + options.name + "');\" ";
						}
						if (!chkObjNull(options.ondblclick)) {
							eleStr = eleStr + " ondblclick=\"" + options.ondblclick + "('" + rowidTemp + "','" + options.name + "');\" ";
						}
						if (!chkObjNull(options.onfocus)) {
							eleStr = eleStr + " onfocus=\"" + options.onfocus + "('" + rowidTemp + "','" + options.name + "');\" ";
						}
						if (!chkObjNull(options.onblur)) {
							eleStr = eleStr + " onblur=\"" + options.onblur + "('" + rowidTemp + "','" + options.name + "');\" ";
						}
						eleStr = eleStr + "/>";
						var jqObj = $(eleStr);
						var mulitdropdownlist = new MultiDropdownListObj(jqObj);
						mulitdropdownlist.initEvent();
						return jqObj.get(0);
					},
					custom_value: function(elem) {
						var elementObj = $(elem);
						return getObject(elementObj).getValue();
					}
				};
			}
		} else if ("columnbuttons" == type.toLowerCase()) {
			// 获取参数
			var name = para.name; // 唯一id
			var dsBtns = para.dsbtns; // 按钮新
			var width = para.width; // 宽度-以文字的个数为准
			// 通用
			jqColPara.name = name;
			jqColPara.index = name;
			if (0 != width) {
				jqColPara.width = (width * 15) + "px";
			}
			jqColPara.align = "center";
			// 其他的操作
			jqColPara.formatoptions = {
				dsBtns: dsBtns
			};
			jqColPara.formatter = function(cellvalue, options, rowObject) {
				var dsBtns = options.colModel.formatoptions.dsBtns;
				var isShowAllBtn = true;
				var map = new HashMap();
				if ("string" == typeof cellvalue) {
					isShowAllBtn = false;
					var arrKey = cellvalue.split(",");
					for (var i = 0,
					n = arrKey.length; i < n; i++) {
						var key = arrKey[i];
						if (chkObjNull(key)) {
							continue;
						}
						map.put(key, null);
					}
				}
				cellvalue = "";
				for (var i = 0,
				n = dsBtns.length; i < n; i++) {
					var btn = dsBtns[i];
					var key = btn.key;
					if (!isShowAllBtn) {
						if (!map.containsKey(key)) {
							continue;
						}
					}
					var value = btn.value;
					var onclick = btn.onclick;
					var color = btn.color;
					var rowid = rowObject.rowid;
					if (!chkObjNull(cellvalue)) {
						cellvalue = cellvalue + " | ";
					}
					cellvalue = cellvalue + '<a href="javascript:void(0);" onclick="' + onclick + '(\'' + rowid + '\');" style="color:' + color + ';font-weight:bold;">' + value + '</a>';
				}
				return cellvalue;
			};
		} else {
			throw new Error("传入的子标签类型不合法");
		}
		arrCols.push(jqColPara);
	}
	arrCols.push({
		name: "rowid",
		index: "rowid",
		hidden: true,
		key: true
	});
	return arrCols;
};

/**
 * 数据清空方法
 * 
 * @return
 */
QueryGridObj.prototype.clear = function() {
	this.loadData([]); // 加载空数据
	return true;
};

/**
 * 新增一行数据
 * 
 * @return
 */
QueryGridObj.prototype.addRowData = function(jsondata, position) {
	var sumArr = this.getSumArr();
	if (sumArr.length > 0) {
		var ysumMap = new HashMap(this.tableobj.footerData("get"));
		var xsumMap = new HashMap();
		xsumMap.put("rn", "合计");
		for (var i = 0,
		n = sumArr.length; i < n; i++) {
			var name = sumArr[i].name;
			var mask = sumArr[i].name;
			if (chkObjNull(mask)) {
				mask = "###########################0.00";
			}
			var sumValue = NumberUtil.getRealValue(ysumMap.get(name), mask);
			sumValue = sumValue + jsondata[name];
			xsumMap.put(name, sumValue);
		}
		// 设置合计
		this.tableobj.footerData("set", xsumMap.values);
	}
	var maxrowid = this.obj.data("maxrowid");
	maxrowid = maxrowid + 1;
	this.obj.data("maxrowid", maxrowid);
	jsondata.rowid = "rowid_" + maxrowid;
	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	this.page = this.initData.get("page");
	if (this.multi && !this.page) { // 如果为多选模式在选择的行中增加这些项
		this.obj.data("multiSelMap").put(jsondata.rowid, false);
	}
	if (chkObjNull(position)) {
		position = QueryGridUtil.defaultAddRowPostion;
	}
	this.tableobj.addRowData(jsondata.rowid, jsondata, position); // 就在表格追加一行数据
	return jsondata.rowid; // 返回rowid字段
};

/**
 * 新增一行数据
 * 
 * @return
 */
QueryGridObj.prototype.addRowMapData = function(mapdata, position) {
	return this.addRowData(mapdata.values, position);
};

/**
 * 获取选中行-返回rowid
 */
QueryGridObj.prototype.getSelectedRow = function() {
	var id = this.tableobj.jqGrid('getGridParam', 'selrow');
	if (chkObjNull(id)) {
		return false;
	}
	return id;
};

/**
 * 获取选中行-返回rowid
 */
QueryGridObj.prototype.getDefalutSelectedRow = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	if (this.multi) {
		var rowids = this.getSelectedRows();
		if (!rowids) {
			return false;
		}
		if (rowids.length == 1) {
			return rowids[0];
		}
		return false;
	} else {
		var rowid = this.getSelectedRow();
		if (rowid) {
			return rowid;
		} else {
			return false;
		}
	}
};

/**
 * 获取选中行--多选模式下使用
 */
QueryGridObj.prototype.getSelectedRows = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	this.page = this.initData.get("page");
	if (this.multi && !this.page) {
		var multiSelMap = this.obj.data("multiSelMap");
		var allids = this.tableobj.getDataIDs(); // 获取所有的行
		if (!chkObjNull(allids)) { // 当前窗口的全部默认不选中
			for (var i = 0,
			n = allids.length; i < n; i++) {
				multiSelMap.put(allids[i], false);
			}
		}
		var ids = this.tableobj.jqGrid('getGridParam', 'selarrrow'); // 所有选中的行
		if (!chkObjNull(ids)) { // 根据选中行重置信息
			for (var i = 0,
			n = ids.length; i < n; i++) {
				multiSelMap.put(ids[i], true);
			}
		}
		this.obj.data("multiSelMap", multiSelMap);

		var arrKey = multiSelMap.keySet();
		var selectRow = [];
		if (!chkObjNull(arrKey)) { // 当前窗口的全部默认不选中
			for (var i = 0,
			n = arrKey.length; i < n; i++) {
				if (multiSelMap.get(arrKey[i])) {
					selectRow.push(arrKey[i]);
				}
			}
		}
		return selectRow;
	} else {
		var ids = this.tableobj.jqGrid('getGridParam', 'selarrrow'); // 所有选中的行
		if (chkObjNull(ids)) { // 根据选中行重置信息
			return false;
		} else {
			return ids;
		}
	}
};

/**
 * 获取一行数据--返回map
 * 
 * @return
 */
QueryGridObj.prototype.getRowMapData = function(rowid) {
	this.dealEditableData4Edit(); // 界面数据回填内存
	var arrResult = this.tableobj.getRowData(rowid);
	if (null == arrResult) {
		return false;
	}
	var mapTmp = new HashMap(arrResult);
	var mapResutl = new HashMap();
	mapResutl.put("rowid", mapTmp.get("rowid"));

	// 数据的解析和处理
	this.initData = this.obj.data("initData"); // 初始化数据
	this.columnds = this.initData.get("columnds");
	for (var i = 0,
	n = this.columnds.length; i < n; i++) {
		var type = this.columnds[i].type;
		var para = this.columnds[i].columnpara;
		var name = para.name; // 唯一id
		if ("columntext" == type.toLowerCase()) {
			var value = mapTmp.get(name);
			var mask = para.mask; // 标签的掩码值
			var dataType = para.datatype; // 标签文本框中输入值的类别
			var sourceMask = para.sourcemask; // 当时间类型是String时，source的mask。
			if ("date" == dataType.toLowerCase()) {
				if (chkObjNull(value)) {
					value = "";
				} else {
					if (chkObjNull(mask)) {
						mask = "yyyy-MM-dd";
					}
					if (chkObjNull(sourceMask)) {
						sourceMask = "yyyyMMdd";
					}
					value = DateUtil.changeFormat(value, mask, sourceMask);
				}
			} else if ("number" == dataType.toLowerCase()) {
				if (chkObjNull(value)) {
					value = 0;
				} else {
					if (chkObjNull(mask)) {
						mask = "###########################0.00";
					}
					value = NumberUtil.getRealValue(value, mask);
				}
			}
			mapResutl.put(name, value);
		} else if ("columndropdown" == type.toLowerCase()) {
			mapResutl.put(name, mapTmp.get(name));
		} else if ("columnmultidropdown" == type.toLowerCase()) {
			var value = mapTmp.get(name);
			value = QueryGridUtil.getCodeStrByContentStr(value, para.dscode);
			mapResutl.put(name, value);
		} else if ("columncheckbox" == type.toLowerCase()) {
			mapResutl.put(name, (("Yes" == mapTmp.get(name)) ? "1": "0"));
		} else if ("columnbuttons" == type.toLowerCase()) {} else {
			throw new Error("传入的子标签类型不合法");
		}
	}
	return mapResutl;
};

/**
 * 获取一行数据--返回json
 * 
 * @return
 */
QueryGridObj.prototype.getRowData = function(rowid) {
	var mapdata = this.getRowMapData(rowid);
	if (mapdata) {
		return mapdata.values;
	} else {
		return false;
	}
};

/**
 * 根据rowid删除一行数据
 * 
 * @param rowid
 * @return
 */
QueryGridObj.prototype.delRowData = function(rowid) {
	var jsondata = this.getRowData(rowid);
	var sumArr = this.getSumArr();
	if (sumArr.length > 0) {
		var ysumMap = new HashMap(this.tableobj.footerData("get"));
		var xsumMap = new HashMap();
		xsumMap.put("rn", "合计");
		for (var i = 0,
		n = sumArr.length; i < n; i++) {
			var name = sumArr[i].name;
			var mask = sumArr[i].name;
			if (chkObjNull(mask)) {
				mask = "###########################0.00";
			}
			var sumValue = NumberUtil.getRealValue(ysumMap.get(name), mask);
			sumValue = sumValue - jsondata[name];
			xsumMap.put(name, sumValue);
		}
		// 设置合计
		this.tableobj.footerData("set", xsumMap.values);
	}

	// 多选模式的从多选的map中，将此id移除
	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	this.page = this.initData.get("page");
	if (this.multi && !this.page) { // 如果为多选模式在选择的行中增加这些项
		this.obj.data("multiSelMap").remove(rowid);
	}
	return this.tableobj.delRowData(rowid);
};

/**
 * 获取单元格数据
 * 
 * @return
 */
QueryGridObj.prototype.getCell = function(rowid, iCol) {
	var mapdata = this.getRowMapData(rowid);
	if (mapdata) {
		return mapdata.get(iCol);
	} else {
		return false;
	}
};

/**
 * 设置单元格数据
 * 
 * @return
 */
QueryGridObj.prototype.setCell = function(rowid, iCol, value) {
	var mapData = this.getRowMapData(rowid);
	mapData.put(iCol, value);
	return this.updateRowMapData(rowid, mapData);
};

/**
 * 更新某一行的数据-json
 * 
 * @return
 */
QueryGridObj.prototype.updateRowData = function(rowid, jsondata) {
	var sumArr = this.getSumArr();
	if (sumArr.length > 0) {
		var jsondata_old = this.getRowData(rowid);
		var ysumMap = new HashMap(this.tableobj.footerData("get"));
		var xsumMap = new HashMap();
		xsumMap.put("rn", "合计");
		for (var i = 0,
		n = sumArr.length; i < n; i++) {
			var name = sumArr[i].name;
			var mask = sumArr[i].mask;
			if (chkObjNull(mask)) {
				mask = "###########################0.00";
			}
			var sumValue = NumberUtil.getRealValue(ysumMap.get(name), mask);
			sumValue = sumValue - jsondata_old[name] + jsondata[name];
			xsumMap.put(name, sumValue);
		}
		// 设置合计
		this.tableobj.footerData("set", xsumMap.values);
	}
	return this.tableobj.setRowData(rowid, jsondata);
};

/**
 * 更新某一行的数据-map
 * 
 * @return
 */
QueryGridObj.prototype.updateRowMapData = function(rowid, map) {
	return this.updateRowData(rowid, map.values);
};

/**
 * 获取所有的数据
 */
QueryGridObj.prototype.getData = function() {
	this.dealEditableData4Edit(); // 界面数据回填内存
	return this.tableobj.jqGrid("getGridParam", "data");
};

/**
 * 获取当前数据窗口中所有的ids
 * 
 * @return
 */
QueryGridObj.prototype.getCurrentDataIds = function() {
	var cuData = this.getCurrentData();
	var ids = [];
	for (var i = 0,
	n = cuData.length; i < n; i++) {
		ids.push(cuData[i].rowid);
	}
	return ids;
};

/**
 * 获取选择的数据集合
 */
QueryGridObj.prototype.getSelectData = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	if (this.multi) {
		var rowids = this.getSelectedRows();
		if (!rowids) {
			return [];
		}

		// 创建临时map
		var mapTmp = new HashMap();
		for (var i = 0,
		n = rowids.length; i < n; i++) {
			mapTmp.put(rowids[i], true);
		}
		var allData = this.getData();
		var rsultData = [];
		for (var i = 0,
		n = allData.length; i < n; i++) {
			var rowData = allData[i];
			if (mapTmp.get(rowData.rowid)) {
				rsultData.push(rowData);
			}
		}

		return rsultData;
	} else {
		var rowid = this.getSelectedRow();
		if (rowid) {
			var arr = [];
			arr.push(this.getRowData(rowid));
			return arr;
		} else {
			return [];
		}
	}
};

/**
 * 获取结果数据--窗口当前情况展示的数据-代码部分摘自jqgrid的源代码
 */
QueryGridObj.prototype.getCurrentData = function() {
	this.dealEditableData4Edit(); // 界面数据回填内存
	var ts = this.tableobj.get(0);
	var st, fndsort = false,
	cmtypes = {},
	grtypes = [],
	grindexes = [],
	srcformat,
	sorttype,
	newformat;
	if (!$.isArray(ts.p.data)) {
		return [];
	}
	var grpview = ts.p.grouping ? ts.p.groupingView: false,
	lengrp,
	gin;
	$.each(ts.p.colModel,
	function() {
		sorttype = this.sorttype || "text";
		if (sorttype == "date" || sorttype == "datetime") {
			if (this.formatter && typeof this.formatter === 'string' && this.formatter == 'date') {
				if (this.formatoptions && this.formatoptions.srcformat) {
					srcformat = this.formatoptions.srcformat;
				} else {
					srcformat = $.jgrid.formatter.date.srcformat;
				}
				if (this.formatoptions && this.formatoptions.newformat) {
					newformat = this.formatoptions.newformat;
				} else {
					newformat = $.jgrid.formatter.date.newformat;
				}
			} else {
				srcformat = newformat = this.datefmt || "Y-m-d";
			}
			cmtypes[this.name] = {
				"stype": sorttype,
				"srcfmt": srcformat,
				"newfmt": newformat
			};
		} else {
			cmtypes[this.name] = {
				"stype": sorttype,
				"srcfmt": '',
				"newfmt": ''
			};
		}
		if (ts.p.grouping) {
			for (gin = 0, lengrp = grpview.groupField.length; gin < lengrp; gin++) {
				if (this.name == grpview.groupField[gin]) {
					var grindex = this.name;
					if (this.index) {
						grindex = this.index;
					}
					grtypes[gin] = cmtypes[grindex];
					grindexes[gin] = grindex;
				}
			}
		}
		if (!fndsort && (this.index == ts.p.sortname || this.name == ts.p.sortname)) {
			st = this.name;
			fndsort = true;
		}
	});
	if (ts.p.treeGrid) {
		$(ts).jqGrid("SortTree", st, ts.p.sortorder, cmtypes[st].stype, cmtypes[st].srcfmt);
		return;
	}
	var compareFnMap = {
		'eq': function(queryObj) {
			return queryObj.equals;
		},
		'ne': function(queryObj) {
			return queryObj.notEquals;
		},
		'lt': function(queryObj) {
			return queryObj.less;
		},
		'le': function(queryObj) {
			return queryObj.lessOrEquals;
		},
		'gt': function(queryObj) {
			return queryObj.greater;
		},
		'ge': function(queryObj) {
			return queryObj.greaterOrEquals;
		},
		'cn': function(queryObj) {
			return queryObj.contains;
		},
		'nc': function(queryObj, op) {
			return op === "OR" ? queryObj.orNot().contains: queryObj.andNot().contains;
		},
		'bw': function(queryObj) {
			return queryObj.startsWith;
		},
		'bn': function(queryObj, op) {
			return op === "OR" ? queryObj.orNot().startsWith: queryObj.andNot().startsWith;
		},
		'en': function(queryObj, op) {
			return op === "OR" ? queryObj.orNot().endsWith: queryObj.andNot().endsWith;
		},
		'ew': function(queryObj) {
			return queryObj.endsWith;
		},
		'ni': function(queryObj, op) {
			return op === "OR" ? queryObj.orNot().equals: queryObj.andNot().equals;
		},
		'in': function(queryObj) {
			return queryObj.equals;
		},
		'nu': function(queryObj) {
			return queryObj.isNull;
		},
		'nn': function(queryObj, op) {
			return op === "OR" ? queryObj.orNot().isNull: queryObj.andNot().isNull;
		}

	},
	query = $.jgrid.from(ts.p.data);
	if (ts.p.ignoreCase) {
		query = query.ignoreCase();
	}

	function tojLinq(group) {
		var s = 0,
		index, gor, ror, opr, rule;
		if (group.groups != null) {
			gor = group.groups.length && group.groupOp.toString().toUpperCase() === "OR";
			if (gor) {
				query.orBegin();
			}
			for (index = 0; index < group.groups.length; index++) {
				if (s > 0 && gor) {
					query.or();
				}
				try {
					tojLinq(group.groups[index]);
				} catch(e) {
					alert(e);
				}
				s++;
			}
			if (gor) {
				query.orEnd();
			}
		}
		if (group.rules != null) {
			if (s > 0) {
				var result = query.select();
				query = $.jgrid.from(result);
				if (ts.p.ignoreCase) {
					query = query.ignoreCase();
				}
			}
			try {
				ror = group.rules.length && group.groupOp.toString().toUpperCase() === "OR";
				if (ror) {
					query.orBegin();
				}
				for (index = 0; index < group.rules.length; index++) {
					rule = group.rules[index];
					opr = group.groupOp.toString().toUpperCase();
					if (compareFnMap[rule.op] && rule.field) {
						if (s > 0 && opr && opr === "OR") {
							query = query.or();
						}
						query = compareFnMap[rule.op](query, opr)(rule.field, rule.data, cmtypes[rule.field]);
					}
					s++;
				}
				if (ror) {
					query.orEnd();
				}
			} catch(g) {
				alert(g);
			}
		}
	}

	if (ts.p.search === true) {
		var srules = ts.p.postData.filters;
		if (srules) {
			if (typeof srules === "string") {
				srules = $.jgrid.parse(srules);
			}
			tojLinq(srules);
		} else {
			try {
				query = compareFnMap[ts.p.postData.searchOper](query)(ts.p.postData.searchField, ts.p.postData.searchString, cmtypes[ts.p.postData.searchField]);
			} catch(se) {}
		}
	}
	if (ts.p.grouping) {
		for (gin = 0; gin < lengrp; gin++) {
			query.orderBy(grindexes[gin], grpview.groupOrder[gin], grtypes[gin].stype, grtypes[gin].srcfmt);
		}
	}
	if (st && ts.p.sortorder && fndsort) {
		if (ts.p.sortorder.toUpperCase() == "DESC") {
			query.orderBy(ts.p.sortname, "d", cmtypes[st].stype, cmtypes[st].srcfmt);
		} else {
			query.orderBy(ts.p.sortname, "a", cmtypes[st].stype, cmtypes[st].srcfmt);
		}
	}
	return query.select();
};

/**
 * 加载数据-本地数据-json格式的数据
 * 
 * @param data
 * @return
 */
QueryGridObj.prototype.loadData = function(data) {
	this.tableobj.clearGridData(true); // 数据清空
	// 处理新数据
	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	this.page = this.initData.get("page");
	this.frozenindex = this.initData.get("frozenindex");

	var multiSelMap = this.obj.data("multiSelMap");
	if (this.multi && !this.page) { // 如果为多选模式在选择的行中增加这些项
		multiSelMap.clear();
	}

	// 自动标识唯一id
	for (var i = 0,
	n = data.length; i < n; i++) {
		var rowdata = data[i];
		rowdata.rowid = "rowid_" + i;
		if (this.multi && !this.page) { // 如果多选，将多有的全部记录起来-默认不选
			multiSelMap.put("rowid_" + i, false);
		}
	}
	this.obj.data("maxrowid", data.length - 1);
	this.obj.data("multiSelMap", multiSelMap);

	// 合计
	this.dealSumCol(data);

	// 重置数据
	this.tableobj.jqGrid("setGridParam", {
		data: data
	});
	this.tableobj.trigger("reloadGrid");

	// 重置可编辑状态
	this.obj.data("currentEditRowid", "");

	// 处理冻结列-分页的情况
	if (this.page && this.frozenindex >= 0) {
		if (data.length > 0) { // 初始数据大于0行，调整列高度
			this.hackFrozenColmHeight();
		}
	}

	return true;
};

/**
 * 选择一行数据
 * 
 * @param rowid
 * @return
 */
QueryGridObj.prototype.selectOneRow = function(rowid) {
	if (chkObjNull(rowid)) {
		return false;
	}

	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	this.page = this.initData.get("page");
	if (this.multi && !this.page) {
		// 特殊处理操作
		var selectrows = this.getSelectedRows();
		if (selectrows) {
			for (var i = 0,
			n = selectrows.length; i < n; i++) {
				if (rowid == selectrows[i]) {
					return true;
				}
			}
		}
		this.obj.data("multiSelMap").put(rowid, true);
		return this.tableobj.setSelection(rowid, true);
	} else {
		if (this.multi) { // 多选模式
			var selectrows = this.getSelectedRows();
			if (selectrows) {
				for (var i = 0,
				n = selectrows.length; i < n; i++) {
					if (rowid == selectrows[i]) {
						return true;
					}
				}
			}
			return this.tableobj.setSelection(rowid, true);
		} else { // 单选模式
			var selectrow = this.getSelectedRow();
			if (rowid == selectrow) {
				return true;
			}
			return this.tableobj.setSelection(rowid, true);
		}
	}
};

/**
 * 不选择一行数据
 * 
 * @param rowid
 * @return
 */
QueryGridObj.prototype.unSelectOneRow = function(rowid) {
	if (chkObjNull(rowid)) {
		return false;
	}
	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	this.page = this.initData.get("page");
	if (this.multi && !this.page) {
		// 特殊处理操作
		this.obj.data("multiSelMap").put(rowid, false);
		var selectrows = this.getSelectedRows();
		if (selectrows) {
			this.unSelectAll();
			for (var i = 0,
			n = selectrows.length; i < n; i++) {
				if (rowid == selectrows[i]) {
					continue;
				}
				this.selectOneRow(selectrows[i]);
			}
		}
		return true;
	} else {
		if (this.multi) { // 多选模式
			// 特殊处理操作
			var selectrows = this.getSelectedRows();
			if (selectrows) {
				this.unSelectAll();
				for (var i = 0,
				n = selectrows.length; i < n; i++) {
					if (rowid == selectrows[i]) {
						continue;
					}
					this.selectOneRow(selectrows[i]);
				}
			}
			return true;
		} else { // 单选模式
			this.unSelectAll();
			return true;
		}
	}
};

/**
 * 全部选择
 * 
 * @param rowid
 * @return
 */
QueryGridObj.prototype.selectAll = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	this.page = this.initData.get("page");
	// 非多选模式，不允许使用
	if (!this.multi) {
		return false;
	}
	if (!this.page) {
		var multiSelMap = this.obj.data("multiSelMap");
		var arrKey = multiSelMap.keySet();
		if (!chkObjNull(arrKey)) {
			for (var i = 0,
			n = arrKey.length; i < n; i++) {
				multiSelMap.put(arrKey[i], true);
			}
		}
		this.obj.data("multiSelMap", multiSelMap);
	}
	var nowids = this.tableobj.getDataIDs(); // 获取当前的所有行
	this.tableobj.resetSelection(); // 重置选择信息
	if (!chkObjNull(nowids)) {
		for (var i = 0,
		n = nowids.length; i < n; i++) {
			this.tableobj.setSelection(nowids[i], true);
		}
	}
	return true;
};

/**
 * 全部不选择
 * 
 * @param rowid
 * @return
 */
QueryGridObj.prototype.unSelectAll = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	this.page = this.initData.get("page");
	// 非多选模式-清除选择
	if (!this.multi) {
		this.tableobj.resetSelection(); // 重置选择信息
		return true;
	}

	if (!this.page) {
		var multiSelMap = this.obj.data("multiSelMap");
		var arrKey = multiSelMap.keySet();
		if (!chkObjNull(arrKey)) {
			for (var i = 0,
			n = arrKey.length; i < n; i++) {
				multiSelMap.put(arrKey[i], false);
			}
		}
		this.obj.data("multiSelMap", multiSelMap);
	}
	this.tableobj.resetSelection(); // 重置选择信息
	return true;
};

/**
 * 数据导出
 */
QueryGridObj.prototype.exportData = function() {
	var data = this.getData();
	if (chkObjNull(data) || data.length <= 0) {
		alert("没有数据，无法导出");
		return;
	}

	var initData = this.obj.data("initData");
	var columnds = initData.get("columnds"); // 初始化数据
	var columngroupds = initData.get("columngroupds"); // 合并信息
	var title = initData.get("title");
	var urlM = new URL("taglib.do", "fwdQueryGridExportData");
	openWindow("数据导出", "icon-page-excel", urlM, 540, 240,
	function(para) {
		if (chkObjNull(para)) {
			return;
		}
		var dcms = para.dcms;
		var wjlx = para.wjlx;
		var url = new URL("taglib.do", "cacheData4Export");
		url.addPara("dcms", dcms);
		url.addPara("wjlx", wjlx);
		url.addPara("title", title);
		url.addListData("griddata", data);
		url.addPara("colcof", JSON.stringify(columnds));
		url.addPara("columngroupds", JSON.stringify(columngroupds));
		AjaxUtil.asyncBizRequest(url,
		function(data) {
			if (AjaxUtil.checkException(data)) {
				AjaxUtil.showException(data);
				return;
			}
			var cacheUiid = data;
			downloadFile2Form("taglib.do?method=downloadQueryGridData&cacheuiid=" + cacheUiid); // 数据下载优化
		});
	});
};

/**
 * grid获取焦点--只是通过选中第一行的方式来，不建议使用。
 * 
 * @return
 */
QueryGridObj.prototype.focus = function() {
	this.selectOneRow("rowid_0");
	this.tableobj.focus();
	return true;
};

/**
 * grid获取是否存在选中的数据
 * 
 * @return
 */
QueryGridObj.prototype.isSelectRow = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	if (this.multi) {
		var rowids = this.getSelectedRows();
		if (!rowids) {
			return false;
		}
		if (rowids.length <= 0) {
			return false;
		}
		return true;
	} else {
		var rowid = this.getSelectedRow();
		if (rowid) {
			return true;
		} else {
			return false;
		}
	}
};

/**
 * grid获取是否存在选中的数据-并且是只选中一行
 * 
 * @return
 */
QueryGridObj.prototype.isSelectOneRow = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	this.multi = this.initData.get("multi");
	if (this.multi) {
		var rowids = this.getSelectedRows();
		if (!rowids) {
			return false;
		}
		if (rowids.length == 1) {
			return true;
		}
		return false;
	} else {
		var rowid = this.getSelectedRow();
		if (rowid) {
			return true;
		} else {
			return false;
		}
	}
};

/**
 * 移动行，向上移动-只可在不分页，默认排序情况下有效
 */
QueryGridObj.prototype.upRow = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	this.page = this.initData.get("page"); // 是否分页
	if (this.page) {
		alert("警告：只允许在非分页模式下，调用该方法！【QueryGridObj.prototype.upRow】");
		return false;
	}
	if (!this.isSelectOneRow()) {
		alert("请先选择一行数据进行移动！");
		return false;
	}
	var rowid_now = this.getSelectedRow();
	var rowid_now_num = Number(rowid_now.substr(6));
	var rowid_next_num = rowid_now_num - 1;
	if (rowid_next_num < 0) {
		alert("当前行已经为第一行，无法再次向上移动");
		return false;
	}
	var rowid_next = "rowid_" + rowid_next_num;
	var nextrowdata = this.getRowData(rowid_next);
	this.updateRowData(rowid_next, this.getRowData(rowid_now));
	this.updateRowData(rowid_now, nextrowdata);
	this.unSelectAll();
	this.selectOneRow(rowid_next);
	return true;
};

/**
 * 移动行，向下移动-只可在不分页，默认排序情况下有效
 */
QueryGridObj.prototype.downRow = function() {
	this.initData = this.obj.data("initData"); // 初始化数据
	this.page = this.initData.get("page"); // 是否分页
	if (this.page) {
		alert("警告：只允许在非分页模式下，调用该方法！【QueryGridObj.prototype.downRow】");
		return false;
	}
	if (!this.isSelectOneRow()) {
		alert("请先选择一行数据进行移动！");
		return;
	}
	var rowid_now = this.getSelectedRow();
	var rowid_now_num = Number(rowid_now.substr(6));
	var rowid_next_num = rowid_now_num + 1;
	if (rowid_next_num >= this.getData().length) {
		alert("当前行已经为最后一行，无法再次向下移动");
		return false;
	}
	var rowid_next = "rowid_" + rowid_next_num;
	var nextrowdata = this.getRowData(rowid_next);
	this.updateRowData(rowid_next, this.getRowData(rowid_now));
	this.updateRowData(rowid_now, nextrowdata);
	this.unSelectAll();
	this.selectOneRow(rowid_next);
	return true;
};

/**
 * 自动调整冻结列高度
 */
QueryGridObj.prototype.hackFrozenColmHeight = function() {
	var gridId = "#" + this.id + "_table";
	$(gridId + '_frozen tr').slice(1).each(function() {
		var rowId = $(this).attr('id');

		var frozenTdHeight = parseFloat($('td:first', this).height());
		var normalHeight = parseFloat($(gridId + ' #' + $(this).attr('id')).find('td:first').height());
		// 如果冻结的列高度小于未冻结列的高度则hack之
		if (frozenTdHeight < normalHeight) {

			$('td', this).each(function() {
				/*
				 * 浏览器差异高度hack
				 */
				var space = 0; // opera默认使用0就可以
				if (QueryGridUtil.isChrome()) {
					space = 0.6;
				} else if (QueryGridUtil.isIE()) {
					space = -0.2;
				} else if (QueryGridUtil.isMozila()) {
					space = 0.5;
				}
				if (!$(this).attr('style') || $(this).attr('style').indexOf('height:') == -1) {
					$(this).attr('style', $(this).attr('style') + ";height:" + (normalHeight + space) + "px !important");
				}
			});
		}
	});
};

/**
 * queryGrid的自用工具方法
 */
var QueryGridUtil = {
	getContentStrByCodeStr: function(codeStr, dsCode, canParseTag) {
		// 根据json数据获取code对应content信息,例如：1,2->男,女
		if (chkObjNull(codeStr)) {
			return "";
		}
		if (chkObjNull(canParseTag)) {
			canParseTag = false;
		}
		var optsList = new List(dsCode);
		var optsJson = optsList.values;
		var valueText = "";
		var arrValue = codeStr.split(",");
		var endLength = 0;
		for (var i = 0,
		n = optsJson.length; i < n; i++) {
			var optJson = optsJson[i];
			for (var j = 0,
			m = arrValue.length; j < m; j++) {
				var oneV = arrValue[j];
				if (optJson.code == oneV) {
					if (canParseTag && !chkObjNull(optJson.color)) {
						valueText = valueText + "<span class='grid-tag' style='box-shadow: 0px 0px 2px 0px " + optJson.color + ";color:" + optJson.color + ";'>" + optJson.content + "</span><span style='display:none;'>,</span>";
						endLength = 36;
					} else {
						valueText = valueText + optJson.content + ",";
						endLength = 1;
					}
					break;
				}
			}
		}
		if (valueText.length > 0) {
			valueText = valueText.substr(0, valueText.length - endLength);
		}
		return valueText;
	},
	getCodeStrByContentStr: function(contentStr, dsCode) {
		// 根据json数据获取content对应code信息,例如：男,女-->1,2
		if (chkObjNull(contentStr)) {
			return "";
		}
		contentStr = contentStr.replace(/<[^>]+>/g, "");
		var optsList = new List(dsCode);
		var optsJson = optsList.values;
		var valueText = "";
		var arrValue = contentStr.split(",");
		for (var i = 0,
		n = arrValue.length; i < n; i++) {
			var oneV = arrValue[i];
			for (var j = 0,
			m = optsJson.length; j < m; j++) {
				var optJson = optsJson[j];
				if (oneV == optJson.content) {
					valueText = valueText + optJson.code + ",";
					break;
				}
				if (j == m - 1) {
					valueText = valueText + oneV + ",";
				}
			}
		}
		if (valueText.length > 0) {
			valueText = valueText.substr(0, valueText.length - 1);
		}
		return valueText;
	},
	// 根据dscode转换为dsData
	chageCode2Data: function(dsCode) {
		var dsData = [];
		for (var i = 0,
		n = dsCode.length; i < n; i++) {
			var rowCode = dsCode[i];
			var rowData = {
				key: rowCode.code,
				value: rowCode.content
			};

			dsData.push(rowData);
		}
		return dsData;
	},
	// 检测是否是IE浏览器
	isIE: function() {
		var _uaMatch = $.uaMatch(navigator.userAgent);
		var _browser = _uaMatch.browser;
		if (_browser == 'msie') {
			return true;
		} else {
			return false;
		}
	},
	// 检测是否是chrome浏览器
	isChrome: function() {
		var _uaMatch = $.uaMatch(navigator.userAgent);
		var _browser = _uaMatch.browser;
		if (_browser == 'chrome') {
			return true;
		} else {
			return false;
		}
	},
	// 检测是否是Firefox浏览器
	isMozila: function() {
		var _uaMatch = $.uaMatch(navigator.userAgent);
		var _browser = _uaMatch.browser;
		if (_browser == 'mozilla') {
			return true;
		} else {
			return false;
		}
	},
	// 检测是否是Firefox浏览器
	isOpera: function() {
		var _uaMatch = $.uaMatch(navigator.userAgent);
		var _browser = _uaMatch.browser;
		if (_browser == 'webkit') {
			return true;
		} else {
			return false;
		}
	},
	// 自动调整高度
	adjustHeight: 100,
	// 新增行默认位置
	defaultAddRowPostion: "first"
};