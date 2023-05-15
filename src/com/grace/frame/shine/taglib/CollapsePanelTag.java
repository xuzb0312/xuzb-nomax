package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.StringUtil;

/**
 * 可折叠面板
 * 
 * @author yjc
 */
public class CollapsePanelTag extends Tag{
	private static final long serialVersionUID = 2623367073178206873L;
	private String name;// 标签名称
	private boolean accordion;// 手风琴效果-那么在进行折叠操作时，始终只会展现当前的面板--默认不开

	/**
	 * 当折叠面板点击展开或收缩时触发<br>
	 * function(show,title,content) <br>
	 * 三个成员：<br>
	 * show //得到当前面板的展开状态，true或者false ：<br>
	 * title //得到当前点击面板的标题区域DOM对象：<br>
	 * content //得到当前点击面板的内容区域DOM对象
	 */
	private String onClick;

	/**
	 * 构造函数
	 */
	public CollapsePanelTag() {
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
		this.accordion = false;
		this.onClick = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.name)) {
				this.name = StringUtil.getUUID();
			}

			StringBuffer strBF = new StringBuffer();
			strBF.append("<div id=\"")
				.append(this.name)
				.append("\" class=\"layui-collapse\"")
				.append(this.accordion ? " lay-accordion" : "")
				.append(" lay-filter=\"")
				.append(this.name)
				.append("\">");

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
			strBF.append("</div>");
			strBF.append("<script type=\"text/javascript\">");
			strBF.append("layui.use('element', function(){");
			strBF.append("var element = layui.element;");
			strBF.append("element.render(\"collapse\",\"")
				.append(this.name)
				.append("\");");
			if (!StringUtil.chkStrNull(this.onClick)) {// 设置了监听事件
				strBF.append("element.on(\"collapse(")
					.append(this.name)
					.append(")\",function(data){")
					.append(this.onClick.trim())
					.append("(data.show,data.title,data.content);});");
			}
			strBF.append("});");
			strBF.append("</script>");
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

	public boolean isAccordion() {
		return accordion;
	}

	public void setAccordion(boolean accordion) {
		this.accordion = accordion;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}
}
