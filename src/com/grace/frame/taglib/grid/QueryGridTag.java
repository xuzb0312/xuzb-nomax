package com.grace.frame.taglib.grid;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import net.sf.json.JSONObject;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.taglib.TabPageTag;
import com.grace.frame.taglib.Tag;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

/**
 * 数据窗口标签
 * 
 * @author yjc
 */
public class QueryGridTag extends Tag{

	private static final long serialVersionUID = 1L;

	// 属性
	private String name;// 唯一id
	private String title;// 标题
	private String dataSource;// 数据内容
	private int height;// 高度
	private boolean multi;// 是否为多选模式
	private boolean page;// 是否多页
	private boolean edit;// 是否修改
	private String groupColumnName;// 分组的列明
	private boolean exportFile;// 是否展示导出文件的按钮-默认展示
	private String sortname;// 默认排序列：排序列的名称
	private String sortorder;// 排序方式：排序顺序，升序或者降序（asc or desc）--默认asc
	private int frozenIndex;// 冻结列索引，默认-1不进行冻结，否则按照所有前的列冻结--只有在分页模式下生效--暂时不建议使用，问题较多
	private String iconCls;// gird的图标
	private boolean sortable;// 排序-默认排序
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排
	private boolean showFooter;// 显示底部工具栏

	/**
	 * 编辑模式--默认传统弹出窗口的模式(normal)； <br>
	 * 取值有： <br>
	 * normal（传统弹出窗口的处理方式）<br>
	 * smart（简洁模式，不弹出窗口， 在界面上可直接编辑,同时界面上可以增加一行，减少一行，编辑一行）<br>
	 * onlyedit(简洁只改模式，与简洁模式类似，但是界面上无增加一行，减少一行，只有编辑)
	 */
	private String editType;

	// 事件
	/**
	 * 参数：rowid,e<br>
	 * 说明：当用户点击当前行在未选择此行时触发。rowid：此行id；e：事件对象。返回值为ture或者false。如果返回true则选择完成，
	 * 如果返回false则不会选择此行也不会触发其他事件
	 */
	private String onBeforeSelectRow;

	/**
	 * 参数rowid,iRow,iCol,e<br>
	 * 双击行时触发。rowid：当前行id；iRow：当前行索引位置；iCol：当前单元格位置索引；e:event对象
	 */
	private String ondblClickRow;

	/**
	 * rowid,iRow,iCol,e<br>
	 * 在行上右击鼠标时触发此事件。rowid：当前行id；iRow：当前行位置索引；iCol：当前单元格位置索引；e：event对象
	 */
	private String onRightClickRow;

	/**
	 * 键盘按键事件-参数event
	 */
	private String onKeyDown;

	/**
	 * 此事件发生在行点击后 rowid 为行ID； status
	 * 为选择状态。当multiselect为true时使用，当行被选中时返回true；为选中时返回false。
	 */
	private String onSelectRow;

	/**
	 * 此事件发生在编辑模式下，当删除按钮点击时，删除前进行触发，当返回true时继续进行删除，返回false不进行删除，参数为rowid/rowids(
	 * 多选模式时)；
	 */
	private String onBeforeDeleteRow;

	/**
	 * multiselect为ture，且点击头部的checkbox时才会触发此事件。aRowids：所有选中行的id集合，为一个数组。status：
	 * boolean变量说明checkbox的选择状态，true选中false不选中。无论checkbox是否选择，aRowids始终有 值
	 */
	private String onSelectAll;

	/**
	 * 部分选择完成后的处理事件-onSelectPartRow(qshh-number,zzhh-number);
	 */
	private String onSelectPartRow;

	/**
	 * 子标签的集合
	 */
	private DataSet columnDs;
	private String data_uiid;// 数据缓存的唯一id
	private DataSet columnGroupDs;// 列合并组信息

	/**
	 * 构造函数
	 */
	public QueryGridTag() {
		this.initTag();
	}

