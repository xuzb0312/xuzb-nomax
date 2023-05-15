package com.grace.frame.debug.biz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.grace.frame.exception.BizException;
import com.grace.frame.exception.BoxException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.DateUtil;
import com.grace.frame.util.Printer;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * 自动化代码
 * 
 * @author yjc
 */
public class AutoCodingBiz extends Biz{

	/**
	 * 查询表
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-18
	 * @since V1.0
	 */
	public final DataMap queryColmInfo4AutoCoding(final DataMap para) throws Exception {
		String tableName = para.getString("tablename");
		if (StringUtil.chkStrNull(tableName)) {
			throw new BizException("表名不允许为空");
		}
		String tableNameTmp = tableName;
		String[] arrTable = tableName.split("\\.");
		if (arrTable.length != 2) {
			throw new BizException("表名不正确");
		}
		String userName = arrTable[0];
		tableName = arrTable[1];

		// 查询
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.column_name name, ");
		sqlBF.append("        a.data_type type, ");
		sqlBF.append("        a.nullable, ");
		sqlBF.append("        a.data_length length, ");
		sqlBF.append("        a.column_id, ");
		sqlBF.append("        b.comments label");
		sqlBF.append("   from sys.all_tab_cols a, sys.all_col_comments b ");
		sqlBF.append("  where a.owner = b.owner ");
		sqlBF.append("    and a.table_name = b.table_name ");
		sqlBF.append("    and a.column_name = b.column_name ");
		sqlBF.append("    and a.owner = ? ");
		sqlBF.append("    and a.table_name = ? ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, userName.toUpperCase());
		this.sql.setString(2, tableName.toUpperCase());
		DataSet dsColm = this.sql.executeQuery();
		dsColm.sort("column_id");

		if (dsColm.size() <= 0) {
			throw new BoxException("表在系统中不存在");
		}

		// 处理
		for (int i = 0, n = dsColm.size(); i < n; i++) {
			String type = dsColm.getString(i, "type");
			String name = dsColm.getString(i, "name").toLowerCase();
			String nullable = dsColm.getString(i, "nullable");
			int length = dsColm.getInt(i, "length");

			dsColm.put(i, "name", name);// 转小写

			if ("N".equals(nullable)) {
				dsColm.put(i, "required", "1");
			} else {
				dsColm.put(i, "required", "0");
			}

			// 先判断code存在的可能性
			if (type.startsWith("VARCHAR") && length <= 3) {
				dsColm.put(i, "datatype", "");
				dsColm.put(i, "code", name.toUpperCase());
			} else {
				// 字符串类型
				dsColm.put(i, "code", "");
				if (type.startsWith("VARCHAR") || type.startsWith("CHAR")) {
					if ((name.endsWith("nd") || name.endsWith("nf"))
							&& length == 4) {
						dsColm.put(i, "datatype", "date");
					} else if (name.endsWith("ny") && length == 6) {
						dsColm.put(i, "datatype", "date");
					} else if (name.endsWith("rq") && length == 8) {
						dsColm.put(i, "datatype", "date");
					} else {
						dsColm.put(i, "datatype", "string");
					}
				} else if ("NUMBER".equalsIgnoreCase(type)) {// 数字类型
					dsColm.put(i, "datatype", "number");
				} else if ("DATE".equals(type)) {// 日期类型
					dsColm.put(i, "datatype", "date");
				}
			}

			// 是否主键
			if (i == 0 && name.endsWith("id")) {
				dsColm.put(i, "ispk", "1");
			} else {
				dsColm.put(i, "ispk", "0");
			}

			// 放入数据
			dsColm.put(i, "isquery", "0");
			dsColm.put(i, "isbizpk", "0");
		}

		// 设置信息
		String bizTable = "";
		String bizKey = "";
		String bizKeyDesc = "";
		if (dsColm.size() > 0) {
			bizTable = tableNameTmp.toLowerCase();
			String[] arrTableName = tableName.split("_");
			for (int i = 0, n = arrTableName.length; i < n; i++) {
				if (!StringUtil.chkStrNull(arrTableName[i])) {
					bizKey = bizKey + this.upperFirstWord(arrTableName[i]);
				}
			}

			sqlBF.setLength(0);
			sqlBF.append(" select comments ");
			sqlBF.append("   from sys.all_tab_comments a ");
			sqlBF.append("  where a.owner = ? ");
			sqlBF.append("    and a.table_name = ? ");
			sqlBF.append("    and a.table_type = 'TABLE' ");
			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, userName.toUpperCase());
			this.sql.setString(2, tableName.toUpperCase());
			DataSet dsTemp = this.sql.executeQuery();
			if (dsTemp.size() > 0) {
				bizKeyDesc = dsTemp.getString(0, "comments");
			}
		}
		DataMap dmSet = new DataMap();
		dmSet.put("biztable", bizTable);
		dmSet.put("bizkey", bizKey);
		dmSet.put("bizkeydesc", bizKeyDesc);

		DataMap rdm = new DataMap();
		rdm.put("dscolm", dsColm);
		rdm.put("dmset", dmSet);
		return rdm;
	}

