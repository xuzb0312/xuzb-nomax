package com.grace.frame.taglib.plugin;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.BizException;
import com.grace.frame.taglib.Tag;
import com.grace.frame.util.TagSupportUtil;

/**
 * 插件引入标签
 * <p>
 * 将不太常用的一些标签单独使用插件引入标签进行引入lib支持；避免在页面加载时加载该插件的lib，加快页面响应时间 <br>
 * 注意：需要使用一定要在标签使用前引入
 * </p>
 * 
 * @author yjc
 */
public class ImportPulginTag extends Tag{
	private static final long serialVersionUID = 1L;
	/**
	 * 当前支持的插件库<br>
	 * 1.echarts标签的使用需要引入echarts库。pulgin="echarts";
	 */
	private String pulgin;// 插件名称

	public ImportPulginTag() {
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
		this.pulgin = null;
	}

	/**
	 * 标签开始时
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuffer strBF = new StringBuffer();

			// 1.echarts标签的使用需要引入echarts库。pulgin="echarts";
			if ("echarts".equalsIgnoreCase(this.pulgin)) {
				// 判断类库支持是否引入
				if (null == this.pageContext.getAttribute("___echarts_lib")) {
					strBF.append("<script type=\"text/javascript\" src=\"./frame/plugins/echarts/echarts.min.js?v=")
						.append(GlobalVars.FRAME_VERSION)
						.append("\"></script>");

					// 页面放入标识标识已经引入，再次使用的时候无需再次引入
					this.pageContext.setAttribute("___echarts_lib", true);
				}
			} else {
				throw new BizException("pulgin参数不合法");
			}

			// 数据的输出
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());
		} catch (IOException e) {
			TagSupportUtil.dealException(e, "ImportPulginTag标签解析时，将html输出到jsp时，出现IO异常："
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

	public String getPulgin() {
		return pulgin;
	}

	public void setPulgin(String pulgin) {
		this.pulgin = pulgin;
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