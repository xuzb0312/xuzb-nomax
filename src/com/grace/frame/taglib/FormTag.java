package com.grace.frame.taglib;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.MathUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

/**
 * form标签，文本内容
 * 
 * @author yjc
 */
public class FormTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String name;// 唯一id
	private String title;// 标题
	private int rowcount;// 每行几个单元格，默认6个
	private String titleAlign;// 标题对齐方式，默认左对齐
	private boolean border;// 该属性生效，即没有边框。
	private boolean tableLine;// 是否展示table布局栅格，默认false
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排
	private int rowWidthPercent;// 每行占用最大百分比，默认97；
	private boolean collapsable;// 是否允许折叠-默认不可折叠
	private boolean collapsed;// 是否折叠-默认没有折叠

	// 标签自用内容；
	private int minColWidth;// 每个单元格的最小百分比
	private int lastColWidth;// 最后一个单元格的百分比
	private int sumColSpan;// 单元格总数
	private String dataSource;// 数据内容
	private DataMap dataSourceDm;// 数据dm
	private DataSet hiddenInputTagsDs;// 隐藏控件的ds
	private HashMap<String, int[]> rowspanMapCoordinate;// 行合并,// y结尾坐标，横向栅格

	private int lastColWidthPercent;// 最后一个单元格的宽度（通过100-rowWidthPercent计算所得）

	private int maxCollapseIndex;// 最大折叠索引
	private boolean inCollapsePart;// 是否处于折叠部分中
	private boolean partCollapsed;// 处于折叠块中的内容是否处于折叠状态

	public FormTag() {
		this.initTag();
	}

	/**
	 * 初始化标签
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-23
	 * @since V1.0
	 */
	private void initTag() {
		this.name = null;
		this.title = null;
		this.rowcount = 6;
		this.titleAlign = "left";
		this.border = true;// 默认有边框
		if ("v2".equalsIgnoreCase(GlobalVars.VIEW_TYPE)) {
			this.tableLine = true;// v2样式
		} else {
			this.tableLine = false;// 默认没有布局栅格
		}

		this.dataSource = null;
		this.dataSourceDm = new DataMap();
		this.hiddenInputTagsDs = new DataSet();
		this.rowspanMapCoordinate = new HashMap<String, int[]>();

		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;

		this.rowWidthPercent = 97;
		this.lastColWidthPercent = 3;

		this.collapsable = false;
		this.collapsed = false;

		this.maxCollapseIndex = 0;
		this.inCollapsePart = false;
		this.partCollapsed = false;
	}

	/**
	 * 设置cellRowsan坐标
	 * 
	 * @author yjc
	 * @date 创建时间 2017-9-28
	 * @since V1.0
	 */
	public void setRowspanMapCoordinate(int rowspan, int colspan) {
		if (rowspan <= 1) {
			return;
		}

		// 因为调用该方法是在，已经设置后单元列合并之后执行的。所以先减掉。
		int sumColsSpanTemp = this.sumColSpan - colspan;

		int cur_x = sumColsSpanTemp % this.rowcount;// x坐标
		int cur_y = sumColsSpanTemp / this.rowcount;// y坐标
		int cur_y_end = cur_y + rowspan;// 结尾y坐标

		int[] coordInfoArr = { cur_y_end, colspan };// y结尾坐标，横向栅格
		this.rowspanMapCoordinate.put("begin_" + cur_x, coordInfoArr);
	}

	// 数据初始化
	private void initData() throws AppException {
		this.sumColSpan = 0;// 单元格的总数为0
		this.minColWidth = (this.rowWidthPercent / this.rowcount);// 每个单元个的百分比--总宽度为this.rowWidthPercent，目的是在每行的结尾自动增加一个3的单元格
		this.lastColWidth = this.rowWidthPercent
				- (this.minColWidth * (this.rowcount - 1));// 最后一个的百分比

		// 数据的初始化
		if (!StringUtil.chkStrNull(this.dataSource)) {
			this.dataSourceDm = (DataMap) this.pageContext.getRequest()
				.getAttribute(this.dataSource);
		}
		if (null == this.dataSourceDm) {
			this.dataSourceDm = new DataMap();
		}

		// 对于dataSource数据的解析--防止有clob或者blob数据--modify.yjc.2016年6月22日
		Object[] keys = this.dataSourceDm.keySet().toArray();
		for (int i = 0, n = keys.length; i < n; i++) {
			String key = (String) keys[i];
			Object oValue = this.dataSourceDm.get(key);
			if (oValue instanceof Clob) {
				this.dataSourceDm.put(key, StringUtil.Colb2String((Clob) oValue));
			} else if (oValue instanceof Blob) {
				this.dataSourceDm.put(key, "[BLOB数据]");
			} else if (oValue instanceof byte[]) {
				this.dataSourceDm.put(key, "[BYTE数据]");
			}
		}

		this.hiddenInputTagsDs = new DataSet();
	}

	/**
	 * 增加隐藏的标签
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-7-9
	 * @since V1.0
	 */
	public void addHiddenInputTag(String name, String value) throws AppException {
		this.hiddenInputTagsDs.addRow();
		this.hiddenInputTagsDs.put(this.hiddenInputTagsDs.size() - 1, "name", name);
		this.hiddenInputTagsDs.put(this.hiddenInputTagsDs.size() - 1, "value", value);
	}

	/**
	 * 从dataSource中获取数据
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-7-3
	 * @since V1.0
	 */
	public Object getDataSourceData(String key) throws AppException {
		Object value;
		try {
			value = this.dataSourceDm.get(key, null);
		} catch (AppException e) {
			value = null;
		}

		return value;
	}

	/**
	 * 换行
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-2
	 * @since V1.0
	 */
	public String nextLine() throws AppException {
		int rowsumcol = (this.sumColSpan % this.rowcount);// 获取当前行的数量
		String str = "";
		if (0 != rowsumcol) {// 未满格
			int rowleftcol = this.rowcount - rowsumcol;// 当前行剩余的数量
			DataMap dm = this.wrapStr4Children(rowleftcol);
			str = dm.getString("start") + dm.getString("end");
		}
		return str;
	}

	/**
	 * 进入折叠部分
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2020-9-1
	 * @since V1.0
	 */
	public int enterPart(boolean partCollapsed) throws AppException {
		this.maxCollapseIndex++;
		this.inCollapsePart = true;
		this.partCollapsed = partCollapsed;
		return this.maxCollapseIndex;
	}

	/**
	 * 出折叠部分
	 * 
	 * @author yjc
	 * @date 创建时间 2020-9-1
	 * @since V1.0
	 */
	public int leavePart() throws AppException {
		this.inCollapsePart = false;
		this.partCollapsed = false;
		return this.maxCollapseIndex;
	}

	/**
	 * 判断是否为换行--防止子元素分为两行展示，提前判断一下是否会换行
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-3
	 * @since V1.0
	 */
	public boolean isBreak(int colspan) {
		int rowsumcol = (this.sumColSpan % this.rowcount);// 获取当前行的数量
		int rowleftcol = this.rowcount - rowsumcol;// 当前行剩余的数量
		if (colspan <= rowleftcol) {// 如果子元素的要求数量大于本行的剩余数量，则需要换行
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取外围包含子标签的str
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-7-2
	 * @since V1.0
	 */
	public DataMap wrapStr4Children(int colspan, String align) throws AppException {
		return this.wrapStr4Children(colspan, 1, align);// 高度默认为1
	}

	/**
	 * 获取外围包含子标签的str
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-7-2
	 * @since V1.0
	 */
	public DataMap wrapStr4Children(int colspan, int rowspan, String align) throws AppException {
		DataMap dm = new DataMap();
		StringBuffer startBF = new StringBuffer();
		StringBuffer endBF = new StringBuffer();

		if (colspan > this.rowcount) {
			colspan = this.rowcount;
		} else if (colspan < 1) {
			throw new AppException("传入的colspan不正确");
		}

		// 对于列合并信息的预处理
		int cur_x = this.sumColSpan % this.rowcount;// x坐标-当前
		int cur_y = this.sumColSpan / this.rowcount;// y坐标-当前
		String rowspanIndexKey = "begin_" + cur_x;// 列合并key
		if (this.rowspanMapCoordinate.containsKey(rowspanIndexKey)) {
			int[] coordInfoArr = this.rowspanMapCoordinate.get(rowspanIndexKey);
			int cur_y_end = coordInfoArr[0];// 列合并结尾y坐标
			int cellColspan = coordInfoArr[1];// 合并单元格
			if (cur_y_end > cur_y) {// 如果当前y超过了合并y则移除合并信息，因为已经合并完成。
				this.sumColSpan = this.sumColSpan + cellColspan;// 调整栅格合计
			}
			if (cur_y_end - 1 == cur_y) {
				this.rowspanMapCoordinate.remove("begin_" + cur_x);
			}
		}

		// 处理
		int rowsumcol = (this.sumColSpan % this.rowcount);// 获取当前行的数量
		int rowleftcol = this.rowcount - rowsumcol;// 当前行剩余的数量
		boolean isoddrow = MathUtil.isEven((this.sumColSpan / this.rowcount) + 1);// 当前行是否为奇数行
		int autofitcol = 0;// 自动补充的数量
		boolean isrowstart = (rowsumcol == 0);// 是否为行首
		if (colspan > rowleftcol) {// 如果子元素的要求数量大于本行的剩余数量，则空格填充本行余下的单元格
			autofitcol = rowleftcol;
		}
		this.sumColSpan = this.sumColSpan + colspan + autofitcol;// 最终处理完成后的总数量
		boolean isrowend = (0 == (this.sumColSpan % this.rowcount));// 是否行尾

		// 开始生成字符串
		if (autofitcol > 0) {
			// 只要存在自动补齐字符串
			startBF.append("<td colspan=\"")
				.append(autofitcol + 1)
				.append("\" width=\"")
				.append((this.minColWidth * (autofitcol - 1) + this.lastColWidth) + 3)
				.append("%\"></td></tr>");
			isoddrow = !isoddrow;// 则转换为偶数行--自动反转
			isrowstart = true;// 新行附加数据
		}

		if (isrowstart) {// 行开始
			if (isoddrow) {// 奇数行
				startBF.append("<tr class=\"formOddTr\"")
					.append(this.inCollapsePart ? " _collapsePart=\""
							+ this.maxCollapseIndex + "\"" : "")
					.append("")
					.append(this.partCollapsed ? " style=\"display:none;\"" : "")
					.append(">");
			} else {// 偶数行
				startBF.append("<tr class=\"formEvenTr\"")
					.append(this.inCollapsePart ? " _collapsePart=\""
							+ this.maxCollapseIndex + "\"" : "")
					.append("")
					.append(this.partCollapsed ? " style=\"display:none;\"" : "")
					.append(">");
			}
		}

		if (colspan == 1) {
			startBF.append("<td ")
				.append("right".equalsIgnoreCase(align) ? ("class=\"" + align + "\"") : "")
				.append("style=\"text-align:" + align + ";")
				.append(isrowend && this.tableLine ? ("border-right:0px;") : "")
				.append("\" width=\"")
				.append((isrowend ? this.lastColWidth : this.minColWidth))
				.append("%\">");
		} else {
			startBF.append("<td ")
				.append("right".equalsIgnoreCase(align) ? ("class=\"" + align + "\"") : "")
				.append("style=\"text-align:" + align + ";")
				.append(isrowend && this.tableLine ? ("border-right:0px;") : "")
				.append("\" colspan=\"")
				.append(colspan)
				.append((rowspan > 1) ? ("\" rowspan=\"" + rowspan) : "")
				.append("\" width=\"")
				.append((isrowend ? (this.minColWidth * (colspan - 1) + this.lastColWidth) : (this.minColWidth * colspan)))
				.append("%\">");
		}

		if (isrowend) {
			if (this.tableLine) {
				if (this.lastColWidthPercent > 0) {
					endBF.append("</td><td width=\"")
						.append(this.lastColWidthPercent)
						.append("%\" style=\"border-left:0px;\"></td></tr>");
				} else {
					endBF.append("</td></tr>");
				}
			} else {
				if (this.lastColWidthPercent > 0) {
					endBF.append("</td><td width=\"")
						.append(this.lastColWidthPercent)
						.append("%\"></td></tr>");
				} else {
					endBF.append("</td></tr>");
				}
			}
		} else {
			endBF.append("</td>");
		}

		// 行合并大于1的，进行处理后续合并信息
		if (rowspan > 1) {
			this.setRowspanMapCoordinate(rowspan, colspan);
		}

		dm.clear();
		dm.put("start", startBF.toString());
		dm.put("end", endBF.toString());
		return dm;
	}

	/**
	 * 获取外围包含子标签的str-默认左对齐
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-7-2
	 * @since V1.0
	 */
	public DataMap wrapStr4Children(int colspan) throws AppException {
		return this.wrapStr4Children(colspan, "left");
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			String nameTemp = this.name;
			if (StringUtil.chkStrNull(nameTemp)) {
				nameTemp = StringUtil.getUUID();
			}

			// 进行数据计算的初始化
			this.initData();

			String borderStr = "";
			if (!this.border) {// 设置存在边框的时候渲染边框和标题
				borderStr = " style=\"border:0px;\"";
			} else {
				if (this.tableLine && !StringUtil.chkStrNull(this.title)) {
					borderStr = " style=\"border-left:0px;border-bottom:0px;border-right:0px;\"";
				}
			}

			// 数据组装
			StringBuffer strBF = new StringBuffer();
			strBF.append("<fieldset class=\"formfieldset\"").append(borderStr);
			TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
			strBF.append(">");
			if (!StringUtil.chkStrNull(this.title)) {
				strBF.append("	<legend style=\"text-align:")
					.append(this.titleAlign)
					.append(";");
				if (this.tableLine) {
					strBF.append("padding:5px;margin-left:15px;");
				}
				strBF.append("\">");
				strBF.append(this.title);
				if (this.collapsable) {// 如果可折叠
					if (this.collapsed) {
						strBF.append("<a href='javascript:void(0)' class='form_title_tools form_title_tools_expand' title='展开'></a>");
					} else {
						strBF.append("<a href='javascript:void(0)' class='form_title_tools' title='折叠'></a>");
					}
				}
				strBF.append("	</legend>");
			}
			strBF.append("	<form obj_type=\"form\" id=\"")
				.append(nameTemp)
				.append("\" class=\"easyui-form\" style=\"margin: 0px;")
				.append(this.collapsed ? "display: none;" : "")
				.append("\">");
			strBF.append("<input type=\"text\" style=\"display:none\"/>");// 目的是屏蔽浏览器当form中只有一个输入框时，回车自动提交的问题。
			strBF.append("		<table width=\"100%\"")
				.append(this.tableLine ? (" class=\"formtable_line\"") : "")
				.append(">");
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
		} catch (Exception e) {
			this.dealException(e);
		}
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * 终止
	 */
	@Override
	public int doEndTag() throws JspException {
		try {
			StringBuffer strBF = new StringBuffer();
			// 标签结束时，查看本行是否慢格，为满格的自动填充满格
			int rowsumcol = (this.sumColSpan % this.rowcount);// 获取当前行的数量
			if (0 != rowsumcol) {// 未满格
				int rowleftcol = this.rowcount - rowsumcol;// 当前行剩余的数量
				DataMap dm = this.wrapStr4Children(rowleftcol);
				strBF.append(dm.getString("start")).append(dm.getString("end"));
			}

			strBF.append("		</table>");
			strBF.append("<div style=\"display:none;\">");
			for (int i = 0, n = this.hiddenInputTagsDs.size(); i < n; i++) {
				String hname = this.hiddenInputTagsDs.getString(i, "name");
				String hvalue = this.hiddenInputTagsDs.getString(i, "value");
				strBF.append("<input obj_type=\"hiddeninput\" type=\"text\" id=\"")
					.append(hname)
					.append("\" ")
					.append(" name=\"")
					.append(hname)
					.append("\" ")
					.append(" value=\"")
					.append(hvalue)
					.append("\" />");
			}

			strBF.append("</div>");
			strBF.append("	</form>");
			strBF.append("</fieldset>");

			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
		} catch (Exception e) {
			this.dealException(e);
		} finally {
			this.release();// 资源释放
		}
		return EVAL_PAGE;
	}

	/**
	 * 资源释放
	 */
	@Override
	public void release() {
		this.initTag();
		super.release();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getRowcount() {
		return rowcount;
	}

	public void setRowcount(int rowcount) {
		this.rowcount = rowcount;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getTitleAlign() {
		return titleAlign;
	}

	public void setTitleAlign(String titleAlign) {
		this.titleAlign = titleAlign;
	}

	public boolean isBorder() {
		return border;
	}

	public void setBorder(boolean border) {
		this.border = border;
	}

	public boolean isTableLine() {
		return tableLine;
	}

	public void setTableLine(boolean tableLine) {
		this.tableLine = tableLine;
	}

	public int getIntroSetp() {
		return introSetp;
	}

	public void setIntroSetp(int introSetp) {
		this.introSetp = introSetp;
	}

	public String getIntroContent() {
		return introContent;
	}

	public void setIntroContent(String introContent) {
		this.introContent = introContent;
	}

	public String getIntroPosition() {
		return introPosition;
	}

	public void setIntroPosition(String introPosition) {
		this.introPosition = introPosition;
	}

	public int getRowWidthPercent() {
		return rowWidthPercent;
	}

	public void setRowWidthPercent(int rowWidthPercent) {
		if (rowWidthPercent > 100) {
			rowWidthPercent = 100;
		}
		if (rowWidthPercent < 90) {
			rowWidthPercent = 90;
		}
		this.rowWidthPercent = rowWidthPercent;
		this.lastColWidthPercent = 100 - rowWidthPercent;
	}

	public boolean isCollapsable() {
		return collapsable;
	}

	public void setCollapsable(boolean collapsable) {
		this.collapsable = collapsable;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}
}