	/**
	 * 首字母大写
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-18
	 * @since V1.0
	 */
	private String upperFirstWord(String str) {
		if (StringUtil.chkStrNull(str)) {
			return "";
		}
		str = str.toLowerCase();
		if (str.length() <= 1) {
			return str.toUpperCase();
		}

		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * 生成code
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-18
	 * @since V1.0
	 */
	public final DataMap genCode(final DataMap para) throws Exception {
		String bizTable = para.getString("biztable");
		String bizKey = para.getString("bizkey");
		String bizKeyDesc = para.getString("bizkeydesc");
		DataSet dsColm = para.getDataSet("gridColm");
		if (StringUtil.chkStrNull(bizTable)) {
			throw new BizException("业务表为空");
		}
		if (StringUtil.chkStrNull(bizKey)) {
			throw new BizException("业务关键字为空");
		}
		if (StringUtil.chkStrNull(bizKeyDesc)) {
			throw new BizException("业务描述为空");
		}

		DataMap rdm = new DataMap();
		rdm.put("jspcode", StringUtil.htmlEncode(this.genJspCode(bizKey, bizKeyDesc, dsColm)));// JSP代码工具
		rdm.put("controllercode", StringUtil.htmlEncode(this.genControllerCode(bizKey, bizKeyDesc)));// controller代码
		rdm.put("bizcode", StringUtil.htmlEncode(this.genBizCode(bizKey, bizKeyDesc, bizTable, dsColm)));
		return rdm;
	}

	/**
	 * Biz代码
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-18
	 * @since V1.0
	 */
	private String genBizCode(String bizKey, String bizKeyDesc,
			String bizTable, DataSet dsColm) throws Exception {
		DataSet dsQuery = new DataSet();
		for (int i = 0, n = dsColm.size(); i < n; i++) {
			String isquery = dsColm.getString(i, "isquery");
			if ("1".equals(isquery)) {
				dsQuery.addRow(dsColm.getRow(i));
				dsQuery.put(dsQuery.size() - 1, "index", dsQuery.size());
			}
		}

		DataSet dsAdd = new DataSet();
		DataSet dsPK = new DataSet();
		for (int i = 0, n = dsColm.size(); i < n; i++) {
			String ispk = dsColm.getString(i, "ispk");
			if (!"1".equals(ispk)) {
				dsAdd.addRow(dsColm.getRow(i));
				dsAdd.put(dsAdd.size() - 1, "index", dsAdd.size());
			} else {
				dsPK.addRow(dsColm.getRow(i));
				dsPK.put(dsPK.size() - 1, "index", dsPK.size());
			}
		}

		String tpl = this.getCodeTpl("bizCodeTpl");
		Printer printer = Printer.getInstanceByGsnr(tpl);
		printer.putPara("ywms", bizKeyDesc);
		printer.putPara("ywzj", bizKey);
		printer.putPara("ywbmc", bizTable);
		printer.putDataSet("dsquery", dsQuery);
		printer.putDataSet("dsadd", dsAdd);
		printer.putDataSet("dspk", dsPK);
		printer.putDataSet("dsall", dsColm);
		printer.putPara("ywzj_xx", bizKey.toLowerCase());
		printer.putPara("dqrq", DateUtil.dateToString(DateUtil.getDBTime(), "yyyy-MM-dd"));
		return printer.getHtmlContent();
	}

	/**
	 * jsp代码生成
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-18
	 * @since V1.0
	 */
	private String genJspCode(String bizKey, String bizKeyDesc, DataSet dsColm) throws Exception {
		StringBuffer strBF = new StringBuffer();
		// 管理页面
		strBF.append(this.genJspCode4Mng(bizKey, bizKeyDesc, dsColm));
		strBF.append("\r\n");
		strBF.append("\r\n");

		// 新增页面
		strBF.append(this.genJspCode4Add(bizKey, bizKeyDesc, dsColm));
		strBF.append("\r\n");
		strBF.append("\r\n");

		// 修改页面
		strBF.append(this.genJspCode4Modify(bizKey, bizKeyDesc, dsColm));
		strBF.append("\r\n");
		strBF.append("\r\n");

		return strBF.toString();
	}

	/**
	 * 修改
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-18
	 * @since V1.0
	 */
	private String genJspCode4Modify(String bizKey, String bizKeyDesc,
			DataSet dsColm) throws Exception {
		// 查询条件
		StringBuffer colBF = new StringBuffer();
		for (int i = 0, n = dsColm.size(); i < n; i++) {
			String ispk = dsColm.getString(i, "ispk");
			String name = dsColm.getString(i, "name");
			if (!"1".equals(ispk)) {
				String label = dsColm.getString(i, "label");
				String datatype = dsColm.getString(i, "datatype");
				String code = dsColm.getString(i, "code");
				int length = dsColm.getInt(i, "length");
				String required = dsColm.getString(i, "required");
				if ("1".equals(required)) {
					required = " required=\"true\"";
				} else {
					required = "";
				}
				String colspan = "2";
				if (length >= 100) {
					colspan = "4";
				}

				// code类型
				if (!StringUtil.chkStrNull(code)) {
					colBF.append("		<ef:dropdownList name=\""
							+ name.toLowerCase() + "\" label=\"" + label
							+ "\" code=\"" + code.toUpperCase() + "\""
							+ required + "></ef:dropdownList>").append("\r\n");
				} else {
					if (StringUtil.chkStrNull(datatype)
							|| "string".equalsIgnoreCase(datatype)) {
						colBF.append("		<ef:textinput name=\""
								+ name.toLowerCase() + "\" label=\"" + label
								+ "\" colspan=\"" + colspan + "\"" + required
								+ "/>").append("\r\n");
					} else if ("date".equalsIgnoreCase(datatype)) {
						String mask = "yyyy-MM-dd";
						String sourceMask = "yyyyMMdd";
						if (name.toLowerCase().endsWith("nd")
								|| name.toLowerCase().endsWith("nf")) {
							mask = "yyyy";
							sourceMask = "yyyy";
						} else if (name.toLowerCase().endsWith("ny")) {
							mask = "yyyy-MM";
							sourceMask = "yyyyMM";
						} else if (name.toLowerCase().endsWith("sj")) {
							mask = "yyyy-MM-dd hh:mm:ss";
							sourceMask = "yyyyMMddhhmmss";
						}
						colBF.append("		<ef:textinput name=\""
								+ name.toLowerCase() + "\" label=\"" + label
								+ "\" dataType=\"date\" mask=\"" + mask
								+ "\" sourceMask=\"" + sourceMask
								+ "\" colspan=\"" + colspan + "\"" + required
								+ "/>").append("\r\n");
					} else if ("number".equalsIgnoreCase(datatype)) {
						colBF.append("		<ef:textinput name=\""
								+ name.toLowerCase()
								+ "\" label=\""
								+ label
								+ "\" dataType=\"number\" mask=\"###########0\" colspan=\""
								+ colspan + "\"" + required + "/>")
							.append("\r\n");
					}
				}
			} else {
				colBF.append("		<ef:hiddenInput name=\"" + name.toLowerCase()
						+ "\"/>").append("\r\n");
			}
		}

		String tpl = this.getCodeTpl("jspCode4ModTpl");
		Printer printer = Printer.getInstanceByGsnr(tpl);
		printer.putPara("ywms", bizKeyDesc);
		printer.putPara("ywzj", bizKey);
		printer.putPara("ywzj_xx", bizKey.toLowerCase());
		printer.putPara("listcolm", colBF.toString());
		return printer.getHtmlContent();
	}

	/**
	 * 新增
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-18
	 * @since V1.0
	 */
	private String genJspCode4Add(String bizKey, String bizKeyDesc,
			DataSet dsColm) throws Exception {
		// 查询条件
		StringBuffer colBF = new StringBuffer();
		for (int i = 0, n = dsColm.size(); i < n; i++) {
			String ispk = dsColm.getString(i, "ispk");
			if (!"1".equals(ispk)) {
				String name = dsColm.getString(i, "name");
				String label = dsColm.getString(i, "label");
				String datatype = dsColm.getString(i, "datatype");
				String code = dsColm.getString(i, "code");
				int length = dsColm.getInt(i, "length");
				String required = dsColm.getString(i, "required");
				if ("1".equals(required)) {
					required = " required=\"true\"";
				} else {
					required = "";
				}
				String colspan = "2";
				if (length >= 100) {
					colspan = "4";
				}

				// code类型
				if (!StringUtil.chkStrNull(code)) {
					colBF.append("		<ef:dropdownList name=\""
							+ name.toLowerCase() + "\" label=\"" + label
							+ "\" code=\"" + code.toUpperCase() + "\""
							+ required + "></ef:dropdownList>").append("\r\n");
				} else {
					if (StringUtil.chkStrNull(datatype)
							|| "string".equalsIgnoreCase(datatype)) {
						colBF.append("		<ef:textinput name=\""
								+ name.toLowerCase() + "\" label=\"" + label
								+ "\" colspan=\"" + colspan + "\"" + required
								+ "/>").append("\r\n");
					} else if ("date".equalsIgnoreCase(datatype)) {
						String mask = "yyyy-MM-dd";
						String sourceMask = "yyyyMMdd";
						if (name.toLowerCase().endsWith("nd")
								|| name.toLowerCase().endsWith("nf")) {
							mask = "yyyy";
							sourceMask = "yyyy";
						} else if (name.toLowerCase().endsWith("ny")) {
							mask = "yyyy-MM";
							sourceMask = "yyyyMM";
						} else if (name.toLowerCase().endsWith("sj")) {
							mask = "yyyy-MM-dd hh:mm:ss";
							sourceMask = "yyyyMMddhhmmss";
						}
						colBF.append("		<ef:textinput name=\""
								+ name.toLowerCase() + "\" label=\"" + label
								+ "\" dataType=\"date\" mask=\"" + mask
								+ "\" sourceMask=\"" + sourceMask
								+ "\" colspan=\"" + colspan + "\"" + required
								+ "/>").append("\r\n");
					} else if ("number".equalsIgnoreCase(datatype)) {
						colBF.append("		<ef:textinput name=\""
								+ name.toLowerCase()
								+ "\" label=\""
								+ label
								+ "\" dataType=\"number\" mask=\"###########0\" colspan=\""
								+ colspan + "\"" + required + "/>")
							.append("\r\n");
					}
				}
			}
		}

		String tpl = this.getCodeTpl("jspCode4AddTpl");
		Printer printer = Printer.getInstanceByGsnr(tpl);
		printer.putPara("ywms", bizKeyDesc);
		printer.putPara("ywzj", bizKey);
		printer.putPara("listcolm", colBF.toString());
		return printer.getHtmlContent();
	}

	/**
	 * 管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-18
	 * @since V1.0
	 */
	private String genJspCode4Mng(String bizKey, String bizKeyDesc,
			DataSet dsColm) throws Exception {
		// 获取业务ID
		DataSet dsYwid = new DataSet();
		for (int i = 0, n = dsColm.size(); i < n; i++) {
			String ispk = dsColm.getString(i, "ispk");
			if ("1".equals(ispk)) {
				dsYwid.addRow();
				dsYwid.put(dsYwid.size() - 1, "ywid", dsColm.getString(i, "name"));
			}
		}

		// 查询条件
		StringBuffer qlBF = new StringBuffer();
		for (int i = 0, n = dsColm.size(); i < n; i++) {
			String isquery = dsColm.getString(i, "isquery");
			if ("1".equals(isquery)) {
				String name = dsColm.getString(i, "name");
				String label = dsColm.getString(i, "label");
				String datatype = dsColm.getString(i, "datatype");
				String code = dsColm.getString(i, "code");
				int length = dsColm.getInt(i, "length");
				String colspan = "2";
				if (length >= 100) {
					colspan = "4";
				}

				// code类型
				if (!StringUtil.chkStrNull(code)) {
					qlBF.append("		<ef:multiDropdownList name=\""
							+ name.toLowerCase() + "\" label=\"" + label
							+ "\" code=\"" + code.toUpperCase()
							+ "\"></ef:multiDropdownList>").append("\r\n");
				} else {
					if (StringUtil.chkStrNull(datatype)
							|| "string".equalsIgnoreCase(datatype)) {
						qlBF.append("		<ef:textinput name=\""
								+ name.toLowerCase() + "\" label=\"" + label
								+ "\" colspan=\"" + colspan + "\"/>")
							.append("\r\n");
					} else if ("date".equalsIgnoreCase(datatype)) {
						String mask = "yyyy-MM-dd";
						String sourceMask = "yyyyMMdd";
						if (name.toLowerCase().endsWith("nd")
								|| name.toLowerCase().endsWith("nf")) {
							mask = "yyyy";
							sourceMask = "yyyy";
						} else if (name.toLowerCase().endsWith("ny")) {
							mask = "yyyy-MM";
							sourceMask = "yyyyMM";
						} else if (name.toLowerCase().endsWith("sj")) {
							mask = "yyyy-MM-dd hh:mm:ss";
							sourceMask = "yyyyMMddhhmmss";
						}
						qlBF.append("		<ef:textinput name=\""
								+ name.toLowerCase() + "\" label=\"" + label
								+ "\" dataType=\"date\" mask=\"" + mask
								+ "\" sourceMask=\"" + sourceMask
								+ "\" colspan=\"" + colspan + "\"/>")
							.append("\r\n");
					} else if ("number".equalsIgnoreCase(datatype)) {
						qlBF.append("		<ef:textinput name=\""
								+ name.toLowerCase()
								+ "\" label=\""
								+ label
								+ "\" dataType=\"number\" mask=\"###########0\" colspan=\""
								+ colspan + "\"/>")
							.append("\r\n");
					}
				}
			}
		}

		// 列表信息
		StringBuffer rlBF = new StringBuffer();
		for (int i = 0, n = dsColm.size(); i < n; i++) {
			String name = dsColm.getString(i, "name");
			String label = dsColm.getString(i, "label");
			String datatype = dsColm.getString(i, "datatype");
			String code = dsColm.getString(i, "code");
			int length = dsColm.getInt(i, "length");
			int width = label.length();
			if (length >= 100) {
				width = 15;
			}

			// code类型
			if (!StringUtil.chkStrNull(code)) {
				rlBF.append("		<ef:columnDropDown name=\"" + name.toLowerCase()
						+ "\" label=\"" + label + "\" code=\""
						+ code.toUpperCase() + "\" width=\"" + width
						+ "\"></ef:columnDropDown>");
			} else {
				if (StringUtil.chkStrNull(datatype)
						|| "string".equalsIgnoreCase(datatype)) {
					rlBF.append("		<ef:columnText name=\"" + name.toLowerCase()
							+ "\" label=\"" + label + "\" width=\"" + width
							+ "\"/>");
				} else if ("date".equalsIgnoreCase(datatype)) {
					String mask = "yyyy-MM-dd";
					String sourceMask = "yyyyMMdd";
					if (name.toLowerCase().endsWith("nd")
							|| name.toLowerCase().endsWith("nf")) {
						mask = "yyyy";
						sourceMask = "yyyy";
					} else if (name.toLowerCase().endsWith("ny")) {
						mask = "yyyy-MM";
						sourceMask = "yyyyMM";
						if (width < 4) {
							width = 4;
						}
					} else if (name.toLowerCase().endsWith("sj")) {
						mask = "yyyy-MM-dd hh:mm:ss";
						sourceMask = "yyyyMMddhhmmss";
						if (width < 9) {
							width = 9;
						}
					} else {
						if (width < 5) {
							width = 5;
						}
					}
					rlBF.append("		<ef:columnText name=\"" + name.toLowerCase()
							+ "\" label=\"" + label
							+ "\" dataType=\"date\" mask=\"" + mask
							+ "\" sourceMask=\"" + sourceMask + "\" width=\""
							+ width + "\"/>");
				} else if ("number".equalsIgnoreCase(datatype)) {
					rlBF.append("		<ef:columnText name=\""
							+ name.toLowerCase()
							+ "\" label=\""
							+ label
							+ "\" dataType=\"number\" mask=\"###########0\" width=\""
							+ width + "\"/>");
				}
			}
			if (i < n - 1) {
				rlBF.append("\r\n");
			}
		}

		String tpl = this.getCodeTpl("jspCode4MngTpl");
		Printer printer = Printer.getInstanceByGsnr(tpl);
		printer.putPara("ywms", bizKeyDesc);
		printer.putPara("ywzj", bizKey);
		printer.putPara("ywzj_xx", bizKey.toLowerCase());
		printer.putPara("listquery", qlBF.toString());
		printer.putPara("listresult", rlBF.toString());
		printer.putDataSet("dsywid", dsYwid);
		return printer.getHtmlContent();
	}

	/**
	 * 生成controller代码
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-18
	 * @since V1.0
	 */
	private String genControllerCode(String bizKey, String bizKeyDesc) throws Exception {
		String tpl = this.getCodeTpl("controllerCodeTpl");
		Printer printer = Printer.getInstanceByGsnr(tpl);
		printer.putPara("ywms", bizKeyDesc);
		printer.putPara("ywzj", bizKey);
		printer.putPara("dqrq", DateUtil.dateToString(DateUtil.getDBTime(), "yyyy-MM-dd"));
		return printer.getHtmlContent();
	}

	/**
	 * 获取getCodeTpl
	 * 
	 * @author yjc
	 * @throws BizException
	 * @throws UnsupportedEncodingException
	 * @date 创建时间 2017-8-18
	 * @since V1.0
	 */
	private String getCodeTpl(String name) throws BizException, UnsupportedEncodingException {
		String filePath = java.net.URLDecoder.decode(new File(this.getClass()
			.getResource("")
			.getPath()).toString(), "UTF-8")
				+ File.separator
				+ "autocodingtpl"
				+ File.separator
				+ name
				+ ".txt";// 文件路径

		// 读取文件内容
		File tplFile = new File(filePath);
		StringBuffer result = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tplFile), "UTF-8"));
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				result.append(s).append("\r\n");
			}
			br.close();
		} catch (Exception e) {
			throw new BizException("获取模板" + name + "出现异常：" + e.getMessage());
		}

		return result.toString();
	}
}
