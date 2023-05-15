package com.grace.frame.taglib.plugin;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import net.sf.json.JSONObject;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.taglib.Tag;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;
import com.grace.frame.util.echarts.json.EchartsOption;

public class EchartsTag extends Tag{
	private static final long serialVersionUID = 1L;
	/**
	 * echarts--使用前必须引入echarts库。
	 */
	private String name;// 名称
	private String style;// 样式
	private String dataSource;// 数据内容-数据-后台必须传入的为net.sf.json.JSONObject,或者EchartsOption,或者String格式--用于设置option的信息

	public EchartsTag() {
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
		this.style = "width:90%;min-height:300px;margin:0px auto;";
		this.dataSource = null;
	}

	/**
	 * 标签开始时
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.name)) {
				throw new AppException("传入的name为空");
			}

			// 判断类库支持是否引入
			if (null == this.pageContext.getAttribute("___echarts_lib")) {
				ImportPulginTag ipt = new ImportPulginTag();
				ipt.setPageContext(this.pageContext);
				ipt.setParent(this.getParent());
				ipt.setPulgin("echarts");
				ipt.doStartTag();
				ipt.doInitBody();
				ipt.doAfterBody();
				ipt.doEndTag();

				// 页面放入标识标识已经引入，再次使用的时候无需再次引入
				this.pageContext.setAttribute("___echarts_lib", true);
			}

			// 数据的初始化
			String jsonOption;// json串
			if (StringUtil.chkStrNull(this.dataSource)) {
				jsonOption = (new JSONObject()).toString();
			} else {
				Object jsonObj = this.pageContext.getRequest()
					.getAttribute(this.dataSource);
				if (null == jsonObj) {
					jsonOption = (new JSONObject()).toString();
				} else if (jsonObj instanceof JSONObject
						|| jsonObj instanceof EchartsOption
						|| jsonObj instanceof String) {
					jsonOption = jsonObj.toString();
				} else {
					throw new BizException("dataSource传入的数据类型不正确，必须为：JSONObject/EchartsOption/String类型");
				}
			}

			// 开始渲染标签
			StringBuffer strBF = new StringBuffer();
			strBF.append("<div obj_type=\"echarts\" id=\"")
				.append(this.name)
				.append("\" name=\"")
				.append(this.name)
				.append("\" ");
			if (!StringUtil.chkStrNull(this.style)) {
				strBF.append("style=\"").append(this.style).append("\"");
			}
			TagSupportUtil.appendln(strBF, "></div>");
			TagSupportUtil.appendln(strBF, "<script type=\"text/javascript\">");
			TagSupportUtil.appendln(strBF, "var ___tmp_" + this.name
					+ " = echarts.init(document.getElementById(\"" + this.name
					+ "\"));");
			TagSupportUtil.appendln(strBF, "___tmp_" + this.name
					+ ".type = \"echarts\";");// 给其增加属性type
			TagSupportUtil.appendln(strBF, "$(\"#" + this.name
					+ "\").data(\"echartsObj\", ___tmp_" + this.name + ");");
			TagSupportUtil.appendln(strBF, "___tmp_" + this.name
					+ ".setOption(" + jsonOption + ");");
			TagSupportUtil.appendln(strBF, "</script>");

			// 数据的输出
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());
		} catch (IOException e) {
			TagSupportUtil.dealException(e, "EchartsTag标签解析时，将html输出到jsp时，出现IO异常："
					+ e.getMessage(), this.pageContext);
		} catch (Exception e) {
			TagSupportUtil.dealException(e, this.pageContext);
		}
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * 标签结束时，执行
	 */
	@Override
	public int doEndTag() throws JspException {
		try {
			this.release();// 资源释放
		} catch (Exception e) {
			TagSupportUtil.dealException(e, this.pageContext);
		}
		return EVAL_PAGE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 资源释放
	 */
	@Override
	public void release() {
		this.initTag();
		super.release();
	}
}