	/**
	 * 标签初始化
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-23
	 * @since V1.0
	 */
	private void initTag() {
		this.name = null;
		this.title = null;
		this.dataSource = null;
		this.height = 15;
		this.multi = false;
		this.page = false;
		this.edit = false;
		this.groupColumnName = null;
		this.editType = "normal";// 传统的编辑模式，只有在edit属性为true时起作用
		this.exportFile = true;// 默认展示导出按钮
		this.sortname = null;
		this.sortorder = "asc";
		this.frozenIndex = -1;// 不仅冻结
		this.iconCls = "icon-table";
		this.sortable = true;// 默认排序
		this.showFooter = true;// 是否展示底部工具栏

		this.onBeforeSelectRow = null;
		this.ondblClickRow = null;
		this.onRightClickRow = null;
		this.onKeyDown = null;
		this.onSelectRow = null;
		this.onBeforeDeleteRow = null;
		this.onSelectAll = null;
		this.onSelectPartRow = null;

		this.data_uiid = null;
		this.columnDs = new DataSet();
		this.columnGroupDs = new DataSet();
		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;
	}

	/**
	 * 增加一列数据
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-7-9
	 * @since V1.0
	 */
	public void addColumn(String type, DataMap para) throws AppException {
		this.columnDs.addRow();
		this.columnDs.put(this.columnDs.size() - 1, "type", type);
		this.columnDs.put(this.columnDs.size() - 1, "columnpara", para);
	}

