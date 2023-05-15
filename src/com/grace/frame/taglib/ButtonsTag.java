package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

/**
 * 按钮组
 * 
 * @author yjc
 */
public class ButtonsTag extends Tag{

	private static final long serialVersionUID = 1L;
	// 属性
	private String closebutton;// 是否默认包含关闭按钮，默认值是
	private String align;// 默认right;
	private int colspan;// 在form中有效，标识所占的单元格数量，form的总单元格数--只有放到form中时有效
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排
	// 自定义
	private String endWrapStr;// 结束的字符串

	public ButtonsTag() {
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
		this.closebutton = "true";
		this.align = "right";
		this.colspan = 0;
		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuffer strBF = new StringBuffer();
			// 如果父容器为form的化，按照单元格进行布局，处理其所占的单元格数
			if (this.getParent() instanceof FormTag) {
				FormTag formTag = (FormTag) this.getParent();
				int colspanTemp = this.colspan;
				if (0 == colspanTemp) {
					colspanTemp = formTag.getRowcount();
				}
				DataMap wrapDm = formTag.wrapStr4Children(colspanTemp);
				strBF.append(wrapDm.getString("start"));// 拼上布局字符串
				this.endWrapStr = wrapDm.getString("end");
			} else {
				this.endWrapStr = null;
			}

			strBF.append("<div style=\"padding:5px 10px 5px 10px;text-align:")
				.append(this.align)
				.append(";\"");
			TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
			strBF.append(">");
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
			if (!"false".equalsIgnoreCase(this.closebutton)) {
				strBF.append("<a href=\"javascript:void(0);\" class=\"easyui-linkbutton\" data-options=\"iconCls:'icon-cross'\" onclick=\"closeWindow();\">关闭</a>");
			}
			strBF.append("</div>");

			// 当父容器为form时，周围环绕上布局字符串
			if (!StringUtil.chkStrNull(this.endWrapStr)) {
				strBF.append(this.endWrapStr);
			}

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

	public String getClosebutton() {
		return closebutton;
	}

	public void setClosebutton(String closebutton) {
		this.closebutton = closebutton;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
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
}
