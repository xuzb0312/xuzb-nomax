package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;

/**
 * form标签
 * 
 * @author yjc
 */
public class FormTag extends Tag{
	private static final long serialVersionUID = -1722037074639628894L;
	private String name;// 唯一标识
	private boolean pane;// 是否窗格方式展示默认false
	private String title;// 标题
	private boolean hidden;// 是否隐藏

	private DataSet dsEvent;// 事件ds存储：类型type,子元素name,事件名称event

	/**
	 * 构造函数
	 */
	public FormTag() {
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
		this.pane = false;
		this.title = null;
		this.hidden = false;

		this.dsEvent = new DataSet();
	}

	/**
	 * 增加事件
	 * 
	 * @author yjc
	 * @date 创建时间 2017-12-7
	 * @since V1.0
	 */
	public void addEvent(String type, String ctagName, String event) {
		DataMap dm = new DataMap();
		dm.put("type", type);
		dm.put("ctagname", ctagName);
		dm.put("event", event);
		this.dsEvent.addRow(dm);
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			JspWriter out = this.pageContext.getOut();
			out.write("<div style=\"display:"
					+ (this.hidden ? "none" : "block") + ";\">");// 外层增加一层div
			// 标题不为空
			if (!StringUtil.chkStrNull(this.title)) {
				TitleTag titleTag = new TitleTag();
				titleTag.setPageContext(this.pageContext);
				titleTag.setParent(this);
				titleTag.setValue(this.title);
				titleTag.doStartTag();
				titleTag.doInitBody();
				titleTag.doAfterBody();
				titleTag.doEndTag();
			}
			if (StringUtil.chkStrNull(this.name)) {
				this.name = StringUtil.getUUID();
			}

			StringBuffer strBF = new StringBuffer();
			strBF.append("<div obj_type=\"form\" id=\"")// form的标签改为div
				.append(this.name)
				.append("\" lay-filter=\"")
				.append(this.name)
				.append("\" class=\"layui-form ")
				.append(this.pane ? " layui-form-pane" : "")
				.append("\">");
			// 输出字符串

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
			strBF.append("</div>");
			strBF.append("<script type=\"text/javascript\">");
			strBF.append("layui.use(\"form\", function(){");
			strBF.append("var form = layui.form;");
			strBF.append("form.render(null,\"")
				.append(this.name)
				.append("\");");

			// 附加事件
			for (int i = 0, n = this.dsEvent.size(); i < n; i++) {
				String type = this.dsEvent.getString(i, "type");
				String ctagName = this.dsEvent.getString(i, "ctagname");
				String event = this.dsEvent.getString(i, "event");

				if ("switch".equalsIgnoreCase(type)) {
					strBF.append("form.on(\"switch("
							+ ctagName
							+ ")\",function(data){"
							+ event
							+ "(data.elem.checked,data.value,data.elem,data.othis);});");// checked:开关是否开启，true或者false;开关value值，也可以通过data.elem.value得到;得到checkbox原始DOM对象;得到美化后的DOM对象
				} else if ("checkbox".equalsIgnoreCase(type)) {
					strBF.append("form.on(\"checkbox("
							+ ctagName
							+ ")\",function(data){"
							+ event
							+ "(data.elem.checked,data.value,data.elem,data.othis);});");// checked:开关是否开启，true或者false;开关value值，也可以通过data.elem.value得到;得到checkbox原始DOM对象;得到美化后的DOM对象
				} else if ("radio".equalsIgnoreCase(type)) {
					strBF.append("form.on(\"radio(" + ctagName
							+ ")\",function(data){" + event
							+ "(data.value,data.elem);});");// 开关value值，elem得到radio原始DOM对象;
				} else if ("select".equalsIgnoreCase(type)) {
					strBF.append("form.on(\"select(" + ctagName
							+ ")\",function(data){" + event
							+ "(data.value,data.elem,data.othis);});");// select:value:选择的值，select-Dom;美化后的dom
				} else {
					throw new AppException("对于事件类型" + type + "程序没有支持，请检查是否准确。");
				}
			}
			strBF.append("});");
			strBF.append("</script>");
			strBF.append("</div>");

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

	public boolean isPane() {
		return pane;
	}

	public void setPane(boolean pane) {
		this.pane = pane;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
}
