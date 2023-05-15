package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;

/**
 * 时间线标签
 * 
 * @author yjc
 */
public class TimelineTag extends Tag{
	private static final long serialVersionUID = -1477422694264840332L;
	private String dataSource;// 数据源--格式为dataSet--列包含title(string-必须),titlebold(boolean可选),icon(string可选),iconcolor(string可选),content(string可选)

	/**
	 * 构造函数
	 */
	public TimelineTag() {
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
		this.dataSource = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuffer strBF = new StringBuffer();
			strBF.append("<ul class=\"layui-timeline\">");
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());

			// --内容
			DataSet dsSource = null;
			// 如果datasource传入的不为空，则需要对数据原进行特殊处理
			if (!StringUtil.chkStrNull(this.dataSource)) {
				dsSource = (DataSet) this.pageContext.getRequest()
					.getAttribute(this.dataSource);// 排序传入前需要先排列好
			}
			if (null != dsSource) {
				for (int i = 0, n = dsSource.size(); i < n; i++) {
					DataMap dmItem = dsSource.get(i);
					String title = dmItem.getString("title");
					boolean titlebold = dmItem.getBoolean("titlebold", true);
					String icon = dmItem.getString("icon", "");
					String iconcolor = dmItem.getString("iconcolor", "");
					String content = dmItem.getString("content", "");

					TimelineItemTag itemTag = new TimelineItemTag();
					itemTag.setPageContext(this.pageContext);
					itemTag.setParent(this);

					// 数据
					itemTag.setTitle(title);
					itemTag.setTitleBold(titlebold);
					if (!StringUtil.chkStrNull(icon)) {
						itemTag.setIcon(icon);
					}
					if (!StringUtil.chkStrNull(iconcolor)) {
						itemTag.setIconColor(iconcolor);
					}
					if (!StringUtil.chkStrNull(content)) {
						itemTag.injectContent("<p>" + content + "</p>");// 注入内容-加入段落
					}
					itemTag.doStartTag();
					itemTag.doInitBody();
					itemTag.doAfterBody();
					itemTag.doEndTag();
				}
			}
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
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write("</ul>");
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

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
}