	/**
	 * 增加列分组
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-27
	 * @since V1.0
	 */
	public void addColumnGroup(String startName, int num, String title,
			String helpTip) throws AppException {
		this.columnGroupDs.addRow();
		this.columnGroupDs.put(this.columnGroupDs.size() - 1, "startname", startName);
		this.columnGroupDs.put(this.columnGroupDs.size() - 1, "num", num);
		this.columnGroupDs.put(this.columnGroupDs.size() - 1, "title", title);
		this.columnGroupDs.put(this.columnGroupDs.size() - 1, "helptip", helpTip);
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.name)) {
				throw new AppException("name属性不允许为空");
			}

			// 增加判断，如果设置了分组列，则分页必须为true
			if (!StringUtil.chkStrNull(this.groupColumnName)) {
				if (!this.page) {
					throw new AppException("当前设置了分组列，分页属性必须设置为true。");
				}
			}
			if (!"asc".equalsIgnoreCase(this.sortorder)
					&& !"desc".equalsIgnoreCase(this.sortorder)) {
				throw new AppException("sortOrder属性设置不正确，只允许为asc或者desc");
			}

			// 增加判断父表签是否为tabPage，如果是，则向上加入tabPage中
			if (this.getParent() instanceof TabPageTag) {
				TabPageTag parentTag = (TabPageTag) this.getParent();
				parentTag.addGridName(this.name);
			}
			this.data_uiid = "_data_" + StringUtil.getUUID();

			// 如果datasource传入的不为空，则需要对数据原进行特殊处理
			if (!StringUtil.chkStrNull(this.dataSource)) {
				DataSet dsSource = (DataSet) this.pageContext.getRequest()
					.getAttribute(this.dataSource);

				// 将此数据放置到session中
				this.pageContext.getSession()
					.setAttribute(this.data_uiid, dsSource);
			}
			if (StringUtil.chkStrNull(this.iconCls)) {
				this.iconCls = "icon-table";
			}

			this.columnDs.clear();// 数据清空

			// 高度初始化调整
			if ("v2".equalsIgnoreCase(GlobalVars.VIEW_TYPE)) {
				this.height = (this.height * 23) / 28;
			}
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
			// 测量宽度使用的
			strBF.append("<div id=\"")
				.append(this.name)
				.append("_width_use\" style=\"width:100%;\"></div>");
			// 表格容器
			strBF.append("<div obj_type=\"querygrid\" style=\"margin:8px 2px 5px 2px;\" id=\"")
				.append(this.name)
				.append("\"");
			TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
			strBF.append("><table id=\"")
				.append(this.name)
				.append("_table\"></table>");
			strBF.append("<div id=\"")
				.append(this.name)
				.append("_pager\" style=\"display:")
				.append((this.showFooter ? "block" : "none"))
				.append(";\"></div>");
			this.appendln(strBF, "</div>");
			// 脚本代码
			this.appendln(strBF, "<script type=\"text/javascript\">");
			this.appendln(strBF, "$(function(){");

			// 数据发送到前台增加
			DataMap clientData = new DataMap();
			clientData.put("datasource", this.dataSource);
			clientData.put("data_uiid", this.data_uiid);
			clientData.put("title", this.title);
			clientData.put("height", this.height);
			clientData.put("onselectall", this.onSelectAll);
			clientData.put("onbeforeselectrow", this.onBeforeSelectRow);
			clientData.put("ondblclickrow", this.ondblClickRow);
			clientData.put("onrightclickrow", this.onRightClickRow);
			clientData.put("onkeydown", this.onKeyDown);
			clientData.put("onselectrow", this.onSelectRow);
			clientData.put("onselectpartrow", this.onSelectPartRow);// add.yjc.2016年11月21日-部分选择函数
			clientData.put("onbeforedeleterow", this.onBeforeDeleteRow);
			clientData.put("columnds", this.columnDs);
			clientData.put("multi", this.multi);
			clientData.put("page", this.page);
			clientData.put("edit", this.edit);
			clientData.put("groupcolumnname", this.groupColumnName);// 增加支持分组操作
			clientData.put("columngroupds", this.columnGroupDs);// 列信息
			clientData.put("edittype", this.editType);
			clientData.put("exportfile", this.exportFile);
			clientData.put("sortname", this.sortname);
			clientData.put("sortorder", this.sortorder);
			clientData.put("frozenindex", this.page ? this.frozenIndex : -1);// 冻结列的支持--只有在分页模式下生效
			clientData.put("iconcls", this.iconCls);
			clientData.put("sortable", this.sortable);

			this.appendln(strBF, "$(\"#" + this.name
					+ "\").data(\"initData\", new HashMap("
					+ JSONObject.fromObject(clientData).toString() + "));");

			this.appendln(strBF, "getObject(\"" + this.name + "\").initGrid();");// 初始化grid表格

			this.appendln(strBF, "});");
			this.appendln(strBF, "</script>");

			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());

			this.release();// 资源释放
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
		} catch (Exception e) {
			this.dealException(e);
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

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getOnBeforeSelectRow() {
		return onBeforeSelectRow;
	}

	public void setOnBeforeSelectRow(String onBeforeSelectRow) {
		this.onBeforeSelectRow = onBeforeSelectRow;
	}

	public String getOndblClickRow() {
		return ondblClickRow;
	}

	public void setOndblClickRow(String ondblClickRow) {
		this.ondblClickRow = ondblClickRow;
	}

	public String getOnRightClickRow() {
		return onRightClickRow;
	}

	public void setOnRightClickRow(String onRightClickRow) {
		this.onRightClickRow = onRightClickRow;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isMulti() {
		return multi;
	}

	public void setMulti(boolean multi) {
		this.multi = multi;
	}

	public boolean isPage() {
		return page;
	}

	public void setPage(boolean page) {
		this.page = page;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public String getOnKeyDown() {
		return onKeyDown;
	}

	public void setOnKeyDown(String onKeyDown) {
		this.onKeyDown = onKeyDown;
	}

	public String getGroupColumnName() {
		return groupColumnName;
	}

	public void setGroupColumnName(String groupColumnName) {
		this.groupColumnName = groupColumnName;
	}

	public String getEditType() {
		return editType;
	}

	public void setEditType(String editType) {
		this.editType = editType;
	}

	public boolean isExportFile() {
		return exportFile;
	}

	public void setExportFile(boolean exportFile) {
		this.exportFile = exportFile;
	}

	public String getOnSelectRow() {
		return onSelectRow;
	}

	public void setOnSelectRow(String onSelectRow) {
		this.onSelectRow = onSelectRow;
	}

	public String getOnBeforeDeleteRow() {
		return onBeforeDeleteRow;
	}

	public void setOnBeforeDeleteRow(String onBeforeDeleteRow) {
		this.onBeforeDeleteRow = onBeforeDeleteRow;
	}

	public String getSortname() {
		return sortname;
	}

	public void setSortname(String sortname) {
		this.sortname = sortname;
	}

	public String getSortorder() {
		return sortorder;
	}

	public void setSortorder(String sortorder) {
		this.sortorder = sortorder;
	}

	public String getOnSelectAll() {
		return onSelectAll;
	}

	public void setOnSelectAll(String onSelectAll) {
		this.onSelectAll = onSelectAll;
	}

	public String getOnSelectPartRow() {
		return onSelectPartRow;
	}

	public void setOnSelectPartRow(String onSelectPartRow) {
		this.onSelectPartRow = onSelectPartRow;
	}

	public int getFrozenIndex() {
		return frozenIndex;
	}

	public void setFrozenIndex(int frozenIndex) {
		this.frozenIndex = frozenIndex;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
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

	public boolean isShowFooter() {
		return showFooter;
	}

	public void setShowFooter(boolean showFooter) {
		this.showFooter = showFooter;
	}
}
