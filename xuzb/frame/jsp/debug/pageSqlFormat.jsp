<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="Sql文本" rowcount="1" rowWidthPercent="100">
		<ef:textarea name="sql" height="390" />
	</ef:form>
	<ef:buttons align="center" closebutton="false">
		<ef:button onclick="btnFormatClick();" value="格式化(5个一组sqlBF)"></ef:button>
		<ef:button onclick="btnFormatOnlySqlClick();" value="格式化(5个一组纯sql)"></ef:button>
		<ef:button onclick="btnFormatClickAppendSqlBF();" value="格式化(增加sqlBF)"></ef:button>
		<ef:button onclick="btnGenCslb();" value="生成参数列表(BIZ)"></ef:button>
		<ef:button onclick="btnGenSqlPara();" value="生成SQL参数(BIZ)"></ef:button>
		<ef:button onclick="btnReback();" value="还原"
			iconCls="icon-arrow-turn-left"></ef:button>
		<ef:button onclick="btnClear();" value="清空"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		get("sql").focus();
	}
	
	var redoList = [];
	function btnReback() {
		var length = redoList.length;
		if (length <= 0) {
			get("sql").clear();
			return;
		}
		var sql = redoList.splice(length - 1, 1);
		get("sql").setValue(sql);
		get("sql").focus();
	}
	
	function btnGenCslb() {
		var sql = get("sql").getValue();
		if (sql == null || '' == sql) {
			return;
		}
		redoList.push(sql);
		sql = trim(sql);
		sql = clearBlank(sql);
		sql = clearBr(sql);
		var sqlArr = sql.split(",");
		var sqlF = "";
		for (var i = 0,
		n = sqlArr.length; i < n; i++) {
			sqlF = sqlF + "String " + sqlArr[i] + " = para.getStr(\"" + sqlArr[i] + "\", FieldValidation.Default());\r\n";
			get("sql").setValue(sqlF);
		}
	}
	
	function btnGenSqlPara() {
		var sql = get("sql").getValue();
		if (sql == null || '' == sql) {
			return;
		}
		redoList.push(sql);
		sql = trim(sql);
		sql = clearBlank(sql);
		sql = clearBr(sql);
		var sqlArr = sql.split(",");
		var sqlF = "";
		for (var i = 0,
		n = sqlArr.length; i < n; i++) {
			sqlF = sqlF + "this.sql.setString(" + (i + 1) + ", " + sqlArr[i] + ");\r\n";
			if ((i + 1) % 5 == 0) {
				sqlF = sqlF + "\r\n";
			}
			get("sql").setValue(sqlF);
		}
	}
	
	//清空    
	function btnClear() {
		var sql = get("sql").getValue();
		if (sql == null || '' == sql) {
			return;
		}
		redoList.push(sql);
		get('sql').clear();
	}
	//5个一组格式化:纯sql    
	function btnFormatOnlySqlClick() {
		fiveFormatSql('0');
	}
	//5个一组格式化:sqlBF    
	function btnFormatClick() {
		fiveFormatSql('1');
	}
	//5个一组格式化    
	function fiveFormatSql(flag) {
		var tempPrefix = "";
		var tempEndfix = "";
		var sqlF = "";
		if ("1" == flag) {
			var tempPrefix = "sqlBF.append(\" ";
			var tempEndfix = " \");";
			sqlF = "StringBuffer sqlBF = new StringBuffer();\r\n\r\nsqlBF.setLength(0);\r\n";
		}
		var tableName = "";
		var sql = get("sql").getValue();
		if (sql == null || '' == sql) {
			return;
		}
		sql = trim(sql);
		if (sql.indexOf("insert", 0) == 0) { //insert    
			redoList.push(sql);
			//获取表名    
			var nameIndex = sql.indexOf("(", 0);
			tableName = sql.substring(0, nameIndex);
			tableName = tableName.replace("insert", "");
			tableName = tableName.replace("into", "");
			tableName = trim(tableName);
			tableName = tableName.replace(" ", "");
			//获取字段列表    
			var colIndex = sql.indexOf(")", nameIndex);
			var colList = sql.substring(nameIndex + 1, colIndex);
			colList = clearBlank(colList);
			colList = clearBr(colList);
			var colListArr = colList.split(",");
			sqlF = sqlF + tempPrefix + "insert into " + tableName + tempEndfix + "\r\n";
			sqlF = sqlF + tempPrefix + "  (";
			for (var i = 0,
			n = colListArr.length; i < n; i++) {
				if (i == (n - 1)) {
					sqlF = sqlF + colListArr[i] + ")" + tempEndfix + "\r\n";
					continue;
				}
				sqlF = sqlF + colListArr[i] + ", ";
				if ((i + 1) % 5 == 0) {
					sqlF = sqlF + trim(tempEndfix) + "\r\n";
					sqlF = sqlF + tempPrefix + "   ";
				}
			}
			if (sql.indexOf("select", 0) <= 0) {
				sqlF = sqlF + tempPrefix + "values" + tempEndfix + "\r\n";
				sqlF = sqlF + tempPrefix + "  (";
				for (var i = 0,
				n = colListArr.length; i < n; i++) {
					if (i == (n - 1)) {
						sqlF = sqlF + "?)" + tempEndfix + "\r\n";
						continue;
					}
					sqlF = sqlF + "?, ";
					if ((i + 1) % 5 == 0) {
						sqlF = sqlF + trim(tempEndfix) + "\r\n";
						sqlF = sqlF + tempPrefix + "   ";
					}
				}
			}
		} else if (sql.indexOf("select", 0) == 0) { //select
			redoList.push(sql);
			//字段    
			var colIndex = sql.indexOf("from", 0);
			var colList = sql.substring(0, colIndex);
			colList = colList.replace("select", "");
			colList = clearBlank(colList);
			colList = clearBr(colList);
			var colListArr = colList.split(",");
			sqlF = sqlF + tempPrefix + "select ";
			for (var i = 0,
			n = colListArr.length; i < n; i++) {
				if (i == (n - 1)) {
					sqlF = sqlF + colListArr[i] + tempEndfix + "\r\n";
					continue;
				}
				sqlF = sqlF + colListArr[i] + ", ";
				if ((i + 1) % 5 == 0) {
					sqlF = sqlF + trim(tempEndfix) + "\r\n";
					sqlF = sqlF + tempPrefix + "       ";
				}
			}
			//表名    
			var nameIndex = sql.indexOf("where", colIndex);
			if (nameIndex < 0) {
				nameIndex = sql.length;
			}
			tableName = sql.substring(colIndex, nameIndex);
			tableName = tableName.replace("from", "");
			tableName = trim(tableName);
			tableName = clearBr(tableName);
			var tableNameArr = tableName.split(",");
			sqlF = sqlF + tempPrefix + "  from ";
			for (var i = 0,
			n = tableNameArr.length; i < n; i++) {
				if (i == 0 && n == 1) {
					sqlF = sqlF + trim(tableNameArr[i]) + tempEndfix + "\r\n";
					continue;
				}
				if (i == 0) {
					sqlF = sqlF + trim(tableNameArr[i]) + "," + tempEndfix + "\r\n";
					continue;
				}
				if (i == (n - 1)) {
					sqlF = sqlF + tempPrefix + "       " + trim(tableNameArr[i]) + tempEndfix + "\r\n";
					continue;
				}
				sqlF = sqlF + tempPrefix + "       " + trim(tableNameArr[i]) + "," + tempEndfix + "\r\n";
			}
			sqlF = sqlF + appendSqlBF(sql.substring(nameIndex), "", tempPrefix, tempEndfix);
		} else {
			MsgBox.alert('暂不支持非insert,select相关sql的5个一组格式化');
			return;
		}
		//insert-selectm模式    
		var selectIndex = sql.indexOf("select", 0);
		if (selectIndex > 0) {
			var sqlSeTemp = sql.substring(selectIndex);
			sqlSeTemp = trim(sqlSeTemp);
			//字段    
			var colIndex = sqlSeTemp.indexOf("from", 0);
			var colList = sqlSeTemp.substring(0, colIndex);
			colList = colList.replace("select", "");
			colList = clearBlank(colList);
			colList = clearBr(colList);
			var colListArr = colList.split(",");
			sqlF = sqlF + tempPrefix + "  select ";
			for (var i = 0,
			n = colListArr.length; i < n; i++) {
				if (i == (n - 1)) {
					sqlF = sqlF + colListArr[i] + tempEndfix + "\r\n";
					continue;
				}
				sqlF = sqlF + colListArr[i] + ", ";
				if ((i + 1) % 5 == 0) {
					sqlF = sqlF + trim(tempEndfix) + "\r\n";
					sqlF = sqlF + tempPrefix + "         ";
				}
			}
			//表名    
			var nameIndex = sqlSeTemp.indexOf("where", colIndex);
			if (nameIndex < 0) {
				nameIndex = sqlSeTemp.length;
			}
			tableName = sqlSeTemp.substring(colIndex, nameIndex);
			tableName = tableName.replace("from", "");
			tableName = trim(tableName);
			tableName = clearBr(tableName);
			var tableNameArr = tableName.split(",");
			sqlF = sqlF + tempPrefix + "    from ";
			for (var i = 0,
			n = tableNameArr.length; i < n; i++) {
				if (i == 0 && n == 1) {
					sqlF = sqlF + trim(tableNameArr[i]) + tempEndfix + "\r\n";
					continue;
				}
				if (i == 0) {
					sqlF = sqlF + trim(tableNameArr[i]) + "," + tempEndfix + "\r\n";
					continue;
				}
				if (i == (n - 1)) {
					sqlF = sqlF + tempPrefix + "         " + trim(tableNameArr[i]) + tempEndfix + "\r\n";
					continue;
				}
				sqlF = sqlF + tempPrefix + "         " + trim(tableNameArr[i]) + "," + tempEndfix + "\r\n";
			}
			sqlF = sqlF + appendSqlBF(sqlSeTemp.substring(nameIndex), "  ", tempPrefix, tempEndfix);
		}
		if ("1" == flag) {
			sqlF = sqlF + "\r\nthis.sql.setSql(sqlBF.toString());";
		}
		get("sql").setValue(sqlF);
	}
	function trim(str) { //删除左右两端的空格    
		return str.replace(/(^\s*)|(\s*$)/g, "");
	}
	//去除空格    
	function clearBlank(str) {
		return str.replace(/\s+/g, "");
	}
	//去除换行    
	function clearBr(key) {
		key = key.replace(/<\/?.+?>/g, "");
		key = key.replace(/[\r\n]/g, "");
		return key;
	}
	//增加sqlBF    
	function btnFormatClickAppendSqlBF() {
		var sql = get("sql").getValue();
		sql = trim(sql);
		if (sql == null || '' == sql) {
			return;
		}
		redoList.push(sql);
		var sqlF = "StringBuffer sqlBF = new StringBuffer();\r\n\r\nsqlBF.setLength(0);\r\n";
		sqlF = sqlF + appendSqlBF(sql);
		sqlF = sqlF + "\r\nthis.sql.setSql(sqlBF.toString());";
		get("sql").setValue(sqlF);
	}
	function appendSqlBF(pSql, pBlank, prefix, endfix) {
		var sql = trim(pSql);
		if (sql == null || '' == sql) {
			return '';
		}
		if (null == prefix) {
			prefix = "sqlBF.append(\" ";
		}
		if (null == endfix) {
			endfix = " \");";
		}
		if (sql.indexOf("where", 0) == 0) {
			if (pBlank == null) {
				pBlank = "";
			}
			sql = " " + pBlank + pSql;
		}
		var sqlLineArr = sql.split("\n");
		var sqlF = "";
		for (var i = 0,
		n = sqlLineArr.length; i < n; i++) {
			var lineStr = clearBr(sqlLineArr[i]);
			if (null == trim(lineStr) || '' == trim(lineStr)) {
				continue;
			}
			if (i == (n - 1)) {
				lineStr = lineStr.replace(";", "");
			}
			sqlF = sqlF + prefix + lineStr + endfix + "\r\n";
		}
		return sqlF;
	}
</script>
